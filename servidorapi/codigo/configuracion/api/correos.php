<?php

declare( strict_types = 1 );

/**
 * Xera a URL da ligazón de verificación que se envía ao usuario por correo.
 *
 * Inclúe o identificador da acción, o UUID do dispositivo cifrado
 * e o idioma no que se mostrará a páxina de verificación.
 *
 * Os parámetros codifícanse para incorporalos correctamente á URL.
 *
 * @param string $id_accion Identificador do dato temporal asociado
 * á acción que se quere verificar.
 * @param string $uuid Identificador do dispositivo desde o que
 * se solicitou a acción.
 * @param string $idioma Código do idioma da páxina de verificación.
 * @return string URL completa da ligazón de verificación.
 */
function crear_url_verificacion( string $id_accion, string $uuid, string $idioma ): string {
    $uuid_cifrada = cifrar_descifrar( $uuid, 'cifrar', 'correos' );
	return 'https://' . DOMINIO . '/accion/verificacion?id_accion=' . rawurlencode( $id_accion ) . '&uuid=' . rawurlencode( $uuid_cifrada ) . '&idioma=' . rawurlencode( $idioma );
}

/**
 * Obtén o asunto e o contido dun correo electrónico adaptados ao idioma indicado.
 *
 * Xera os textos correspondentes ás operacións de rexistro, inicio de sesión
 * e borrado de conta, incorporando a URL de verificación e os prazos
 * de validez ou eliminación que correspondan.
 *
 * Se o idioma solicitado non está dispoñible, utilízase o castelán.
 * Se o tipo de correo non está definido para o idioma seleccionado,
 * lánzase unha excepción.
 *
 * Os saltos de liña da mensaxe deben manterse como separadores de parágrafo,
 * xa que a clase Correo transforma cada salto de liña nun parágrafo HTML.
 *
 * @param string $tipo Tipo de operación: 'rexistro', 'inicio' ou 'borrado'.
 * @param string $idioma Código do idioma: 'gl', 'es' ou 'en'.
 * @param string $url URL da ligazón de verificación que se incluirá no correo.
 * @return array Array asociativo cos elementos:
 * - asunto: asunto do correo electrónico.
 * - mensaxe: contido do correo electrónico.
 * @throws OutOfRangeException Se non existe un texto definido para o tipo
 * de operación solicitado.
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
 * Envía un correo electrónico de verificación ao usuario.
 *
 * Obtén o asunto e a mensaxe adaptados ao tipo de operación e ao idioma
 * indicado, e crea unha instancia da clase Correo para realizar o envío.
 *
 * Se o envío non se completa correctamente, lánzase unha excepción.
 *
 * @param string $correo Enderezo de correo electrónico do destinatario.
 * @param string $tipo Tipo de operación: 'rexistro', 'inicio' ou 'borrado'.
 * @param string $url URL da ligazón de verificación incluída na mensaxe.
 * @param string $idioma Código do idioma do correo: 'gl', 'es' ou 'en'.
 * @return void
 * @throws OutOfRangeException Se non existe un texto para o tipo de operación.
 * @throws RuntimeException Se non se consegue enviar o correo electrónico.
 */
function enviar_correo_verificacion( string $correo, string $tipo, string $url, string $idioma ): void {

    $texto = coller_texto_correo( $tipo, $idioma, $url );

    $envio = new Correo( $correo, $texto[ 'asunto' ], $texto[ 'mensaxe' ] );

    //Se o envío falla, a clase non devolve identificador
    if ( $envio->boundary === null ) {
        throw new RuntimeException( 'Non se puido enviar o correo de verificación' );
    }

}