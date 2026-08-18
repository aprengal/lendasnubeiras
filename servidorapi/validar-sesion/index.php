<?php

http_response_code( 503 );

$resposta = [ "mensaxe" => "Fun descuberto" ];

echo json_encode( $resposta, JSON_UNESCAPED_SLASHES );