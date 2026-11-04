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

        //Para que funcione a join ao buscar por id, é preciso aclarar a táboa co alias escollido
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
     * @return array|null array cos datos da sesión ou null se non existe.
     */
    private function coller_sesion( string $token ): ?array {

      $hash = hash_hmac( 'sha512', $token, salgado( 'sesion' ) );

      if ( isset( $this->sesions[ $hash ] ) ) {
         return $this->sesions[ $hash ];
      }

      return null;

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
     * Cambia o estado dunha sesión de pendente (0) a activa (1) 
     * a partir do token de sesión, serializa os novos datos e actualiza 
     * o rexistro temporal na base de datos.
     *
     * @param string $token Token da sesión que se desexa activar.
     * @return bool True se se modificou o estado correctamente. False en caso contrario.
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
     * Comproba e valida o peche da sesión actual comparando os hashes de seguridade.
     *
     * @return bool True se a sesión coincide de forma íntegra. False se non coincide
     */
    public function pechar_sesion_actual() {

       if ( $this->existe() && usuario_actual()->id == $this->id ) {

          $rastro = analizar_rastro_sesion();
          $token = $rastro[ 'token' ];
          $datos_sesion = $this->coller_sesion( $token );
          $agora = $datos_sesion[ 'creada' ];

          $hash_peche = hash_hmac( 'sha512', $agora . '|' . $this->uuid . '|' . $token, salgado( 'sesion' ) );

          return hash_equals( $hash_peche, $datos_sesion[ 'peche' ] );

       }

        return false;

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
     * @param string  $accion        Tipo de acción ('crear', 'verificar', destruir_unha', 'destruir_varias').
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

    }

}