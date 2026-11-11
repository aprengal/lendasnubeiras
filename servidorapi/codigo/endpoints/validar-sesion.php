<?php

declare( strict_types = 1 );

/**
 * POST /sesion/validacion
 *
 * Valida o rastro da sesión recibido na cabeceira da petición e comproba
 * que o usuario existe e que a sesión continúa activa.
 *
 * Execútase ao abrir a aplicación para recuperar unha sesión anterior
 * e durante o proceso de inicio de sesión, mentres se agarda a que o usuario
 * verifique a ligazón recibida por correo electrónico.
 *
 * Se a sesión é válida, devolve o identificador, o correo electrónico
 * e o rol do usuario.
 *
 * Respostas: 200 (sesión válida), 401 (sesión ausente ou non válida),
 * 404 (usuario inexistente).
 */
function endpoint_validar_sesion(): void {

    esixir_metodo( 'POST' );

    validar_corpo( coller_corpo(), [ 'uuid' => [ 'tipo' => 'uuid' ] ] );

    $rastro = analizar_cabeceira_sesion();

    if ( !$rastro ) {
       responder( 401 );
    }

    $usuario = coller_usuario( 'uuid', $rastro[ 'usuario' ] );

    if ( !$usuario->existe() ) {
       responder( 404 );
    }

    if ( !$usuario->verificar_sesion( $rastro[ 'token' ] ) ) {
        responder( 401 );
    }

    $datos_resposta = [
        'id'     => $usuario->id,
        'correo' => $usuario->correo,
        'rol'    => $usuario->rol->value
    ];

    responder( 200, $datos_resposta );

}