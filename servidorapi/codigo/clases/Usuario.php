<?php

declare ( strict_types = 1 );

/**
 * Clase que representa a un usuario do sistema e xestiona tanto
 * os seus datos básicos como o seu ciclo de vida e sesións.
 */
final readonly class Usuario {

    /**
     * @var int Identificador único do usuario na base de datos.
     */
    public int $id;

    /**
     * @var string Identificador único do usuario.
     */
    public string $uuid;

    /**
     * @var string Enderezo de correo electrónico do usuario.
     */
    public string $correo;

    /**
     * @var int Estado actual da conta (ex. 0 = pendente, 1 = activo, 2 = suspendido, etc.).
     */
    public int $estado;

    /**
     * @var Rol Rol asignado ao usuario dentro do sistema (instancia do enum Rol).
     */
    public Rol $rol;

    /**
     * @var array Array asociativo coas sesións activas do usuario cargadas temporalmente.
     */
    public array $sesions;

    /**
     * Construtor da clase Usuario.
     * Busca os datos do usuario (ou emprega caché) e inicializa as súas propiedades e sesións.
     *
     * @param string      $campo          Campo polo que se realiza a busca (ex. 'id', 'correo', 'uuid').
     * @param string|int  $valor          Valor correspondente ao campo de busca.
     * @param bool        $revisar_cache  Indica se se debe comprobar a caché estática.
     * @param ?stdClass   $datos          Obxecto opcional con datos precargados para evitar consultas.
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
     * Método máxico para asignar propiedades dinamicamente (empregado para cargar sesións).
     * Se a propiedade comeza por 'ses_id_', deseraliza e engade a sesión correspondente.
     *
     * @param string  $nome   Nome da propiedade.
     * @param mixed   $valor  Valor da propiedade.
     */
    public function __set( string $nome, $valor ) {

       if ( str_starts_with( $nome, 'ses_id_' ) ) {

          if ( !hai_serializacion( $valor ) ) {
             return;
          }

          $nome = str_replace( 'ses_id_', '', $nome );
          $valor = unserialize( $valor, [ 'allowed_classes' => false ] );
          return $this->cargar_sesions( 'engadir', $nome, $valor );

       }

    }

    /**
     * Asigna valores por defecto ás propiedades da clase se non viñeron definidos na BD ou caché.
     */
    private function autocubrir_propiedades() {

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
     * Xestiona a carga, almacenaxe e limpeza estática das sesións dun usuario na petición actual.
     *
     * @param string  $accion  Acción a realizar ('engadir' ou baleiro para recuperar/limpar).
     * @param string  $nome    Identificador único da sesión.
     * @param array   $valor   Datos da sesión.
     * @return array  Devolve o array de sesións almacenadas.
     */
    private function cargar_sesions( string $accion = '', string $nome = '', array $valor = [] ) {

       static $sesions = [];

       if ( $accion == 'engadir' ) {
          return $sesions[ $nome ] = $valor;
       }

       $copia_ses = $sesions;
       $sesions = [];

       return $copia_ses;

    }

    /**
     * Realiza a consulta á base de datos para obter os datos dun usuario e as súas sesións temporais activas.
     *
     * @param string      $campo          Campo polo que buscar.
     * @param string|int  $valor          Valor da busca.
     * @param bool        $revisar_cache  Indica se se comproba a caché global.
     * @return mixed      Devolve o resultado da busca ou null se non existe.
     */
    private function buscar_usuario( string $campo, string|int $valor, bool $revisar_cache ) {

       if ( $revisar_cache ) {
          return coller_usuario( $campo, $valor );
       }

       // Se o valor está baleiro ou vale menos de 0, non se executa a busca
       if ( $valor === '' || ( is_int( $valor ) && $valor <= 0 ) ) { return; }

       $busca_usuario = [
          'columnas' => [ 'id', 'uuid', 'correo', 'estado', 'rol' ],
          'onde' => [ $campo => $valor ],
          'limit' => 1
       ];

       $usuario = db()->select( 'usuarios', $busca_usuario );

       if ( !$usuario ) {
          return;
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
     * Comproba se o usuario existe realmente na base de datos e ten un correo válido.
     *
     * @return bool True se existe, false en caso contrario.
     */
    function existe() {
       return $this->id > 0 && $this->correo !== '';
    }

    /**
     * Comproba se o usuario dispón dun permiso específico en función do seu rol.
     *
     * @param string  $permiso  Nome do permiso que se desexa comprobar.
     * @return bool   True se posúe o permiso, false en caso contrario.
     */
    public function pode( string $permiso ) {

       if ( !$this->rol ) {
          return false;
       }

       $permisos = coller_permisos( $this->rol );

       return in_array( $permiso, $permisos, true );

    }

    /**
     * Recupera os datos dunha sesión concreta a partir do seu token utilizando un hash HMAC.
     *
     * @param string  $token  Token de sesión.
     * @return mixed  Array cos datos da sesión ou null se non existe.
     */
    private function coller_sesion( string $token ) {

       $hash = hash_hmac( 'sha512', $token, salgado( 'sesion' ) );

       if ( isset( $this->sesions[ $hash ] ) ) {
          return $this->sesions[ $hash ];
       }

    }

    /**
     * Valida se un token de sesión é activo e correcto para este usuario.
     *
     * @param string  $token  Token de sesión a comprobar.
     * @return bool   True se a sesión é válida.
     */
    public function verificar_sesion( string $token ) {

        $datos = $this->coller_sesion( $token );

        if ( !isset( $datos[ 'estado' ] ) ) {
            return false;
        }

        return $datos[ 'estado' ] === 1;

    }

    /**
     * Crea unha nova sesión activa para o usuario, xerando un token seguro e gardándoa temporalmente.
     *
     * @param int     $agora       Marca de tempo Unix actual.
     * @param int     $caducidade  Marca de tempo de caducidade da sesión.
     * @return string O token de sesión xerado.
     */
    public function crear_sesion( int $agora, int $caducidade ) {

       $sesion = [];

       if ( !empty( $_SERVER[ 'HTTP_USER_AGENT' ] ) ) {
          $sesion[ 'axente' ] = strlen( $_SERVER[ 'HTTP_USER_AGENT' ] ) < 300 ? $_SERVER[ 'HTTP_USER_AGENT' ] : 'demasiado longo';
       }

       $token = xerar_token( 50, false );

       $sesion[ 'creada' ] = $agora;
       $sesion[ 'peche' ] = hash_hmac( 'sha512', $agora . '|' . $this->uuid . '|' . $token, salgado( 'sesion' ) );
       $sesion[ 'estado' ] = 0;

       $sesion = serialize( $sesion );

       if ( $caducidade - $agora < 3 * DIA_EN_SEGUNDOS ) {
          //'Creouse unha sesión de curta duración e aumentase 1 hora a súa caducidade para que poida usar solicitudes de AJAX ou POST'
          $caducidade += HORA_EN_SEGUNDOS;
       }

       $this->axustar_sesion( $token, $sesion, 'crear', $caducidade );

       return $token;

    }

    /**
     * Alónga a duración da sesión actual do usuario se se dan as condicións de tempo requiridas.
     * A sesión alóngase no momento en que se valida ao abrir a aplicación.
     */
    public function alongar_sesion_actual() {

       if ( $this->existe() && usuario_actual()->id == $this->id ) {

          $rastro = analizar_rastro_sesion();

          if ( !$rastro[ 'alongar_ses' ] ) {
             return;
          }

          if ( $rastro[ 'alongar_ses' ] < time() ) {

             $token = $rastro[ 'token' ];
             $caducidade = $rastro[ 'data_caducidade' ] + DIA_EN_SEGUNDOS;

             $this->axustar_sesion( $token, '', 'alongar_ses_act', $caducidade );

          }

       }

    }

    /**
     * Comproba e valida o peche da sesión actual comparando os hashes de seguridade.
     *
     * @return bool|null True se a sesión coincide de forma íntegra.
     */
    public function pechar_sesion_actual() {

       if ( $this->existe() && usuario_actual()->id == $this->id ) {

          $rastro = analizar_rastro_sesion();
          $token = $rastro[ 'token' ];
          $datos_ses = $this->coller_sesion( $token );
          $agora = $datos_ses[ 'creada' ];

          $hash_peche = hash_hmac( 'sha512', $agora . '|' . $this->uuid . '|' . $token, salgado( 'sesion' ) );

          return hash_equals( $hash_peche, $datos_ses[ 'peche' ] );

       }

    }

    /**
     * Destrúe unha sesión específica do usuario a partir do seu token.
     *
     * @param string  $token  Token da sesión que se desexa destruir.
     */
    public function destruir_sesion( string $token ) {
       $this->axustar_sesion( $token, '', 'destruir_unha', 0 );
    }

    /**
     * Destrúe varias ou todas as sesións dun usuario (opcionalmente mantendo un token concreto).
     *
     * @param string  $token_manter  Token da sesión que se desexa conservar (se procede).
     */
    public function destruir_sesions( string $token_manter = '' ) {
       $this->axustar_sesion( $token_manter, '', 'destruir_varias', 0 );
    }

    /**
     * Método interno para aplicar cambios nas sesións na base de datos (inserir, actualizar, borrar).
     *
     * @param string  $token         Token de sesión.
     * @param string  $datos_sesion  Datos serializados da sesión.
     * @param string  $accion        Tipo de acción ('crear', 'alongar_ses_act', 'destruir_unha', 'destruir_varias').
     * @param int     $caducidade    Nova data de caducidade.
     * @return mixed  Resultado da xeración de autenticación posterior.
     */
    private function axustar_sesion( string $token, string $datos_sesion, string $accion, int $caducidade ) {

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

          case 'alongar_ses_act':

             if ( usuario_actual()->id != $this->id ) {
                throw new LogicException( 'Só se pode alongar a sesión actual do usuario actual' );
             }

             $accion_db = 'update';
             $datos = [
                'nova_info' => [
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

             if ( $token ) { // Destrúense outras sesións

                $datos = [
                   'id' => $this->id,
                   'nome' => [
                      'operador' => '!=',
                      'valor' => 'ses_id_' . $hash
                   ]
                ];

                break;

             }

             $datos = [ 'id' => $this->id ]; // Destrúense todas as sesións

             break;

          default:
             throw new DomainException( 'Tratouse de realizar unha acción (' . $accion . ') non definida co identificador ' . $hash . ' para o usuario ' . $this->id );

       }

       db()->$accion_db( 'info_temporal', $datos );

        return xerar_autenticado( $this, [ 'data_caducidade' => $caducidade, 'token' => $token ], true );

    }

}