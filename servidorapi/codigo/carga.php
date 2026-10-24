<?php

declare ( strict_types = 1 );


/**
 * Neste arquivo cárgase a configuración esencial antes de que a API poida funcionar
 * 
 */

//Constantes
require_once "codigo/constantes/constantes-publicas.php";
require_once "codigo/constantes/constantes-privadas.php";

//Clases
require_once "codigo/clases/DB.php";
require_once "codigo/configuracion/Rol.php";
require_once "codigo/clases/Usuario.php";
require_once "codigo/clases/Correo.php";

//Configuración
require_once "codigo/configuracion/funcions-base.php";
require_once "codigo/configuracion/novas-funcions.php";
require_once "codigo/configuracion/depuracion.php";
require_once "codigo/configuracion/seguridade.php";
require_once "codigo/configuracion/permisos.php";
require_once "codigo/configuracion/rastros.php";
require_once "codigo/opcions.php";


require_once "codigo/carga-api.php";

arrancar_api();
