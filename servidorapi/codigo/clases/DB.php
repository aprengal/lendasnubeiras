<?php

declare ( strict_types = 1 );

final readonly class DB {

	public mysqli $conexion;
	
	//táboas
	public string $roles, $permisos, $permisos_roles, $usuarios;
	public string $categorias, $destinatarios, $actividades;
	public string $opcions, $info_temporal, $tarefas_programadas;

	public function __construct( string $host, string $nome_us, string $contrasinal, string $nome_base ) {

		try {
			$this->conexion = new mysqli( $host, $nome_us, $contrasinal, $nome_base, 3306 );
		} catch ( Exception $problema ) {

			$mensaxe = 'Ocorreu un erro á hora de conectarse á base de datos.' . "\n\t\t" . 'Detalles: ' . $problema->getMessage();
			rexistrar_problema( new ProblemaMysqli( $mensaxe ) );
			erro();

		}

		$this->crear_taboas();

	}

	private function crear_taboas() {

		$prefixo = 'LendasNubeiras_417072_';

		//Recórrense os nomes das propiedades (foreach sobre $this daría os valores e saltaría as que aínda non teñen valor)
		foreach ( ( new ReflectionClass( $this ) )->getProperties() as $propiedade ) {

			$nome = $propiedade->getName();

			if ( $nome == 'conexion' ) {
				continue;
			}

			$this->$nome = $prefixo . $nome;

		}

	}

	//Valídanse os resultados e despois devólvense os valores
	private function validar_datos( array $datos ) {

		$datos_validos = [];

		foreach ( $datos as $identificador => $valor ) {
	
			if ( !is_scalar( $valor ) ) {
				throw new InvalidArgumentException( 'Só se admiten valores escalables no elemento ' . $identificador );
			}

			$datos_validos[] = $valor;

		}

		return $datos_validos;

	}

	private function reemprazo() {
		return '?';
	}

	public function insert( string $taboa, array $datos, ?array $actualizar = null ) {

		$taboa_db = $this->establecer_taboa( $taboa );
		$reemprazos = $valores = [];

		//Se non existe o índice 0, significa que só se quere insertar 1 soa fila
		if ( !isset( $datos[ 0 ] ) ) {
			$datos = [ $datos ];
		}

		$columnas = array_keys( $datos[ 0 ] );
		$cantidade = count( $datos[ 0 ] );

		//Creación de valores e reemprazos
		foreach ( $datos as $elemento ) {
	
			if ( !is_array( $elemento ) ) {
				throw new InvalidArgumentException( 'Trataron de pasarse datos para insertar na base de datos sen ter forma de array' );
			}

			$datos_validos = $this->validar_datos( $elemento );
			$array_reemprazos = array_fill( 0, $cantidade, $this->reemprazo() );

			$reemprazos[] = '( ' . implode( ',', $array_reemprazos ) . ' )';
			$valores = array_merge( $valores, $datos_validos );

		}

		$consulta = 'INSERT ' . $taboa_db . '( ' . implode( ', ', $columnas ) . ' ) VALUES ' . implode( ', ', $reemprazos );

		if ( $actualizar ) {

			$columnas_act = [];

			foreach ( $actualizar as $columna ) {
				$columnas_act[] = $columna . ' = VALUES( ' . $columna . ' )';
			}

			$consulta .= ' ON DUPLICATE KEY UPDATE ' . implode( ', ', $columnas_act );

		}

		return $this->executar_consulta( $consulta, $valores, 'insert' );

	}

	public function select( string $taboa, array $datos, string $saida = 'obxecto' ) {

		if ( empty( $datos[ 'columnas' ] ) ) {
			throw new LogicException( 'Hai que indicar que columnas se seleccionan nunha consulta select' );
		}

		$columnas = $valores = [];

		//Cada columna pode levar un alias con AS. As claves do array de columnas son só etiquetas
		foreach ( (array) $datos[ 'columnas' ] as $identificador ) {

			foreach ( (array) $identificador as $columna ) {
				$columnas[] = $columna;
			}

		}

		$consulta = 'SELECT';

		if ( !empty( $datos[ 'distinto' ] ) ) {
			$consulta .= ' DISTINCT';
		}

		$consulta .= ' ' . implode( ', ', $columnas ) . "\n\t\t" . 'FROM ' . $this->establecer_taboa( $taboa );

		if ( !empty( $datos[ 'alias' ] ) ) {
			$consulta .= ' AS ' . $datos[ 'alias' ];
		}

		if ( !empty( $datos[ 'join' ] ) ) {

			if ( empty( $datos[ 'alias' ] ) ) {
				throw new LogicException( 'Unha consulta con JOIN necesita un alias na táboa principal' );
			}

			$consulta .= $this->engadir_join( $datos[ 'alias' ], $datos[ 'join' ] );

		}

		if ( !empty( $datos[ 'onde' ] ) ) {
			$onde = $this->establecer_condicions( 'WHERE', $datos[ 'onde' ] );
			$consulta .= $onde[ 'consulta' ];
			$valores = array_merge( $valores, $onde[ 'valores' ] );
		}

		if ( !empty( $datos[ 'ordenar' ] ) ) {

			$lista_orde = [];

			foreach ( $datos[ 'ordenar' ] as $identificador => $orde ) {

				if ( !in_array( $orde, [ 'ASC', 'DESC' ], true ) ) {
					throw new OutofBoundsException( 'A orde indicada non é correcta: ' . $orde );
				}

				$lista_orde[] = $identificador . ' ' . $orde;

			}

			$consulta .= ' ORDER BY ' . implode( ', ', $lista_orde );

		}

		if ( !empty( $datos[ 'limit' ] ) ) {
			$consulta .= $this->engadir_limit( (array) $datos[ 'limit' ] );
		}

		$res_busca = $this->executar_consulta( $consulta, $valores, 'select' );

		if ( in_array( $saida, [ 'obxecto', 'array' ], true ) ) {

			$elementos = $res_busca->fetch_all( MYSQLI_ASSOC );

			if ( !count( $elementos ) ) {
				return;
			}

			$cantidade = 0;
			$columna_principal = $datos[ 'principal' ] ?? 'id';

			//Incluise a columna principal?
			if ( !isset( $elementos[ 0 ][ $columna_principal ] ) ) {
				throw new LogicException( 'A columna principal ' . $columna_principal . ' non está presente nos resultados da última consulta realizada' );
			}

			$resultado = [];

			foreach ( $elementos as $fila ) {

				$id_act = $fila[ $columna_principal ];

				//Xa está definido o elemento?
				if ( !isset( $resultado[ $id_act ] ) ) {

					$resultado[ $id_act ] = new stdClass();
					$resultado[ $id_act ]->$columna_principal = $id_act;
					$cantidade++;

				}

				foreach ( $fila as $columna => $valor ) {

					if ( $columna == $columna_principal ) {
						continue;
					}

					//Serve para asociar máis datos a unha mesma columna principal dentro da mesma táboa
					if ( $columna == 'campo' ) {

						$nome_columna = $valor;
						continue;

					}

					//Habería contido só se a columna anterior foi campo
					if ( empty( $nome_columna ) ) {
						$nome_columna = $columna;
					}

					if ( is_numeric( $valor ) ) {
						$valor = ctype_digit( (string) $valor ) ? (int) $valor : (float) $valor;
					}

					$resultado[ $id_act ]->$nome_columna = $valor;
					$nome_columna = '';

				}

			}

			if ( $cantidade === 1 && $saida == 'obxecto' ) {
				$resultado = $resultado[ $fila[ $columna_principal ] ];
			} elseif( $saida == 'array' ) {

				foreach ( $resultado as $clave => $elemento ) {
					$resultado[ $clave ] = (array) $elemento;
				}

			}

		} else {
			$resultado = $res_busca->fetch_column();
		}

		$res_busca->free_result();

		return $resultado;

	}

	public function update( string $taboa, array $datos ) {

		if ( empty( $datos[ 'nova_info' ] ) ) {
			throw new InvalidArgumentException( 'Hai que indicar os datos que se queren gardar nun UPDATE' );
		}

		if ( empty( $datos[ 'onde' ] ) ) {
			throw new LogicException( 'Un UPDATE sen condicións cambiaría todas as filas da táboa' );
		}

		$valores = $partes_consulta = [];

		foreach ( $datos[ 'nova_info' ] as $columna => $valor ) {

			$partes_consulta[] = $columna . ' = ' . $this->reemprazo();
			$valores[] = $this->validar_datos( [ $columna => $valor ] )[ 0 ];

		}

		$onde = $this->establecer_condicions( 'WHERE', $datos[ 'onde' ] );
		$valores = array_merge( $valores, $onde[ 'valores' ] );

		$consulta = 'UPDATE ' . $this->establecer_taboa( $taboa ) . ' SET ' . implode( ',' . "\n\t", $partes_consulta ) . $onde[ 'consulta' ];

		if ( !empty( $datos[ 'limit' ] ) ) {
			$consulta .= $this->engadir_limit( (array) $datos[ 'limit' ] );
		}

		return $this->executar_consulta( $consulta, $valores, 'update' );

	}

	public function delete( string $taboa, array $datos ) {

		$onde = $this->establecer_condicions( 'WHERE', $datos[ 'onde' ] ?? $datos );

		$consulta = 'DELETE FROM ' . $this->establecer_taboa( $taboa ) . $onde[ 'consulta' ];

		if ( !empty( $datos[ 'limit' ] ) ) {
			$consulta .= $this->engadir_limit( (array) $datos[ 'limit' ] );
		}

		return $this->executar_consulta( $consulta, $onde[ 'valores' ], 'delete' );

	}

	public function iniciar_transaccion() {
		return $this->conexion->query( 'START TRANSACTION' );
	}

	public function executar_transaccion() {
		return $this->conexion->query( 'COMMIT' );
	}

	public function cancelar_transaccion() {
		return $this->conexion->query( 'ROLLBACK' );
	}

	private function establecer_taboa( string $taboa ) {
	
		if ( isset( $this->$taboa ) ) {
			return $this->$taboa;
		}

		throw new OutOfRangeException( 'Non existe a táboa ' . $taboa );
		
	}

	private function engadir_join( string $alias_principal, array $datos_join ) {

		if ( !isset( $datos_join [ 0 ] ) ) {
			$datos_join = [ $datos_join ];
		}

		$consulta = '';
		$joins_admitidas = [ 'INNER', 'LEFT', 'RIGHT', 'CROSS' ];

		foreach ( $datos_join as $join ) {

			if ( !isset( $join[ 'tipo' ], $join[ 'principal' ], $join[ 'secundaria' ], $join[ 'taboa-join' ] ) ) {
				throw new InvalidArgumentException( 'Faltan detalles esenciais para engadir unha JOIN (tipo, principal, secundaria e taboa-join)' );
			}

			if ( !in_array( $join[ 'tipo' ], $joins_admitidas, true ) ) {
				throw new OutOfBoundsException( 'Indicouse un tipo de JOIN non admitida' );
			}

			$taboa_secundaria = $this->establecer_taboa( $join[ 'taboa-join' ] );

			$secundaria = explode( '.', $join[ 'secundaria' ] );

			$consulta_join = $join[ 'principal' ] != $secundaria[ 1 ] ? ' ON ' . $alias_principal . '.' . $join[ 'principal' ] . ' = ' . $join[ 'secundaria' ] : ' USING (' . $join[ 'principal' ] . ')';

			$consulta .= ' ' . $join[ 'tipo' ] . ' JOIN ' . $taboa_secundaria . ' AS ' . $secundaria[ 0 ] . $consulta_join;

		}

		return $consulta;

	}

	private function engadir_limit( array $limit ) {

		if ( count( $limit ) > 2 ) {
			throw new OutOfRangeException( 'Só se poden poñer 2 valores como máximo no apartado LIMIT' );
		}

		foreach ( $limit as $numero ) {

			if ( !is_int( $numero ) ) {
				throw new InvalidArgumentException( 'Os valores LIMIT só poden ser numéricos. Introduciuse ' . $numero );
			}

		}

		return ' LIMIT ' . implode( ', ', $limit );

	}

	private function establecer_condicions( string $palabra_clave, array $datos ) {

		$partes_consulta = $valores = [];

		foreach ( $datos as $columna => $info ) {

			$operador = $info[ 'operador' ] ?? '=';

			switch( $operador ) {

				case '=':
				case '!=':
				case '<':
				case '<=':
				case '>':
				case '>=':
				case 'LIKE':
				case 'NOT LIKE':

					$valor = $info[ 'valor' ] ?? $info;
					$partes_consulta[] = $columna . ' ' . $operador . ' ' . $this->reemprazo();
					$valores[] = $this->validar_datos( [ $columna => $valor ] )[ 0 ];

					break;

				case 'IN':
				case 'BETWEEN':
					
					$reemprazos = [];

					foreach ( $info[ 'valores' ] as $valor ) {
						$reemprazos[] = $this->reemprazo();
						$valores[] = $this->validar_datos( [ $columna => $valor ] )[ 0 ];
					}

					if ( $info[ 'operador' ] == 'IN' ) {
						$reemprazos = '( ' . implode( ', ', $reemprazos ) . ' )';
					} else {
						$reemprazos = $reemprazos[ 0 ] . ' AND ' . $reemprazos[ 1 ];
					}

					$partes_consulta[] = $columna . ' ' . $info[ 'operador' ] . ' ' . $reemprazos;

					break;

				default:
					throw new OutOfBoundsException( 'O operador ' . $operador . ' non entra dentro dos operadores admitidos' );

			}

		}

		if ( empty( $partes_consulta ) ) {
			throw new LogicException( 'Unha lista de condicións con ' . $palabra_clave . ' non debería estar baleira' );
		}

		$consulta = "\n\t\t" .  $palabra_clave . ' ' . implode( ' AND ', $partes_consulta );

		return [ 'consulta' => $consulta, 'valores' => $valores ];

	}

	private function executar_consulta( string $consulta, array $valores, string $accion ) {

		$intentos = 6;

		for ( $i = 0; $i < $intentos; $i++ ) {

			try {

				$resultado = $this->conexion->execute_query( $consulta, $valores );

				if ( $accion == 'select' ) {
					return $resultado;
				}

				if ( $accion == 'insert' && $this->conexion->affected_rows == 1 ) {
					return $this->conexion->insert_id;
				}

				return $this->conexion->affected_rows;

			} catch ( mysqli_sql_exception $e ) {

				$erros_conexion = [ 2006, 2013 ];
				$mensaxe = 'Ocorreu un erro á hora de executarse a consulta ' . $consulta . "\n\t\t" . 'Detalles: ' . $this->conexion->error;

				if ( !in_array( $e->getCode(), $erros_conexion ) ) {
					throw new ProblemaMysqli( $mensaxe );
				}

				sleep( 1 );

			}

		}

		erro();

	}

}