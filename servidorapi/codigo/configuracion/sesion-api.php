<?php

declare ( strict_types = 1 );

/**
 * Funcións de sesión específicas da API.
 * 
 * Aquí quedan agrupadas todas as chamadas ao sistema de sesións (Usuario e rastros),
 * para que, se cambia a forma de xestionalas, só haxa que tocar este arquivo.
 * 
 * Unha sesión só é válida cando o usuario preme na ligazón que recibe por correo.
 * Iso márcase co dato temporal 'sesion_activa', que se crea ao verificar a ligazón.
 */

/**
 * Crea unha sesión para un usuario e devolve:
 * 
 * - token: valor da cookie de sesión, codificado para poder usalo directamente
 *          na cabeceira Cookie.
 * - activacion: nome do dato temporal que activa a sesión.
 * 
 * A sesión queda creada pero inactiva ata que se cree o dato de activación.
 */
function crear_sesion_api( Usuario $usuario ): array {

	$agora = time();
	$caducidade = $agora + DURACION_SESION;
	$peche_ses = xerar_token( 15, false );

	$token = $usuario->crear_sesion( $agora, $caducidade, $peche_ses );

	$valor_rastro = crear_rastro_sesion( $usuario, [
		'caducidade_sesion' => $caducidade,
		'esquema' => 'sesion',
		'token' => $token,
		'peche_ses' => $peche_ses,
		'alongar_ses' => $agora + DIA_EN_SEGUNDOS
	] );

	if ( !$valor_rastro ) {
		throw new RuntimeException( 'Non se puido crear o rastro da sesión' );
	}

	return [
		'token' => rawurlencode( $valor_rastro ),
		'activacion' => nome_dato( 'sesion_activa', $token )
	];

}

/**
 * Obtén o usuario da sesión actual. Se a sesión non é válida, non está activada
 * ou a conta non está activa, responde cun 403.
 */
function esixir_sesion(): Usuario {

	$usuario = usuario_act();

	if ( !$usuario->existe() || $usuario->estado !== ESTADO_USUARIO_ACTIVO ) {
		responder( 403 );
	}

	if ( !hai_dato_temporal( nome_dato( 'sesion_activa', coller_token_sesion() ) ) ) {
		responder( 403 );
	}

	return $usuario;

}
