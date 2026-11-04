<?php

declare ( strict_types = 1 );

/**
 * Valida a autenticidade e vixencia do rastro de sesión actual do usuario.
 * Comproba a caducidade, a existencia do usuario, a validez do HMAC e o estado da sesión.
 *
 * @return Usuario|null O obxecto Usuario se a autenticación é correcta, ou null en caso contrario.
 */
function validar_rastro_sesion(): ?Usuario {

    $rastro = analizar_rastro_sesion();

    if ( !$rastro ) {
       return null;
    }

    $caducidade_rastro = $caducidade_sesion = $rastro[ 'data_caducidade' ];
    $token   = $rastro[ 'token' ];
    $uuid    = $rastro[ 'usuario' ];
    $dispositivo_rastro = $rastro[ 'dispositivo' ];
    $hmac    = $rastro[ 'hmac' ];
    $esquema = $rastro[ 'esquema' ];

    // 1ª Comprobación - Caducou o rastro?
    if ( $caducidade_rastro < time() ) {
       reautenticar();
       return null;
    }

    $id_dispositivo = id_dispositivo();

    // 2ª Comprobación - Faise dende o mesmo dispositivo no que se iniciou sesión?
    if ( $dispositivo_rastro != $id_dispositivo ) {
        reautenticar();
        return null;
    }

    $usuario = coller_usuario( 'uuid', $uuid );

    // 3ª Comprobación - Existe o usuario?
    if ( !$usuario->existe() ) {
       return null;
    }

    $uuid_frag = mb_substr( $usuario->uuid, 8, 5 );

    $algo = 'sha256';

    $key = hash_hmac( $algo, $uuid_frag . '|' . $token . '|' . $usuario->correo . '|' . $id_dispositivo . '|' . $caducidade_sesion, salgado( $esquema ) );
    $hash = hash_hmac( $algo, $token . '|' . $usuario->correo . '|' . $id_dispositivo . '|' . $caducidade_sesion, $key );

    // 4ª Comprobación - Coinciden os hashes (cambio de datos de usuario)
    if ( !hash_equals( $hash, $hmac ) ) {
       reautenticar();
       return null;
    }

    // 5ª Comprobación - A sesión é correcta e está activa?
    if ( !$usuario->verificar_sesion( $token ) ) {
       reautenticar();
       return null;
    }

    return $usuario;

}

/**
 * Responde cun código HTTP 401 (Unauthorized) cando falla a reautenticación.
 */
function reautenticar(): void {
    responder( 401 );
}

/**
 * Analiza o rastro de sesión da cabecera HTTP ou cookie e devolve os seus elementos.
 * Se non hai rastro ou non é válido, devolve null.
 *
 * O resultado gárdase nunha caché estática durante a petición para evitar
 * descifrar o rastro varias veces.
 *
 * @return array|null Array asociativo cos datos extraídos do rastro ou null se non é válido.
 */
function analizar_rastro_sesion(): ?array {

    static $analizados = [];

    $cabeceiras = getallheaders();
    $sesion = $cabeceiras[ RASTRO_SESION ] ?? $_COOKIE[ RASTRO_SESION ] ?? '';

    if ( str_starts_with( $sesion, 'Bearer ' ) ) {
        $sesion = substr( $sesion, 7 );
    }

    if ( empty( $sesion ) ) {
        return null;
    }

    $rastro = $sesion;
    $esquema = 'sesion';

    if ( array_key_exists( $rastro, $analizados ) ) {
        return $analizados[ $rastro ];
    }

    $info_rastro = cifrar_descifrar( $rastro, 'descifrar', $esquema );
    $lista = explode( '|', $info_rastro );

    if ( count( $lista ) != 5 ) {
        $analizados[ $rastro ] = null;
        return null;
    }

    $elementos = [
        'data_caducidade' => $lista[ 0 ],
        'token'           => $lista[ 1 ],
        'usuario'         => $lista[ 2 ],
        'dispositivo'     => $lista[ 3 ],
        'hmac'            => $lista[ 4 ],
        'esquema'         => $esquema
    ];

    return $analizados[ $rastro ] = $elementos;

}

/**
 * Recupera o token de sesión contido no rastro actual.
 *
 * @return string O token de sesión ou unha cadea baleira se non existe.
 */
function coller_token_sesion(): string {
    $rastro = analizar_rastro_sesion();
    return $rastro[ 'token' ] ?? '';
}