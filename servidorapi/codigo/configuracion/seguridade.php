<?php

declare ( strict_types = 1 );

function validar_correo( string $correo ) {

	$correo = trim( $correo );

	if ( strlen( $correo ) < 5 ) {
		throw new Exception( 'Tratouse de introducir un correo demasiado curto.' );
	}

	$orixinal = $correo;
	$partes = explode( '@', $correo );

	$local = preg_replace( '/[^a-zA-Z0-9!#$%&\'*+\/=?^_`{|}~\.-]/', '', $partes[ 0 ] );
	$local = trim( preg_replace( '/\.{2,}/', '.', $local ), '.' );

	if ( !$local ) {
		throw new Exception( 'Tratouse de introducir un correo sen local.' );
	}

	if ( !isset( $partes[ 1 ] ) ) {
		throw new Exception( 'Tratouse de introducir un correo sen @' );
	}

	$dominio = preg_replace( '/\.{2,}/', '', $partes[ 1 ] );

	if ( !$dominio ) {
		throw new Exception( 'Tratouse de introducir un correo sen dominio.' );
	}

	$dominio = trim( $dominio, " \t\n\r\0\x0B." );
	$subdominios = explode( '.', $dominio );
	$sub_valido = [];

	foreach ( $subdominios as $subdominio ) {

		$subdominio = trim( $subdominio, " \t\n\r\0\x0B-" );
		$subdominio = preg_replace( '/[^a-zA-Z0-9-]+/', '', $subdominio );

		if ( '' !== $subdominio ) {
			$sub_valido[] = $subdominio;
		}

	}

	if ( 2 > count( $sub_valido ) ) {
		throw new Exception( 'Tratouse de introducir un correo sen punto na súa extensión (.com, .es...).' );
	}

	$dominio = implode( '.', $sub_valido );
	$correo_final = $local . '@' . $dominio;

	if ( $orixinal !== $correo_final ) {
		error_log( 'Denegouse un correo electrónico. Orixinal: ' . $orixinal . ' // Revisado: ' . $correo_final );
		throw new Exception( 'Insertouse un correo electrónico que contiña caracteres non válidos' );
	}

	return $correo_final;

}

//Escapar
function revisar_utf8( string $string ) {

	if ( strlen( $string ) == 0 ) {
		return;
	}

	if ( preg_match( '/^./us', $string ) ) {
		return 1;
	}

	throw new LogicException( message: 'Fallou a validación de ' . $string . ' porque contén caracteres non UTF-8' );

}

//Sanitizar
function sanitizar_clave( string|int $clave ) {
	return preg_replace( '/[^a-z0-9_-]/', '', $clave );
}

function sanitizar_campo( string $contido ) {

	if ( $contido === '' || !revisar_utf8( $contido ) ) {
		error_log( 'Invalidouse un campo porque estaba baleiro ou contiña carácteres non UTF8' );
		return '';
	}

	$contido = strip_tags( $contido );
	$contido = str_replace( [ "\r", "\n", "\t" ], '', $contido );

	while ( str_contains( $contido, '  ' ) ) {
		$contido = str_replace( '  ', ' ', $contido );
	}

	return $contido;

}

function salgado( string $grupo ) {

	$salgados = [ //claves de 64 caracteres
		'sesion' => [
			'clave' => CLAVE_SESION,
			'sal'   => SALGADO_SESION
		]
	];

	if ( !isset( $salgados[ $grupo ] ) ) {
		throw new OutOfRangeException( 'O grupo de salgado ' . $grupo . ' non existe' );
	}

	$clave_secreta = explode( '%', coller_opcion( 'clave_secreta' ) );

	return $clave_secreta[ 0 ] .  $salgados[ $grupo ][ 'clave' ] . $salgados[ $grupo ][ 'sal' ] . $clave_secreta[ 1 ];

}

function num_rand( int $min = 0, int $max = 0 ) {
	$max = $max > 0 ? $max : PHP_INT_MAX;
	return random_int( $min, $max );
}

function xerar_token( int $lonxitude = 16, bool $especiais = true ) {

	$lista = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';

	if ( $especiais ) {
		$lista .= '!@#$%"^&*()';
	}

	$contrasinal = '';
	$lonx_lista = mb_strlen( $lista ) - 1;

	for ( $i = 0; $i < $lonxitude; $i++ ) {
		$numero = num_rand( 0, $lonx_lista );
		$contrasinal .= mb_substr( $lista, $numero, 1 );
	}

	return $contrasinal;

}

//Cifrado de información
//Tomado como referencia de https://gist.github.com/oropesa/8709641686bfb565c9f8f1bcf4c24969
function cifrar_descifrar( string $contido, string $modo, string $nome_clave, ?Usuario $usuario = null ) {

	$metodo = 'AES-256-CBC';
	$hash = hash( 'sha256', coller_clave_cifrado( $nome_clave, $usuario ) );
	$iv = substr( $hash, 0, 16 );

	//A codificación 64 bits emprégase para evitar que se corrompan os datos se se gardan nun arquivo ou nunha base de datos
	switch ( $modo ) {
		case 'cifrar':
			return openssl_encrypt( $contido, $metodo, $hash, 0, $iv );
		case 'descifrar':
			return openssl_decrypt( $contido, $metodo, $hash, 0, $iv );
		case 'cifrar_64':
			return base64_encode( openssl_encrypt( $contido, $metodo, $hash, 0, $iv ) );
		case 'descifrar_64':
			return openssl_decrypt( base64_decode( $contido, true ), $metodo, $hash, 0, $iv );
		default:
			return;
	}

}

function coller_clave_cifrado( string $grupo, ?Usuario $usuario ) {

	$claves = [
		'sesion'    => [
			'clave' => CIFRADO_CLAVE_SESION,
			'sal'   => CIFRADO_SALGADO_SESION
		],
		'correos'   => [
			'clave' => CIFRADO_CLAVE_CORREOS,
			'sal'   => CIFRADO_SALGADO_CORREOS
		]
	];

	if ( !isset( $claves[ $grupo ] ) ) {
		throw new OutOfRangeException( 'A clave de cifrado co nome ' . $grupo . ' non existe' );
	}

	if ( $usuario && $usuario->existe() ) {
		return $usuario->id . $claves[ $grupo ][ 'clave' ] . $usuario->id . $claves[ $grupo ][ 'sal' ];
	}

	$clave_secreta = explode( '%', coller_opcion( 'clave_secreta' ) );

	return $clave_secreta[ 0 ] . $claves[ $grupo ][ 'clave' ] . $claves[ $grupo ][ 'sal' ] . $clave_secreta[ 1 ];

}