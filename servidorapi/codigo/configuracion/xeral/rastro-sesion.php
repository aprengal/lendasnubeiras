<?php

declare ( strict_types = 1 );

/**
 * Valida a autenticidade e vixencia do rastro de sesión actual do usuario.
 * Comproba a caducidade, a existencia do usuario, a validez do HMAC e o estado da sesión.
 *
 * @return Usuario|null O obxecto Usuario se a autenticación é correcta, ou null en caso contrario.
 */
function validar_rastro_sesion() {

    $rastro = analizar_rastro_sesion();

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

/**
 * Analiza o rastro de sesión da cookie e devolve os seus elementos. Se non hai rastro ou non é válido, non devolve nada.
 *
 * O resultado gárdase durante a petición: consúltase varias veces (usuario, token...) e descifrar o rastro ten custo.
 *
 * @return array|null Array asociativo cos datos extraídos do rastro ou null se non é válido.
 */
function analizar_rastro_sesion() {

    static $analizados = [];

    if ( empty( $_COOKIE[ RASTRO_SESION ] ) ) {
       return;
    }

    $rastro = $_COOKIE[ RASTRO_SESION ];
    $esquema = 'sesion';

    if ( array_key_exists( $rastro, $analizados ) ) {
       return $analizados[ $rastro ];
    }

    $info_rastro = cifrar_descifrar( $rastro, 'descifrar', $esquema );

    // Cada campo do rastro está separado por un '|'
    $lista = explode( '|', $info_rastro );

    if ( count( $lista ) != 6 ) {
       $analizados[ $rastro ] = null;
       return;
    }

    $elementos = [
       'data_caducidade' => $lista[ 0 ],
       'token'           => $lista[ 1 ],
       'uuid'            => $lista[ 2 ],
       'hmac'            => $lista[ 3 ],
       'peche_ses'       => $lista[ 4 ],
       'alongar_ses'     => $lista[ 5 ],
       'esquema'         => $esquema
    ];

    return $analizados[ $rastro ] = $elementos;

}

/**
 * Recupera o token de sesión contido no rastro actual.
 *
 * @return string O token de sesión ou unha cadea baleira se non existe.
 */
function coller_token_sesion() {
    $rastro = analizar_rastro_sesion();
    return $rastro[ 'token' ] ?? '';
}