<?php

declare( strict_types = 1 );

/**
 * Clase final e inmutable encargada de xestionar a conexión cunha base de datos MySQL
 * mediante MySQLi, así como de construír e executar consultas SQL preparadas.
 *
 * Centraliza as operacións de inserción, consulta, actualización e eliminación de datos,
 * proporcionando mecanismos para prefixar os nomes das táboas, validar os valores
 * empregados nas consultas e xestionar transaccións.
 *
 * As consultas constrúense mediante prepared statements para separar os valores
 * dos parámetros da sentenza SQL e reducir o risco de inxección SQL.
 *
 * A clase tamén permite realizar insercións múltiples, actualizacións por duplicidade,
 * consultas con JOIN, filtrado, ordenación e limitación de resultados.
 */
final readonly class DB {

    /** @var mysqli Conexión activa co servidor MySQL mediante a extensión MySQLi. */
    public mysqli $conexion;

    /** @var string Nome prefixado da táboa que almacena os roles do sistema. */
    public string $roles;

    /** @var string Nome prefixado da táboa que almacena os permisos dispoñibles. */
    public string $permisos;

    /** @var string Nome prefixado da táboa que relaciona os permisos cos roles. */
    public string $permisos_roles;

    /** @var string Nome prefixado da táboa que almacena os usuarios do sistema. */
    public string $usuarios;

    /** @var string Nome prefixado da táboa que almacena as categorías. */
    public string $categorias;

    /** @var string Nome prefixado da táboa que almacena os destinatarios. */
    public string $destinatarios;

    /** @var string Nome prefixado da táboa que almacena as actividades. */
    public string $actividades;

    /** @var string Nome prefixado da táboa que almacena as opcións de configuración do sistema. */
    public string $opcions;

    /** @var string Nome prefixado da táboa que almacena información temporal e datos de sesión. */
    public string $info_temporal;

    /** @var string Nome prefixado da táboa que almacena as tarefas programadas. */
    public string $tarefas_programadas;

    /**
     * Inicializa a conexión coa base de datos MySQL e configura os nomes das táboas.
     *
     * Establece unha conexión mediante MySQLi empregando os datos de acceso recibidos
     * e, unha vez establecida, inicializa automaticamente as propiedades correspondentes
     * ás táboas mediante o prefixo definido na clase.
     *
     * Se se produce unha excepción durante a conexión, rexístrase o problema e
     * interrompe a execución mediante o mecanismo de erro global.
     *
     * @param string $host Enderezo ou nome do servidor MySQL.
     * @param string $nome_us Nome de usuario empregado para acceder á base de datos.
     * @param string $contrasinal Contrasinal de acceso á base de datos.
     * @param string $nome_base Nome da base de datos á que se realizará a conexión.
     * @return void
     */
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

    /**
     * Inicializa automaticamente os nomes das táboas da clase.
     *
     * Percorre mediante Reflection as propiedades definidas na clase DB e asigna
     * a cada unha delas o prefixo establecido, seguido do nome da propia propiedade.
     *
     * A propiedade correspondente á conexión MySQLi exclúese deste proceso.
     *
     * Este mecanismo permite centralizar a nomenclatura das táboas e evitar
     * ter que escribir o prefixo manualmente en cada consulta.
     *
     * @return void
     */
    private function crear_taboas(): void {

        $prefixo = PREFIXO_DB;

        foreach ( ( new ReflectionClass( $this ) )->getProperties() as $propiedade ) {

            $nome = $propiedade->getName();

            if ( $nome == 'conexion' ) {
                continue;
            }

            $this->$nome = $prefixo . $nome;

        }

    }

    /**
     * Valida os valores que se empregarán como parámetros nas consultas SQL.
     *
     * Percorre o array recibido e comproba que todos os seus elementos sexan valores
     * escalares ou nulos.
     *
     * Os valores válidos devólvense nun array indexado, conservando a orde orixinal
     * dos elementos para facilitar a súa vinculación cos marcadores de posición.
     *
     * @param array $datos Array cos valores que se deben validar.
     * @return array Array indexado cos valores validados.
     * @throws InvalidArgumentException Se algún dos valores non é escalar nin nulo.
     */
    private function validar_datos( array $datos ): array {

        $datos_validos = [];

        foreach ( $datos as $identificador => $valor ) {

            // Permitimos escalares ou null explicitamente
            if ( !is_scalar( $valor ) && $valor !== null ) {
                throw new InvalidArgumentException( 'Só se admiten valores escalares ou nulos no elemento ' . $identificador );
            }

            $datos_validos[] = $valor;

        }

        return $datos_validos;

    }

    /**
     * Devolve o marcador de posición empregado nas consultas preparadas.
     *
     * Centraliza a definición do carácter que representa os parámetros vinculados
     * ás consultas SQL mediante MySQLi.
     *
     * @return string Marcador de posición SQL (?).
     */
    private function reemprazo(): string {
        return '?';
    }

    /**
     * Constrúe e executa unha sentenza SQL INSERT mediante parámetros preparados.
     *
     * Permite inserir unha única fila ou varias filas nunha mesma operación.
     * Os nomes das columnas obtéñense das claves do array recibido, mentres que
     * os valores se vinculan á consulta mediante marcadores de posición.
     *
     * Opcionalmente, permite configurar unha operación UPSERT mediante
     * ON DUPLICATE KEY UPDATE, indicando as columnas que se deben actualizar
     * cando se produce un conflito cunha clave única ou primaria.
     *
     * O nome da táboa obtense mediante o sistema interno de prefixación.
     *
     * @param string $taboa Nome lóxico da táboa na que se realizará a inserción.
     * @param array $datos Datos que se inserirán. Pode ser un array asociativo
     * correspondente a unha fila ou un array de varias filas.
     * @param ?array $actualizar Columnas que se actualizarán en caso de duplicidade.
     * Se é null, realizarase unha inserción convencional.
     * @return mixed Devolve o identificador da fila inserida cando se afecta unha soa
     * fila; noutros casos, devolve o número de filas afectadas.
     * @throws InvalidArgumentException Se os datos proporcionados non teñen unha
     * estrutura válida ou conteñen valores inválidos.
     */
    public function insert( string $taboa, array $datos, ?array $actualizar = null ) {

        $taboa_db = $this->establecer_taboa( $taboa );
        $reemprazos = $valores = [];

        if ( !isset( $datos[ 0 ] ) ) {
            $datos = [ $datos ];
        }

        $columnas = array_keys( $datos[ 0 ] );
        $cantidade = count( $columnas );

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

    /**
     * Constrúe e executa unha consulta SQL SELECT con soporte para múltiples opcións.
     *
     * Permite seleccionar columnas concretas, aplicar DISTINCT, asignar un alias
     * á táboa principal, incorporar cláusulas JOIN, establecer condicións WHERE,
     * ordenar os resultados e limitar o número de filas devoltas.
     *
     * Como característica especial para estruturas de estilo columnar (EAV), se nos
     * resultados existe unha columna chamada 'campo', o seu valor empregarase como
     * nome dinámico para a propiedade do obxecto resultante.
     *
     * @param string $taboa Nome lóxico da táboa principal.
     * @param array $datos Configuración da consulta SQL.
     * @param string $saida Formato de saída: 'obxecto', 'array' ou outro valor.
     * @return mixed Resultado da consulta segundo o formato solicitado.
     * @throws LogicException Se non se indican columnas ou falta o alias principal.
     * @throws OutOfBoundsException Se a dirección de ordenación non é válida.
     */
    public function select( string $taboa, array $datos, string $saida = 'obxecto' ) {

        if ( empty( $datos[ 'columnas' ] ) ) {
            throw new LogicException( 'Hai que indicar que columnas se seleccionan nunha consulta select' );
        }

        $columnas = $valores = [];

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
                    throw new OutOfBoundsException( 'A orde indicada non é correcta: ' . $orde );
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
                return null;
            }

            $cantidade = 0;
            $columna_principal = $datos[ 'principal' ] ?? 'id';

            if ( !isset( $elementos[ 0 ][ $columna_principal ] ) ) {
                throw new LogicException( 'A columna principal ' . $columna_principal . ' non está presente nos resultados' );
            }

            $resultado = [];

            foreach ( $elementos as $fila ) {
                $id_act = $fila[ $columna_principal ];

                if ( !isset( $resultado[ $id_act ] ) ) {
                    $resultado[ $id_act ] = new stdClass();
                    $resultado[ $id_act ]->$columna_principal = $id_act;
                    $cantidade++;
                }

                foreach ( $fila as $columna => $valor ) {

                    if ( $columna == $columna_principal ) {
                        continue;
                    }

                    //Por se hai táboas con estilo columnar e se enganden máis adiante
                    if ( $columna == 'campo' ) {
                        $nome_columna = $valor;
                        continue;
                    }

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
                $resultado = $resultado[ array_key_first( $resultado ) ];
            } elseif ( $saida == 'array' ) {
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

    /**
     * Constrúe e executa unha sentenza SQL UPDATE mediante parámetros preparados.
     *
     * @param string $taboa Nome lóxico da táboa que se actualizará.
     * @param array $datos Configuración dos novos valores e das condicións.
     * @return int Número de filas afectadas pola operación.
     * @throws InvalidArgumentException Se non se indican novos valores.
     * @throws LogicException Se non se establecen condicións WHERE.
     */
    public function update( string $taboa, array $datos ): int {

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

    /**
     * Constrúe e executa unha sentenza SQL DELETE mediante parámetros preparados.
     *
     * @param string $taboa Nome lóxico da táboa na que se eliminarán rexistros.
     * @param array $datos Condicións de eliminación.
     * @return int Número de filas afectadas pola operación.
     */
    public function delete( string $taboa, array $datos ): int {

        $onde = $this->establecer_condicions( 'WHERE', $datos[ 'onde' ] ?? $datos );
        $consulta = 'DELETE FROM ' . $this->establecer_taboa( $taboa ) . $onde[ 'consulta' ];

        if ( !empty( $datos[ 'limit' ] ) ) {
            $consulta .= $this->engadir_limit( (array) $datos[ 'limit' ] );
        }

        return $this->executar_consulta( $consulta, $onde[ 'valores' ], 'delete' );

    }

    /**
     * Inicia unha nova transacción na conexión actual coa base de datos.
     *
     * @return bool True se a transacción se inicia correctamente; false noutro caso.
     */
    public function iniciar_transaccion(): bool {
        return $this->conexion->query( 'START TRANSACTION' );
    }

    /**
     * Confirma os cambios realizados durante a transacción activa.
     *
     * @return bool True se a confirmación se realiza correctamente; false noutro caso.
     */
    public function executar_transaccion(): bool {
        return $this->conexion->query( 'COMMIT' );
    }

    /**
     * Cancela a transacción activa e reverte os cambios realizados nela.
     *
     * @return bool True se a reversión se realiza correctamente; false noutro caso.
     */
    public function cancelar_transaccion(): bool {
        return $this->conexion->query( 'ROLLBACK' );
    }

    /**
     * Obtén o nome real dunha táboa a partir do seu identificador lóxico.
     *
     * @param string $taboa Identificador lóxico da táboa.
     * @return string Nome real da táboa na base de datos.
     * @throws OutOfRangeException Se non existe unha táboa coa identificación indicada.
     */
    private function establecer_taboa( string $taboa ): string {
        if ( isset( $this->$taboa ) ) {
            return $this->$taboa;
        }
        throw new OutOfRangeException( 'Non existe a táboa ' . $taboa );
    }

    /**
     * Constrúe as cláusulas JOIN dunha consulta SELECT.
     *
     * @param string $alias_principal Alias da táboa principal.
     * @param array $datos_join Configuración do JOIN.
     * @return string Fragmento SQL correspondente ás cláusulas JOIN.
     */
    private function engadir_join( string $alias_principal, array $datos_join ): string {

        if ( !isset( $datos_join[ 0 ] ) ) {
            $datos_join = [ $datos_join ];
        }

        $consulta = '';
        $joins_admitidas = [ 'INNER', 'LEFT', 'RIGHT', 'CROSS' ];

        foreach ( $datos_join as $join ) {
            if ( !isset( $join[ 'tipo' ], $join[ 'principal' ], $join[ 'secundaria' ], $join[ 'taboa-join' ] ) ) {
                throw new InvalidArgumentException( 'Faltan detalles esenciais para engadir unha JOIN' );
            }

            if ( !in_array( $join[ 'tipo' ], $joins_admitidas, true ) ) {
                throw new OutOfBoundsException( 'Indicouse un tipo de JOIN non admitida' );
            }

            $taboa_secundaria = $this->establecer_taboa( $join[ 'taboa-join' ] );
            $secundaria = explode( '.', $join[ 'secundaria' ] );

            if ( !isset( $secundaria[ 1 ] ) ) {
                throw new InvalidArgumentException( 'O formato da columna secundaria no JOIN debe ser alias.columna' );
            }

            $consulta_join = $join[ 'principal' ] != $secundaria[ 1 ]
                ? ' ON ' . $alias_principal . '.' . $join[ 'principal' ] . ' = ' . $join[ 'secundaria' ]
                : ' USING (' . $join[ 'principal' ] . ')';

            $consulta .= ' ' . $join[ 'tipo' ] . ' JOIN ' . $taboa_secundaria . ' AS ' . $secundaria[ 0 ] . $consulta_join;
        }

        return $consulta;

    }

    /**
     * Constrúe unha cláusula SQL LIMIT a partir dos valores proporcionados.
     *
     * @param array $limit Array con un ou dous valores enteiros.
     * @return string Fragmento SQL correspondente á cláusula LIMIT.
     */
    private function engadir_limit( array $limit ): string {

        if ( count( $limit ) > 2 ) {
            throw new OutOfRangeException( 'Só se poden poñer 2 valores como máximo no apartado LIMIT' );
        }

        foreach ( $limit as $numero ) {
            if ( !is_int( $numero ) ) {
                throw new InvalidArgumentException( 'Os valores LIMIT só poden ser numéricos' );
            }
        }

        return ' LIMIT ' . implode( ', ', $limit );

    }

    /**
     * Constrúe condicións SQL para cláusulas como WHERE mediante operadores admitidos.
     *
     * @param string $palabra_clave Palabra SQL que identifica a cláusula.
     * @param array $datos Array asociativo coas condicións.
     * @return array Array asociativo con 'consulta' e 'valores'.
     */
    private function establecer_condicions( string $palabra_clave, array $datos ): array {

        $partes_consulta = $valores = [];

        foreach ( $datos as $columna => $info ) {
            $operador = $info[ 'operador' ] ?? '=';

            switch ( $operador ) {
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
                    if ( empty( $info[ 'valores' ] ) || !is_array( $info[ 'valores' ] ) ) {
                        throw new InvalidArgumentException( 'O operador ' . $operador . ' require un array de valores non baleiro' );
                    }

                    if ( $operador === 'BETWEEN' && count( $info[ 'valores' ] ) !== 2 ) {
                        throw new InvalidArgumentException( 'O operador BETWEEN require exactamente dous valores' );
                    }

                    $reemprazos = [];

                    foreach ( $info[ 'valores' ] as $valor ) {
                        $reemprazos[] = $this->reemprazo();
                        $valores[] = $this->validar_datos( [ $columna => $valor ] )[ 0 ];
                    }

                    if ( $operador == 'IN' ) {
                        $reemprazos = '( ' . implode( ', ', $reemprazos ) . ' )';
                    } else {
                        $reemprazos = $reemprazos[ 0 ] . ' AND ' . $reemprazos[ 1 ];
                    }

                    $partes_consulta[] = $columna . ' ' . $operador . ' ' . $reemprazos;
                    break;

                default:
                    throw new OutOfBoundsException( 'O operador ' . $operador . ' non entra dentro dos operadores admitidos' );
            }
        }

        if ( empty( $partes_consulta ) ) {
            throw new LogicException( 'Unha lista de condicións con ' . $palabra_clave . ' non debería estar baleira' );
        }

        $consulta = "\n\t\t" . $palabra_clave . ' ' . implode( ' AND ', $partes_consulta );

        return [ 'consulta' => $consulta, 'valores' => $valores ];

    }

    /**
     * Executa unha consulta SQL mediante MySQLi e parámetros vinculados.
     *
     * @param string $consulta Sentenza SQL que se executará.
     * @param array $valores Array ordenado cos valores que se vincularán.
     * @param string $accion Tipo de operación.
     * @return mixed Resultado da consulta.
     */
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