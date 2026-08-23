package com.aprengal.lendasnubeiras.ui.navegacion

import kotlinx.serialization.Serializable

@Serializable
sealed interface Pantalla {

    //Autenticación

    @Serializable
    data object Benvida : Pantalla

    @Serializable
    data object IniciarSesion : Pantalla

    @Serializable
    data object Rexistro : Pantalla

    //Lectura

    @Serializable
    data object Inicio : Pantalla

    @Serializable
    data object Axustes : Pantalla

    @Serializable
    data object Actividades : Pantalla

    @Serializable
    data class ActividadeDetalle( val id: Int ) : Pantalla

    @Serializable
    data class Buscar( val termo: String ) : Pantalla

    @Serializable
    data object Idioma : Pantalla

    //@Serializable
    //data object Animacions : Pantalla

    //Creación

    @Serializable
    data object ListarActividades : Pantalla

    @Serializable
    data object CrearActividade : Pantalla

    @Serializable
    data object ModificarActividade : Pantalla

    //Administración

    @Serializable
    data object Administrar : Pantalla

}