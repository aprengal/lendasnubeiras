<?php

declare ( strict_types = 1 );

/**
 * POST /actividade/modificar
 * 
 * Modifica, borra (estado 3) ou restaura (estado 0 a 2) unha actividade.
 * Envíanse todos os campos da actividade.
 * 
 * Respostas: 200, 400, 403, 404
 */
function endpoint_modificar_actividade(): void {

	esixir_metodo( 'POST' );

	$usuario = esixir_sesion();
	$datos = validar_corpo( coller_corpo(), regras_actividade( true ) );

	$actual = coller_actividade( $datos[ 'id' ] );

	if ( !$actual ) {
		responder( 404 );
	}

	if ( !pode_xestionar_actividade( $usuario, $actual[ 'id_autoria' ], $actual[ 'estado' ], $datos[ 'estado' ] ) ) {
		responder( 403 );
	}

	//Só os editores e administradores poden cambiar a autoría
	$cambia_autoria = $datos[ 'id_autoria' ] !== $actual[ 'id_autoria' ];

	if ( $cambia_autoria && !in_array( $usuario->rol, [ Rol::EDITOR, Rol::ADMINISTRADOR ], true ) ) {
		responder( 403 );
	}

	$id = $datos[ 'id' ];
	unset( $datos[ 'id' ] );

	db()->update( 'actividades', [
		'nova_info' => $datos,
		'onde' => [ 'id' => $id ],
		'limit' => 1
	] );

	responder( 200 );

}