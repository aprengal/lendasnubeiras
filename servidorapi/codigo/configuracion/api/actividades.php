<?php

declare( strict_types = 1 );

/**
 * Constrúe a configuración común das consultas de actividades.
 *
 * Recupera os datos das actividades mediante JOIN coas táboas
 * categoria e destinatario, substituíndo os seus identificadores
 * polas claves correspondentes.
 *
 * @return array Configuración común para as consultas de actividades.
 */
function busca_actividades(): array {

	$datos = [
        'columnas' => COLUMNAS_ACTIVIDADE,
        'alias' => 'a',
        'join' => [
            [
                'tipo' => 'INNER',
                'principal' => 'id_categoria',
                'secundaria' => 'c.id',
                'taboa-join' => 'categorias',
                'alias' => 'c'
            ],
            [
                'tipo' => 'INNER',
                'principal' => 'id_destinatario',
                'secundaria' => 'd.id',
                'taboa-join' => 'destinatarios',
                'alias' => 'd'
            ]
        ]
    ];

    return $datos;

}

/**
 * Devolve as regras de validación dos campos dunha actividade.
 *
 * As regras varían segundo a operación:
 *
 * - Ao crear, admítense os estados definitivos do servidor (0, 1, 2)
 *   e os estados temporais do cliente (-1, -2, -3).
 * - Ao modificar, requírese o identificador da actividade e admítense
 *   os estados do servidor (0, 1, 2, 3).
 *
 * @param bool $modificar Indica se a validación corresponde a unha
 * modificación ou á creación dunha actividade.
 * @return array Array asociativo coas regras de validación de cada campo.
 */
function regras_actividade( bool $modificar ): array {

	$regras = [
		'clave_titulo' => [ 'tipo' => 'texto', 'max' => 100 ],
		'id_autoria' => [ 'tipo' => 'enteiro', 'min' => 0 ],
		'clave_categoria' => [ 'tipo' => 'texto', 'max' => 15 ],
		'clave_destinatario' => [ 'tipo' => 'texto', 'max' => 15 ],
		'estado' => [
			'tipo' => 'enteiro',
			'valores' => $modificar ? [ 0, 1, 2, 3 ] : [ 0, 1, 2, -1, -2, -3 ]
		],
		'duracion' => [ 'tipo' => 'enteiro', 'min' => 1, 'max' => 180 ],
		'data_modificado' => [ 'tipo' => 'enteiro', 'min' => 0 ]
	];

	if ( $modificar ) {
		$regras = [ 'id' => [ 'tipo' => 'enteiro', 'min' => 1 ] ] + $regras;
	}

	return $regras;

}

/**
 * Formata os datos dunha actividade para a súa devolución ao cliente.
 *
 * Converte explicitamente os valores aos tipos esperados pola aplicación,
 * evitando diferenzas no formato da resposta JSON cando a base de datos
 * devolve valores numéricos como cadeas de texto.
 *
 * @param array $fila Fila da actividade obtida da base de datos.
 * @return array Datos da actividade cos tipos normalizados.
 */
function formatar_actividade( array $fila ): array {

	return [
		'id' => (int) $fila[ 'id' ],
		'clave_titulo' => (string) $fila[ 'clave_titulo' ],
		'id_autoria' => (int) $fila[ 'id_autoria' ],
		'clave_categoria' => (string) $fila[ 'clave_categoria' ],
		'clave_destinatario' => (string) $fila[ 'clave_destinatario' ],
		'estado' => (int) $fila[ 'estado' ],
		'duracion' => (int) $fila[ 'duracion' ],
		'data_modificado' => (int) $fila[ 'data_modificado' ]
	];

}

/**
 * Recupera unha actividade da base de datos mediante o seu identificador.
 *
 * Consulta os campos definidos en COLUMNAS_ACTIVIDADE e obtén as claves
 * da categoría e do destinatario mediante as correspondentes JOIN.
 * Normaliza os tipos dos valores mediante formatar_actividade().
 *
 * @param int $id Identificador da actividade no servidor.
 * @return array|null Datos da actividade formateados ou null se non existe.
 */
