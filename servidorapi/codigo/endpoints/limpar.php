<?php

declare( strict_types = 1 );

/**
 * GET /limpar
 *
 * Executa as tarefas programadas de mantemento do servidor.
 *
 * Non require autenticación. Impide a execución simultánea mediante
 * un bloqueo temporal que caduca automaticamente aos 5 minutos.
 *
 * Tarefas:
 * - Elimina os datos temporais cuxa data de caducidade xa expirou.
 * - Elimina definitivamente as actividades que levan máis de 30 días
 *   no estado borrado.
 * - Elimina definitivamente as contas cuxo período de borrado
 *   superou os 30 días.
 *
 * O bloqueo elimínase ao finalizar a execución, mesmo se se produce
 * unha excepción durante as tarefas.
 *
 * @return void
 *
 * Respostas: 204, 409
 */
function endpoint_limpar(): void {

    esixir_metodo( 'GET' );

    $bloqueo = 'limpar_en_curso';

    if ( (int) coller_dato_temporal( $bloqueo ) ) {
        responder( 409 );
    }

    // O bloqueo caduca só aos 5 minutos por se a execución falla a metade
    crear_dato_temporal( $bloqueo, 1, 5 * MINUTO_EN_SEGUNDOS );

    try {

        // 1. Elimina os datos temporais caducados
        db()->delete( 'info_temporal', [
            'onde' => [ 'data_caducidade' => [ 'operador' => '<', 'valor' => time() ] ]
        ] );

        // 2. Baleira a papeleira de actividades caducadas
        db()->delete( 'actividades', [
            'onde' => [
                'estado' => ESTADO_ACTIVIDADE_BORRADA,
                'data_modificado' => [ 'operador' => '<', 'valor' => time() - ( DIAS_PAPELEIRA * DIA_EN_SEGUNDOS ) ]
            ]
        ] );

        // 3. Borra de forma definitiva as contas caducadas baseándose no estado e no tempo
        borrar_contas_caducadas();

    } finally {
        borrar_dato_temporal( $bloqueo );
    }

    responder( 204 );

}