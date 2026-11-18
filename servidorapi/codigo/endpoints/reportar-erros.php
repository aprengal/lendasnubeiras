<?php

declare( strict_types = 1 );


/**
 * POST /reportar-erros
 *
 * Rexistra no servidor un erro comunicado pola aplicación Android.
 *
 * Os reportes almacénanse separados do rexistro de erros internos de PHP.
 *
 * Cada reporte gárdase nunha liña JSON coa marca temporal
 * e a descrición do erro.
 *
 * As descricións límpanse e limítanse a 18000 caracteres.
 *
 * Respostas: 200 (reporte rexistrado), 400 (datos incorrectos),
 * 429 (límite alcanzado), 500 (erro ao gardar o reporte).
 */
function endpoint_reportar_erros(): void {

    esixir_metodo( 'POST' );

    $campos = [ 'uuid' => [ 'tipo' => 'uuid' ], 'erro' => [ 'tipo' => 'texto', 'max' => 18000 ] ];
    $datos = validar_corpo( coller_corpo(), $campos );
    $identificador = $datos[ 'uuid' ];

    if ( !revisar_intentos( 'reportes', $identificador, 1, MINUTO_EN_SEGUNDOS ) ) {
        responder( 429 );
    }

    $erro = sanitizar_campo( $datos[ 'erro' ] );

    if ( $erro === '' ) {
        responder( 400 );
    }

    if ( mb_strlen( $erro ) > 2000 ) {
        $erro = mb_substr( $erro, 0, 2000 ) . ' [TRUNCADO]';
    }

    $directorio = ORIXE . 'reportes/';

    $reporte = [ 'data' => time(), 'erro' => $erro ];
    $liña = json_encode( $reporte, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES );

    if ( $liña === false || file_put_contents( $directorio . 'reportes.txt', $liña . PHP_EOL, FILE_APPEND | LOCK_EX ) === false ) {
        responder( 500 );
    }

    responder( 200 );

}