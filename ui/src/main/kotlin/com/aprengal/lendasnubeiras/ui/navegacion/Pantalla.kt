package com.aprengal.lendasnubeiras.ui.navegacion

import com.aprengal.lendasnubeiras.data.usuarios.Permisos
import com.aprengal.lendasnubeiras.data.usuarios.Usuario

sealed class Pantalla( val tipo: TIPO, val enlaces: Boolean, val permiso: ( Usuario ) -> Boolean = { true } ) {

    enum class TIPO { SCAFFOLD, SOSUPERIOR, SEN_MENUS }

    open val ruta: String = this::class.simpleName!!.lowercase()

    //Autenticación
    object Apertura: Pantalla( TIPO.SEN_MENUS, false, Permisos::podeIniciarSesion )
    object Rexistro: Pantalla( TIPO.SEN_MENUS, false, Permisos::podeIniciarSesion )
    object IniciarSesion: Pantalla( TIPO.SEN_MENUS, false, Permisos::podeIniciarSesion )

    //Lectura
    object Inicio: Pantalla( TIPO.SCAFFOLD, false, Permisos::podeLer )
    object Axustes: Pantalla( TIPO.SOSUPERIOR, true, Permisos::podeLer )
    object Actividades: Pantalla( TIPO.SOSUPERIOR, true, Permisos::podeLer )

    object ActividadeDetalle: Pantalla( TIPO.SOSUPERIOR, true, Permisos::podeLer ) {
        override val ruta: String = "${super.ruta}/{id}"
    }

    /*object Detalle : Pantalla( TIPO.SCAFFOLD, true ) {
        override val ruta: String = "${super.ruta}/{id}/{test}"
    }*/

    object Buscar : Pantalla( TIPO.SCAFFOLD, true, Permisos::podeLer ) {
        override val ruta: String = "${super.ruta}/{termo}"
    }

    //object Mapa: Pantalla( TIPO.SCAFFOLD, true )
    object Idioma: Pantalla( TIPO.SCAFFOLD, true )
    object Animacions: Pantalla( TIPO.SCAFFOLD, true )

    //Creación
    object ListarActividades: Pantalla( TIPO.SOSUPERIOR, false, Permisos::podeCrear )
    object CrearActividade: Pantalla( TIPO.SOSUPERIOR, false, Permisos::podeCrear )
    object ModificarActividade: Pantalla( TIPO.SOSUPERIOR, false, Permisos::podeEditar )

    //Administración
    object Administrar: Pantalla( TIPO.SOSUPERIOR, false, Permisos::podeAdministrar )

    fun crearRuta( vararg valores: Any ): String {

        var rutaModificable = ruta

        val aperturas = rutaModificable.count{ c -> c == '{' }
        val cierres = rutaModificable.count { c -> c == '}' }

        check( aperturas == cierres ) { "Ruta mal formada: $ruta" }
        check( valores.size == aperturas ) { "Número incorrecto de argumentos para a ruta: $ruta (${ valores.contentToString() })" }

        for ( valor in valores ) {

            val inicio = rutaModificable.indexOf( "{" )
            val fin = rutaModificable.indexOf( "}" )

            rutaModificable = rutaModificable.replaceRange( inicio, fin + 1, valor.toString() )

        }

        return rutaModificable

    }

}