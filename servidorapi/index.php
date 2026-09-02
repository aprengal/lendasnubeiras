<?php

header( 'Content-Type: application/json; charset=utf-8' );

if ( empty( $_SERVER[ 'REQUEST_URI' ] ) ) {

    http_response_code( 404 );

    $resposta = [ "mensaxe" => "O recurso solicitado non existe" ];

    echo json_encode( $resposta );
    exit;

}

http_response_code( 404 );

$ruta = parse_url( $_SERVER[ 'REQUEST_URI' ], PHP_URL_PATH );

$resposta = [ "mensaxe" => "O recurso solicitado " . $ruta . " non existe" ];

echo json_encode( $resposta, JSON_UNESCAPED_SLASHES );