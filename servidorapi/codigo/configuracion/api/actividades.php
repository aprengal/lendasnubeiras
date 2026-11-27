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

	$datos_busca = busca_actividades();
    $datos_busca[ 'onde' ] = [ 'a.id' => $id ];

	$filas = db()->select( 'actividades', $datos_busca, 'array' );

	if ( !$filas ) {
		return null;
	}

	return formatar_actividade( reset( $filas ) );

}

/**
 * Recupera as actividades publicadas (estado 2).
 *
 * As consultas recuperan tamén as claves da categoría e do destinatario
 * mediante JOIN coas táboas correspondentes.
 *
 * @param Usuario|null $usuario Mantense por compatibilidade de firma.
 * @return array Lista de actividades publicadas cos seus tipos normalizados.
 */
function coller_actividades_visibles( ?Usuario $usuario ): array {

    $datos_busca = busca_actividades();
    $datos_busca[ 'onde' ] = [ 'estado' => [ 'operador' => 'IN', 'valores' => [ 2 ] ] ];

    $filas = db()->select( 'actividades', $datos_busca, 'array' ) ?? [];

    return array_values( array_map( 'formatar_actividade', $filas ) );

}