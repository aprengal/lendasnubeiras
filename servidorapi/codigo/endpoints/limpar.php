<?php

declare ( strict_types = 1 );

/**
 * GET /limpar
 * 
 * Executa as tarefas programadas. Non require sesión. Se xa hai unha execución en curso, responde cun 409.
 * 
 * Tarefas:
 * - Elimina os datos temporais caducados.
 * - Borra de forma definitiva as contas que pediron o borrado hai máis de DIAS_BORRADO_CONTA días.
 * - Baleira a papeleira: elimina de forma definitiva as actividades que levan máis de DIAS_PAPELEIRA días no estado borrado.
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
        $limiteTempoContas = time() - ( DIAS_BORRADO_CONTA * DIA_EN_SEGUNDOS );

        db()->delete( 'usuarios', [
            'onde' => [
                'estado' => ESTADO_USUARIO_BORRADO,
                'data_modificado' => [ 'operador' => '<', 'valor' => $limiteTempoContas ]
            ]
        ] );

    } finally {
        borrar_dato_temporal( $bloqueo );
    }

    responder( 204 );

}