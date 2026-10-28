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
require_once "codigo/clases/Rol.php";
require_once "codigo/clases/Usuario.php";
require_once "codigo/clases/Correo.php";

//Configuración
require_once "codigo/configuracion/xeral/funcions-base.php";
require_once "codigo/configuracion/xeral/novas-funcions.php";
require_once "codigo/configuracion/xeral/depuracion.php";
require_once "codigo/configuracion/xeral/seguridade.php";
require_once "codigo/configuracion/xeral/permisos.php";
require_once "codigo/configuracion/xeral/rastro-sesion.php";
require_once "codigo/configuracion/xeral/usuario-actual.php";
require_once "codigo/opcions.php";

require_once "codigo/carga-api.php";

arrancar_api();
