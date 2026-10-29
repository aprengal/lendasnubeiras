<?php

declare ( strict_types = 1 );

/**
 * POST /actividade/crear
 * 
 * Crea unha actividade e devolve o seu id, que asigna o servidor.
 * 
 * Respostas: 200, 400, 403
 */
function endpoint_crear_actividade(): void {

	esixir_metodo( 'POST' );

	$usuario = esixir_sesion();
	$datos = validar_corpo( coller_corpo(), regras_actividade( false ) );

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
