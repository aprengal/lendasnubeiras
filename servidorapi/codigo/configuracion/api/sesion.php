<?php

declare( strict_types = 1 );

/**
 * Crea unha sesión para un usuario e xera as súas credenciais cifradas.
 *
 * Calcula as datas de creación e caducidade, crea o token asociado
 * á sesión e xera as credenciais cifradas que se enviarán ao cliente.
 *
 * A sesión queda rexistrada no servidor, pero non se pode utilizar
 * ata que se complete o proceso de activación mediante a ligazón
 * de verificación enviada ao correo electrónico.
 *
 * @param Usuario $usuario Usuario ao que pertence a sesión.
 * @param string $id_dispositivo Identificador do dispositivo asociado
 * á sesión.
 * @return array Array asociativo cos seguintes elementos:
 * - sesion: credencial cifrada que se enviará ao cliente.
 * - token: identificador interno da sesión.
 * @throws RuntimeException Se non se poden xerar as credenciais
 * cifradas da sesión.
 */
function crear_sesion_api( Usuario $usuario, string $id_dispositivo ): array {

    $agora = time();
    $caducidade = $agora + DURACION_SESION;

    $token = $usuario->crear_sesion( $agora, $caducidade, $id_dispositivo );

    $argumentos_sesion = [
        'caducidade_sesion' => $caducidade,
        'id_dispositivo' => $id_dispositivo,
        'esquema' => 'sesion',
        'token' => $token
    ];

    $datos_sesion = crear_datos_sesion( $usuario, $argumentos_sesion );

    if ( !$datos_sesion ) {
        throw new RuntimeException( 'Non se puido crear a sesión' );
    }

    return [
        'sesion' => $datos_sesion,
        'token' => $token
    ];

}

/**
 * Obtén o usuario asociado á sesión actual e comproba que a súa conta
 * estea activa.
 *
 * Se non existe un usuario asociado a unha sesión válida, responde
 * cun erro HTTP 401.
 *
 * Se o usuario existe pero a súa conta non está activa, responde
 * cun erro HTTP 403.
 *
 * @return Usuario Usuario autenticado coa conta activa.
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