<?php

declare ( strict_types = 1 );

/**
 * GET /accion/verificacion
 * 
 * Verifica unha acción (rexistro, inicio de sesión ou borrado de conta) coa ligazón que recibe o usuario por correo.
 * Execútase ao premer sobre a ligazón, polo que os datos van na URL (id_accion e uuid).
 * 
 * A ligazón só se pode usar unha vez. Cando algo falla (parámetros ausentes ou incorrectos,
 * ligazón caducada, descoñecida ou xa usada) responde sempre co mesmo 409, para dar a menor información posible.
 * 
 * Respostas: 200, 409
 */
function endpoint_verificar_accion(): never {

	esixir_metodo( 'GET' );

	$id_accion = $_GET[ 'id_accion' ] ?? null;
	$uuid = $_GET[ 'uuid' ] ?? null;

	if ( !is_string( $id_accion ) || preg_match( '/^[a-zA-Z0-9]{40}$/', $id_accion ) !== 1 || !es_uuid( $uuid ) ) {
		responder( 409 );
	}

	$nome = nome_dato( 'accion', $id_accion );
	$dato = coller_dato_temporal( $nome );

	if ( !is_string( $dato ) || !hai_serializacion( $dato ) ) {
		responder( 409 );
	}

	$accion = unserialize( $dato, [ 'allowed_classes' => false ] );

	//O uuid da ligazón ten que ser o do dispositivo que fixo a solicitude
	if ( !is_array( $accion ) || !isset( $accion[ 'tipo' ], $accion[ 'uuid' ] ) || !hash_equals( (string) $accion[ 'uuid' ], strtolower( $uuid ) ) ) {
		responder( 409 );
	}

	//Bórrase antes de executar a acción para que non se poida usar dúas veces
	borrar_dato_temporal( $nome );

	switch ( $accion[ 'tipo' ] ) {

		case 'rexistro':
			verificar_rexistro( $accion );
			break;

		case 'inicio':
			verificar_inicio( $accion );
			break;

		case 'borrado':
			verificar_borrado( $accion );
			break;

		default:
			responder( 409 );

	}

	responder( 200 );

}

/**
 * Verifica un rexistro: crea o usuario en estado pendente, á espera de que un administrador o aprobe.
 */
function verificar_rexistro( array $accion ) {

	if ( !isset( $accion[ 'correo' ] ) ) {
		responder( 409 );
	}

	//Entre a solicitude e a verificación o correo ou o dispositivo poderían ter sido rexistrados
	if ( coller_usuario( 'correo', $accion[ 'correo' ] )->existe() || coller_usuario( 'uuid', $accion[ 'uuid' ] )->existe() ) {
		responder( 409 );
	}

	crear_usuario_pendente( $accion[ 'correo' ], $accion[ 'uuid' ] );

}

/**
 * Verifica un inicio de sesión: activa a sesión que se creou ao solicitalo.
 */
function verificar_inicio( array $accion ) {

	if ( !isset( $accion[ 'activacion' ] ) ) {
		responder( 409 );
	}

	crear_dato_temporal( $accion[ 'activacion' ], '1', DURACION_SESION );

}

/**
 * Verifica un borrado de conta: a conta pasa ao estado borrado, péchanse as súas sesións e comeza o prazo
 * de DIAS_BORRADO_CONTA días, pasado o cal /limpar bórraa de forma definitiva.
 */
function verificar_borrado( array $accion ) {

	if ( !isset( $accion[ 'id' ] ) ) {
		responder( 409 );
	}

	$usuario = coller_usuario( 'id', (int) $accion[ 'id' ], false );

	//Só se pode borrar unha conta activa que non sexa de administrador (nin a reservada)
	if ( !$usuario->existe() || $usuario->estado !== ESTADO_USUARIO_ACTIVO || $usuario->rol === Rol::ADMINISTRADOR || es_usuario_legado( $usuario ) ) {
		responder( 409 );
	}

	db()->update( 'usuarios', [
		'nova_info' => [ 'estado' => ESTADO_USUARIO_BORRADO, 'data_borrado' => time() ],
		'onde' => [ 'id' => $usuario->id ],
		'limit' => 1
	] );

	pechar_sesions( $usuario->id );

}
