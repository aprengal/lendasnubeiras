<?php

/**
 * Crea unha cookie (rastro) no navegador coas opcións de seguridade e caducidade especificadas.
 * Opcionalmente, cifra o contido se se indica un grupo de cifrado.
 *
 * @param string      $nome     Nome da cookie.
 * @param string|int  $contido  Valor ou contido a almacenar.
 * @param string      $ruta     Ruta do servidor para a que estará dispoñible a cookie (por defecto '/').
 * @param string      $grupo    Grupo ou clave de cifrado opcional.
 * @param int         $duracion Tempo de vida en segundos da cookie (por defecto 60).
 */
function crear_rastro( string $nome, string|int $contido, string $ruta = '/', string $grupo = '', int $duracion = 60 ) {

    $opcions = [
       'expires' => $duracion ? time() + $duracion : 0,
       'path' => $ruta,
       'secure' => 1,
       'httponly' => 1,
       'samesite' => 'Strict'
    ];

    if ( $grupo ) {
       $contido = cifrar_descifrar( $contido, 'cifrar', $grupo );
    }

    setcookie( $nome, $contido, $opcions );

}

/**
 * Elimina unha cookie (rastro) do navegador definindo a súa caducidade no pasado.
 *
 * @param string  $nome     Nome da cookie a borrar.
 * @param string  $ruta     Ruta asociada á cookie.
 * @param string  $dominio  Dominio asociado á cookie.
 */
function borrar_rastro( string $nome, string $ruta = '/', string $dominio = '' ) {
    setcookie( $nome, '', 1, $ruta, $dominio );
}

/**
 * Valida a autenticidade e vixencia do rastro de sesión actual do usuario.
 * Comproba a caducidade, a existencia do usuario, a validez do HMAC e o estado da sesión.
 *
 * @return Usuario|null O obxecto Usuario se a autenticación é correcta, ou null en caso contrario.
 */
function validar_rastro() {

    $rastro = analizar_rastro();

    if ( !$rastro ) {
       return;
    }

    $caducidade_rastro = $caducidade_sesion = $rastro[ 'data_caducidade' ];
    $token   = $rastro[ 'token' ];
    $uuid    = $rastro[ 'uuid' ];
    $hmac    = $rastro[ 'hmac' ];
    $alongar_ses = $rastro[ 'alongar_ses' ];
    $peche_ses = $rastro[ 'peche_ses' ];
    $esquema = $rastro[ 'esquema' ];

    // AJAX e solicitudes POST no caso de rastros de sesión (que non caducan)
    if ( !$alongar_ses && ( 'POST' === $_SERVER[ 'REQUEST_METHOD' ] ) ) {
       $caducidade_rastro += HORA_EN_SEGUNDOS;
    }

    // 2ª Comprobación - Caducou o rastro?
    if ( $caducidade_rastro < time() ) {
       reautenticar();
       return;
    }

    $usuario = coller_usuario( 'uuid', $uuid );

    // 3ª Comprobación - Existe o usuario?
    if ( !$usuario->existe() ) {
       return;
    }

    $uuid_frag = mb_substr( $usuario->uuid, 8, 5 );

    $algo = 'sha256';

    $key = hash_hmac( $algo, $uuid_frag . '|' . $token . '|' . $usuario->correo . '|' . $alongar_ses . '|' . $caducidade_sesion . '|' . $peche_ses, salgado( $esquema ) );
    $hash = hash_hmac( $algo, $token . '|' . $usuario->correo . '|' . $alongar_ses . '|' . $caducidade_sesion . '|' . $peche_ses, $key );

    // 4ª Comprobación - Coinciden os hashes (cambio de datos de usuario)
    if ( !hash_equals( $hash, $hmac ) ) {
       reautenticar();
       return;
    }

    // 5ª Comprobación - A sesión é correcta e está activa?
    if ( !$usuario->verificar_sesion( $token ) ) {
       reautenticar();
       return;
    }

    return $usuario;

}

