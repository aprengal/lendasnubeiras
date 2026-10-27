<?php

declare ( strict_types = 1 );

/**
 * Retorna a instancia única (singleton) da clase de base de datos DB,
 * inicializándoa cos parámetros de configuración se non o estivese previamente.
 *
 * @return DB O obxecto de conexión DB activo.
 */
function db() {

    static $db;

    if ( !isset( $db ) ) {

       // Host
       $dbhost     = 'localhost';

       // Usuario
       $dbuser     = USUARIO_DB;
       $dbpassword = CONTASINAL_DB;
       $dbname     = NOME_DB;

       $db = new DB( $dbhost, $dbuser, $dbpassword, $dbname );

    }

    return $db;

}

/**
 * Comproba se unha cadea de texto ten o formato dun array ou obxecto serializado en PHP.
 *
 * @param mixed  $string  O valor ou cadea a comprobar.
 * @return bool  True se coincide co patrón de serialización (`a:N:{...}`), false en caso contrario.
 */
function hai_serializacion( mixed $string ) {
    return preg_match( '/^a:\d+:{/', $string ) === 1;
}

/**
 * Valida que un campo de busca estea permitido dentro dun grupo específico (ex. 'usuario' ou 'publicacion').
 *
 * @param string  $campo  O nome do campo que se desexa validar.
 * @param string  $grupo  O grupo de entidades ao que pertence o campo.
 * @throws OutOfRangeException Se o grupo ou o campo non son válidos ou permitidos.
 */
function validar_campo_busca( string $campo, string $grupo ) {

    $campos = [
       'usuario' => [ 'id', 'correo', 'uuid' ],
       'publicacion' => [ 'id', 'slug', 'titulo' ]
    ];

    if ( !isset( $campos[ $grupo ] ) ) {
       throw new OutOfRangeException( 'Non se pode validar o campo ' . $campo . ' porque o grupo ' . $grupo . ' non existe' );
    }

    if ( !in_array( $campo, $campos[ $grupo ], true ) ) {
       throw new OutOfRangeException( 'O elemento ' . $campo . ' non se atopa na lista de campos permitidos no grupo ' . $grupo );
    }

}

/**
 * Constrúe e cifra o token de rastro de sesión (cookie) baseado en dobre hash HMAC para un usuario.
 *
 * @param Usuario  $usuario      O obxecto Usuario asociado á sesión.
 * @param array    $args_sesion  Array cos argumentos necesarios (caducidade_sesion, esquema, token, peche_ses, alongar_ses).
 * @return string  O valor cifrado e listo para gardar na cookie de rastro.
 * @throws LogicException Se o usuario non existe.
 * @throws InvalidArgumentException Se faltan detalles básicos no array de argumentos.
 */
function crear_rastro_sesion( Usuario $usuario, array $args_sesion ) {

    if ( !$usuario->existe() ) {
       throw new LogicException( 'Só se poden crear rastros de sesión para usuarios que existen' );
    }

    if ( !isset( $args_sesion[ 'caducidade_sesion' ], $args_sesion[ 'esquema' ], $args_sesion[ 'token' ], $args_sesion[ 'peche_ses' ], $args_sesion[ 'alongar_ses' ] ) ) {
       throw new InvalidArgumentException( 'Faltan detalles básicos para crear un rastro' );
    }

    $caducidade_sesion = $args_sesion[ 'caducidade_sesion' ];
    $esquema = $args_sesion[ 'esquema' ];
    $token = $args_sesion[ 'token' ];
    $peche_ses = $args_sesion[ 'peche_ses' ];
    $alongar_ses = $args_sesion[ 'alongar_ses' ];

    $uuid_frag = mb_substr( $usuario->uuid, 8, 5 );

    $key = hash_hmac( 'sha256', $uuid_frag . '|' . $token . '|' . $usuario->correo . '|' . $alongar_ses . '|' . $caducidade_sesion . '|' . $peche_ses, salgado( $esquema ) );
    $hash = hash_hmac( 'sha256', $token . '|' . $usuario->correo . '|' . $alongar_ses . '|' . $caducidade_sesion . '|' . $peche_ses, $key );

    $cookie = cifrar_descifrar( $caducidade_sesion . '|' . $token . '|' . $usuario->uuid . '|' . $hash . '|' . $peche_ses . '|' . $alongar_ses, 'cifrar', $esquema ) ;

    return $cookie;

}

/**
 * Xestiona o proceso de autenticación dun usuario, creando ou validando sesións
 * na base de datos e configurando as cookies de rastro correspondentes.
 *
 * @param Usuario  $usuario    O obxecto Usuario que se está autenticando.
 * @param array    $elementos  Parámetros opcionais de sesión (token, peche_ses, data_caducidade, rastros).
 * @param bool     $recordar   Indica se se quere lembrar a sesión a longo prazo.
 * @return mixed   Array cos datos de sesión se non se xeran rastros automáticos.
 */
function xerar_autenticado( Usuario $usuario, array $elementos, bool $recordar ) {

    if ( $recordar ) {
       $caducidade_rastro = $caducidade_sesion = $elementos[ 'data_caducidade' ] ?? time() + 30 * DIA_EN_SEGUNDOS;
    } else {
       $caducidade_rastro = 0;
       $caducidade_sesion = time() + 2 * DIA_EN_SEGUNDOS;
    }

    if ( empty( $elementos[ 'peche_ses' ] ) ) {
       $elementos[ 'peche_ses' ] = xerar_token( 15, false );
    }

    if ( empty( $elementos[ 'token' ] ) ) {

       if ( $usuario->sesions && !$usuario->pode( 'administrar' ) ) {
          $usuario->destruir_sesions();
       }

       $elementos[ 'token' ] = $usuario->crear_sesion( time(), $caducidade_sesion, $elementos[ 'peche_ses' ] );

    }

    if ( empty( $elementos[ 'rastros' ] ) ) {
       return [ 'token' => $elementos[ 'token' ], 'data_caducidade' => $caducidade_sesion, 'peche_ses' => $elementos[ 'peche_ses' ] ];
    }

    $args_sesion = [
       'caducidade_sesion' => $caducidade_sesion,
       'esquema' => 'sesion',
       'token' => $elementos[ 'token' ],
       'peche_ses' => $elementos[ 'peche_ses' ],
       'alongar_ses' => $recordar ? time() + DIA_EN_SEGUNDOS : 0
    ];

    $valor_rastro_basico = crear_rastro_sesion( $usuario, $args_sesion );

    $info_rastro = [
       'expires' => $caducidade_rastro,
       'path' => RUTA_RASTRO,
       'secure' => 1,
       'httponly' => 1,
       'samesite' => 'Lax'
    ];

    setcookie( RASTRO_SESION, $valor_rastro_basico, $info_rastro );

}