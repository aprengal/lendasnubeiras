<?php

declare ( strict_types = 1 );

/**
 * POST /reportar-erros
 * 
 * Rexistra un erro da aplicación no rexistro de erros do servidor (error_log).
 * Non se envía información a ningunha plataforma externa.
 * 
 * Respostas: 200, 400, 403
 */
function endpoint_reportar_erros(): never {

	esixir_metodo( 'POST' );

	$usuario = esixir_sesion();

	$datos = validar_corpo( coller_corpo(), [
		'id_peticion' => [ 'tipo' => 'uuid' ],
		'erro' => [ 'tipo' => 'texto', 'max' => 20000 ]
	] );

	$erro = sanitizar_campo( $datos[ 'erro' ] );

	if ( $erro === '' ) {
		responder( 400 );
	}

	if ( mb_strlen( $erro ) > 2000 ) {
		$erro = mb_substr( $erro, 0, 2000 ) . ' [TRUNCADO]';
	}

	error_log( 'Erro reportado pola aplicación. Usuario: ' . $usuario->id . ' // Petición: ' . $datos[ 'id_peticion' ] . ' // Detalles: ' . $erro );

	responder( 200 );

}
