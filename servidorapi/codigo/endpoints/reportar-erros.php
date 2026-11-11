<?php

declare( strict_types = 1 );


/**
 * POST /reportar-erros
 *
 * Rexistra no servidor un erro comunicado pola aplicación Android.
 *
 * Require unha sesión autenticada e recibe o identificador único da petición
 * na que se produciu o erro, xunto coa súa descrición.
 *
 * Os reportes almacénanse en ORIXE . "reportes/reportes.txt", separados
 * do rexistro de erros internos de PHP. Cada reporte gárdase nunha liña
 * en formato JSON, incluíndo a marca temporal Unix, o identificador
 * do usuario, o identificador da petición e a descrición do erro.
 *
 * As descricións límpanse e limítanse a 2000 caracteres.
 *
 * Respostas: 200 (reporte rexistrado), 400 (datos incorrectos),
 * 401 (sesión non válida), 500 (erro ao gardar o reporte).
 */
function endpoint_reportar_erros(): void {

    esixir_metodo( 'POST' );

    $usuario = esixir_sesion();

    $datos = validar_corpo( coller_corpo(), [
        'id_peticion' => [ 'tipo' => 'uuid' ],
        'erro' => [ 'tipo' => 'texto', 'max' => 2000 ]
    ] );

    $erro = sanitizar_campo( $datos[ 'erro' ] );

    if ( $erro === '' ) {
        responder( 400 );
    }

    if ( mb_strlen( $erro ) > 2000 ) {
        $erro = mb_substr( $erro, 0, 2000 ) . ' [TRUNCADO]';
    }

    $directorio = ORIXE . 'reportes/';

    // Crear o directorio se aínda non existe
    if ( !is_dir( $directorio ) && !mkdir( $directorio, 0750, true ) && !is_dir( $directorio ) ) {
        responder( 500 );
    }

    $reporte = [
        'data' => time(),
        'usuario' => $usuario->id,
        'id_peticion' => $datos[ 'id_peticion' ],
        'erro' => $erro
    ];

    $liña = json_encode( $reporte, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES );

    if ( $liña === false || file_put_contents( $directorio . 'reportes.txt', $liña . PHP_EOL, FILE_APPEND | LOCK_EX ) === false ) {
        responder( 500 );
    }

    responder( 200 );

}