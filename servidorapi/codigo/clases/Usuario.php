<?php

declare( strict_types = 1 );

/**
 * Representa un usuario do sistema e xestiona os seus datos,
 * permisos, sesións e operacións asociadas á súa conta.
 */
final readonly class Usuario {

    /**
     * @var int Identificador único do usuario na base de datos.
     */
    public int $id;

    /**
     * @var string Identificador UUID único do usuario.
     */
    public string $uuid;

    /**
     * @var string Enderezo de correo electrónico asociado á conta.
     */
    public string $correo;

    /**
     * @var int Estado actual da conta (0 = pendente, 1 = activo, 2 = suspendido, 3 = en proceso de borrado).
     */
    public int $estado;

    /**
     * @var Rol Rol asignado ao usuario, representado mediante unha instancia do enum Rol.
     */
    public Rol $rol;

    /**
     * @var array Array asociativo coas sesións do usuario cargadas durante a petición actual.
     * As claves identifican os tokens mediante o seu hash HMAC.
     */
    public array $sesions;

    /**
     * Construtor da clase Usuario.
     *
     * Inicializa un usuario a partir dos datos proporcionados ou recuperados
     * mediante unha busca na base de datos ou na caché estática.
     *
     * Se se proporcionan datos precargados, empréganse directamente sen
     * realizar ningunha consulta adicional.
     *
     * Durante a inicialización:
     * - Convértese o rol recibido como texto na súa correspondente instancia
     *   do enum Rol.
     * - As propiedades recibidas asígnanse dinamicamente, permitindo que
     *   __set() procese e cargue as sesións do usuario.
     * - Recupéranse as sesións almacenadas na caché estática da petición.
     * - Se o usuario non existe, consérvase o campo e valor da busca para
     *   identificar a súa inexistencia na caché.
     * - As propiedades que non teñan valor reciben os seus valores por defecto.
     *
     * @param string     $campo          Campo polo que se realiza a busca (ex. 'id', 'correo' ou 'uuid').
     * @param string|int $valor          Valor correspondente ao campo de busca.
     * @param bool       $revisar_cache  Indica se se debe consultar a caché estática de usuarios.
     * @param ?stdClass  $datos          Datos precargados do usuario. Se se proporcionan, omítese a busca.
     */
    public function __construct( string $campo, string|int $valor, bool $revisar_cache = true, ?stdClass $datos = null ) {

       if ( !$datos ) {
          $datos = $this->buscar_usuario( $campo, $valor, $revisar_cache );
       }

       if ( $datos ) {

          foreach ( $datos as $clave => $valor_o ) {

             if ( $clave === 'rol' && is_string( $valor_o ) ) {
                $valor_o = Rol::from( $valor_o );
             }

             $this->$clave = $valor_o;

          }

          $this->sesions = $this->cargar_sesions();

       } else {
          $this->$campo = $valor; // Caché de non existencia
       }

       $this->autocubrir_propiedades();

    }

    /**
     * Intercepta a asignación de propiedades para cargar sesións do usuario.
     *
     * Só procesa as propiedades cuxo nome comeza polo prefixo 'ses_id_'.
     * Comproba que o valor recibido conteña datos serializados válidos,
     * deserialízaos sen permitir a creación de obxectos e incorpora os
     * datos da sesión á caché estática mediante cargar_sesions().
     *
     * As propiedades que non teñan ese prefixo ignóranse.
     *
     * @param string $nome  Nome da propiedade que se intenta asignar.
     * @param mixed  $valor Valor recibido, que debe conter os datos serializados da sesión.
     */
    public function __set( string $nome, $valor ): void {

        if ( str_starts_with( $nome, 'ses_id_' ) ) {

            if ( !hai_serializacion( $valor ) ) {
                return;
            }

            $nome = str_replace( 'ses_id_', '', $nome );
            $valor = unserialize( $valor, [ 'allowed_classes' => false ] );

            $this->cargar_sesions( 'engadir', $nome, $valor );

        }

    }

    /**
     * Inicializa as propiedades do usuario cos seus valores por defecto.
     *
     * Percorre as propiedades básicas da clase e asigna un valor inicial
     * a aquelas que non estean definidas ou teñan valor null.
     *
     * Non sobrescribe as propiedades que xa teñan un valor asignado,
     * polo que permite completar os datos dun usuario parcialmente cargado
     * desde a base de datos ou a caché.
     *
     * @return void
     */
    private function autocubrir_propiedades(): void {

        $base = [
            'id' => -1,
            'uuid' => '',
            'correo' => '',
            'estado' => 0,
            'rol' => Rol::MONITOR,
            'sesions' => []
        ];

        foreach ( $base as $clave => $valor ) {

            if ( !isset( $this->$clave ) ) {
                $this->$clave = $valor;
            }

        }

    }

    /**
     * Xestiona a caché estática das sesións dun usuario durante a petición actual.
     *
     * Permite engadir sesións á caché ou recuperar todas as sesións almacenadas
     * e baleirar a caché nunha única operación.
     *
     * Comportamento segundo a acción:
     *
     * - 'engadir': almacena os datos de sesión en $valor baixo o identificador
     *   $nome e devolve os datos engadidos.
     * - Calquera outro valor: devolve todas as sesións almacenadas e baleira
     *   a caché para evitar que os datos se reutilicen noutras operacións.
     *
     * A caché é estática, polo que se comparte entre todas as instancias da
     * clase durante a execución da petición actual.
     *
     * @param string $accion Acción que se vai realizar ('engadir' ou baleiro para recuperar e limpar).
     * @param string $nome   Identificador único da sesión que se vai almacenar.
     * @param array  $valor  Datos da sesión que se van almacenar.
     * @return array Datos da sesión engadida ou todas as sesións recuperadas.
     */
    private function cargar_sesions( string $accion = '', string $nome = '', array $valor = [] ): array {

        static $sesions = [];

        if ( $accion === 'engadir' ) {
            return $sesions[ $nome ] = $valor;
        }

        $copia_ses = $sesions;
        $sesions = [];

        return $copia_ses;

    }

    /**
     * Recupera un usuario da base de datos e carga as súas sesións temporais vixentes.
     *
     * Busca o usuario polo campo e valor indicados, obtendo os seus datos
     * básicos e o rol asociado mediante unha unión coa táboa roles.
     *
     * O parámetro $revisar_cache determina como se realiza a busca:
     * - true: delega en coller_usuario(), que utiliza o mecanismo de caché global.
     * - false: realiza unha consulta directa á base de datos, sen utilizar a caché.
     *
     * Unha vez recuperado o usuario, consulta os seus rexistros temporais para
     * cargar no obxecto todas as sesións identificadas co prefixo 'ses_id_'
     * cuxa data de caducidade aínda non se alcanzou.
     *
     * Cada sesión incorpórase ao obxecto como unha propiedade identificada
     * polo nome do seu rexistro temporal e co seu valor correspondente.
     *
     * Se o valor de busca está baleiro ou é un enteiro menor ou igual a cero,
     * non se realiza ningunha consulta.
     *
     * @param string $campo Campo da táboa usuarios polo que realizar a busca.
     * @param string|int $valor Valor que debe coincidir co campo indicado.
     * @param bool $revisar_cache Indica se se debe utilizar a caché global.
     * @return stdClass|null Obxecto cos datos e sesións vixentes, ou null se non se atopa.
     */
    private function buscar_usuario( string $campo, string|int $valor, bool $revisar_cache ): ?stdClass {

        if ( $revisar_cache ) {
            return coller_usuario( $campo, $valor );
        }

        // Se o valor está baleiro ou vale menos de 0, non se executa a busca
        if ( $valor === '' || ( is_int( $valor ) && $valor <= 0 ) ) {
            return null;
        }

        // Para que funcione a JOIN ao buscar por id, cómpre indicar o alias da táboa
        $campo_onde = "u.$campo";

        $busca_usuario = [
            'columnas' => [ 'u.id', 'u.uuid', 'u.correo', 'u.estado', 'r.clave as rol' ],
            'alias' => 'u',
            'join' => [
                [ 'tipo' => 'INNER', 'principal' => 'id_rol', 'secundaria' => 'r.id', 'taboa-join' => 'roles' ]
            ],
            'onde' => [ $campo_onde => $valor ],
            'limit' => 1
        ];

        $usuario = db()->select( 'usuarios', $busca_usuario );

        if ( !$usuario instanceof stdClass ) {
            return null;
        }

        // As sesións gárdanse como datos temporais do usuario (ses_id_[hash])
        $busca_sesions = [
            'columnas' => [ 'nome', 'valor' ],
            'principal' => 'nome',
            'onde' => [
                'id' => $usuario->id,
                'nome' => [
                    'operador' => 'LIKE',
                    'valor' => 'ses_id_%'
                ],
                'data_caducidade' => [
                    'operador' => '>=',
                    'valor' => time()
                ]
            ]
        ];

        $sesions = db()->select( 'info_temporal', $busca_sesions, 'array' ) ?? [];

        foreach ( $sesions as $nome => $fila ) {
            $usuario->$nome = $fila[ 'valor' ];
        }

        return $usuario;

    }

    /**
     * Comproba se o obxecto representa un usuario existente.
     *
     * Considera que o usuario existe se ten un identificador positivo
     * e un enderezo de correo electrónico non baleiro.
     *
     * @return bool True se o usuario se considera existente; false en caso contrario.
     */
    function existe() {
       return $this->id > 0 && $this->correo !== '';
    }

    /**
     * Comproba se o rol do usuario ten un permiso determinado.
     *
     * Recupera os permisos asociados ao rol e comproba se inclúen
     * o permiso solicitado. Se o usuario non ten un rol asignado,
     * devolve false.
     *
     * @param string $permiso Clave do permiso que se quere comprobar.
     * @return bool True se o rol ten o permiso; false en caso contrario.
     */
    public function pode( string $permiso ) {

       if ( !$this->rol ) {
          return false;
       }

       $permisos = coller_permisos( $this->rol );

       return in_array( $permiso, $permisos, true );

   }

    /**
     * Recupera os datos dunha sesión do usuario mediante o seu token.
     *
     * Calcula o HMAC do token e utilízao como índice para buscar a sesión
     * na caché interna do usuario, sen realizar consultas á base de datos.
     *
     * @param string $token Token da sesión que se quere recuperar.
     * @return array|null Datos da sesión se existe na caché; null en caso contrario.
     */
    private function coller_sesion( string $token ): ?array {

      $hash = hash_hmac( 'sha512', $token, salgado( 'sesion' ) );

      if ( isset( $this->sesions[ $hash ] ) ) {
         return $this->sesions[ $hash ];
      }

      return null;

   }

    /**
     * Comproba se existe unha sesión activa para este usuario.
     *
     * Recupera os datos da sesión mediante o seu token e comproba
     * que o seu estado sexa activo (1).
     *
     * @param string $token Token da sesión que se quere comprobar.
     * @return bool True se a sesión existe e está activa; false en caso contrario.
     */
    public function verificar_sesion( string $token ): bool {

        $datos = $this->coller_sesion( $token );

        if ( !isset( $datos[ 'estado' ] ) ) {
            return false;
        }

        return $datos[ 'estado' ] === 1;

    }

    /**
     * Crea unha nova sesión pendente de activación para o usuario.
     *
     * Xera un token seguro e garda os datos da sesión en info_temporal.
     * A sesión permanece pendente (estado 0) ata que o usuario abra a
     * ligazón de activación recibida por correo.
     *
     * A sesión ten dúas caducidades:
     * - A duración propia da sesión, indicada en $caducidade.
     * - O prazo de activación, limitado por DURACION_LIGAZON (15 minutos).
     *
     * Se a sesión non se activa dentro dese prazo, o rexistro temporal
     * poderá eliminarse sen chegar a activar a sesión.
     *
     * @param int $agora      Marca de tempo Unix de creación da sesión.
     * @param int $caducidade Marca de tempo Unix de caducidade da sesión.
     * @return string Token seguro xerado para identificar a sesión.
     */
    public function crear_sesion( int $agora, int $caducidade ): string {

        $sesion = [];

        $token = xerar_token( 50, false );

        $sesion[ 'creada' ] = $agora;
        $sesion[ 'caducidade' ]= $caducidade;
        $sesion[ 'peche' ] = hash_hmac( 'sha512', $agora . '|' . $this->uuid . '|' . $token, salgado( 'sesion' ) );
        $sesion[ 'estado' ] = 0;

        $sesion = serialize( $sesion );

        //Este número ponse máis baixo por se a sesión non é activada e se poida borrar antes na base de datos,
        $caducidade_enlace = $agora + DURACION_LIGAZON;

        $this->axustar_sesion( $token, $sesion, 'crear', $caducidade_enlace );

        return $token;

    }

    /**
     * Activa unha sesión que se atopa en estado pendente (0).
     *
     * Recupera os datos da sesión mediante o seu token, comproba que exista
     * e que aínda non estea activa, cambia o seu estado a activo (1) e
     * actualiza o rexistro correspondente en info_temporal.
     *
     * @param string $token Token da sesión que se quere activar.
     * @return bool True se a sesión existe e estaba pendente; false se non
     *              existe ou xa está activa.
     */
    public function activar_sesion( string $token ): bool {

        $datos_sesion = $this->coller_sesion( $token );

        if ( !is_array( $datos_sesion ) || $datos_sesion[ 'estado' ] !== 0 ) {
            return false;
        }

        $caducidade = $datos_sesion[ 'caducidade' ];
        $datos_sesion[ 'estado' ] = 1;
        $sesion = serialize( $datos_sesion );

        $this->axustar_sesion( $token, $sesion, 'verificar', $caducidade );

        return true;

    }

   /**
    * Verifica a autenticidade da solicitude de peche da sesión actual.
    *
    * Comproba que o usuario existe e corresponde co usuario autenticado
    * na petición. A continuación, calcula o hash de seguridade esperado
    * e compárao co almacenado na sesión.
    *
    * @return bool True se a solicitude de peche é válida, false en caso contrario.
    */
    public function verificar_peche_sesion() {

       if ( $this->existe() && usuario_actual()->id == $this->id ) {

          $rastro = analizar_cabeceira_sesion();
          $token = $rastro[ 'token' ];
          $datos_sesion = $this->coller_sesion( $token );
          $agora = $datos_sesion[ 'creada' ];

          $hash_peche = hash_hmac( 'sha512', $agora . '|' . $this->uuid . '|' . $token, salgado( 'sesion' ) );

          return hash_equals( $hash_peche, $datos_sesion[ 'peche' ] );

       }

        return false;

    }

    /**
     * Elimina unha sesión específica do usuario.
     *
     * @param string $token Token da sesión que se vai eliminar.
     */
    public function destruir_sesion( string $token ): void {
        $this->axustar_sesion( $token, '', 'destruir_unha', 0 );
    }

    /**
     * Elimina as sesións do usuario.
     *
     * Se se indica un token, elimínanse todas as sesións agás a correspondente
     * a ese token. Se non se indica, elimínanse todas as sesións.
     *
     * @param string $token_manter Token da sesión que se quere conservar.
     * Se está baleiro, elimínanse todas.
     */
    public function destruir_sesions( string $token_manter = '' ): void {
        $this->axustar_sesion( $token_manter, '', 'destruir_varias', 0 );
    }

    /**
     * Crea, actualiza o elimina os rexistros das sesións dun usuario en info_temporal.
     *
     * O nome de cada rexistro constrúese a partir dun hash HMAC do token de sesión,
     * evitando almacenar o token orixinal como identificador.
     *
     * Accións dispoñibles:
     * - crear: insire unha nova sesión.
     * - verificar: actualiza os datos e a caducidade dunha sesión.
     * - destruir_unha: elimina unha sesión concreta.
     * - destruir_varias: elimina todas as sesións agás a indicada por $token.
     *   Se $token está baleiro, elimina todas as sesións do usuario.
     *
     * @param string $token         Token da sesión afectada. Pode estar baleiro
     *                              ao eliminar todas as sesións.
     * @param string $datos_sesion  Datos serializados da sesión.
     * @param string $accion        Operación que se vai realizar.
     * @param int    $caducidade    Marca temporal Unix de caducidade da sesión.
     *
     * @throws DomainException Se se solicita unha operación non definida.
     */
    private function axustar_sesion( string $token, string $datos_sesion, string $accion, int $caducidade ): void {

        $hash = hash_hmac( 'sha512', $token, salgado( 'sesion' ) );

        switch( $accion ) {

            case 'crear':

                $accion_db = 'insert';
                $datos = [
                    'id' => $this->id,
                    'nome' => 'ses_id_' . $hash,
                    'valor' => $datos_sesion,
                    'data_caducidade' => $caducidade
                ];

                break;

            case 'verificar':

                $accion_db = 'update';
                $datos = [
                    'nova_info' => [
                        'valor' => $datos_sesion,
                        'data_caducidade' => $caducidade
                    ],
                    'onde' => [
                        'id' => $this->id,
                        'nome' => 'ses_id_' . $hash
                    ]
                ];

                break;

            case 'destruir_unha':

                $accion_db = 'delete';
                $datos = [
                    'id' => $this->id,
                    'nome' => 'ses_id_' . $hash
                ];

                break;

            case 'destruir_varias':

                $accion_db = 'delete';

                if ( $token !== '' ) {

                    $datos = [
                        'id' => $this->id,
                        'nome' => [
                            'operador' => '!=',
                            'valor' => 'ses_id_' . $hash
                        ]
                    ];

                    break;

                }

                $datos = [ 'id' => $this->id ];

                break;

            default:
                throw new DomainException( 'Tratouse de realizar unha acción (' . $accion . ') non definida co identificador ' . $hash . ' para o usuario ' . $this->id );

        }

        db()->$accion_db( 'info_temporal', $datos );

    }

}