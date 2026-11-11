<?php

declare( strict_types = 1 );

/**
 * POST /conta/iniciar-sesion
 *
 * Solicita o inicio de sesión dun usuario mediante o seu enderezo
 * de correo electrónico.
 *
 * Crea unha sesión pendente de activación e envía ao correo do usuario
 * unha ligazón de verificación no idioma indicado na petición.
 *
 * Devolve o rastro cifrado da sesión, que só será válido unha vez
 * verificada a ligazón recibida por correo electrónico.
 *
 * Establécese un intervalo mínimo de 60 segundos entre solicitudes
 * de inicio de sesión para un mesmo usuario.
 *
 * @return void
 *
 * Respostas: 200, 400, 403, 404, 429
 */
function endpoint_iniciar_sesion(): void {

    esixir_metodo( 'POST' );

    $corpo = validar_corpo( coller_corpo(), [
        'correo' => [ 'tipo' => 'correo' ],
        'uuid'   => [ 'tipo' => 'uuid' ],
        'idioma' => [ 'tipo' => 'texto', 'max' => 2, 'valores' => IDIOMAS_API ]
    ] );

    $correo = $corpo[ 'correo' ];
    $usuario = coller_usuario( 'correo', $correo );

    if ( !$usuario->existe() ) {
        responder( 404 );
    }

    if ( $usuario->estado !== ESTADO_USUARIO_ACTIVO ) {
        responder( 403 );
    }

    if ( !revisar_intentos( 'inicio_sesion', $correo, 1, MINUTO_EN_SEGUNDOS ) ) {
        responder( 429 );
    }

    $sesion = crear_sesion_api( $usuario, $corpo[ 'uuid' ] );
    $token_ligazon = xerar_token( 40, false );

    crear_dato_temporal( nome_dato( 'accion', $token_ligazon ), [
      'uuid'    => $corpo[ 'uuid' ],
      'usuario' => $usuario->uuid,
      'tipo'    => 'inicio',
      'token'   => $sesion[ 'token' ]
    ], DURACION_LIGAZON );

    enviar_correo_verificacion( $correo, 'inicio', crear_url_verificacion( $token_ligazon, $corpo[ 'uuid' ], $corpo[ 'idioma' ] ), $corpo[ 'idioma' ] );

    $datos_resposta = [ 'sesion' => $sesion[ 'sesion' ] ];

    responder( 200, $datos_resposta );

}