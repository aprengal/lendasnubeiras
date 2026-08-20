package com.aprengal.lendasnubeiras.data.configuracion.api


enum class MetodoApi {
    GET, POST, DELETE;
}

enum class RutaApi ( val ruta: String ) {

    REXISTRO( "rexistro" ),
    INICIOSESION( "iniciar-sesion" ),
    ACTUALIZAR( "actualizar-actividades" ),
    REPORTARERRO( "reportar-erros" ),
    VALIDARSESION( "validar-sesion" );

}