package com.aprengal.lendasnubeiras.data.configuracion.api


enum class MetodoApi { GET, POST, DELETE }

enum class RutaApi ( val ruta: String ) {

    REXISTRO( "rexistro" ),
    ACCESO( "iniciar-sesion" ),
    ACTUALIZAR( "actualizar-actividades" ),
    REPORTES( "reportar-erros" ),
    VALIDACION( "validar-sesion" );

}