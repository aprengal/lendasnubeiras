<?php

declare ( strict_types = 1 );

/**
 * POST /iniciar-sesion
 * 
 * Crea unha sesión e envía ao correo do usuario, no idioma da aplicación, unha ligazón para activala.
 * Devolve o token da sesión, pero non é válido ata que se preme na ligazón.
 * Só se pode enviar un correo cada 60 segundos.
 * 
 * Respostas: 200, 400, 403, 404, 429
 */
function endpoint_iniciar_sesion(): never {

	esixir_metodo( 'POST' );

	$corpo = validar_corpo( coller_corpo(), [
		'correo' => [ 'tipo' => 'correo' ],
		'uuid' => [ 'tipo' => 'uuid' ],
		'idioma' => [ 'tipo' => 'texto', 'max' => 2, 'valores' => IDIOMAS_API ]
	] );

	$correo = $corpo[ 'correo' ];
	$usuario = coller_usuario( 'correo', $correo );

	if ( !$usuario->existe() ) {
		responder( 404 );
	}

	if ( $usuario->estado !== ESTADO_USUARIO_ACTIVO ) {
		responder( 403 );
	}

	if ( !reservar_intento( 'inicio_sesion', $correo, 1, MINUTO_EN_SEGUNDOS ) ) {
		responder( 429 );
	}

	$sesion = crear_sesion_api( $usuario );
	$ligazon = xerar_token( 40, false );

	crear_dato_temporal( nome_dato( 'accion', $ligazon ), [
		'tipo' => 'inicio',
		'uuid' => $corpo[ 'uuid' ],
		'idioma' => $corpo[ 'idioma' ],
		'activacion' => $sesion[ 'activacion' ]
	], DURACION_LIGAZON );

	enviar_correo_verificacion( $correo, 'inicio', crear_url_verificacion( $ligazon, $corpo[ 'uuid' ] ), $corpo[ 'idioma' ] );

	responder( 200, [ 'token' => $sesion[ 'token' ] ] );

}
