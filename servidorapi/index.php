<?php

declare ( strict_types = 1 );

//Carga de configuración
error_reporting( E_ALL );
mb_internal_encoding( 'UTF-8' );
ini_set( 'memory_limit', '256M' );

//Constantes iniciais antes da primeira carga
const NOME = 'Lendas Nubeiras';
const DOMINIO = 'lendasnubeiras.duckdns.org';
const BASE = __DIR__;
define( 'ORIXE', dirname( __DIR__, 1 ) . '/' );

ini_set( "error_log", ORIXE . "error_log" );

header( 'Content-Type: application/json; charset=utf-8' );

require_once "codigo/carga.php";