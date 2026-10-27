<?php

declare ( strict_types = 1 );

/**
 * Valida e normaliza un enderezo de correo electrónico, comprobando o formato do nome local,
 * a presenza de arroba, a estrutura de subdominios e a extensión válida.
 *
 * @param string  $correo  Enderezo de correo electrónico a validar.
 * @return string O correo electrónico validado e normalizado.
 * @throws Exception Se o correo é demasiado curto, non ten formato válido ou contén caracteres non permitidos.
 */
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

// Escapar
/**
 * Comproba se unha cadea de texto ten unha codificación UTF-8 válida.
 *
 * @param string  $string  A cadea de texto a comprobar.
 * @return int|null 1 se é UTF-8 válida, ou null se está baleira.
 * @throws LogicException Se a cadea contén caracteres non UTF-8.
 */
function revisar_utf8( string $string ) {

    if ( strlen( $string ) == 0 ) {
       return;
    }

    if ( preg_match( '/^./us', $string ) ) {
       return 1;
    }

    throw new LogicException( message: 'Fallou a validación de ' . $string . ' porque contén caracteres non UTF-8' );

}

// Sanitizar
/**
 * Sanitiza unha clave ou identificador, eliminando calquera carácter que non sexa
 * unha letra minúscula, número, guión ou guión baixo.
 *
 * @param string|int  $clave  A clave ou identificador a sanitizar.
 * @return string A clave sanitizada.
 */
function sanitizar_clave( string|int $clave ) {
    return preg_replace( '/[^a-z0-9_-]/', '', $clave );
}

/**
 * Sanitiza un campo de texto eliminando etiquetas HTML, saltos de liña, tabulacións
 * e espazos duplicados, validando ademais que estea en formato UTF-8.
 *
 * @param string  $contido  O texto a sanitizar.
 * @return string O texto limpo e sanitizado.
 */
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

/**
 * Constrúe e devolve unha cadea de salgado (salt) combinada coa clave secreta
 * do sistema para un grupo específico.
 *
 * @param string  $grupo  O nome do grupo de salgado (ex. 'sesion').
 * @return string A cadea completa de salgado e claves combinadas.
 * @throws OutOfRangeException Se o grupo de salgado indicado non existe.
 */
function salgado( string $grupo ) {

    $salgados = [ // claves de 64 caracteres
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

/**
 * Xera un número enteiro aleatorio seguro dende o punto de vista criptográfico
 * entre un valor mínimo e máximo establecidos.
 *
 * @param int  $min  Valor mínimo (por defecto 0).
 * @param int  $max  Valor máximo (por defecto 0, que se mapea a PHP_INT_MAX).
 * @return int O número aleatorio xerado.
 */
function num_rand( int $min = 0, int $max = 0 ) {
    $max = $max > 0 ? $max : PHP_INT_MAX;
    return random_int( $min, $max );
}

/**
 * Xera un token aleatorio seguro dunha lonxitude determinada,
 * permitindo opcionalmente incluír caracteres especiais.
 *
 * @param int   $lonxitude  Lonxitude do token a xerar (por defecto 16).
 * @param bool  $especiais  Indica se se deben incluír caracteres especiais (por defecto true).
 * @return string O token alfanumérico xerado.
 */
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

// Cifrado de información
/**
 * Cifra ou descifra información utilizando o algoritmo AES-256-CBC,
 * con soporte para codificación Base64 e claves asociadas a usuarios ou grupos.
 *
 * @param string    $contido     O texto ou datos a cifrar/descifrar.
 * @param string    $modo        Modo de operación ('cifrar', 'descifrar', 'cifrar_64', 'descifrar_64').
 * @param string    $nome_clave  Nome do grupo de clave de cifrado.
 * @param ?Usuario  $usuario     Obxecto Usuario opcional para contextualizar a clave.
 * @return mixed    O resultado do cifrado/descifrado ou null se o modo non é válido.
 */
function cifrar_descifrar( string $contido, string $modo, string $nome_clave, ?Usuario $usuario = null ) {

    $metodo = 'AES-256-CBC';
    $hash = hash( 'sha256', coller_clave_cifrado( $nome_clave, $usuario ) );
    $iv = substr( $hash, 0, 16 );

    // A codificación 64 bits emprégase para evitar que se corrompan os datos se se gardan nun arquivo ou nunha base de datos
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

/**
 * Recupera a clave de cifrado e o salgado axeitados para un grupo específico,
 * tendo en conta se existe un usuario asociado para personalizar o derivado.
 *
 * @param string    $grupo    Nome do grupo de cifrado (ex. 'sesion', 'correos').
 * @param ?Usuario  $usuario  Obxecto Usuario opcional.
 * @return string A clave de cifrado xerada e combinada.
 * @throws OutOfRangeException Se a clave de cifrado co nome indicado non existe.
 */
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