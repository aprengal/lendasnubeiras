<?php

declare ( strict_types = 1 );

/**
 * Funcións de sesión específicas da API.
 * 
 * Aquí quedan agrupadas todas as chamadas ao sistema de sesións (Usuario e rastros),
 * para que, se cambia a forma de xestionalas, só haxa que tocar este arquivo.
 * 
 * Unha sesión só é válida cando o usuario preme na ligazón que recibe por correo
 * e cambiase o estado a 1 nos metadatos da sesión
 */

/**
 * Crea unha sesión para un usuario e devolve:
 * 
 * - sesion: valor da credencial cifrada da sesión, lista para enviar no corpo da resposta JSON ao cliente Android.
 * - activacion: nome do dato temporal que activa a sesión.
 *
 * A sesión queda creada pero inactiva ata que se cree o dato de activación.
 */
function crear_sesion_api( Usuario $usuario ): array {

    $agora = time();
    $caducidade = $agora + DURACION_SESION;

    $token = $usuario->crear_sesion( $agora, $caducidade );

    $datos_sesion = crear_datos_sesion( $usuario, [
       'caducidade_sesion' => $caducidade,
       'esquema' => 'sesion',
       'token' => $token,
       'alongar_ses' => $agora + DIA_EN_SEGUNDOS
    ] );

    if ( !$datos_sesion ) {
       throw new RuntimeException( 'Non se puido crear a sesión' );
    }

    return [
       'sesion' => $datos_sesion,
       'activacion' => nome_dato( 'sesion_activa', $token )
    ];

}

/**
 * Obtén o usuario da sesión actual. Se a sesión non é válida, responde cun 401.
 * Se a conta non está activa, responde cun 403.
 */
function esixir_sesion(): Usuario {

	$usuario = usuario_actual();

	// O usuario actual é válido?
	if ( !$usuario->existe() ) {
		responder( 401 );
	}

	// Se a conta está suspendida, non activa ou en proceso de borrado -> 403
	if ( $usuario->estado !== ESTADO_USUARIO_ACTIVO ) {
		responder( 403 );
	}

	// Se a sesión non está activa (falta o dato temporal de activación) -> 401
	/*if ( !hai_dato_temporal( nome_dato( 'sesion_activa', coller_token_sesion() ) ) ) {
		responder( 401 );
	}*/

	return $usuario;

}