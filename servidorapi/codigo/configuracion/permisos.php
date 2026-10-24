<?php

declare ( strict_types = 1 );

function coller_permisos( Rol $rol ) {

    static $permisos_rol;

    if ( !isset( $permisos_rol ) ) {

        //DB agrupa as filas pola columna principal (rol) e a columna 'campo' dá nome á propiedade que leva a seguinte (valor)
        $busca_permisos = [
            'columnas' => [
                't2.clave' => 't2.clave AS rol',
                't3.nome'  => 't3.nome AS campo',
                'valor'    => '1 AS valor'
            ],
            'principal' => 'rol',
            'alias' => 't1',
            'join' => [
                [ 'tipo' => 'INNER', 'principal' => 'id_rol', 'secundaria' => 't2.id', 'taboa-join' => 'roles' ],
                [ 'tipo' => 'INNER', 'principal' => 'id_permiso', 'secundaria' => 't3.id', 'taboa-join' => 'permisos' ]
            ]
        ];

        $permisos = db()->select( 'permisos_roles', $busca_permisos, 'array' ) ?? [];
        $permisos_rol = [];

        foreach ( $permisos as $nome_rol => $fila ) {
            unset( $fila[ 'rol' ] );
            $permisos_rol[ $nome_rol ] = array_keys( $fila );
        }

    }

    if ( !isset( $permisos_rol[ $rol->value ] ) ) {
        throw new Exception( 'Pasouse un rol descoñecido: ' . $rol->value );
    }

    return $permisos_rol[ $rol->value ];

}
