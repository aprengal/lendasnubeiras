<?php

declare ( strict_types = 1 );

/**
 * GET /limpar
 * 
 * Executa as tarefas programadas. Non require sesión. Se xa hai unha execución en curso, responde cun 409.
 * 
 * Tarefas:
 * - Elimina os datos temporais caducados.
 * - Borra de forma definitiva as contas que pediron o borrado hai máis de DIAS_BORRADO_CONTA días (ver borrar_contas_caducadas).
 * - Baleira a papeleira: elimina de forma definitiva as actividades que levan máis de DIAS_PAPELEIRA días
 *   no estado borrado. Como borrar unha actividade é só cambiarlle o estado (e se pode restaurar), a data de
 *   borrado é a da súa última modificación (data_modificado), en segundos como o resto de datas do servidor.
 * 
 * Respostas: 204, 409
 */
function endpoint_limpar(): void {

	esixir_metodo( 'GET' );

	$bloqueo = 'limpar_en_curso';

	if ( (int) coller_dato_temporal( $bloqueo ) ) {
		responder( 409 );
	}

	//O bloqueo caduca soo aos 5 minutos por se a execución falla a metade
	crear_dato_temporal( $bloqueo, 1, 5 * MINUTO_EN_SEGUNDOS );

	try {

		db()->delete( 'info_temporal', [
			'onde' => [ 'data_caducidade' => [ 'operador' => '<', 'valor' => time() ] ]
		] );

		db()->delete( 'actividades', [
			'onde' => [
				'estado' => ESTADO_ACTIVIDADE_BORRADA,
				'data_modificado' => [ 'operador' => '<', 'valor' => time() - DIAS_PAPELEIRA * DIA_EN_SEGUNDOS ]
			]
		] );

		borrar_contas_caducadas();

	} finally {
		borrar_dato_temporal( $bloqueo );
	}

	responder( 204 );

}
