<?php

declare ( strict_types = 1 );

/**
 * Funcións comúns dos endpoints das actividades.
 * 
 */

/**
 * Regras de validación dunha actividade[cite: 9].
 *
 * Ao crear, admítense os estados do servidor (0, 1, 2) e os temporais do cliente (-1, -2, -3).
 * Ao modificar, pídese o id e admítense os estados do 0 ao 3 (incluíndo o 3 de borrada).
 */
function regras_actividade( bool $modificar ): array {

	$regras = [
		'clave_titulo' => [ 'tipo' => 'texto', 'max' => 100 ],
		'id_autoria' => [ 'tipo' => 'enteiro', 'min' => 0 ],
		'id_categoria' => [ 'tipo' => 'texto', 'max' => 15 ],
		'id_destinatario' => [ 'tipo' => 'texto', 'max' => 15 ],
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
 * Devolve unha actividade cos tipos de datos que espera o cliente.
 * 
 * A base de datos pode devolver como números textos que parecen números,
 * así que se forzan os tipos para que o JSON sexa sempre igual.
 */
function formatar_actividade( array $fila ): array {

	return [
		'id' => (int) $fila[ 'id' ],
		'clave_titulo' => (string) $fila[ 'clave_titulo' ],
		'id_autoria' => (int) $fila[ 'id_autoria' ],
		'id_categoria' => (string) $fila[ 'id_categoria' ],
		'id_destinatario' => (string) $fila[ 'id_destinatario' ],
		'estado' => (int) $fila[ 'estado' ],
		'duracion' => (int) $fila[ 'duracion' ],
		'data_modificado' => (int) $fila[ 'data_modificado' ]
	];

}

function coller_actividade( int $id ): ?array {

	$busca = [
		'columnas' => COLUMNAS_ACTIVIDADE,
		'onde' => [ 'id' => $id ],
		'limit' => 1
	];

	$filas = db()->select( 'actividades', $busca, 'array' );

	if ( !$filas ) {
		return null;
	}

	return formatar_actividade( reset( $filas ) );

}

/**
 * Devolve as actividades que pode recibir un usuario (ou un usuario anónimo se é null).
 *
 * Todos reciben as publicadas (2). Os editores e administradores reciben todas.
 * Os monitores reciben as publicadas. Os demais usuarios con sesión (autores, colaboradores, etc.)
 * reciben ademais os seus propios borradores (0) e pendentes (1).
 * As actividades borradas (3) non se envían; o cliente xestiona iso pola súa conta
 * para borrar as actividades non enviadas no propio dispositivo.
 */
function coller_actividades_visibles( ?Usuario $usuario ): array {

    $rol = $usuario?->rol;
    $todas = in_array( $rol, [ Rol::EDITOR, Rol::ADMINISTRADOR ], true );

    $busca = [ 'columnas' => COLUMNAS_ACTIVIDADE ];

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
 * Indica se un usuario pode gardar unha actividade segundo o seu rol.
 * 
 * - Administrador e editor: calquera actividade.
 * - Autor: só as súas propias.
 * - Colaborador: só as súas propias e sen publicar (nin antes nin despois).
 * - Monitor e resto: ningunha.
 * 
 * Ao crear, o estado actual é 0.
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
