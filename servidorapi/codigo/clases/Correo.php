<?php

declare( strict_types = 1 );

/**
 * Xestiona o envío de correos electrónicos mediante unha conexión SMTP
 * autenticada e cifrada mediante TLS.
 *
 * A clase constrúe as cabeceiras e o contido das mensaxes, xestiona os
 * destinatarios e realiza o envío directamente ao servidor SMTP.
 */
final readonly class Correo {

    /** @var string Enderezo de correo electrónico empregado como remitente. */
    private const DENDE_CORREO = DENDE_CORREO;

    /** @var int Porto do servidor SMTP. */
    private const PORTO_C = 587;

    /** @var string Enderezo do servidor SMTP. */
    private const SERVIDOR_C = SERVIDOR_CORREO;

    /** @var string Nome de usuario empregado na autenticación SMTP. */
    private const USUARIO_C = USUARIO_CORREO;

    /** @var string Credencial empregada na autenticación SMTP. */
    private const CONTRASINAL_C = AUTORIZACION_CORREO;

    /**
     * @var ?string Identificador Message-ID xerado para o correo.
     *              É null se o envío non chega a completarse correctamente.
     */
    public ?string $boundary;

    /** @var mixed Socket empregado para a comunicación co servidor SMTP. */
    private mixed $socket;

    /** @var string Nome de host empregado nos comandos EHLO. */
    private string $hostname;

    /** @var array<string, string> Mensaxes asociadas aos distintos erros das operacións SMTP. */
    private array $erros;

    /** @var array<int, array{0: string, 1: string}> Lista de destinatarios, cos seus enderezos e nomes asociados. */
    private array $para;

    /** @var ?string Identificador Message-ID do correo ao que se responde, se existe. */
    private ?string $in_reply_to;

    /**
     * Constrúe e envía un correo electrónico mediante SMTP.
     *
     * Inicializa a configuración interna, prepara os destinatarios,
     * normaliza o corpo da mensaxe e establece a conexión co servidor.
     *
     * Os erros producidos durante o proceso rexístranse mediante
     * rexistrar_problema(). Se o envío non se completa, boundary queda a null.
     *
     * @param string|array<string, string> $para Enderezo do destinatario ou array asociativo de enderezos e nomes (correo => nome).
     * @param string $asunto Asunto do correo.
     * @param string $mensaxe Corpo da mensaxe en texto plano.
     * @param array<string, string> $reply_to Destinatarios opcionais da cabeceira Reply-To.
     * @param ?string $boundary Identificador do correo anterior para establecer unha resposta encadeada.
     */
    public function __construct( string|array $para, string $asunto, string $mensaxe, array $reply_to = [], ?string $boundary = null ) {

        $identificador = null;

        try {

            $this->cargar_erros();
            $this->in_reply_to = $boundary;

            $plano = $this->axustar_texto_plano( $mensaxe );
            $destinatarios = [];

            if ( !is_array( $para ) ) {
                $destinatarios[] = [ $para, '' ];
            } else {
                foreach ( $para as $correo => $nome ) {
                    $destinatarios[] = [ (string) $correo, (string) $nome ];
                }
            }

            $this->para = $destinatarios;
            $this->hostname = $this->establecer_hostname();

            $identificador = $this->enviar( $asunto, $plano, $reply_to );

        } catch ( Exception $erro ) {
            rexistrar_problema( $erro );
        }

        $this->boundary = $identificador;

    }

    /**
     * Determina o nome de host que se empregará nos comandos EHLO.
     *
     * Utiliza, por orde de preferencia, o nome do servidor web,
     * o nome do equipo ou o nome da aplicación como alternativa.
     *
     * Elimina os caracteres de control antes de devolvelo.
     *
     * @return string Nome de host preparado para a comunicación SMTP.
     */
    private function establecer_hostname(): string {

        if ( !empty( $_SERVER[ 'SERVER_NAME' ] ) ) {
            $resultado = $_SERVER[ 'SERVER_NAME' ];
        } elseif ( function_exists( 'gethostname' ) && gethostname() !== false ) {
            $resultado = gethostname();
        } else {
            $resultado = NOME;
        }

        return $this->limpar_cabeceira( $resultado );

    }

    /**
     * Elimina os caracteres de control e os espazos sobrantes dun valor
     * destinado a formar parte dunha cabeceira de correo.
     *
     * Evita a introdución de saltos de liña que poderían permitir
     * a inxección de cabeceiras.
     *
     * @param string $valor Valor que se desexa limpar.
     * @return string Valor sen caracteres de control.
     */
    private function limpar_cabeceira( string $valor ): string {
        return trim( preg_replace( '/[\x00-\x1F\x7F]+/', ' ', $valor ) );
    }

    /**
     * Prepara un valor para empregalo nunha cabeceira MIME.
     *
     * Elimina previamente os caracteres de control e codifica en Base64
     * os valores que conteñen caracteres non ASCII, seguindo RFC 2047.
     *
     * @param string $valor Valor da cabeceira.
     * @return string Valor limpo e, se é necesario, codificado.
     */
    private function codificar_cabeceira( string $valor ): string {

        $valor = $this->limpar_cabeceira( $valor );

        if ( preg_match( '/^[\x20-\x7E]*$/', $valor ) ) {
            return $valor;
        }

        return mb_encode_mimeheader( $valor, 'UTF-8', 'B', "\r\n" );

    }

    /**
     * Inicializa o mapa de mensaxes asociado aos posibles erros SMTP.
     *
     * As mensaxes empréganse para identificar a operación que fallou
     * cando o servidor non devolve o código de resposta esperado.
     */
    private function cargar_erros(): void {

        $this->erros = [
            'tls'    => 'Non se puido iniciar unha comunicación TLS co servidor',
            'ehlo'   => 'Non se puido iniciar a comunicación co servidor',
            'auth'   => 'Non se puido iniciar a autenticación co servidor',
            'usuar'  => 'Nome de usuario descoñecido',
            'contr'  => 'As credenciais de inicio de sesión non coinciden',
            'from'   => 'Rexeitouse o correo From',
            'correo' => 'Rexeitouse o correo To: ',
            'data'   => 'Non se acepta o envío de datos',
            'envio'  => 'Datos non aceptados',
            'peche'  => 'Non puido pecharse a conexión'
        ];

    }

    /**
     * Normaliza o texto plano dunha mensaxe de correo.
     *
     * Elimina os saltos de liña iniciais e converte os separadores
     * entre liñas recibidas en separadores de parágrafo, evitando
     * a acumulación de liñas baleiras consecutivas.
     *
     * @param string $plano Texto orixinal da mensaxe.
     * @return string Texto normalizado con saltos de liña CRLF.
     */
    private function axustar_texto_plano( string $plano ): string {

        while ( str_starts_with( $plano, "\r\n" ) ) {
            $plano = trim( $plano, "\r\n" );
        }

        $plano = str_replace( [ "\r\n", "\r", "\n" ], "\r\n", $plano );
        $plano = str_replace( "\r\n", "\r\n\r\n", $plano );

        while ( str_contains( $plano, "\r\n\r\n\r\n" ) ) {
            $plano = str_replace( "\r\n\r\n\r\n", "\r\n\r\n", $plano );
        }

        return $plano;

    }

    /**
     * Establece a conexión co servidor SMTP e realiza o proceso completo de envío.
     *
     * Executa a negociación inicial, establece a comunicación TLS,
     * autentica a aplicación, prepara os destinatarios e as cabeceiras
     * e transmite o contido da mensaxe.
     *
     * Devolve o identificador Message-ID xerado para o correo.
     *
     * @param string $asunto Asunto do correo.
     * @param string $texto_plano Corpo da mensaxe en texto plano.
     * @param array<string, string> $reply_to Destinatarios da cabeceira de resposta.
     * @return string Identificador Message-ID do correo enviado.
     * @throws LogicException Se o corpo da mensaxe está baleiro.
     * @throws Exception Se falla a conexión ou algunha operación SMTP.
     */
    private function enviar( string $asunto, string $texto_plano, array $reply_to = [] ): string {

        if ( empty( $texto_plano ) ) {
            throw new LogicException( 'O texto alternativo da mensaxe debe estar sempre presente nun correo' );
        }

        $conexion_timeout = 30;

        $socket = fsockopen(
            self::SERVIDOR_C,
            self::PORTO_C,
            $errorNumber,
            $errorMessage,
            $conexion_timeout
        );

        if ( empty( $socket ) ) {
            throw new Exception( 'Erro de SMTP: Non se puido conectar ó host SMTP: ' . $errorMessage . '(' . $errorNumber . ')' );
        }

        $this->socket = $socket;

        $this->coller_resposta();
        $this->enviar_comando( 'EHLO ' . $this->hostname, 250, $this->erros[ 'ehlo' ] );

        // TLS e comprobación rigorosa do resultado da criptografía
        $this->enviar_comando( 'STARTTLS', 220, $this->erros[ 'tls' ] );
        if ( !stream_socket_enable_crypto( $this->socket, true, STREAM_CRYPTO_METHOD_TLS_CLIENT ) ) {
            throw new Exception( $this->erros[ 'tls' ] );
        }
        $this->enviar_comando( 'EHLO ' . $this->hostname, 250, $this->erros[ 'ehlo' ] );

        $this->enviar_comando( 'AUTH LOGIN', 334, $this->erros[ 'auth' ] );
        $this->enviar_comando( base64_encode( self::USUARIO_C ), 334, $this->erros[ 'usuar' ] );
        $this->enviar_comando( base64_encode( self::CONTRASINAL_C ), 235, $this->erros[ 'contr' ] );
        $this->enviar_comando( 'MAIL FROM: <' . self::DENDE_CORREO . '>', 250, $this->erros[ 'from' ] );

        $cabeceiras_correo = [];
        $cabeceiras_correo[ 'Date' ] = date( 'r' );

        $destinatarios_to = [];
        foreach ( $this->para as $datos ) {
            $destinatarios_to[] = trim( $this->codificar_cabeceira( $datos[ 1 ] ) . ' <' . validar_correo( $datos[ 0 ] ) . '>' );
            $this->enviar_comando( 'RCPT TO: <' . $datos[ 0 ] . '>', 250, $this->erros[ 'correo' ] . $datos[ 0 ] );
        }

        $cabeceiras_correo[ 'To' ] = implode( ', ', $destinatarios_to );
        $cabeceiras_correo[ 'From' ] = $this->codificar_cabeceira( NOME ) . ' <' . validar_correo( self::DENDE_CORREO ) . '>';

        if ( !empty( $reply_to ) ) {
            $destinatarios_reply = [];
            foreach ( $reply_to as $correo => $nome ) {
                $destinatarios_reply[] = $this->codificar_cabeceira( (string) $nome ) . ' <' . validar_correo( (string) $correo ) . '>';
            }
            $cabeceiras_correo[ 'Reply-To' ] = implode( ', ', $destinatarios_reply );
        }

        $cabeceiras_correo[ 'Subject' ] = $this->codificar_cabeceira( $asunto );

        $boundary = xerar_token( 58, false );

        $cabeceiras_correo[ 'Message-ID' ] = '<' . $this->limpar_cabeceira( $boundary . '@' . DOMINIO ) . '>';

        if ( !empty( $this->in_reply_to ) ) {
            $cabeceiras_correo[ 'In-Reply-To' ] = '<' . $this->limpar_cabeceira( $this->in_reply_to . '@' . DOMINIO ) . '>';
            $cabeceiras_correo[ 'References' ] = '<' . $this->limpar_cabeceira( $this->in_reply_to . '@' . DOMINIO ) . '>';
        }

        $cabeceiras_correo[ 'MIME-Version' ] = '1.0';
        $cabeceiras_correo[ 'Content-Type' ] = 'text/plain; charset=UTF-8';
        $cabeceiras_correo[ 'Content-Transfer-Encoding' ] = 'quoted-printable';

        $mensaxe = quoted_printable_encode( $texto_plano ) . "\r\n\r\n";

        $cabeceiras = '';
        foreach ( $cabeceiras_correo as $clave => $valor ) {
            $cabeceiras .= $clave . ': ' . $valor . "\r\n";
        }

        $this->enviar_comando( 'DATA', 354, $this->erros[ 'data' ] );

        // Se unha liña empeza cun punto hai que duplicalo para evitar fin prematuro da mensaxe
        $datos = substr( str_replace( "\r\n.", "\r\n..", "\r\n" . $cabeceiras . "\r\n" . $mensaxe ), 2 );

        // Para que un correo se envíe, a última liña debe ser un punto baleiro
        $this->enviar_comando( $datos . "\r\n" . '.', 250, $this->erros[ 'envio' ] );

        return $boundary;

    }

    /**
     * Pecha a conexión co servidor SMTP.
     *
     * Envía o comando QUIT e pecha o socket, rexistrando calquera
     * problema que se produza durante o peche.
     */
    public function __destruct() {

        if ( isset( $this->socket ) && is_resource( $this->socket ) ) {
            try {
                $this->enviar_comando( 'QUIT', 221, $this->erros[ 'peche' ] );
            } catch ( Exception $problema ) {
                rexistrar_problema( $problema );
            } finally {
                fclose( $this->socket );
            }
        }

    }

    /**
     * Recolle e le a resposta do servidor SMTP a través do socket activo.
     *
     * @return string O código numérico de resposta do servidor.
     */
    private function coller_resposta(): string {

        $response = $resultado = '';
        $resposta_timeout = 8;

        stream_set_timeout( $this->socket, $resposta_timeout );

        while ( ( $line = fgets( $this->socket, 515 ) ) !== false ) {

            $response .= trim( $line ) . "\n";

            if ( strlen( $line ) >= 4 && substr( $line, 3, 1 ) == ' ' ) {
                $resultado = substr( $line, 0, 3 );
                break;
            }

            $info = stream_get_meta_data( $this->socket );
            if ( $info[ 'timed_out' ] ) {
                break;
            }

        }

        return $resultado;

    }

    /**
     * Envía un comando SMTP ó servidor e valida se o código de resposta coincide co agardado.
     * Emprega o atributo SensitiveParameter para evitar filtrar credenciais nos logs de erros.
     *
     * @param #[\SensitiveParameter] string $command Comando SMTP a enviar.
     * @param int $esperado Código de resposta SMTP agardado.
     * @param string $mensaxe Mensaxe de excepción se o código non coincide.
     * @throws Exception Se a resposta non coincide co agardado.
     */
    private function enviar_comando( #[\SensitiveParameter] string $command, int $esperado, string $mensaxe ): void {

        if ( !isset( $this->socket ) || !is_resource( $this->socket ) ) {
            return;
        }

        fputs( $this->socket, $command . "\r\n" );

        if ( (int) $this->coller_resposta() !== $esperado ) {
            throw new Exception( $mensaxe );
        }

    }

}