package org.aprengal.lendasnubeiras.ui.navegacion

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import org.aprengal.lendasnubeiras.data.bd.operacions.Actividades.collerActividade
import org.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeAcceder
import org.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeAdministrar
import org.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeCrear
import org.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeEditar
import org.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeIniciarSesion
import org.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeLer
import org.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeRexistrarse
import org.aprengal.lendasnubeiras.ui.navegacion.Locais.LocalNavegacion
import kotlin.reflect.KClass

internal object ControlAcceso {

    //Hai que mirar se isto ao final acaba duplicando comportamento en ListaNavegacion
    //Non se pode navegar á ruta que nunca se rexistrou
    private fun verificarAcceso( ruta: KClass<out Ruta> ): Boolean {

        val permiso = when ( ruta ) {

            //Calquera
            Ruta.Axustes::class -> PodeAcceder

            // Autenticación
            Ruta.Benvida::class -> PodeIniciarSesion
            Ruta.Acceso::class -> PodeIniciarSesion
            Ruta.Rexistro::class -> PodeRexistrarse

            // Lectura
            Ruta.Inicio::class -> PodeLer
            Ruta.Actividades::class -> PodeLer
            Ruta.ActividadeDetalle::class -> PodeLer
            Ruta.Buscar::class -> PodeLer
            Ruta.Idioma::class -> PodeLer

            // Creación
            Ruta.ListarActividades::class -> PodeCrear
            Ruta.CrearActividade::class -> PodeCrear
            Ruta.ModificarActividade::class -> PodeEditar

            // Administración
            Ruta.Administrar::class -> PodeAdministrar

            else -> error( "A ruta ${ ruta.simpleName } non ten permiso asignado" )

        }

        return permiso()

    }

    @Composable
    internal fun comprobarAcceso( ruta: KClass<out Ruta> ): Boolean {

        if ( verificarAcceso( ruta ) ) return true

        val navegacion = LocalNavegacion.current

        Log.wtf( "PERMISO", "Tratouse de realizar un acceso indebido" )
        LaunchedEffect( Unit ) { navegacion.reiniciar() }

        return false

    }

    @Composable
    internal fun verificarRuta( ruta: Ruta ): Boolean {

        val navegacion = LocalNavegacion.current

        val redirixir = when ( ruta ) {
            is Ruta.ActividadeDetalle -> if ( collerActividade( ruta.id ) == null ) Ruta.Actividades else null
            else -> null
        }

        if ( redirixir != null ) {
            LaunchedEffect( redirixir ) { navegacion.redirixir( redirixir ) }
            return false
        }

        return true

    }

}