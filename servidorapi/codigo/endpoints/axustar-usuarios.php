<?php

declare( strict_types = 1 );

/**
 * POST /axustar-usuarios
 *
 * Permite aos administradores modificar os datos dunha conta de usuario.
 * Pódese cambiar o enderezo de correo electrónico, o rol e o estado,
 * enviando unicamente os campos que se desexan modificar.
 *
 * O identificador do usuario é obrigatorio. Tamén se require a UUID
 * do dispositivo na petición.
 *
 * Non se permite modificar a conta do usuario reservado.
 * Tampouco se pode asignar directamente o estado de borrado.
 *
 * Se o correo electrónico xa pertence a outro usuario, a operación
 * non se realiza.
 *
 * Ao modificar o estado dunha conta en proceso de borrado, cancélase
 * a eliminación definitiva restablecendo a data de borrado.
 *
 * Respostas: 200, 400, 403, 404, 409
 */
function endpoint_axustar_usuarios(): void {

	esixir_metodo( 'POST' );

	$usuario = esixir_sesion();

	if ( !$usuario->pode( 'xestionar_usuarios' ) ) {
		responder( 403 );
	}

	$datos = validar_corpo( coller_corpo(), [
	    'uuid' => [ 'tipo' => 'uuid' ],
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