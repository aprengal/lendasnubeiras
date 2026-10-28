<?php

declare ( strict_types = 1 );

/**
 * Xestiona o usuario actual da petición, validando o seu rastro de sesión ou devolvendo un usuario anónimo.
 * Permite tamén reiniciar a caché do usuario actual se se solicita explicitamente.
 *
 * @string  $accion  Acción a realizar ('coller' ou 'reiniciar').
 * @return  Usuario  A instancia do obxecto Usuario correspondente.
 */
function usuario_actual( string $accion = 'coller' ) {

    static $usuario;

    if ( $accion == 'coller' ) {

       if ( $usuario ) {
          return $usuario;º
       }

       $novo_usuario = validar_rastro_sesion();

       return $usuario = $novo_usuario instanceof Usuario ? $novo_usuario : new Usuario( 'id', -1, false );

    }

    if ( $accion == 'reiniciar' ) {

       if ( !$usuario || !$usuario->existe() ) {
          throw new LogicException( 'Só se pode reiniciar o usuario actual se está definido e fai referencia a un usuario que exista' );
       }

       $rastro = analizar_rastro_sesion();
       $uuid = $rastro[ 'uuid' ];

       if ( $usuario->uuid == $uuid ) {
          return $usuario = coller_usuario( 'uuid', $uuid, false ); // Sen memoria: hai que ler o usuario de novo
       }

    }

    return $usuario = new Usuario( 'id', -1, false );

}

/**
 * Devolve un usuario buscándoo por id, correo ou uuid.
 *
 * Cada usuario cárgase unha soa vez por petición (2 consultas: o usuario e as súas sesións).
 * Se se volve pedir co mesmo campo e valor devólvese o que xa estaba cargado, aínda que non exista,
 * polo que non se repiten consultas. A memoria só dura ata que remata a petición.
 *
 * Como a clase Usuario é inmutable, compartir o obxecto é seguro. Se se modifica un usuario e hai que
 * lelo de novo na mesma petición, hai que pasar $memoria = false para forzar unha lectura nova.
 */
function coller_usuario( string $campo, string|int $valor, bool $memoria = true ) {

	static $usuarios = [];

	validar_campo_busca( $campo, 'usuario' );

	if ( 'id' === $campo && ( !is_numeric( $valor ) || $valor < -1 ) ) {
		throw new OutOfBoundsException( 'Non se pode mirar o usuario co valor ' . $valor );
	}

	$valor = $campo == 'id' ? (int) $valor : trim( $valor );
	$clave = $campo . ':' . $valor;

	if ( $memoria && isset( $usuarios[ $clave ] ) ) {
		return $usuarios[ $clave ];
	}

	try {
		$usuario = new Usuario( $campo, $valor, false );
	} catch ( Exception $problema ) {
		rexistrar_problema( $problema );
		//Se falla a lectura non se garda na memoria, para que se poida intentar de novo
		return coller_usuario( 'id', -1 );
	}

	return $usuarios[ $clave ] = $usuario;

}

/**
 * Destrúe a sesión actual do usuario activo e finaliza a execución para evitar que se execute código.
 */
function destruir_sesion_actual() {

    $token = coller_token_sesion();

    $usuario = usuario_actual();

    if ( $token ) {
       $usuario->destruir_sesion( $token );
    }

    usuario_actual( 'anonimo' );
    exit( 200 );

}