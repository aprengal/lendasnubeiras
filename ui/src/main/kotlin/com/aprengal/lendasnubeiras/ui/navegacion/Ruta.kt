package com.aprengal.lendasnubeiras.ui.navegacion

import kotlinx.serialization.Serializable

@Serializable
sealed interface Ruta {

    //Autenticación

    @Serializable
    object Benvida : Ruta

    @Serializable
    object Acceso : Ruta

    @Serializable
    object Rexistro : Ruta

    //Lectura

    @Serializable
    object Inicio : Ruta

    @Serializable
    object Axustes : Ruta

    @Serializable
    object Actividades : Ruta

    @Serializable
    data class ActividadeDetalle( val id: Long ) : Ruta

    @Serializable
    data class Buscar( val termo: String ) : Ruta

    @Serializable
    object Idioma : Ruta

    //Creación

    @Serializable
    object ListarActividades : Ruta

    @Serializable
    object CrearActividade : Ruta

    @Serializable
    object ModificarActividade : Ruta

    //Administración

    @Serializable
    object Administrar : Ruta

}