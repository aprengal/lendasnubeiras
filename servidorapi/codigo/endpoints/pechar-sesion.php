<?php

declare( strict_types = 1 );

/**
 * POST /conta/pechar-sesion
 *
 * Solicita o peche da sesión autenticada no dispositivo actual.
 *
 * Comproba que a sesión pertence ao usuario autenticado e que se pode
 * pechar correctamente. Se a comprobación é satisfactoria, destrúe
 * a sesión correspondente no servidor.
 *
 * Se a sesión non se pode verificar, non se realiza ningunha operación
 * e devólvese un erro de conflito.
 *
 * @return void
 *
 * Respostas: 200, 401, 409
 */
function endpoint_pechar_sesion(): void {

    esixir_metodo( 'POST' );

    validar_corpo( coller_corpo(), [
        'uuid' => [ 'tipo' => 'uuid' ]
    ] );

    $usuario = esixir_sesion();
    $resultado = $usuario->verificar_peche_sesion();

    if ( $resultado ) {

        $rastro = analizar_cabeceira_sesion();
        $token = $rastro[ 'token' ];

        $usuario->destruir_sesion( $token );

        responder( 200 );

    }

    responder( 409 );

}