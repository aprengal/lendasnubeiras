<?php

http_response_code( 503 );

header( 'Content-Type: application/json; charset=utf-8' );

$resposta = [ "mensaxe" => "Fun descuberto" ];

echo json_encode( $resposta, JSON_UNESCAPED_SLASHES );