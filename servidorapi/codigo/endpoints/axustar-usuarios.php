<?php

declare ( strict_types = 1 );

/**
 * POST /axustar-usuarios
 * 
 * Só para administradores. Cambia o correo, o rol ou o estado dun usuario.
 * Só se envían os campos que se queren cambiar.
 * 
 * Se se cambia o correo, as sesións do usuario deixan de ser válidas,
 * porque o correo forma parte do rastro de sesión.
 * 
 * O usuario reservado non se pode modificar (409). Cambiar o estado dunha conta en proceso de borrado cancela o borrado.
 * 
 * Respostas: 200, 400, 403, 404, 409
 */
function endpoint_axustar_usuarios(): void {

	esixir_metodo( 'POST' );

	$usuario = esixir_sesion();

	if ( !$usuario->pode( 'administrar' ) ) {
		responder( 403 );
	}

	$datos = validar_corpo( coller_corpo(), [
		'id' => [ 'tipo' => 'enteiro', 'min' => 1 ],
		'correo' => [ 'tipo' => 'correo', 'opcional' => true ],
		'rol' => [ 'tipo' => 'texto', 'max' => 15, 'opcional' => true ],
		'estado' => [ 'tipo' => 'enteiro', 'valores' => [ ESTADO_USUARIO_PENDENTE, ESTADO_USUARIO_ACTIVO, ESTADO_USUARIO_SUSPENDIDO ], 'opcional' => true ]
	] );

	$cambios = $datos;
	unset( $cambios[ 'id' ] );

	if ( !$cambios ) {
		responder( 400 );
	}

	if ( isset( $cambios[ 'rol' ] ) ) {

		$rol = Rol::tryFrom( mb_strtolower( $cambios[ 'rol' ] ) );

		if ( !$rol ) {
			responder( 400 );
		}

		$cambios[ 'rol' ] = $rol->value;

	}

	$obxectivo = coller_usuario( 'id', $datos[ 'id' ] );

	if ( !$obxectivo->existe() ) {
		responder( 404 );
	}

	//O usuario reservado non se pode modificar
	if ( usuario_legado( $obxectivo ) ) {
		responder( 409 );
	}

	if ( isset( $cambios[ 'correo' ] ) ) {

		$existente = coller_usuario( 'correo', $cambios[ 'correo' ] );

		if ( $existente->existe() && $existente->id !== $obxectivo->id ) {
			responder( 409 );
		}

	}

	//Cambiar o estado dunha conta en proceso de borrado cancela o borrado
	if ( $obxectivo->estado === ESTADO_USUARIO_BORRADO && isset( $cambios[ 'estado' ] ) ) {
		$cambios[ 'data_borrado' ] = 0;
	}

	db()->update( 'usuarios', [
		'nova_info' => $cambios,
		'onde' => [ 'id' => $obxectivo->id ],
		'limit' => 1
	] );

	responder( 200 );

}
