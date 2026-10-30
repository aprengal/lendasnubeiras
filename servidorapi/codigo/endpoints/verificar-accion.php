<?php

declare ( strict_types = 1 );

/**
 * GET /accion/verificacion
 * 
 * Verifica unha acción (rexistro, inicio de sesión ou borrado de conta) coa ligazón que recibe o usuario por correo.
 * Execútase ao premer sobre a ligazón, polo que os datos van na URL (id_accion e uuid).
 * 
 * A ligazón só se pode usar unha vez. Cando algo falla (parámetros ausentes ou incorrectos,
 * ligazón caducada, descoñecida ou xa usada) responde sempre co mesmo 409, para dar a menor información posible.
 * 
 * Respostas: 200, 409
 */
function endpoint_verificar_accion(): void {

	esixir_metodo( 'GET' );

	$id_accion = $_GET[ 'id_accion' ] ?? null;
	$uuid = $_GET[ 'uuid' ] ?? null;

	if ( !is_string( $id_accion ) || preg_match( '/^[a-zA-Z0-9]{40}$/', $id_accion ) !== 1 || !verificar_uuid(( $uuid ) ) {
		responder( 409 );
	}

	$nome = nome_dato( 'accion', $id_accion );
	$dato = coller_dato_temporal( $nome );

	if ( !is_string( $dato ) || !hai_serializacion( $dato ) ) {
		responder( 409 );
	}

	$accion = unserialize( $dato, [ 'allowed_classes' => false ] );

	//O uuid da ligazón ten que ser o do dispositivo que fixo a solicitude
	if ( !is_array( $accion ) || !isset( $accion[ 'tipo' ], $accion[ 'uuid' ] ) || !hash_equals( (string) $accion[ 'uuid' ], strtolower( $uuid ) ) ) {
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

    $idioma = $accion[ 'idioma' ] ?? 'es';
    amosar_pantalla_exito( $idioma );

}

/**
 * Verifica un rexistro: crea o usuario en estado pendente, á espera de que un administrador o aprobe.
 */
function verificar_rexistro( array $accion ) {

	if ( !isset( $accion[ 'correo' ] ) ) {
		responder( 409 );
	}

	//O correo xa está rexistrado?
	if ( coller_usuario( 'correo', $accion[ 'correo' ] )->existe() ) {
		responder( 409 );
	}

	crear_usuario_pendente( $accion[ 'correo' ] );

}

/**
 * Verifica un inicio de sesión: activa a sesión que se creou ao solicitalo.
 */
function verificar_inicio( array $accion ) {

	if ( !isset( $accion[ 'activacion' ] ) ) {
		responder( 409 );
	}

    //Hai que revisar este sistema de validación de inicio de sesión
    //En validación non se devolve información, pero o valor do token gardado depende da data de caducidade
    //un valor que se cambia cada 24 horas, polo que tería que enviarse un novo token ou habería que cambiar
    //este valor para que non teña como referencia a data de cadudidade

	//crear_dato_temporal( $accion[ 'activacion' ], '1', DURACION_SESION );

}

/**
 * Verifica un borrado de conta: a conta pasa ao estado borrado, péchanse as súas sesións e comeza o prazo
 * de DIAS_BORRADO_CONTA días, pasado o cal /limpar bórraa de forma definitiva.
 */
function verificar_borrado( array $accion ) {

	if ( !isset( $accion[ 'id' ] ) ) {
		responder( 409 );
	}

	$usuario = coller_usuario( 'id', (int) $accion[ 'id' ], false );

	//Só se pode borrar unha conta activa que non sexa de administrador (nin a reservada)
	if ( !$usuario->existe() || $usuario->estado !== ESTADO_USUARIO_ACTIVO || $usuario->rol === Rol::ADMINISTRADOR || usuario_legado( $usuario ) ) {
		responder( 409 );
	}

	db()->update( 'usuarios', [
		'nova_info' => [ 'estado' => ESTADO_USUARIO_BORRADO, 'data_borrado' => time() ],
		'onde' => [ 'id' => $usuario->id ],
		'limit' => 1
	] );

	pechar_sesions( $usuario->id );

}

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