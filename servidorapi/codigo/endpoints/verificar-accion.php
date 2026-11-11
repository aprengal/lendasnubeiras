<?php

declare( strict_types = 1 );

/**
 * GET /accion/verificacion
 *
 * Verifica unha acción de rexistro, inicio de sesión ou borrado de conta
 * mediante os datos incluídos na ligazón recibida por correo electrónico.
 *
 * Comproba a validez do token, a existencia da acción e a correspondencia
 * do UUID do dispositivo. Unha vez superadas estas comprobacións, elimina
 * o dato temporal antes de executar a operación para garantir o seu uso único.
 *
 * Se a verificación falla, amosa unha páxina HTML de erro cun código HTTP 409.
 * Se se produce un erro inesperado no servidor, amosa unha páxina HTTP 500.
 * Se todo remata correctamente, amosa unha páxina de éxito cun código HTTP 200.
 */
function endpoint_verificar_accion(): void {

    esixir_metodo( 'GET' );

    $idioma = $_GET[ 'idioma' ] ?? 'es';
    $idioma = is_string( $idioma ) && in_array( $idioma, [ 'gl', 'es', 'en' ], true ) ? $idioma : 'es';

    try {

        $id_accion = $_GET[ 'id_accion' ] ?? '';
        $uuid_cifrado = $_GET[ 'uuid' ] ?? '';

        if (
            !is_string( $id_accion ) ||
            preg_match( '/^[a-zA-Z0-9]{40}$/', $id_accion ) !== 1 ||
            !is_string( $uuid_cifrado ) ||
            empty( $uuid_cifrado )
        ) {
            amosar_pantalla_erro( $idioma );
        }

        $uuid = cifrar_descifrar( $uuid_cifrado, 'descifrar', 'correos' );

        if ( !verificar_uuid( $uuid ) ) {
            amosar_pantalla_erro( $idioma );
        }

        // Recuperar os datos temporais da acción
        $nome = nome_dato( 'accion', $id_accion );
        $dato = coller_dato_temporal( $nome );

        if ( !is_string( $dato ) || !hai_serializacion( $dato ) ) {
            amosar_pantalla_erro( $idioma );
        }

        $accion = unserialize( $dato, [ 'allowed_classes' => false ] );

        // Comprobar a estrutura e o dispositivo asociado á acción
        if (
            !is_array( $accion ) ||
            !isset( $accion[ 'tipo' ], $accion[ 'uuid' ] ) ||
            !is_string( $accion[ 'uuid' ] ) ||
            !hash_equals( $accion[ 'uuid' ], strtolower( $uuid ) )
        ) {
            amosar_pantalla_erro( $idioma );
        }

        // Consumir o token antes de executar a operación
        borrar_dato_temporal( $nome );

        // Executar a operación correspondente
        $verificada = match ( $accion[ 'tipo' ] ) {
            'rexistro' => verificar_rexistro( $accion ),
            'inicio'   => verificar_inicio( $accion ),
            'borrado'  => verificar_borrado( $accion ),
            default    => false
        };

        if ( !$verificada ) {
            amosar_pantalla_erro( $idioma );
        }

        amosar_pantalla_exito( $idioma );

    } catch ( Throwable $problema ) {
        rexistrar_problema( $problema );
        amosar_pantalla_erro( $idioma, 500 );
    }

}

/**
 * Verifica unha solicitude de rexistro e crea o usuario correspondente
 * en estado pendente de aprobación.
 *
 * Comproba que o correo estea presente nos datos da acción e que non
 * exista xa un usuario rexistrado con ese enderezo.
 *
 * @param array $accion Datos temporais da acción, incluído o correo electrónico.
 * @return bool True se o rexistro se realizou correctamente, false se non é válido.
 */
function verificar_rexistro( array $accion ): bool {

    if ( empty( $accion[ 'correo' ] ) || !is_string( $accion[ 'correo' ] ) ) {
        return false;
    }

    if ( coller_usuario( 'correo', $accion[ 'correo' ] )->existe() ) {
        return false;
    }

    crear_usuario_pendente( $accion[ 'correo' ] );

    return true;

}

/**
 * Verifica unha solicitude de inicio de sesión e activa a sesión do usuario.
 *
 * Comproba que existan o UUID e o token nos datos da acción, que o usuario
 * estea rexistrado e activo e que a activación da sesión se realice correctamente.
 *
 * @param array $accion Datos temporais da acción, incluídos o UUID do usuario e o token.
 * @return bool True se a sesión se activou correctamente, false en caso contrario.
 */
function verificar_inicio( array $accion ): bool {

    if (
        empty( $accion[ 'usuario' ] ) ||
        !is_string( $accion[ 'usuario' ] ) ||
        empty( $accion[ 'token' ] ) ||
        !is_string( $accion[ 'token' ] )
    ) {
        return false;
    }

    $usuario = coller_usuario( 'uuid', $accion[ 'usuario' ], false );

    if ( !$usuario->existe() || $usuario->estado !== ESTADO_USUARIO_ACTIVO ) {
        return false;
    }

    return $usuario->activar_sesion( $accion[ 'token' ] );

}

/**
 * Verifica unha solicitude de borrado e actualiza o estado da conta.
 *
 * Só permite borrar contas existentes e activas que non pertenzan
 * a administradores nin correspondan a usuarios legados.
 *
 * Unha vez actualizado o estado da conta, destrúe todas as súas sesións activas.
 *
 * @param array $accion Datos temporais da acción, incluído o identificador do usuario.
 * @return bool True se a conta se marcou correctamente para o seu borrado.
 */
