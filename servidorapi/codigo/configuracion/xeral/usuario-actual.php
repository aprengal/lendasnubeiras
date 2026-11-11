<?php

declare( strict_types = 1 );

/**
 * Obtén o usuario asociado á petición actual.
 *
 * Se non hai ningún usuario cargado, valida o rastro de sesión da cabeceira
 * HTTP e obtén os datos do usuario correspondente. Se non existe unha sesión
 * válida, devolve un usuario anónimo.
 *
 * A instancia do usuario consérvase nunha variable estática para reutilizala
 * durante a petición.
 *
 * Permite tamén forzar a actualización dos datos do usuario mediante
 * a acción 'reiniciar'. Para iso, comproba que o usuario actual existe
 * e que segue correspondendo ao identificado no rastro de sesión,
 * e volve cargalo sen utilizar a caché de usuarios.
 *
 * @param string $accion Acción que se quere realizar: 'coller' para obter
 * o usuario actual ou 'reiniciar' para actualizar os seus datos.
 * @return Usuario Instancia do usuario actual ou un usuario anónimo
 * se non existe unha sesión válida.
 * @throws LogicException Se se solicita reiniciar o usuario actual
 * sen ter previamente un usuario autenticado e existente.
 */
function usuario_actual( string $accion = 'coller' ) {

    static $usuario;

    if ( $accion == 'coller' ) {

       if ( $usuario ) {
          return $usuario;
       }

       $novo_usuario = validar_cabeceira_sesion();

       return $usuario = $novo_usuario instanceof Usuario ? $novo_usuario : new Usuario( 'id', -1, false );

    }

    if ( $accion == 'reiniciar' ) {

       if ( !$usuario || !$usuario->existe() ) {
          throw new LogicException( 'Só se pode reiniciar o usuario actual se está definido e fai referencia a un usuario que exista' );
       }

       $rastro = analizar_cabeceira_sesion();
       $uuid = $rastro[ 'usuario' ];

       if ( $usuario->uuid == $uuid ) {
          return $usuario = coller_usuario( 'uuid', $uuid, false ); // Sen memoria: hai que ler o usuario de novo
       }

    }

    return $usuario = new Usuario( 'id', -1, false );

}

/**
 * Obtén un usuario da base de datos mediante o seu identificador,
 * enderezo de correo electrónico ou UUID.
 *
 * Cada usuario almacénase nunha caché estática durante a petición,
 * utilizando como clave o campo de busca e o seu valor. As consultas
 * posteriores cos mesmos parámetros reutilizan a instancia almacenada,
 * mesmo cando esta representa un usuario inexistente.
 *
 * A carga dun usuario inclúe tamén a información das súas sesións.
 * Como a clase Usuario é inmutable, as instancias poden reutilizarse
 * con seguridade durante toda a petición.
 *
 * Permite desactivar a caché mediante o parámetro $memoria para forzar
 * unha nova lectura da base de datos cando os datos puideran cambiar.
 *
 * @param string $campo Campo empregado para localizar o usuario:
 * 'id', 'correo' ou 'uuid'.
 * @param string|int $valor Valor correspondente ao campo de busca.
 * @param bool $memoria Indica se se debe consultar e utilizar a caché.
 * Por defecto, true.
 * @return Usuario Instancia do usuario localizado ou un usuario
 * anónimo se non se pode completar a consulta.
 * @throws OutOfBoundsException Se se busca por identificador cun valor
 * non numérico ou inferior a -1.
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
 * Destrúe a sesión actual do usuario e restablece o contexto de autenticación.
 *
 * Se existe un token de sesión, invalídao mediante o método [Usuario::destruir_sesion()].
 *
 * A continuación, establece o usuario actual como anónimo e finaliza
 * inmediatamente a execución do script devolvendo o código 200.
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