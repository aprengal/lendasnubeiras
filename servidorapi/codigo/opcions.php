<?php //Opcións sempre

declare ( strict_types = 1 );

/**
 * Devolve o valor dunha opción, ou false se non existe.
 * 
 * Cada opción lese unha soa vez por petición, porque se consulta moitas veces
 * (por exemplo, a clave secreta en cada hash de sesión). A memoria só dura ata que remata a petición,
 * así que, se unha opción se cambia, o novo valor vese na petición seguinte.
 */
function coller_opcion( string $nome ) {

	static $opcions = [];

	if ( array_key_exists( $nome, $opcions ) ) {
		return $opcions[ $nome ];
	}

	$args = [
		'columnas' => 'valor',
		'principal' => 'nome',
		'onde' => [ 'nome' => $nome ],
		'limit' => 1
	];

	return $opcions[ $nome ] = db()->select( 'opcions', $args, 'variable' );

}

//Datos temporais
function crear_dato_temporal( string $nome, mixed $valor, int $duracion = 0 ) {

	$db = db();

	if ( $duracion < 0 ) {
		error_log( 'Non se pode crear o dato temporal ' . $nome . ' con duración negativa' );
		return;
	}

	$valor = !is_scalar( $valor ) ? serialize( $valor ) : $valor;
	$caducidade = time() + $duracion;

	$datos_onde = [ 'id' => 0, 'nome' => $nome ];
	$datos_info = [ 'valor' => $valor, 'data_caducidade' => $caducidade ];

	$valor_act = coller_dato_temporal( $nome );

	if ( in_array( $valor_act, [ false, '' ], true ) ) { //Non existe o dato temporal

		$datos = $datos_onde + $datos_info;

		$db->insert( 'info_temporal', $datos, [ 'valor', 'data_caducidade' ] );

	} else {

		$args = [
			'nova_info' => $datos_info,
			'onde' => $datos_onde
		];

		$db->update( 'info_temporal', $args );

	}

}

function coller_dato_temporal( string $nome ) {

	$args = [
		'columnas' => 'valor',
		'onde' => [
			'id' => 0,
			'nome' => $nome,
			'data_caducidade' => [
				'operador' => '>=',
				'valor' => time()
			]
		],
		'limit' => 1
	];

	$valor = db()->select( 'info_temporal', $args, 'variable' );

	return $valor;

}

function borrar_dato_temporal( string $nome ) {

	$datos = [
		'onde' => [
			'id' => 0,
			'nome' => $nome
		],
		'limit' => 1
	];

	db()->delete( 'info_temporal', $datos );

}