<?php

declare ( strict_types = 1 );

/**
 * Enumerado de tipo string que define os diferentes roles
 * de usuario dispoñibles no sistema.
 */
enum Rol: string {

    /** Rol de monitor. */
    case MONITOR       = 'monitor';

    /** Rol de colaborador. */
    case COLABORADOR   = 'colaborador';

    /** Rol de autor. */
    case AUTOR         = 'autor';

    /** Rol de editor. */
    case EDITOR        = 'editor';

    /** Rol de administrador. */
    case ADMINISTRADOR = 'administrador';

}