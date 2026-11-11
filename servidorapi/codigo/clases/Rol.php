<?php

declare( strict_types = 1 );

/**
 * Enumerado de tipo string que define os roles dispoñibles no sistema.
 *
 * Cada rol determina os permisos e as operacións que pode realizar
 * un usuario dentro da aplicación.
 */
enum Rol: string {

    /** Rol básico de monitor. */
    case MONITOR       = 'monitor';

    /** Rol de colaborador, con permisos limitados de edición. */
    case COLABORADOR   = 'colaborador';

    /** Rol de autor, con capacidade para xestionar as súas propias actividades. */
    case AUTOR         = 'autor';

    /** Rol de editor, con permisos de xestión de actividades. */
    case EDITOR        = 'editor';

    /** Rol de administrador, con permisos de xestión global do sistema. */
    case ADMINISTRADOR = 'administrador';

}