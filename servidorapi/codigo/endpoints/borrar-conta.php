<?php

declare( strict_types = 1 );

/**
 * POST /conta/borrado
 *
 * Solicita o borrado da conta do usuario autenticado.
 *
 * Envíase unha ligazón de confirmación ao correo electrónico do usuario.
 * A ligazón caduca aos 15 minutos e só se pode utilizar unha vez.
 *
 * Unha vez verificada, a conta péchase e queda pendente de eliminación
 * definitiva durante o período establecido en DIAS_BORRADO_CONTA días.
 *
 * A solicitude require unha sesión autenticada e a confirmación mediante
 * a ligazón enviada ao correo electrónico. Non require aprobación
 * administrativa.
 *
 * Os administradores e os usuarios legados non poden solicitar
 * o borrado da súa propia conta.
 *
 * Tras enviar o correo de confirmación, destrúese a sesión actual.
 *
 * Establécese un intervalo mínimo de 60 segundos entre solicitudes
 * de borrado para un mesmo usuario.
 *
 * @return void
 *
 * Respostas: 200, 400, 401, 409, 429
 */
function endpoint_borrar_conta(): void {

    esixir_metodo( 'POST' );

    $usuario = esixir_sesion();

    if ( $usuario->rol === Rol::ADMINISTRADOR || usuario_legado( $usuario ) ) {
        responder( 409 );
    }

    $corpo = validar_corpo( coller_corpo(), [
        'uuid' => [ 'tipo' => 'uuid' ],
        'idioma' => [ 'tipo' => 'texto', 'max' => 2, 'valores' => IDIOMAS_API ]
    ] );

    if ( !revisar_intentos( 'borrado_conta', (string) $usuario->id, 1, MINUTO_EN_SEGUNDOS ) ) {
        responder( 429 );
    }

    $resultado = $usuario->verificar_peche_sesion();

    if ( !$resultado ) {
        responder( 409 );
    }

    $ligazon = xerar_token( 40, false );

    crear_dato_temporal( nome_dato( 'accion', $ligazon ), [
        'tipo' => 'borrado',
        'id' => $usuario->id,
        'uuid' => $corpo[ 'uuid' ],
        'idioma' => $corpo[ 'idioma' ]
    ], DURACION_LIGAZON );

    enviar_correo_verificacion(
        $usuario->correo,
        'borrado',
        crear_url_verificacion( $ligazon, $corpo[ 'uuid' ], $corpo[ 'idioma' ] ),
        $corpo[ 'idioma' ]
    );

    $rastro = analizar_cabeceira_sesion();
    $usuario->destruir_sesion( $rastro[ 'token' ] );

    responder( 200 );

}