package org.aprengal.lendasnubeiras.ui.navegacion

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Rutas de navegación da aplicación.
 *
 * Cada subtipo representa unha pantalla. Como implementan [NavKey], poden
 * gardarse na pila de navegación de Navigation 3 e serializarse para
 * conservar o estado (por exemplo, ao rotar a pantalla).
 */
@Serializable
sealed interface Ruta: NavKey {

    // Autenticación

    /** Pantalla de benvida: permite iniciar sesión con conta ou como anónimo. */
    @Serializable
    object Benvida : Ruta

    /** Pantalla de acceso: inicio de sesión co correo electrónico. */
    @Serializable
    object Acceso : Ruta

    /** Pantalla de rexistro: creación dunha conta nova. */
    @Serializable
    object Rexistro : Ruta

    // Lectura

    /** Pantalla principal da aplicación. */
    @Serializable
    object Inicio : Ruta

    /** Axustes da aplicación: tema e peche de sesión. */
    @Serializable
    object Axustes : Ruta

    /** Catálogo con todas as actividades dispoñibles. */
    @Serializable
    object Catalogo : Ruta

    /**
     * Detalle dunha actividade concreta.
     *
     * @property clave Identificador da actividade que se vai mostrar.
     */
    @Serializable
    data class ActividadeDetalle(val clave: String) : Ruta

    /** Pantalla de busca de actividades mediante campos pechados. */
    @Serializable
    object Buscar : Ruta

    /**
     * Resultado dunha busca.
     *
     * @property termo Valor da busca co que se obtén o resultado.
     */
    @Serializable
    data class BuscaDetalle(val termo: String) : Ruta

    /*@Serializable
    object Idioma : Ruta*/

    //Creación

    /** Listaxe das actividades que o usuario pode xestionar. */
    @Serializable
    object ListarActividades : Ruta

    /** Formulario de creación dunha actividade nova. */
    @Serializable
    object CrearActividade : Ruta

    /** Formulario de modificación dunha actividade existente. */
    @Serializable
    object ModificarActividade : Ruta

    // Administración

    /** Pantalla de administración, só para usuarios con rol de administrador. */
    @Serializable
    object Administrar : Ruta

}