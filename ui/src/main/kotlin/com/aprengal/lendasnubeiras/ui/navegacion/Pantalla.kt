package com.aprengal.lendasnubeiras.ui.navegacion

import com.aprengal.lendasnubeiras.data.usuarios.Permiso
import com.aprengal.lendasnubeiras.data.usuarios.PodeAdministrar
import com.aprengal.lendasnubeiras.data.usuarios.PodeCrear
import com.aprengal.lendasnubeiras.data.usuarios.PodeEditar
import com.aprengal.lendasnubeiras.data.usuarios.PodeIniciarSesion
import com.aprengal.lendasnubeiras.data.usuarios.PodeLer
import com.aprengal.lendasnubeiras.data.usuarios.PodeRexistrarse

sealed class Pantalla( val ruta: String, val tipo: TIPO, val enlaces: Boolean, val permiso: Permiso ) {

    enum class TIPO { SCAFFOLD, SOSUPERIOR, SEN_MENUS }

    //Autenticación
    object Apertura: Pantalla( "apertura", TIPO.SEN_MENUS, false, PodeIniciarSesion )
    object IniciarSesion: Pantalla( "iniciar-sesion", TIPO.SEN_MENUS, false, PodeIniciarSesion )
    object Rexistro: Pantalla( "rexistro", TIPO.SEN_MENUS, false, PodeRexistrarse )

    //Lectura
    object Inicio: Pantalla( "inicio", TIPO.SCAFFOLD, false, PodeLer )
    object Axustes: Pantalla( "axustes", TIPO.SOSUPERIOR, true, PodeLer )
    object Actividades: Pantalla( "actividades", TIPO.SOSUPERIOR, true, PodeLer )
    object ActividadeDetalle: Pantalla( "actividade-detalle/{id}", TIPO.SOSUPERIOR, true, PodeLer )

    object Buscar : Pantalla( "buscar/{termo}", TIPO.SCAFFOLD, true, PodeLer )

    object Idioma: Pantalla( "idioma", TIPO.SCAFFOLD, true, PodeLer )
    object Animacions: Pantalla( "animacions", TIPO.SCAFFOLD, true, PodeLer )

    //Creación
    object ListarActividades: Pantalla( "listar-actividades", TIPO.SOSUPERIOR, false, PodeCrear )
    object CrearActividade: Pantalla( "crear-actividade", TIPO.SOSUPERIOR, false, PodeCrear )
    object ModificarActividade: Pantalla( "modificar-actividade", TIPO.SOSUPERIOR, false, PodeEditar )

    //Administración
    object Administrar: Pantalla( "administrar", TIPO.SOSUPERIOR, false, PodeAdministrar )

    fun crearRuta( vararg valores: Any ): String {

        var rutaModificable = ruta

        val aperturas = rutaModificable.count{ c -> c == '{' }
        val peches = rutaModificable.count { c -> c == '}' }

        check( aperturas == peches ) { "Ruta mal formada: $ruta" }
        check( valores.size == aperturas ) { "Número incorrecto de argumentos para a ruta: $ruta (${ valores.contentToString() })" }

        for ( valor in valores ) {

            val inicio = rutaModificable.indexOf( "{" )
            val fin = rutaModificable.indexOf( "}" )

            rutaModificable = rutaModificable.replaceRange( inicio, fin + 1, valor.toString() )

        }

        return rutaModificable

    }

}