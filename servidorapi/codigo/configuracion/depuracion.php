<?php

declare ( strict_types = 1 );

function erro() {

	http_response_code( 503 );

    echo json_encode( [
        'error' => true,
        'messages' => [
            'gl' => 'Houbo un problema coa última petición.',
            'es' => 'Ha habido un problema con la última petición.',
            'en' => 'There was a problem with the last request.'
        ]
    ], JSON_UNESCAPED_SLASHES );

    exit;
	
}

class ProblemaMysqli extends Exception {}

function rexistrar_problema( Throwable $problema, bool $capturado = true ) {

	$trace = rexistro_seguimento( $problema->getTrace() );
	$clase = get_class( $problema );

	$mensaxe = $problema->getMessage();

	if ( $problema instanceof Exception ) {
		$tipo = 'Excepción';
		$xenero = 'a';
	} else {
		$tipo = 'Erro';
		$xenero = 'o';
	}

	$inicio_mensaxe = $tipo . ' ' . $clase;
	$inicio_mensaxe .= $capturado ? " capturad". $xenero .":\n\t\t" : " non capturad" . $xenero . ":\n\t\t";

	//Falta engadir o arquivo e liña de onde se xerou o erro
	if ( str_contains( $mensaxe, ', called in' ) ) {

		$orixinal  =  [ ', called in', BASE, 'on line ', '\\' ];
		$traducions = [ '. ' . "\n\t\t" . 'Xerad' . $xenero . ' en', 'INSTALACION', '[Liña ', '/' ];	
		$mensaxe = str_replace( $orixinal, $traducions, $mensaxe ) . ']';

	} else {

		$mensaxe .= "\n\t\t" . 'Xerad' . $xenero . ' en ' . str_replace( [ BASE, '\\' ], [ 'INSTALACION', '/' ], $problema->getFile() ) . ' [Liña ' . $problema->getLine() . ']';

	}

	error_log( $inicio_mensaxe . strip_tags( $mensaxe ) . $trace );

	if ( !$capturado ) {
		db()->cancelar_transaccion();
		erro();
	}

}

function rexistro_seguimento( array $traza ) {

	$traza = array_reverse( $traza );
	$resultado = [];
	$num = 1;
	$ignoradas = [ 'require', 'require_once' ];
	$funcions_confidenciais = [ 'verificar_contrasinal', 'password_verify', 'protexer_contrasinal', 'password_hash'/*, 'actualizar_usuario'*/ ];

	foreach ( $traza as $elemento ) {

		if ( in_array( $elemento[ 'function' ], $ignoradas, true ) ) {
			continue;
		}

		$resultado[ $num ] = $num . ' - ';

		if ( isset( $elemento[ 'class' ] ) ) {
			$resultado[ $num ] .= $elemento[ 'class' ] . $elemento[ 'type' ] . $elemento[ 'function' ];
		} else {
			$resultado[ $num ] .= $elemento[ 'function' ];
		}

		if ( !empty( $elemento[ 'args' ] ) ) {

			$argumentos = [];

			foreach ( $elemento[ 'args' ] as $argumento ) {

				if( !is_scalar( $argumento ) ) {
					$argumento = get_debug_type( $argumento );
				} elseif ( is_string( $argumento ) ) {

					if ( in_array( $elemento[ 'function' ], $funcions_confidenciais, true ) || str_contains( $argumento, '@' ) ) {
						$argumento = 'CONFIDENCIAL';
					} elseif ( strlen( $argumento ) > 50 ) {
						$argumento = substr( $argumento, 0, 50 ) . ' [TRUNCADO]';
					}

				} else if ( is_bool( $argumento ) ) {
					$argumento = $argumento ? "true" : "false";
				}

				$argumentos[] = $argumento;

			}

			$resultado[ $num ] .= '( ' . implode( ', ', $argumentos ) . ' )';

		}

		if ( isset( $elemento[ 'file' ] ) ) {
			$resultado[ $num ] .=  "\n\t\t" . 'Chamada dende ' . $elemento[ 'file' ] . ' [Liña ' . $elemento[ 'line' ] . ']';
		}

		$resultado[ $num ] = str_replace( [ BASE, '\\' ], [ "INSTALACION", '/' ] , $resultado[ $num ] );

		$num++;

	}

	if ( $resultado ) {
		return '. Rastro:' . "\n\t" . implode( "\n\t", $resultado );
	}

	return "\n\t\t" . 'Non hai rastro a seguir. A función debeu chamarse directamente nun arquivo de carga';

}

function comprobar_args() {

	if ( func_num_args() != 0 ) {
		throw new BadMethodCallException( 'Chamouse incorrectamente á función ' . __FUNCTION__  . ' empregando argumentos' );
	}

    $traza = debug_backtrace();
	$ignoradas = [ 'comprobar_args', 'require', 'require_once' ];

	foreach ( $traza as $elemento ) {

		if ( !empty( $elemento[ 'function' ] && in_array( $elemento[ 'function' ], $ignoradas, true ) ) ) {
			continue;
		}
		
		$datos = $elemento;
		break;

	}

	if ( empty( $datos ) ) {
		throw new BadMethodCallException( 'Chamouse incorrectamente á función ' . __FUNCTION__ );
	}

    if ( !empty( $datos[ 'object' ] ) ) {
        $reflexion = new ReflectionMethod($datos[ 'object' ], $datos[ 'function' ] );
        $nome_funcion = $datos[ 'class' ] . $datos[ 'type' ] . $datos['function'];
    } else {
        $reflexion = new ReflectionFunction($datos[ 'function' ]);
        $nome_funcion = $datos['function'];
    }

    $parametros = count( $reflexion->getParameters() );
    $args = isset( $datos[ 'args' ] ) ? count($datos[ 'args' ] ) : 0;

    if ( $args > $parametros ) {
        throw new ArgumentCountError( "Demasiados argumentos na función $nome_funcion" );
    }

}


register_shutdown_function( function () {

	$erro = error_get_last();

	if ( !$erro ) {
		return;
	}

	error_log( 'Erro detectado de tipo ' . array_search( $erro[ 'type' ], get_defined_constants( true )[ 'Core' ], true ) );

	$tipos_erros = [ E_ERROR, E_COMPILE_ERROR, E_PARSE ];

	if ( in_array( $erro[ 'type' ], $tipos_erros, true ) ) {
		erro();
	}

} );

set_exception_handler( fn ( Throwable $problema ) => rexistrar_problema( $problema, false ) );

set_error_handler( function( int $num, string $mensaxe, $arquivo, int $liña ) {

	switch( $num ) {

		case E_WARNING:
		case E_USER_WARNING:
			$inicio_mensaxe = 'Advertencia';
			break;
		case E_USER_NOTICE:
		case E_NOTICE:
			$inicio_mensaxe = 'Aviso';
			break;
		case E_DEPRECATED:
		case E_USER_DEPRECATED:
			$inicio_mensaxe = 'Atención!';
			break;
		default:
			$inicio_mensaxe = 'Erro detectado de tipo ' . array_search( $num, get_defined_constants( true )[ 'Core' ] );
			throw new ErrorException( $inicio_mensaxe . ': ' . $mensaxe, 0, $num, $arquivo, $liña );

	}

	if ( $num != E_USER_WARNING ) {
		$arquivo = str_replace( [ BASE, '\\' ], [ 'INSTALACION', '/' ], $arquivo );
		$mensaxe .= "\n\t\t" . 'Ocorreu en ' . $arquivo . ' [Liña ' . $liña . ']';
	}

	error_log( $inicio_mensaxe . ': ' . $mensaxe );

} );