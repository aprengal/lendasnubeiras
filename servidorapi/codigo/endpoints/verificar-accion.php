<?php

declare ( strict_types = 1 );

/**
 * GET /accion/verificacion
 * 
 * Endpoint encargado de verificar unha acción (rexistro, inicio de sesión ou borrado de conta)
 * a partir da ligazón recibida por correo polo usuario. Os datos viaxan na URL (id_accion e uuid).
 * 
 * Aplica un sistema de uso único (*one-time use*): valida a estrutura e existencia do token,
 * comproba a coincidencia do UUID do dispositivo, deriva á función de verificación correspondente,
 * elimina o rexistro temporal e amosa a pantalla de éxito. Responde sempre con 409 ante calquera anomalía
 * para non revelar información sensible.
 * 
 * Respostas: 200, 409
 */
function endpoint_verificar_accion(): void {

    esixir_metodo( 'GET' );

    $id_accion = $_GET[ 'id_accion' ] ?? "";
    $uuid = !empty( $_GET[ 'uuid' ] ) ? cifrar_descifrar( $_GET[ 'uuid' ], 'descifrar', 'correos' ) : "";

    if ( !is_string( $id_accion ) || preg_match( '/^[a-zA-Z0-9]{40}$/', $id_accion ) !== 1 || !verificar_uuid( $uuid ) ) {
        responder( 409 );
    }

    $nome = nome_dato( 'accion', $id_accion );
    $dato = coller_dato_temporal( $nome );

    if ( !is_string( $dato ) || !hai_serializacion( $dato ) ) {
        responder( 409 );
    }

    $accion = unserialize( $dato, [ 'allowed_classes' => false ] );

    // O uuid da ligazón ten que ser o do dispositivo que fixo a solicitude
    if ( !is_array( $accion ) || !isset( $accion[ 'tipo' ], $accion[ 'uuid' ] ) || !hash_equals( $accion[ 'uuid' ], strtolower( $uuid ) ) ) {
        responder( 409 );
    }

    switch ( $accion[ 'tipo' ] ) {

        case 'rexistro':
            verificar_rexistro( $accion );
            break;

        case 'inicio':
            verificar_inicio( $accion );
            break;

        case 'borrado':
            verificar_borrado( $accion );
            break;

        default:
            responder( 409 );

    }

    borrar_dato_temporal( $nome );

    $idioma = $_GET[ 'idioma' ] ?? 'es';
    amosar_pantalla_exito( $idioma );

}

/**
 * Verifica un rexistro: valida que o correo estea presente no array de acción,
 * comproba que non exista xa un usuario rexistrado con ese mesmo enderezo
 * e procede a crear o usuario en estado pendente.
 *
 * @param array $accion Array asociativo cos datos temporais da acción (inclúe 'correo').
 */
function verificar_rexistro( array $accion ) {

    if ( !isset( $accion[ 'correo' ] ) ) {
        responder( 409 );
    }

    // O correo xa está rexistrado?
    if ( coller_usuario( 'correo', $accion[ 'correo' ] )->existe() ) {
        responder( 409 );
    }

    crear_usuario_pendente( $accion[ 'correo' ] );

}

/**
 * Verifica un inicio de sesión: valida que existan o UUID e o token no array de acción,
 * comproba que o usuario exista e estea activo no sistema, e procede a activar a sesión
 * correspondente. Responde con 409 se falla calquera comprobación.
 *
 * @param array $accion Array asociativo cos datos temporais da acción (inclúe 'uuid' e 'token').
 */
function verificar_inicio( array $accion ) {

    if ( !isset( $accion[ 'usuario' ], $accion[ 'token' ] ) ) {
        responder( 409 );
    }

    $usuario = coller_usuario( 'uuid', $accion[ 'usuario' ], false );

    if ( !$usuario->existe() || $usuario->estado !== ESTADO_USUARIO_ACTIVO ) {
        responder( 409 );
    }

    if ( !$usuario->activar_sesion( $accion[ 'token' ] ) ) {
        responder( 409 );
    }

}

/**
 * Verifica un borrado de conta: valida que o identificador estea presente no array de acción,
 * comproba que o usuario exista, estea activo, non sexa administrador nin un usuario legado,
 * e finalmente actualiza o seu estado a borrado, rexistra a data e pecha todas as súas sesións activas.
 *
 * @param array $accion Array asociativo cos datos temporais da acción (inclúe 'id').
 */
function verificar_borrado( array $accion ) {

    if ( !isset( $accion[ 'id' ] ) ) {
        responder( 409 );
    }

    $usuario = coller_usuario( 'id', (int) $accion[ 'id' ], false );

    // Só se pode borrar unha conta activa que non sexa de administrador (nin a reservada)
    if ( !$usuario->existe() || $usuario->estado !== ESTADO_USUARIO_ACTIVO || $usuario->rol === Rol::ADMINISTRADOR || usuario_legado( $usuario ) ) {
        responder( 409 );
    }

    db()->update( 'usuarios', [
        'nova_info' => [ 'estado' => ESTADO_USUARIO_BORRADO, 'data_borrado' => time() ],
        'onde' => [ 'id' => $usuario->id ],
        'limit' => 1
    ] );

    $usuario->destruir_sesions();

}

/**
 * Xera e renderiza unha páxina HTML de resposta exitosa adaptada ao idioma indicado,
 * enviando os headers correspondentes e detendo a execución do script.
 *
 * @param string $idioma Código do idioma para a visualización ('gl', 'es', 'en').
 * @return never Esta función detén a execución mediante exit.
 */
function amosar_pantalla_exito( string $idioma ): never {

    $titulos = [
        'gl' => 'Verificación Exitosa',
        'es' => 'Verificación Exitosa',
        'en' => 'Successful Verification'
    ];

    $mensaxes = [
        'gl' => 'Acción realizada correctamente. Xa podes pechar esta ventá.',
        'es' => 'Acción realizada correctamente. Ya puedes cerrar esta ventana.',
        'en' => 'Action completed successfully. You can now close this window.'
    ];

    $titulo = $titulos[ $idioma ] ?? $titulos[ 'es' ];
    $texto = $mensaxes[ $idioma ] ?? $mensaxes[ 'es' ];

    header( 'Content-Type: text/html; charset=UTF-8' );
    http_response_code( 200 );

    echo '<!DOCTYPE html>
    <html lang="' . htmlspecialchars( $idioma ) . '">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>' . htmlspecialchars( $titulo ) . '</title>
        <link rel="stylesheet" href="/css/estilo.css">
    </head>
    <body>
        <div class="card">
            <div class="checkmark">&#10003;</div>
            <p>' . htmlspecialchars( $texto ) . '</p>
        </div>
    </body>
    </html>';

    exit;

}