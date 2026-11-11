<?php

declare( strict_types = 1 );

/**
 * POST /conta/rexistro
 *
 * Inicia o proceso de rexistro dun usuario novo a partir do seu enderezo
 * de correo electrónico.
 *
 * Comproba que non exista xa unha conta asociada ao correo e que non se
 * superase o límite de 3 intentos de rexistro nas últimas 24 horas.
 *
 * Se as comprobacións son correctas, xera un token de verificación e
 * almacena temporalmente os datos do rexistro. A continuación, envía
 * ao correo indicado unha ligazón de verificación cunha validez de
 * 15 minutos, no idioma seleccionado na aplicación.
 *
 * Unha vez verificada a ligazón, créase a conta en estado pendente
 * de aprobación administrativa.
 *
 * Respostas: 200 (solicitude procesada), 400 (datos incorrectos),
 * 409 (o usuario xa existe), 429 (límite de intentos superado).
 */
function endpoint_rexistro(): void {

	esixir_metodo( 'POST' );

	$corpo = validar_corpo( coller_corpo(), [
		'correo' => [ 'tipo' => 'correo' ],
		'uuid' => [ 'tipo' => 'uuid' ],
		'idioma' => [ 'tipo' => 'texto', 'max' => 2, 'valores' => IDIOMAS_API ]
	] );

	$correo = $corpo[ 'correo' ];

    //Se o usuario xa existe, descártase o intento
	if ( coller_usuario( 'correo', $correo )->existe() ) {
		responder( 409 );
	}

	//Máximo de 3 intentos en 24 horas por correo
	if ( !revisar_intentos( 'rexistro', $correo, 3, DIA_EN_SEGUNDOS ) ) {
		responder( 429 );
	}

	$ligazon = xerar_token( 40, false );

	crear_dato_temporal( nome_dato( 'accion', $ligazon ), [
		'tipo' => 'rexistro',
		'correo' => $correo,
		'uuid' => $corpo[ 'uuid' ]
	], DURACION_LIGAZON );

	enviar_correo_verificacion( $correo, 'rexistro', crear_url_verificacion( $ligazon, $corpo[ 'uuid' ], $corpo[ 'idioma' ] ), $corpo[ 'idioma' ] );

	responder( 200 );

}