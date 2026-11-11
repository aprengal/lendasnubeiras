<?php

declare( strict_types = 1 );

/**
 * POST /actividade/actualizar
 *
 * Devolve a lista de actividades dispoñibles para o usuario,
 * segundo os permisos de visibilidade establecidos en coller_actividades_visibles().
 *
 * Admite solicitudes autenticadas e anónimas. Require o identificador
 * único do dispositivo no corpo da petición.
 *
 * Establécese un intervalo mínimo de 60 segundos entre solicitudes.
 * O límite identifícase polo usuario se está autenticado ou polo
 * dispositivo no caso das solicitudes anónimas.
 *
 * @return void
 *
 * Respostas: 200, 400, 429
 */
function endpoint_actualizar(): void {

    esixir_metodo( 'POST' );
    $corpo = validar_corpo( coller_corpo(), [ 'uuid' => [ 'tipo' => 'uuid' ] ] );

    $usuario = usuario_actual();
    $identificador = $usuario->existe() ? (string) $usuario->id : $corpo[ 'uuid' ];

    if ( !revisar_intentos( 'actualizar', $identificador, 1, MINUTO_EN_SEGUNDOS ) ) {
        responder( 429 );
    }

    responder( 200, [ 'actividades' => coller_actividades_visibles( $usuario->existe() ? $usuario : null ) ] );

}