<?php

declare( strict_types = 1 );

/**
 * POST /actividade/crear
 *
 * Crea unha nova actividade na base de datos do servidor.
 *
 * Require unha sesión autenticada e os datos necesarios para definir
 * a actividade. O servidor asigna o identificador definitivo e devolve
 * o ID e o estado resultante da creación.
 *
 * Os estados negativos empregados temporalmente polo cliente convértense
 * nos estados definitivos do servidor:
 * - -1: borrador (0).
 * - -2: pendente (1).
 * - -3: publicado (2).
 *
 * A creación está suxeita aos permisos do usuario e ás restricións
 * asociadas á autoría e ao estado solicitado.
 *
 * Non se permite crear actividades directamente no estado borrado.
 *
 * @return void
 *
 * Respostas: 201, 400, 401, 403
 */
function endpoint_crear_actividade(): void {

	esixir_metodo( 'POST' );

	$usuario = esixir_sesion();

	//Que permiso mínimo fai falta para empregar este endpoint??
    if ( !$usuario->pode( 'crear_actividade' ) ) {
        responder( 403 );
    }

	$campos = regras_actividade( false ) + [ 'uuid' => [ 'tipo' => 'uuid' ] ];
	$datos = validar_corpo( coller_corpo(), $campos );

	/*
	 * Conversión de estados temporais do cliente aos estados definitivos do servidor:
	 * O cliente manexa estados negativos para distinguir o fluxo local:
	 * -1 (borrador) convértese en 0 (borrador no servidor)
	 * -2 (pendente) convértese en 1 (pendente no servidor)
	 * -3 (publicado) convértese en 2 (publicado no servidor)
	 *
	 * Utilízase o valor absoluto menos un (abs($estado) - 1) para mapear
	 * de forma inversa: |-1| - 1 = 0, |-2| - 1 = 1, |-3| - 1 = 2.
	 */
	if ( $datos[ 'estado' ] < 0 ) {
		$datos[ 'estado' ] = abs( $datos[ 'estado' ] ) - 1;
	}

	if ( !pode_xestionar_actividade( $usuario, $datos[ 'id_autoria' ], 0, $datos[ 'estado' ] ) ) {
		responder( 403 );
	}

	$id = db()->insert( 'actividades', $datos );

	if ( !$id ) {
		throw new LogicException( 'Non se obtivo o id da actividade creada' );
	}

	responder( 201, [ 'id' => $id, 'estado' => $datos[ 'estado' ] ] );

}
