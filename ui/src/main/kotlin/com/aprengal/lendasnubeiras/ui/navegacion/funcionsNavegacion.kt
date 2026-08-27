package com.aprengal.lendasnubeiras.ui.navegacion

import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import com.aprengal.lendasnubeiras.data.localizacion.L10nSingular
import com.aprengal.lendasnubeiras.data.usuarios.PodeAcceder
import com.aprengal.lendasnubeiras.data.usuarios.PodeAdministrar
import com.aprengal.lendasnubeiras.data.usuarios.PodeCrear
import com.aprengal.lendasnubeiras.data.usuarios.PodeEditar
import com.aprengal.lendasnubeiras.data.usuarios.PodeIniciarSesion
import com.aprengal.lendasnubeiras.data.usuarios.PodeLer
import com.aprengal.lendasnubeiras.data.usuarios.PodeRexistrarse
import com.aprengal.lendasnubeiras.ui.reutilizables.LocalPantalla
import com.aprengal.lendasnubeiras.ui.reutilizables.Texto
import kotlin.reflect.KClass

fun collerPantallaInicial(): Pantalla {

    val pantalla = when {
        PodeLer() -> Pantalla.Inicio
        PodeRexistrarse() -> Pantalla.Benvida
        else -> error( "Non se puido determinar a pantalla inicial" )
    }

    return pantalla

}

private fun verificarAcceso( pantalla: KClass<out Pantalla> ): Boolean {

    val permiso = when ( pantalla ) {

        //Calquera
        Pantalla.Axustes::class -> PodeAcceder

        // Autenticación
        Pantalla.Benvida::class -> PodeIniciarSesion
        Pantalla.Acceso::class -> PodeIniciarSesion
        Pantalla.Rexistro::class -> PodeRexistrarse

        // Lectura
        Pantalla.Inicio::class -> PodeLer
        Pantalla.Actividades::class -> PodeLer
        Pantalla.ActividadeDetalle::class -> PodeLer
        Pantalla.Buscar::class -> PodeLer
        Pantalla.Idioma::class -> PodeLer

        // Creación
        Pantalla.ListarActividades::class -> PodeCrear
        Pantalla.CrearActividade::class -> PodeCrear
        Pantalla.ModificarActividade::class -> PodeEditar

        // Administración
        Pantalla.Administrar::class -> PodeAdministrar

        else -> error( "A pantalla ${ pantalla.simpleName } non ten permiso asignado" )

    }

    return permiso()

}

@Composable
fun comprobarAcceso( controlador: NavHostController, pantalla: KClass<out Pantalla> ): Boolean {

    if ( verificarAcceso( pantalla ) ) return true

    Log.wtf( "PERMISO", "Tratouse de realizar un acceso indebido" )
    val pantallaInicial = collerPantallaInicial()

    LaunchedEffect( Unit ) {
        controlador.navigate( pantallaInicial ) { popUpTo( 0 ) }
    }

    return false

}

@Composable
fun AmosarTitulo() {

    val titulo = when ( LocalPantalla.current ) {
        //Pantalla.Actividades -> TODO()
        Pantalla.Axustes -> L10nSingular.TITULO_AXUSTES
        Pantalla.Benvida -> L10nSingular.TITULO_BENVIDA
        //is Pantalla.Buscar -> TODO()
        //Pantalla.CrearActividade -> TODO()
        //Pantalla.Idioma -> TODO()
        Pantalla.Acceso -> L10nSingular.TITULO_ACCESO
        //Pantalla.Inicio -> TODO()
        //Pantalla.ListarActividades -> TODO()
        //Pantalla.ModificarActividade -> TODO()
        Pantalla.Rexistro -> L10nSingular.TITULO_REXISTRO
        else -> error( "A pantalla ${ LocalPantalla.current::class.simpleName } non ten título asignado" )
    }

    Texto( titulo, estilo = MaterialTheme.typography.headlineLarge  )

}