/**
 * Responde cun código HTTP 401 (Unauthorized) cando falla a reautenticación.
 */
function reautenticar() {
    responder( 401 );
}

// Autenticar
/**
 * Analiza o rastro de sesión da cookie e devolve os seus elementos. Se non hai rastro ou non é válido, non devolve nada.
 *
 * O resultado gárdase durante a petición: consúltase varias veces (usuario, token...) e descifrar o rastro ten custo.
 *
 * @return array|null Array asociativo cos datos extraídos do rastro ou null se non é válido.
 */
function analizar_rastro() {

    static $analizados = [];

    if ( empty( $_COOKIE[ RASTRO_SESION ] ) ) {
       return;
    }

    $cookie = $_COOKIE[ RASTRO_SESION ];
    $esquema = 'sesion';

    if ( array_key_exists( $cookie, $analizados ) ) {
       return $analizados[ $cookie ];
    }

    $rastro = cifrar_descifrar( $cookie, 'descifrar', $esquema );

    // Cada campo do rastro está separado por un '|'
    $lista = explode( '|', $rastro );

    if ( count( $lista ) != 6 ) {
       $analizados[ $cookie ] = null;
       return;
    }

    $elementos = [
       'data_caducidade'  => $lista[ 0 ],
       'token'       => $lista[ 1 ],
       'uuid'        => $lista[ 2 ],
       'hmac'        => $lista[ 3 ],
       'peche_ses'   => $lista[ 4 ],
       'alongar_ses' => $lista[ 5 ],
       'esquema'     => $esquema
    ];

    return $analizados[ $cookie ] = $elementos;

}


// Autenticación
/**
 * Xestiona o usuario actual da petición, validando o seu rastro de sesión ou devolvendo un usuario anónimo.
 * Permite tamén reiniciar a caché do usuario actual se se solicita explicitamente.
 *
 * @string  $accion  Acción a realizar ('coller' ou 'reiniciar').
 * @return  Usuario  A instancia do obxecto Usuario correspondente.
 */
function usuario_act( string $accion = 'coller' ) {

    static $usuario;

    if ( $accion == 'coller' ) {

       if ( $usuario ) {
          return $usuario;
       }

       $novo_usuario = validar_rastro();

       return $usuario = $novo_usuario instanceof Usuario ? $novo_usuario : new Usuario( 'id', -1, false );

    }

    if ( $accion == 'reiniciar' ) {

       if ( !$usuario || !$usuario->existe() ) {
          throw new LogicException( 'Só se pode reiniciar o usuario actual se está definido e fai referencia a un usuario que exista' );
       }

       $rastro = analizar_rastro();
       $uuid = $rastro[ 'uuid' ];

       if ( $usuario->uuid == $uuid ) {
          return $usuario = coller_usuario( 'uuid', $uuid, false ); // Sen memoria: hai que ler o usuario de novo
       }

    }

    return $usuario = new Usuario( 'id', -1, false );

}

// Autenticación
/**
 * Recupera o token de sesión contido no rastro actual.
 *
 * @return string O token de sesión ou unha cadea baleira se non existe.
 */
function coller_token_sesion() {
    $rastro = analizar_rastro();
    return $rastro[ 'token' ] ?? '';
}

// Autenticación
/**
 * Destrúe a sesión actual do usuario activo, limpa as cookies de rastro e finaliza a execución.
 */
function destruir_sesion_actual() {

    $token = coller_token_sesion();

    $usuario = usuario_act();

    if ( $token ) {
       $usuario->destruir_sesion( $token );
    }

    usuario_act( 'anonimo' );
    limpar_rastros_sesion( $usuario->pode( 'administrar' ) );

}

/**
 * Borra o rastro de sesión das cookies do navegador e encerra a execución cun código 200.
 *
 * @param bool  $cova  Indicador de limpeza (parámetro de compatibilidade de fluxo).
 */
function limpar_rastros_sesion( bool $cova ) {

    borrar_rastro( RASTRO_SESION, RUTA_RASTRO );

    exit( 200 );

}