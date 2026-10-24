<?php

declare ( strict_types = 1 );

/**
 * POST /actividade/crear
 * 
 * Crea unha actividade e devolve o seu id, que asigna o servidor.
 * 
 * Respostas: 200, 400, 403
 */
function endpoint_crear_actividade(): never {

	esixir_metodo( 'POST' );

	$usuario = esixir_sesion();
	$datos = validar_corpo( coller_corpo(), regras_actividade( false ) );

	if ( !pode_xestionar_actividade( $usuario, $datos[ 'id_autoria' ], 0, $datos[ 'estado' ] ) ) {
		responder( 403 );
	}

	$id = db()->insert( 'actividades', $datos );

	if ( !$id ) {
		throw new LogicException( 'Non se obtivo o id da actividade creada' );
	}

	responder( 200, [ 'id' => $id ] );

}
