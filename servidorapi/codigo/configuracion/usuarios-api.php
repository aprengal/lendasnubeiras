<?php

declare ( strict_types = 1 );

/**
 * Funcións da API para crear, protexer e borrar usuarios.
 * 
 */

/**
 * Crea un usuario novo en estado pendente, á espera de que un administrador o aprobe.
 * 
 * Devolve o id do usuario. O rol inicial é o máis básico (monitor).
 */
function crear_usuario_pendente( string $correo, string $uuid ): int {

	return db()->insert( 'usuarios', [
		'uuid' => $uuid,
		'correo' => $correo,
		'estado' => ESTADO_USUARIO_PENDENTE,
		'rol' => Rol::MONITOR->value
	] );

}

/**
 * Indica se un usuario é o usuario reservado (CORREO_USUARIO_LEGADO), ao que pasan as actividades
 * publicadas das contas borradas. Non se pode modificar nin borrar.
 */
function es_usuario_legado( Usuario $usuario ): bool {
	return mb_strtolower( $usuario->correo ) === CORREO_USUARIO_LEGADO;
}

/**
 * Pecha todas as sesións dun usuario.
 */
function pechar_sesions( int $id ) {

	if ( $id <= 0 ) {
		throw new InvalidArgumentException( 'Non se poden pechar as sesións do usuario ' . $id );
	}

	db()->delete( 'info_temporal', [
		'onde' => [ 'id' => $id, 'nome' => [ 'operador' => 'LIKE', 'valor' => 'ses_id_%' ] ]
	] );

}

/**
 * Borra de forma definitiva unha conta de usuario.
 * 
 * - Borradores, pendentes e actividades da papeleira: bórranse.
 * - O resto (as publicadas) pasan ao usuario reservado, para que o catálogo non perda contido.
 * - Bórranse tamén os datos temporais do usuario (sesións...) e, por último, o propio usuario.
 * 
 * Execútase nunha transacción: ou se fai todo ou non se fai nada.
 */
function borrar_conta( int $id, int $id_legado ) {

	if ( $id <= 0 || $id === $id_legado ) {
		throw new InvalidArgumentException( 'Non se pode borrar a conta ' . $id );
	}

	db()->iniciar_transaccion();

	db()->delete( 'actividades', [
		'onde' => [
			'id_autoria' => $id,
			'estado' => [ 'operador' => 'IN', 'valores' => [ ESTADO_ACTIVIDADE_BORRADOR, ESTADO_ACTIVIDADE_PENDENTE, ESTADO_ACTIVIDADE_BORRADA ] ]
		]
	] );

	db()->update( 'actividades', [
		'nova_info' => [ 'id_autoria' => $id_legado ],
		'onde' => [ 'id_autoria' => $id ]
	] );

	db()->delete( 'info_temporal', [ 'onde' => [ 'id' => $id ] ] );
	db()->delete( 'usuarios', [ 'onde' => [ 'id' => $id ], 'limit' => 1 ] );

	db()->executar_transaccion();

}

/**
 * Borra as contas cuxo prazo de borrado (DIAS_BORRADO_CONTA) rematou. Devolve cantas borrou.
 * 
 * Se non existe o usuario reservado non se borra ningunha, porque as actividades publicadas quedarían sen autor.
 */
function borrar_contas_caducadas(): int {

	$limite = time() - DIAS_BORRADO_CONTA * DIA_EN_SEGUNDOS;

	//data_borrado vale 0 cando a conta non está en proceso de borrado, polo que se exclúe
	$contas = db()->select( 'usuarios', [
		'columnas' => 'id',
		'onde' => [
			'estado' => ESTADO_USUARIO_BORRADO,
			'data_borrado' => [ 'operador' => 'BETWEEN', 'valores' => [ 1, $limite - 1 ] ]
		]
	], 'array' );

	if ( !$contas ) {
		return 0;
	}

	$legado = coller_usuario( 'correo', CORREO_USUARIO_LEGADO );

	if ( !$legado->existe() ) {
		error_log( 'Non se borraron as contas caducadas porque non existe o usuario reservado ' . CORREO_USUARIO_LEGADO );
		return 0;
	}

	foreach ( array_keys( $contas ) as $id ) {
		borrar_conta( (int) $id, $legado->id );
	}

	return count( $contas );

}
