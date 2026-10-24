<?php

declare ( strict_types = 1 );

final readonly class Correo {

	private const DENDE_CORREO = DENDE_CORREO;

	private const PORTO_C = 587;
	private const PROTOCOLO_C = 'tls';

	private const SERVIDOR_C = SERVIDOR_CORREO;
	private const USUARIO_C = USUARIO_CORREO;
	private const CONTRASINAL_C = AUTORIZACION_CORREO;

	public ?string $boundary;

	private mixed $socket;
	private string $hostname;
	private array $erros;
	private array $para;
	private ?string $in_reply_to;

	function __construct( string|array $para, string $asunto, string $mensaxe, array $reply_to = [], ?string $boundary = null ) {

		try {

			$this->cargar_erros();
			$this->in_reply_to = $boundary;

			$plano = $this->axustar_texto_plano( $mensaxe );
			$destinatarios = [];

			if ( !is_array( $para ) ) {
				$destinatarios[] = [ $para, '' ];
			} else {

				foreach( $para as $correo => $nome ) {
					$destinatarios[] = [ $correo, $nome ];
				}
				
			}

			$this->para = $destinatarios;
			$this->hostname = $this->establecer_hostname();

			$boundary = $this->enviar( $asunto, $plano, $reply_to );

		} catch ( Exception $erro ) {
			rexistrar_problema( $erro );
		}

		$this->boundary = $boundary;

	}

	private function establecer_hostname() {

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
	 * Elimina os saltos de liña e caracteres de control dun valor para que non se poidan inxectar cabeceiras.
	 */
	private function limpar_cabeceira( string $valor ) {
		return trim( preg_replace( '/[\x00-\x1F\x7F]+/', ' ', $valor ) );
	}

	/**
	 * Prepara o valor dunha cabeceira. Se contén caracteres que non son ASCII (como acentos),
	 * codifícase en base64 (RFC 2047) para que o cliente de correo o amose ben.
	 */
	private function codificar_cabeceira( string $valor ) {

		$valor = $this->limpar_cabeceira( $valor );

		if ( preg_match( '/^[\x20-\x7E]*$/', $valor ) ) {
			return $valor;
		}

		return mb_encode_mimeheader( $valor, 'UTF-8', 'B', "\r\n" );

	}

	private function cargar_erros() {

		$this->erros = [
			'tls'   => 'Non se puido iniciar unha comunicación TLS co servidor',
			'ehlo'  => 'Non se puido iniciar a comunicación co servidor',
			'auth'  => 'Non se puido iniciar a autenticación co servidor',
			'usuar' => 'Nome de usuario descoñecido',
			'contr' => 'As credenciais de inicio de sesión non coinciden',
			'from'  => 'Rexeitouse o correo From',
			'correo'=> 'Rexeitouse o correo To: ',
			'data'  => 'Non se acepta o envío de datos',
			'envio' => 'Datos non aceptados',
			'peche' => 'Non puido pecharse a conexión'
		];

	}

	private function axustar_texto_plano( string $plano ) {

		//Supresión de \r\n ó principio da mensaxe
		while ( str_starts_with( $plano, "\r\n" ) ) {
			$plano = trim( $plano, "\r\n" );
		}

		//Conversión de parágrafos dunha liña a dúas
		$plano = str_replace( "\r\n", "\r\n\r\n", $plano );

		while( str_contains( $plano, "\r\n\r\n\r\n" ) ) {
			$plano = str_replace( "\r\n\r\n\r\n", "\r\n\r\n", $plano );
		}

		return $plano;

	}

	private function enviar( string $asunto, string $texto_plano, array $reply_to = [] ) {

		if ( empty( $texto_plano ) ) {
			throw new LogicException( 'O texto alternativo da mensaxe debe estar sempre presente nun correo' );
		}

		$mensaxe = '';
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

		//O socket só se garda se a conexión foi correcta, para que __destruct non intente pechar un false
		$this->socket = $socket;

		$this->coller_resposta();
		$this->enviar_comando( 'EHLO ' . $this->hostname, 250, $this->erros[ 'ehlo' ] );

		//TLS
		$this->enviar_comando( 'STARTTLS', 220, $this->erros[ 'tls' ] );
		stream_socket_enable_crypto( $this->socket, true, STREAM_CRYPTO_METHOD_TLS_CLIENT );
		$this->enviar_comando( 'EHLO ' . $this->hostname, 250, $this->erros[ 'ehlo' ] );

		$this->enviar_comando( 'AUTH LOGIN', 334, $this->erros[ 'auth' ] );
		$this->enviar_comando( base64_encode( self::USUARIO_C ), 334, $this->erros[ 'usuar' ] );
		$this->enviar_comando( base64_encode( self::CONTRASINAL_C ), 235, $this->erros[ 'contr' ] );
		$this->enviar_comando( 'MAIL FROM: <' . self::DENDE_CORREO . '>', 250, $this->erros[ 'from' ] );

		$cabeceiras_correo = [];
		$cabeceiras_correo[ 'Date' ] = date( 'r' );

		foreach ( $this->para as $clave => $datos ) {
			$cabeceiras_correo[ 'To' ] = trim( $this->codificar_cabeceira( $datos[ 1 ] ) . ' <' . validar_correo( $datos[ 0 ] ) . '>' );
			$this->enviar_comando( 'RCPT TO: <' . $datos[ 0 ] . '>', 250, $this->erros[ 'correo' ] . $datos[ 0 ] );
		}

		$cabeceiras_correo[ 'From' ] = $this->codificar_cabeceira( NOME ) . ' <' . validar_correo( self::DENDE_CORREO ) . '>';

		if ( !empty( $reply_to ) ) {
			foreach( $reply_to as $correo => $nome ) {
				$cabeceiras_correo[ 'Reply-To' ] = $this->codificar_cabeceira( $nome ) . ' <' . validar_correo( $correo ) . '>';
				$cabeceiras_correo[ 'Return-Path' ] = $this->codificar_cabeceira( $nome ) . ' <' . validar_correo( $correo ) . '>';
			}
		}

		$cabeceiras_correo[ 'Subject' ] = $this->codificar_cabeceira( $asunto );

		$boundary = xerar_token( 58, false );

		$cabeceiras_correo[ 'Message-ID' ] = '<' . $this->limpar_cabeceira( $boundary . '@' . DOMINIO ) . '>';

		if ( !empty( $this->in_reply_to ) ) {
			$cabeceiras_correo[ 'In-Reply-to' ] = '<' . $this->limpar_cabeceira( $this->in_reply_to . '@' . DOMINIO ) . '>';
			$cabeceiras_correo[ 'References' ] = '<' . $this->limpar_cabeceira( $this->in_reply_to . '@' . DOMINIO ) . '>';
		}

		$cabeceiras_correo[ 'MIME-Version' ] = '1.0';
		$cabeceiras_correo[ 'Content-Type' ] = 'text/plain; charset=UTF-8';
		$cabeceiras_correo[ 'Content-Transfer-Encoding' ] = 'quoted-printable';

		if ( !empty( $texto_plano ) ) {

			if ( !empty( $texto_html ) ) {
				$mensaxe .= '--apg1=_' . $boundary . "\r\n";
				$mensaxe .= 'Content-Type: text/plain; charset=UTF-8' . "\r\n";
				$mensaxe .= 'Content-Transfer-Encoding: quoted-printable' . "\r\n" . "\r\n";
			}

			$mensaxe_alt = strip_tags( $texto_plano );

			$mensaxe .= quoted_printable_encode( $mensaxe_alt ) . "\r\n" . "\r\n";

		}

		$cabeceiras = '';
		foreach ( $cabeceiras_correo as $clave => $valor ) {
			$cabeceiras .= $clave . ': ' . $valor . "\r\n";
		}

		$this->enviar_comando( 'DATA', 354, $this->erros[ 'data' ] );

		//Se unha liña empeza cun punto hai que duplicalo. Se non, unha liña só co punto remataría a mensaxe antes de tempo
		//(pasa cando a codificación quoted-printable parte unha liña xusto antes dun punto)
		$datos = substr( str_replace( "\r\n.", "\r\n..", "\r\n" . $cabeceiras . "\r\n" . $mensaxe ), 2 );

		//Para que un correo se envíe, a última liña debe ser un punto baleiro
		$this->enviar_comando( $datos . "\r\n" . '.', 250, $this->erros[ 'envio' ] );

		return $boundary;

	}

	public function __destruct( ) {

		try {
			$this->enviar_comando( 'QUIT', 221, $this->erros[ 'peche' ] );
		} catch ( Exception $problema ) {
			rexistrar_problema( $problema );
		}

		if ( isset( $this->socket ) ) {
			fclose( $this->socket );
		}

	}

	private function coller_resposta() {

		$response = $resultado = '';
		$resposta_timeout = 8;

		stream_set_timeout( $this->socket, $resposta_timeout );

		while ( ( $line = fgets( $this->socket, 515 ) ) !== false ) {

			$response .= trim( $line ) . "\n";

			if ( substr( $line, 3, 1 ) == ' ' ) {
				$resultado = substr( $line, 0, 3 );
				break;
			}

		}

		return $resultado;

	}

	//O comando pode levar as credenciais, polo que se marca para que non aparezan nos rastros dos erros
	private function enviar_comando( #[\SensitiveParameter] string $command, int $esperado, string $mensaxe ) {

		if ( !isset( $this->socket ) ) {
			return;
		}

		fputs( $this->socket, $command . "\r\n" );

		if ( $this->coller_resposta() != $esperado ) {
			throw new Exception( $mensaxe );
		}

	}

}