<?php

declare ( strict_types = 1 );

/**
 * POST /actividade/actualizar
 *
 * Devolve a lista de actividades dispoñibles que pode ver o usuario (ver coller_actividades_visibles).
 * A sesión é opcional (admite uso anónimo) e requírese enviar a uuid do dispositivo no corpo da petición.
 * Só se pode solicitar unha vez cada 60 segundos por dispositivo (ou usuario, se está autenticado).
 *
 * Respostas: 200, 400, 429
 */
function endpoint_actualizar(): void {

    esixir_metodo( 'POST' );

    $corpo = validar_corpo( coller_corpo(), [
        'uuid' => [ 'tipo' => 'uuid' ]
    ] );

    $usuario = usuario_actual();
    $identificador = $usuario->existe() ? (string) $usuario->id : $corpo[ 'uuid' ];

    if ( !revisar_intentos( 'actualizar', $identificador, 1, MINUTO_EN_SEGUNDOS ) ) {
        responder( 429 );
    }

    responder( 200, [ 'actividades' => coller_actividades_visibles( $usuario->existe() ? $usuario : null ) ] );

}