function coller_actividade( int $id ): ?array {

	$busca = busca_actividades();
    $busca[ 'onde' ] = [ 'a.id' => $id ];

	$filas = db()->select( 'actividades', $busca, 'array' );

	if ( !$filas ) {
		return null;
	}

	return formatar_actividade( reset( $filas ) );

}

/**
 * Recupera as actividades que pode consultar un usuario segundo o seu rol.
 *
 * As actividades borradas (3) nunca se inclúen nos resultados.
 *
 * A visibilidade distribúese do seguinte xeito:
 *
 * - Usuarios anónimos: actividades publicadas (2).
 * - Monitores: actividades publicadas (2).
 * - Autores e colaboradores: actividades publicadas (2) e as súas
 *   propias actividades en borrador (0) ou pendentes (1).
 * - Editores e administradores: todas as actividades, agás as borradas.
 *
 * Os borradores e as actividades pendentes doutros usuarios non se
 * mostran a autores nin colaboradores.
 *
 * As consultas recuperan tamén as claves da categoría e do destinatario
 * mediante JOIN coas táboas correspondentes.
 *
 * @param Usuario|null $usuario Usuario cuxas actividades visibles
 * se queren recuperar, ou null para unha consulta anónima.
 * @return array Lista de actividades visibles cos seus tipos normalizados.
 */
function coller_actividades_visibles( ?Usuario $usuario ): array {

    $rol = $usuario?->rol;
    $todas = in_array( $rol, [ Rol::EDITOR, Rol::ADMINISTRADOR ], true );

    $busca = busca_actividades();

    if ( !$todas ) {
        $busca[ 'onde' ] = [ 'estado' => [ 'operador' => 'IN', 'valores' => [ 2 ] ] ];
    }

    $filas = db()->select( 'actividades', $busca, 'array' ) ?? [];

    if ( $usuario && $usuario->existe() && !in_array( $rol, [ Rol::MONITOR, Rol::EDITOR, Rol::ADMINISTRADOR ], true ) && !$todas ) {

        $busca_propias = [
            'columnas' => COLUMNAS_ACTIVIDADE,
            'onde' => [
                'id_autoria' => $usuario->id,
                'estado' => [ 'operador' => 'IN', 'valores' => [ 0, 1 ] ]
            ]
        ];

        $filas += db()->select( 'actividades', $busca_propias, 'array' ) ?? [];

    }

    ksort( $filas );

    return array_values( array_map( 'formatar_actividade', $filas ) );

}

/**
 * Comproba se un usuario pode crear ou modificar unha actividade
 * segundo o seu rol, a súa autoría e os estados implicados.
 *
 * As regras de autorización son:
 *
 * - Administradores e editores: poden xestionar calquera actividade.
 * - Autores: poden xestionar unicamente as súas propias actividades.
 * - Colaboradores: poden xestionar as súas propias actividades sempre
 *   que tanto o estado actual como o novo sexan anteriores á publicación.
 * - Monitores e demais roles: non poden xestionar actividades.
 *
 * Ao crear unha actividade, o estado actual debe indicarse como 0.
 *
 * @param Usuario $usuario Usuario que solicita a operación.
 * @param int $id_autoria Identificador do autor da actividade.
 * @param int $estado_actual Estado actual da actividade.
 * @param int $estado_novo Estado que se quere establecer.
 * @return bool True se o usuario ten permisos para realizar a operación.
 */
function pode_xestionar_actividade( Usuario $usuario, int $id_autoria, int $estado_actual, int $estado_novo ): bool {

	$propia = $id_autoria === $usuario->id;

	return match ( $usuario->rol ) {
		Rol::ADMINISTRADOR, Rol::EDITOR => true,
		Rol::AUTOR => $propia,
		Rol::COLABORADOR => $propia && $estado_actual < 2 && $estado_novo < 2,
		default => false
	};

}
