<?php

declare ( strict_types = 1 );

enum Rol: string {

    case MONITOR       = 'monitor';
    case COLABORADOR   = 'colaborador';
    case AUTOR         = 'autor';
    case EDITOR        = 'editor';
    case ADMINISTRADOR = 'administrador';

}