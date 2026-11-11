<?php

declare( strict_types = 1 );

/**
 * POST /actividade/modificar
 *
 * Modifica os datos dunha actividade existente ou cambia o seu estado.
 *
 * Permite crear borradores, enviar actividades a revisión, publicalas,
 * borralas ou restauralas, segundo os permisos do usuario autenticado.
 *
 * Require unha sesión autenticada e o envío de todos os campos
 * da actividade, incluído o seu identificador no servidor.
 *
 * A modificación está suxeita aos permisos do usuario e ás restricións
 * asociadas á autoría e ao estado actual da actividade.
 *
 * Só os editores e administradores poden cambiar a autoría.
 *
 * @return void
 *
 * Respostas: 200, 400, 401, 403, 404
 */
function endpoint_modificar_actividade(): void {

	esixir_metodo( 'POST' );

	$usuario = esixir_sesion();

	//Que permiso mínimo fai falta para empregar este endpoint??
    if ( !$usuario->pode( 'editar_actividade' ) ) {
        responder( 403 );
    }

	$campos = regras_actividade( true ) + [ 'uuid' => [ 'tipo' => 'uuid' ] ];
	$datos = validar_corpo( coller_corpo(), $campos );

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