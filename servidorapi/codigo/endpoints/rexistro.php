<?php

declare ( strict_types = 1 );

/**
 * POST /rexistro
 * 
 * Inicia o rexistro dun usuario novo co seu correo. Envíase unha ligazón de verificación
 * que dura 15 minutos, no idioma da aplicación. Os datos do rexistro quedan nun dato temporal ata que se verifique.
 * Despois, a solicitude queda en espera ata que un administrador a aprobe.
 * 
 * Respostas: 200, 400, 409, 429
 */
function endpoint_rexistro(): void {

	esixir_metodo( 'POST' );

	$corpo = validar_corpo( coller_corpo(), [
		'correo' => [ 'tipo' => 'correo' ],
		'uuid' => [ 'tipo' => 'uuid' ],
		'idioma' => [ 'tipo' => 'texto', 'max' => 2, 'valores' => IDIOMAS_API ]
	] );

	$correo = $corpo[ 'correo' ];

	//Máximo de 3 intentos en 24 horas por correo
	if ( !revisar_intentos( 'rexistro', $correo, 3, DIA_EN_SEGUNDOS ) ) {
		responder( 429 );
	}

	if ( coller_usuario( 'correo', $correo )->existe() ) {
		responder( 409 );
	}

	$ligazon = xerar_token( 40, false );

	crear_dato_temporal( nome_dato( 'accion', $ligazon ), [
		'tipo' => 'rexistro',
		'correo' => $correo,
		'uuid' => $corpo[ 'uuid' ],
		'idioma' => $corpo[ 'idioma' ]
	], DURACION_LIGAZON );

	enviar_correo_verificacion( $correo, 'rexistro', crear_url_verificacion( $ligazon, $corpo[ 'uuid' ], $corpo[ 'idioma' ] ), $corpo[ 'idioma' ] );

	responder( 200 );

}
