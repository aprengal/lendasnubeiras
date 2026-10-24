<?php

declare ( strict_types = 1 );

/**
 * POST /conta/borrado
 * 
 * O usuario pide borrar a súa propia conta. Envíase unha ligazón de verificación ao seu correo (en
 * canto se confirma, a conta péchase e bórrase de forma definitiva pasados DIAS_BORRADO_CONTA días).
 * Só se pode enviar un correo cada 60 segundos.
 * 
 * Os administradores non poden borrar a súa conta. Non fai falta que un administrador aprobe o
 * borrado: a seguridade ven da sesión activa máis a ligazón do correo.
 * 
 * Respostas: 200, 400, 403, 409, 429
 */
function endpoint_borrar_conta(): never {

	esixir_metodo( 'POST' );

	$usuario = esixir_sesion();

	if ( $usuario->rol === Rol::ADMINISTRADOR || es_usuario_legado( $usuario ) ) {
		responder( 409 );
	}

	$corpo = validar_corpo( coller_corpo(), [
		'idioma' => [ 'tipo' => 'texto', 'max' => 2, 'valores' => IDIOMAS_API ]
	] );

	if ( !reservar_intento( 'borrado_conta', (string) $usuario->id, 1, MINUTO_EN_SEGUNDOS ) ) {
		responder( 429 );
	}

	$ligazon = xerar_token( 40, false );

	crear_dato_temporal( nome_dato( 'accion', $ligazon ), [
		'tipo' => 'borrado',
		'id' => $usuario->id,
		'uuid' => $usuario->uuid,
		'idioma' => $corpo[ 'idioma' ]
	], DURACION_LIGAZON );

	enviar_correo_verificacion( $usuario->correo, 'borrado', crear_url_verificacion( $ligazon, $usuario->uuid ), $corpo[ 'idioma' ] );

	responder( 200 );

}
