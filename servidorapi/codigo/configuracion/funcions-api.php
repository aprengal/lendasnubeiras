<?php

declare ( strict_types = 1 );

/**
 * Funcións comúns que empregan todos os endpoints da API.
 * 
 */

/**
 * Envía a resposta da API e remata a execución.
 * 
 * Se non hai datos (por exemplo, nun 204) non se envía corpo.
 */
function responder( int $codigo, ?array $datos = null ): never {

	http_response_code( $codigo );

	if ( $datos !== null ) {
		echo json_encode( $datos, JSON_UNESCAPED_SLASHES | JSON_UNESCAPED_UNICODE | JSON_THROW_ON_ERROR );
	}

	exit;

}

/**
 * Comproba que a petición use o método HTTP que espera o endpoint.
 * Se non coincide, responde cun 405.
 */
function esixir_metodo( string $metodo ) {

	$metodo_actual = $_SERVER[ 'REQUEST_METHOD' ] ?? '';

	if ( $metodo_actual !== $metodo ) {
		header( 'Allow: ' . $metodo );
		responder( 405 );
	}

}

/**
 * Le o corpo da petición, que debe ser un JSON. Se non o é, responde cun 400.
 */
function coller_corpo(): array {

	$contido = file_get_contents( 'php://input' );

	if ( !$contido || strlen( $contido ) > 20000 ) {
		responder( 400 );
	}

	try {
		$corpo = json_decode( $contido, true, 8, JSON_THROW_ON_ERROR );
	} catch ( JsonException ) {
		responder( 400 );
	}

	if ( !is_array( $corpo ) ) {
		responder( 400 );
	}

	return $corpo;

}

/**
 * Valida os campos do corpo segundo unhas regras e devolve só os campos validados.
 * Se falta un campo obrigatorio ou algún é incorrecto, responde cun 400.
 * 
 * Cada regra ten un 'tipo' (enteiro, texto, correo ou uuid) e, opcionalmente,
 * 'min', 'max', 'valores' (lista de valores admitidos) e 'opcional'.
 */
function validar_corpo( array $corpo, array $campos ): array {

	$validado = [];

	foreach ( $campos as $nome => $regras ) {

		if ( !isset( $corpo[ $nome ] ) ) {

			if ( empty( $regras[ 'opcional' ] ) ) {
				responder( 400 );
			}

			continue;

		}

		$validado[ $nome ] = validar_valor( $corpo[ $nome ], $regras );

	}

	return $validado;

}

function validar_valor( mixed $valor, array $regras ) {

	switch ( $regras[ 'tipo' ] ) {

		case 'enteiro':

			if ( !is_int( $valor ) ) {
				responder( 400 );
			}

			if ( isset( $regras[ 'min' ] ) && $valor < $regras[ 'min' ] ) {
				responder( 400 );
			}

			if ( isset( $regras[ 'max' ] ) && $valor > $regras[ 'max' ] ) {
				responder( 400 );
			}

			if ( isset( $regras[ 'valores' ] ) && !in_array( $valor, $regras[ 'valores' ], true ) ) {
				responder( 400 );
			}

			return $valor;

		case 'texto':

			if ( !is_string( $valor ) ) {
				responder( 400 );
			}

			$valor = trim( $valor );

			if ( $valor === '' || mb_strlen( $valor ) > ( $regras[ 'max' ] ?? 255 ) ) {
				responder( 400 );
			}

			if ( isset( $regras[ 'valores' ] ) && !in_array( $valor, $regras[ 'valores' ], true ) ) {
				responder( 400 );
			}

			return $valor;

		case 'correo':

			if ( !is_string( $valor ) ) {
				responder( 400 );
			}

			try {
				return validar_correo( mb_strtolower( trim( $valor ) ) );
			} catch ( Exception ) {
				responder( 400 );
			}

		case 'uuid':

			if ( !es_uuid( $valor ) ) {
				responder( 400 );
			}

			return strtolower( $valor );

		default:
			throw new InvalidArgumentException( 'Tipo de campo descoñecido: ' . $regras[ 'tipo' ] );

	}

}

/**
 * Indica se un valor é un UUID con formato correcto.
 */
function es_uuid( mixed $valor ): bool {
	return is_string( $valor ) && preg_match( '/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i', $valor ) === 1;
}

/**
 * Crea o nome dun dato temporal a partir dun grupo e dun valor.
 * O valor non se garda en claro, senón co seu hash.
 */
function nome_dato( string $grupo, string $valor ): string {
	return $grupo . '_' . hash( 'sha256', $valor );
}

function hai_dato_temporal( string $nome ): bool {
	return !in_array( coller_dato_temporal( $nome ), [ false, '' ], true );
}

/**
 * Rexistra un intento para limitar a frecuencia dunha acción.
 * 
 * Cada intento é un dato temporal que dura o tempo indicado, polo que a
 * ventá é móbil: se hai $maximo intentos sen caducar, non se permite outro.
 * 
 * Devolve true se o intento está permitido e false se se superou o límite.
 */
function reservar_intento( string $grupo, string $valor, int $maximo, int $duracion ): bool {

	for ( $i = 1; $i <= $maximo; $i++ ) {

		$nome = nome_dato( $grupo . '_' . $i, $valor );

		if ( !hai_dato_temporal( $nome ) ) {
			crear_dato_temporal( $nome, '1', $duracion );
			return true;
		}

	}

	return false;

}
