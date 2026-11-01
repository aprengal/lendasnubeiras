<?php

declare ( strict_types = 1 );

/**
 * POST /sesion/validacion
 * 
 * Comproba que a sesión da cookie é correcta. Execútase ao abrir a aplicación.
 * 
 * Respostas: 200, 401, 404
 */
function endpoint_validar_sesion(): void {

    esixir_metodo( 'POST' );

    $rastro = analizar_rastro_sesion();

    if ( !$rastro ) {
       responder( 401 );
    }

    if ( !coller_usuario( 'uuid', $rastro[ 'uuid' ] )->existe() ) {
       responder( 404 );
    }

    responder( 200 );

}
