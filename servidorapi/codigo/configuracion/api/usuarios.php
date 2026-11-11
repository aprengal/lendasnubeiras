<?php

declare( strict_types = 1 );

/**
 * Crea unha nova conta de usuario en estado pendente, á espera
 * de que un administrador aprobe a solicitude.
 *
 * Asigna inicialmente o rol básico de monitor e xera un identificador
 * e un UUID únicos para a nova conta.
 *
 * Se falla a inserción por un problema de MySQL, realiza ata cinco
 * intentos para completar a operación. Se non consegue crear a conta,
 * responde cun HTTP 409.
 *
 * @param string $correo Enderezo de correo electrónico da nova conta.
 */
function crear_usuario_pendente( string $correo ): void {

    $args = [ 'columnas' => 'id', 'onde' => [ 'clave' => 'monitor' ], 'limit' => 1 ];
    $id_monitor = db()->select( 'roles', $args, 'unico' );

    $inserido = false;
    $reintentos = 5;
    $intento = 0;

    do {

        $uuid = xerar_token( 32, false );
        $id_usuario = xerar_id_unica();

        try {

            $intento++;

            db()->insert( 'usuarios', [
                'id' => $id_usuario,
                'uuid' => $uuid,
                'correo' => $correo,
                'estado' => ESTADO_USUARIO_PENDENTE,
                'id_rol' => $id_monitor
            ] );

            $inserido = true;

        } catch ( ProblemaMysqli $e ) {
            error_log( "Intentouse crear un usuario, pero fallou a inserción: " . $e->getMessage() );
        }

    } while ( !$inserido && $intento < $reintentos );

    if ( !$inserido ) {
        responder( 409 );
    }

}

/**
 * Comproba se un usuario corresponde á conta reservada do sistema.
 *
 * A identificación realízase comparando o seu enderezo de correo
 * electrónico con CORREO_USUARIO_LEGADO, sen distinguir maiúsculas
 * e minúsculas.
 *
 * Esta conta recibe as actividades publicadas dos usuarios eliminados
 * e non pode modificarse nin eliminarse.
 *
 * @param Usuario $usuario Usuario que se quere comprobar.
 * @return bool True se corresponde á conta reservada, false en caso contrario.
 */
function usuario_legado( Usuario $usuario ): bool {
	return mb_strtolower( $usuario->correo ) === CORREO_USUARIO_LEGADO;
}

/**
 * Elimina definitivamente unha conta de usuario e xestiona os seus datos asociados.
 *
 * Elimina as actividades que se atopan nos estados de borrador, pendente
 * ou borrado. As actividades publicadas transfírense á conta reservada,
 * conservando así o contido do catálogo.
 *
 * Tamén elimina os datos temporais asociados ao usuario e, finalmente,
 * o propio rexistro da conta.
 *
 * Todas as operacións execútanse dentro dunha transacción para evitar
 * que a eliminación quede parcialmente aplicada.
 *
 * @param int $id Identificador da conta que se quere eliminar.
 * @param int $id_legado Identificador da conta reservada que recibirá
 * as actividades publicadas.
 * @throws InvalidArgumentException Se o identificador non é válido
 * ou corresponde á conta reservada.
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
 * Elimina definitivamente as contas cuxo prazo de borrado xa rematou.
 *
 * Localiza as contas que permanecen no estado de borrado e cuxa data
 * de eliminación supera o prazo establecido en DIAS_BORRADO_CONTA.
 *
 * Antes de eliminar as contas, comproba que existe o usuario reservado,
 * ao que se transferirán as actividades publicadas. Se non existe,
 * non se elimina ningunha conta.
 *
 * Cada conta elimínase mediante borrar_conta(), que xestiona tamén
 * as actividades e os datos temporais asociados.
 *
 * @return int Número de contas eliminadas definitivamente.
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
