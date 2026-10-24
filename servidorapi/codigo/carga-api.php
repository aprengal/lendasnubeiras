<?php

declare ( strict_types = 1 );

//Funcións comúns
require_once "codigo/configuracion/funcions-api.php";
require_once "codigo/configuracion/sesion-api.php";
require_once "codigo/configuracion/actividades-api.php";
require_once "codigo/configuracion/correos-api.php";
require_once "codigo/configuracion/usuarios-api.php";

//Endpoints
require_once "codigo/endpoints/rexistro.php";
require_once "codigo/endpoints/iniciar-sesion.php";
require_once "codigo/endpoints/verificar-accion.php";
require_once "codigo/endpoints/borrar-conta.php";
require_once "codigo/endpoints/validar-sesion.php";
require_once "codigo/endpoints/actualizar.php";
require_once "codigo/endpoints/crear-actividade.php";
require_once "codigo/endpoints/modificar-actividade.php";
require_once "codigo/endpoints/reportar-erros.php";
require_once "codigo/endpoints/axustar-usuarios.php";
require_once "codigo/endpoints/limpar.php";

function arrancar_api() {

	header( 'Content-Type: application/json; charset=utf-8' );

	if ( empty( $_SERVER[ 'REQUEST_URI' ] ) ) {
		responder( 404, [ 'mensaxe' => 'O recurso solicitado non existe' ] );
	}

	//Co prefixo '/' evítase que parse_url tome unha ruta que empece por '//' como un host
	$ruta = parse_url( '/' . ltrim( $_SERVER[ 'REQUEST_URI' ], '/' ), PHP_URL_PATH );
	$ruta = is_string( $ruta ) ? $ruta : '';

	//Elimínanse as barras do principio e do final para evitar redireccións.
	//A ruta distingue maiúsculas e minúsculas. As rutas son as de RutasApi
	switch ( trim( $ruta, '/' ) ) {

		//Autenticación
		case 'rexistro':
			endpoint_rexistro();
			break;

		case 'iniciar-sesion':
			endpoint_iniciar_sesion();
			break;

		case 'accion/verificacion':
			endpoint_verificar_accion();
			break;

		case 'sesion/validacion':
			endpoint_validar_sesion();
			break;

		//Conta
		case 'conta/borrado':
			endpoint_borrar_conta();
			break;

		//Actividades
		case 'actividade/actualizar':
			endpoint_actualizar();
			break;

		case 'actividade/crear':
			endpoint_crear_actividade();
			break;

		case 'actividade/modificar':
			endpoint_modificar_actividade();
			break;

		//Administración
		case 'axustar-usuarios':
			endpoint_axustar_usuarios();
			break;

		//Sistema
		case 'reportar-erros':
			endpoint_reportar_erros();
			break;

		case 'limpar':
			endpoint_limpar();
			break;

	}

	responder( 404, [ 'mensaxe' => 'O recurso solicitado ' . $ruta . ' non existe' ] );

}