function verificar_borrado( array $accion ): bool {

    if ( !isset( $accion[ 'id' ] ) || !is_numeric( $accion[ 'id' ] ) ) {
        return false;
    }

    $usuario = coller_usuario( 'id', (int) $accion[ 'id' ], false );

    if (
        !$usuario->existe() ||
        $usuario->estado !== ESTADO_USUARIO_ACTIVO ||
        $usuario->rol === Rol::ADMINISTRADOR ||
        usuario_legado( $usuario )
    ) {
        return false;
    }

    $filas = db()->update( 'usuarios', [
        'nova_info' => [
            'estado' => ESTADO_USUARIO_BORRADO,
            'data_borrado' => time()
        ],
        'onde' => [
            'id' => $usuario->id
        ],
        'limit' => 1
    ] );

    if ( $filas !== 1 ) {
        return false;
    }

    $usuario->destruir_sesions();

    return true;

}

/**
 * Amosa unha páxina HTML indicando que a verificación se realizou correctamente
 * e detén a execución do script.
 *
 * @param string $idioma Idioma da mensaxe: gl, es ou en.
 * @return never
 */
function amosar_pantalla_exito( string $idioma ): never {

    $mensaxes = [
        'gl' => 'Acción realizada correctamente. Xa podes pechar esta ventá.',
        'es' => 'Acción realizada correctamente. Ya puedes cerrar esta ventana.',
        'en' => 'Action completed successfully. You can now close this window.'
    ];

    amosar_pantalla( $mensaxes[ $idioma ] ?? $mensaxes[ 'es' ], 200, $idioma );

}

/**
 * Amosa unha páxina HTML indicando que a verificación non se puido completar
 * e detén a execución do script.
 *
 * Non revela se o token existe, caducou, xa foi utilizado ou se os datos
 * asociados á acción son incorrectos.
 *
 * @param string $idioma Idioma da mensaxe: gl, es ou en.
 * @param int $codigo Código HTTP da resposta. Por defecto, 409.
 * @return never
 */
function amosar_pantalla_erro( string $idioma, int $codigo = 409 ): never {

    $mensaxes = [
        'gl' => 'Non foi posible completar a acción. A ligazón pode ser incorrecta, caducada ou xa utilizada. Se o problema continúa, solicita unha nova.',
        'es' => 'No ha sido posible completar la acción. El enlace puede ser incorrecto, haber caducado o haber sido utilizado. Si el problema continúa, solicita uno nuevo.',
        'en' => 'The action could not be completed. The link may be invalid, expired or already used. If the problem persists, request a new one.'
    ];

    if ( !in_array( $codigo, [ 409, 500 ], true ) ) {
        $codigo = 409;
    }

    if ( $codigo === 500 ) {
        $mensaxes = [
            'gl' => 'Produciuse un erro interno no servidor. Téntao de novo máis tarde.',
            'es' => 'Se ha producido un error interno en el servidor. Inténtalo de nuevo más tarde.',
            'en' => 'An internal server error occurred. Please try again later.'
        ];
    }

    amosar_pantalla( $mensaxes[ $idioma ] ?? $mensaxes[ 'es' ], $codigo, $idioma );

}

/**
 * Amosar unha pantalla co resultado dunha verificación.
 *
 * Xera e amosa unha páxina HTML co título, a icona e a mensaxe
 * correspondentes ao resultado da verificación, incluíndo a precarga
 * da folla de estilos.
 *
 * O título e a icona determínanse en función do código HTTP:
 * - 200: verificación correcta.
 * - Calquera outro código: erro na verificación.
 *
 * @param string $mensaxe Mensaxe que se amosará na pantalla.
 * @param int    $codigo  Código de estado HTTP da resposta.
 * @param string $idioma  Código do idioma da páxina ('gl', 'es' ou 'en').
 * @return never
 */
function amosar_pantalla( string $mensaxe, int $codigo, string $idioma ): never {

    $titulos = [
        'gl' => $codigo === 200 ? 'Verificación correcta' : 'Erro na verificación',
        'es' => $codigo === 200 ? 'Verificación correcta' : 'Error en la verificación',
        'en' => $codigo === 200 ? 'Verification successful' : 'Verification error'
    ];

    $titulo = $titulos[ $idioma ] ?? $titulos[ 'es' ];

    $titulo_html = htmlspecialchars( $titulo, ENT_QUOTES | ENT_SUBSTITUTE, 'UTF-8' );
    $mensaxe_html = htmlspecialchars( $mensaxe, ENT_QUOTES | ENT_SUBSTITUTE, 'UTF-8' );
    $idioma_html = htmlspecialchars( $idioma, ENT_QUOTES | ENT_SUBSTITUTE, 'UTF-8' );

    header( 'Content-Type: text/html; charset=UTF-8' );
    http_response_code( $codigo );

   $clase_icona = $codigo === 200 ? 'checkmark' : 'error-mark';
   $simbolo = $codigo === 200 ? '&#10003;' : '&#10007;';

   echo '<!DOCTYPE html>
   <html lang="' . $idioma_html . '">
   <head>
       <meta charset="UTF-8">
       <meta name="viewport" content="width=device-width, initial-scale=1.0">
       <title>' . $titulo_html . '</title>
       <link rel="preload" href="/css/estilo.css" as="style">
       <link rel="stylesheet" href="/css/estilo.css">
   </head>
   <body>
       <div class="card">
           <div class="' . $clase_icona . '">' . $simbolo . '</div>
           <h1>' . $titulo_html . '</h1>
           <p>' . $mensaxe_html . '</p>
       </div>
   </body>
   </html>';

    exit;

}