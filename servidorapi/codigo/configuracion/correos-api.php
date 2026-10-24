<?php

declare ( strict_types = 1 );

/**
 * Correos que envía a API: textos nos idiomas da aplicación e envío.
 * 
 */

/**
 * Crea a URL da ligazón de verificación que recibe o usuario.
 * 
 * O id da acción identifica o dato temporal e o uuid é o do dispositivo que fixo a solicitude.
 */
function crear_url_verificacion( string $id_accion, string $uuid ): string {
	return 'https://' . DOMINIO . '/accion/verificacion?id_accion=' . rawurlencode( $id_accion ) . '&uuid=' . rawurlencode( $uuid );
}

/**
 * Devolve o asunto e a mensaxe dun correo no idioma da aplicación.
 * 
 * O tipo é 'rexistro', 'inicio' ou 'borrado'. Se o idioma non existe, envíase en castelán.
 * Hai que usar un único salto de liña entre parágrafos, porque a clase Correo
 * converte cada salto nun parágrafo novo.
 */
function coller_texto_correo( string $tipo, string $idioma, string $url ): array {

	$minutos = (int) ( DURACION_LIGAZON / MINUTO_EN_SEGUNDOS );
	$dias = DIAS_BORRADO_CONTA;

	$textos = [
		'gl' => [
			'rexistro' => [
				'asunto' => 'Confirma o teu rexistro en ' . NOME,
				'mensaxe' => "Ola,\r\n"
					. 'Recibimos unha solicitude de rexistro en ' . NOME . " con este enderezo de correo electrónico.\r\n"
					. 'Para confirmala, abre a seguinte ligazón nos próximos ' . $minutos . " minutos:\r\n"
					. $url . "\r\n"
					. "Despois, un administrador revisará a túa solicitude.\r\n"
					. 'Se non fuches ti quen fixo esta solicitude, ignora esta mensaxe.'
			],
			'inicio' => [
				'asunto' => 'Inicia sesión en ' . NOME,
				'mensaxe' => "Ola,\r\n"
					. 'Recibimos unha solicitude para iniciar sesión en ' . NOME . " con este enderezo de correo electrónico.\r\n"
					. 'Para activar a sesión, abre a seguinte ligazón nos próximos ' . $minutos . " minutos:\r\n"
					. $url . "\r\n"
					. 'Se non fuches ti quen fixo esta solicitude, ignora esta mensaxe e non abras a ligazón.'
			],
			'borrado' => [
				'asunto' => 'Confirma o borrado da túa conta en ' . NOME,
				'mensaxe' => "Ola,\r\n"
					. 'Recibimos unha solicitude para borrar a túa conta en ' . NOME . " con este enderezo de correo electrónico.\r\n"
					. 'Para confirmala, abre a seguinte ligazón nos próximos ' . $minutos . " minutos:\r\n"
					. $url . "\r\n"
					. 'Unha vez confirmada, a conta quedará pechada e eliminarase definitivamente en ' . $dias . " días. Durante ese prazo non poderás iniciar sesión.\r\n"
					. 'Se non fuches ti quen fixo esta solicitude, ignora esta mensaxe e non abras a ligazón.'
			]
		],
		'es' => [
			'rexistro' => [
				'asunto' => 'Confirma tu registro en ' . NOME,
				'mensaxe' => "Hola,\r\n"
					. 'Hemos recibido una solicitud de registro en ' . NOME . " con esta dirección de correo electrónico.\r\n"
					. 'Para confirmarla, abre el siguiente enlace en los próximos ' . $minutos . " minutos:\r\n"
					. $url . "\r\n"
					. "Después, un administrador revisará tu solicitud.\r\n"
					. 'Si no has sido tú quien ha hecho esta solicitud, ignora este mensaje.'
			],
			'inicio' => [
				'asunto' => 'Inicia sesión en ' . NOME,
				'mensaxe' => "Hola,\r\n"
					. 'Hemos recibido una solicitud para iniciar sesión en ' . NOME . " con esta dirección de correo electrónico.\r\n"
					. 'Para activar la sesión, abre el siguiente enlace en los próximos ' . $minutos . " minutos:\r\n"
					. $url . "\r\n"
					. 'Si no has sido tú quien ha hecho esta solicitud, ignora este mensaje y no abras el enlace.'
			],
			'borrado' => [
				'asunto' => 'Confirma el borrado de tu cuenta en ' . NOME,
				'mensaxe' => "Hola,\r\n"
					. 'Hemos recibido una solicitud para borrar tu cuenta en ' . NOME . " con esta dirección de correo electrónico.\r\n"
					. 'Para confirmarla, abre el siguiente enlace en los próximos ' . $minutos . " minutos:\r\n"
					. $url . "\r\n"
					. 'Una vez confirmada, la cuenta quedará cerrada y se eliminará definitivamente en ' . $dias . " días. Durante ese plazo no podrás iniciar sesión.\r\n"
					. 'Si no has sido tú quien ha hecho esta solicitud, ignora este mensaje y no abras el enlace.'
			]
		],
		'en' => [
			'rexistro' => [
				'asunto' => 'Confirm your registration at ' . NOME,
				'mensaxe' => "Hello,\r\n"
					. 'We received a registration request at ' . NOME . " using this email address.\r\n"
					. 'To confirm it, open the following link within the next ' . $minutos . " minutes:\r\n"
					. $url . "\r\n"
					. "An administrator will then review your request.\r\n"
					. 'If you did not make this request, please ignore this message.'
			],
			'inicio' => [
				'asunto' => 'Log in to ' . NOME,
				'mensaxe' => "Hello,\r\n"
					. 'We received a request to log in to ' . NOME . " using this email address.\r\n"
					. 'To activate the session, open the following link within the next ' . $minutos . " minutes:\r\n"
					. $url . "\r\n"
					. 'If you did not make this request, please ignore this message and do not open the link.'
			],
			'borrado' => [
				'asunto' => 'Confirm the deletion of your account at ' . NOME,
				'mensaxe' => "Hello,\r\n"
					. 'We received a request to delete your account at ' . NOME . " using this email address.\r\n"
					. 'To confirm it, open the following link within the next ' . $minutos . " minutes:\r\n"
					. $url . "\r\n"
					. 'Once confirmed, your account will be closed and permanently deleted in ' . $dias . " days. During that time you will not be able to log in.\r\n"
					. 'If you did not make this request, please ignore this message and do not open the link.'
			]
		]
	];

	//Se o idioma non existe, envíase en castelán
	if ( !isset( $textos[ $idioma ] ) ) {
		$idioma = 'es';
	}

	if ( !isset( $textos[ $idioma ][ $tipo ] ) ) {
		throw new OutOfRangeException( 'Non hai texto de correo para o tipo ' . $tipo );
	}

	return $textos[ $idioma ][ $tipo ];

}

/**
 * Envía ao usuario o correo coa ligazón de verificación.
 * 
 * O tipo indica a acción que se verifica ('rexistro', 'inicio' ou 'borrado'), a URL é a ligazón
 * que recibe o usuario e o idioma ('gl', 'es' ou 'en') é o da aplicación.
 * Se non se pode enviar, lanza unha excepción.
 */
function enviar_correo_verificacion( string $correo, string $tipo, string $url, string $idioma ) {

	$texto = coller_texto_correo( $tipo, $idioma, $url );

	$envio = new Correo( $correo, $texto[ 'asunto' ], $texto[ 'mensaxe' ] );

	//Se o envío falla, a clase non devolve identificador
	if ( $envio->boundary === null ) {
		throw new RuntimeException( 'Non se puido enviar o correo de verificación' );
	}

}
