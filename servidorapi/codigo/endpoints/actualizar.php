<?php

declare ( strict_types = 1 );

/**
 * GET /actividade/actualizar
 * 
 * Devolve a lista de actividades que pode ver o usuario (ver coller_actividades_visibles).
 * Só se pode solicitar unha vez cada 60 segundos por usuario.
 * 
 * Respostas: 200, 403, 429
 */
function endpoint_actualizar(): never {

	esixir_metodo( 'GET' );

	$usuario = esixir_sesion();

	if ( !reservar_intento( 'actualizar', (string) $usuario->id, 1, MINUTO_EN_SEGUNDOS ) ) {
		responder( 429 );
	}

	responder( 200, coller_actividades_visibles( $usuario ) );

}
