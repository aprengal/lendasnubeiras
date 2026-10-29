<?php

declare ( strict_types = 1 );

/**
 * Funcións comúns que empregan todos os endpoints da API.
 */

/**
 * Envía a resposta da API e remata a execución.
 *
 * Se non hai datos (por exemplo, nun 204) non se envía corpo.
 *
 * @param int         $codigo  Código de estado HTTP da resposta.
 * @param ?array      $datos   Datos opcionais a codificar en JSON.
 * @return never
 */
function responder( int $codigo, ?array $datos = null ): void {

    http_response_code( $codigo );

    if ( $datos !== null ) {
       echo json_encode( $datos, JSON_UNESCAPED_SLASHES | JSON_UNESCAPED_UNICODE | JSON_THROW_ON_ERROR );
    }

    exit;

}

/**
 * Comproba que a petición use o método HTTP que espera o endpoint.
 * Se non coincide, responde cun 405 (Method Not Allowed).
 *
 * @param string  $metodo  Método HTTP esperado (ex. 'GET', 'POST').
 */
function esixir_metodo( string $metodo ) {

    $metodo_actual = $_SERVER[ 'REQUEST_METHOD' ] ?? '';

    if ( $metodo_actual !== $metodo ) {
       header( 'Allow: ' . $metodo );
       responder( 405 );
    }

}

/**
 * Le o corpo da petición, que debe ser un JSON.
 * Se a lonxitude supera os 20.000 bytes, responde cun 413.
 * Se o formato é incorrecto ou contido está baleiro, devólvese un código 400
 *
 * @return array O corpo da petición decodificado como array asociativo.
 */
function coller_corpo(): array {

    $contido = file_get_contents( 'php://input' );

    if ( !$contido || strlen( $contido ) > 20000 ) {
       responder( 400 );
    }

    if ( strlen( $contido ) > 20000 ) {
       responder( 413 );
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
 *
 * @param array  $corpo   Array asociativo coller do corpo da petición.
 * @param array  $campos  Regras de validación para cada campo.
 * @return array O array cos datos xa validados.
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

/**
 * Valida un valor individual en función do tipo e regras especificadas.
 * Responde cun 400 se non cumpre as condicións ou lanza unha excepción se o tipo é descoñecido.
 *
 * @param mixed  $valor   O valor que se desexa validar.
 * @param array  $regras  Regras de validación aplicables ao valor.
 * @return mixed O valor validado e transformado (se procede).
 * @throws InvalidArgumentException Se o tipo de campo indicado non está definido.
 */
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
 *
 * @param mixed  $valor  O valor a comprobar.
 * @return bool  True se ten formato UUID válido, false en caso contrario.
 */
function es_uuid( mixed $valor ): bool {
    return is_string( $valor ) && preg_match( '/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i', $valor ) === 1;
}

/**
 * Crea o nome dun dato temporal a partir dun grupo e dun valor.
 * O valor non se garda en claro, senón co seu hash sha256.
 *
 * @param string  $grupo  Nome do grupo ou categoría temporal.
 * @param string  $valor  Valor orixinal a cifrar en hash.
 * @return string O identificador único e seguro do dato temporal.
 */
function nome_dato( string $grupo, string $valor ): string {
    return $grupo . '_' . hash( 'sha256', $valor );
}

/**
 * Comproba se existe un dato temporal activo no sistema.
 *
 * @param string  $nome  Identificador do dato temporal.
 * @return bool   True se existe e ten valor, false en caso contrario.
 */
function hai_dato_temporal( string $nome ): bool {
    return !in_array( coller_dato_temporal( $nome ), [ false, '' ], true );
}

/**
 * Rexistra un intento para limitar a frecuencia dunha acción (rate limiting).
 *
 * Cada intento é un dato temporal que dura o tempo indicado, polo que a
 * ventá é móbil: se hai $maximo intentos sen caducar, non se permite outro.
 *
 * @param string  $grupo     Grupo de control de intentos.
 * @param string  $valor     Identificador único do usuario ou IP.
 * @param int     $maximo    Número máximo de intentos permitidos na ventá de tempo.
 * @param int     $duracion  Duración en segundos de cada intento temporal.
 * @return bool   True se o intento está permitido, false se se superou o límite.
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
