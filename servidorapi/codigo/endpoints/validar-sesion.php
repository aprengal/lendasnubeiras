<?php

declare ( strict_types = 1 );

/**
 * POST /sesion/validacion
 * 
 * Comproba que a sesión da cookie é correcta e segue activa, e alóngaa.
 * Execútase ao abrir a aplicación.
 * 
 * Respostas: 200, 403, 404
 */
function endpoint_validar_sesion(): never {

	esixir_metodo( 'POST' );

	$rastro = analizar_rastro();

	if ( !$rastro ) {
		responder( 403 );
	}

	if ( !coller_usuario( 'uuid', $rastro[ 'uuid' ] )->existe() ) {
		responder( 404 );
	}

	$usuario = esixir_sesion();

	$usuario->alongar_sesion_actual();
	crear_dato_temporal( nome_dato( 'sesion_activa', coller_token_sesion() ), '1', DURACION_SESION );

	responder( 200 );

}
