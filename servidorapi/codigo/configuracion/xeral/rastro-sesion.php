<?php

declare( strict_types = 1 );

/**
 * Valida a autenticidade e a vixencia dos datos de sesión recibidos
 * na cabeceira HTTP.
 *
 * Realiza as seguintes comprobacións:
 * - Verifica que a sesión non caducase.
 * - Comproba que a solicitude procede do dispositivo asociado á sesión.
 * - Verifica que o usuario asociado á sesión existe.
 * - Comproba a integridade dos datos mediante a validación do HMAC,
 *   detectando posibles modificacións nos datos do usuario ou da sesión.
 * - Verifica que o token corresponde a unha sesión activa do usuario.
 *
 * Se algunha das comprobacións de validez falla, solicita a
 * reautenticación mediante unha resposta HTTP 401.
 *
 * @return Usuario|null Obxecto [Usuario] se todas as comprobacións son
 * satisfactorias, ou null se non se pode validar a sesión.
 */
function validar_cabeceira_sesion(): ?Usuario {

    $datos_sesion = analizar_cabeceira_sesion();

    if ( !$datos_sesion ) {
       return null;
    }

    $caducidade_sesion = $datos_sesion[ 'data_caducidade' ];
    $token = $datos_sesion[ 'token' ];
    $uuid = $datos_sesion[ 'usuario' ];
    $id_dispositivo_sesion = $datos_sesion[ 'dispositivo' ];
    $hmac = $datos_sesion[ 'hmac' ];
    $esquema = $datos_sesion[ 'esquema' ];

    // 1ª Comprobación - Caducou a sesión?
    if ( $caducidade_sesion < time() ) {
       reautenticar();
       return null;
    }

    $id_dispositivo = id_dispositivo();

    // 2ª Comprobación - Coincide o dispositivo?
    if ( $id_dispositivo_sesion != $id_dispositivo ) {
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

    // 4ª Comprobación - Coinciden os hashes?
    if ( !hash_equals( $hash, $hmac ) ) {
       reautenticar();
       return null;
    }

    // 5ª Comprobación - A sesión continúa activa?
    if ( !$usuario->verificar_sesion( $token ) ) {
       reautenticar();
       return null;
    }

    return $usuario;

}

/**
 * Devolve unha resposta HTTP 401 (Unauthorized) para indicar que a solicitude
 * non está autenticada ou que a sesión non é válida.
 */
function reautenticar(): void {
    responder( 401 );
}

/**
 * Obtén e descifra os datos de sesión recibidos na cabeceira HTTP.
 *
 * Se o valor da cabeceira inclúe o texto `Bearer`, este elimínase
 * antes de procesar os datos de sesión.
 *
 * Se a cabeceira non existe, está baleira ou o seu contido non ten
 * un formato válido, devolve null.
 *
 * Os resultados gárdanse nunha caché estática durante a execución
 * da petición para evitar descifrar varias veces o mesmo valor.
 *
 * @return array|null Array asociativo cos datos da sesión ou null
 * se non se poden obter ou descifrar correctamente.
 */
function analizar_cabeceira_sesion(): ?array {

    static $datos_analizados = [];

    $cabeceiras = getallheaders();
    $valor_sesion = $cabeceiras[ CABECEIRA_SESION ] ?? '';

    if ( str_starts_with( $valor_sesion, 'Bearer ' ) ) {
        $valor_sesion = substr( $valor_sesion, 7 );
    }

    if ( empty( $valor_sesion ) ) {
        return null;
    }

    $esquema = 'sesion';

    if ( array_key_exists( $valor_sesion, $datos_analizados ) ) {
        return $datos_analizados[ $valor_sesion ];
    }

    $info_sesion = cifrar_descifrar( $valor_sesion, 'descifrar', $esquema );
    $lista = explode( '|', $info_sesion );

    if ( count( $lista ) != 5 ) {
        $datos_analizados[ $valor_sesion ] = null;
        return null;
    }

    $datos_sesion = [
        'data_caducidade' => $lista[ 0 ],
        'token'           => $lista[ 1 ],
        'usuario'         => $lista[ 2 ],
        'dispositivo'     => $lista[ 3 ],
        'hmac'            => $lista[ 4 ],
        'esquema'         => $esquema
    ];

    return $datos_analizados[ $valor_sesion ] = $datos_sesion;

}

/**
 * Obtén o token de sesión a partir dos datos de autenticación da cabeceira HTTP.
 *
 * @return string Token de sesión ou cadea baleira se non existe unha sesión válida.
 */
function coller_token_sesion(): string {
    $datos_sesion = analizar_cabeceira_sesion();
    return $datos_sesion[ 'token' ] ?? '';
}