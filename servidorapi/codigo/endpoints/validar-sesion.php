<?php

declare ( strict_types = 1 );

/**
 * POST /sesion/validacion
 * 
 * Comproba que a sesión é correcta. Execútase ao abrir a aplicación.
 * 
 * Respostas: 200, 401, 404
 */
function endpoint_validar_sesion(): void {

    esixir_metodo( 'POST' );

    validar_corpo( coller_corpo(), [ 'uuid' => [ 'tipo' => 'uuid' ] ] );

    $rastro = analizar_rastro_sesion();

    if ( !$rastro ) {
       responder( 401 );
    }

    $usuario = coller_usuario( 'uuid', $rastro[ 'usuario' ] );

    if ( !$usuario->existe() ) {
       responder( 404 );
    }

    if ( !$usuario->verificar_sesion( $rastro[ 'token' ] ) ) {
        responder( 401 );
    }

    $datos_resposta = [
        'id'     => $usuario->id,
        'correo' => $usuario->correo,
        'rol'    => $usuario->rol->value
    ];

    responder( 200, $datos_resposta );

}