
<?php

declare ( strict_types = 1 );

/**
 * POST /conta/pechar-sesion
 *
 * O usuario solicita pechar sesión. Este é unha das poucas situacións nas que unha resposta 401 ou 403
 * non indicarían un erro como tal, senón que non hai nada que facer porque a sesión xa caducou ou é posible
 * que estea suspendido.
 *
 * Respostas: 200, 403, 409
 */
function endpoint_pechar_sesion(): void {

	esixir_metodo( 'POST' );

	$usuario = esixir_sesion();
	$resultado = $usuario->pechar_sesion_actual();

	if ( $resultado ) {
	    $rastro = analizar_rastro_sesion();
	    $token = $rastro[ 'token' ];
	    $usuario->destruir_sesion( $token );
	    responder( 200 );
	}

    responder( 409 );

}