package com.aprengal.lendasnubeiras.ui.navegacion

import android.util.Log
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.aprengal.lendasnubeiras.data.configuracion.db.DB.collerActividade
import com.aprengal.lendasnubeiras.data.localizacion.Idioma
import com.aprengal.lendasnubeiras.data.usuarios.PodeAcceder
import com.aprengal.lendasnubeiras.data.usuarios.PodeAdministrar
import com.aprengal.lendasnubeiras.data.usuarios.PodeCrear
import com.aprengal.lendasnubeiras.data.usuarios.PodeEditar
import com.aprengal.lendasnubeiras.data.usuarios.PodeIniciarSesion
import com.aprengal.lendasnubeiras.data.usuarios.PodeLer
import com.aprengal.lendasnubeiras.data.usuarios.PodeRexistrarse
import com.aprengal.lendasnubeiras.ui.pantallas.NovaActividade
import com.aprengal.lendasnubeiras.ui.pantallas.PantallaAcceso
import com.aprengal.lendasnubeiras.ui.pantallas.PantallaAxustes
import com.aprengal.lendasnubeiras.ui.pantallas.PantallaBenvida
import com.aprengal.lendasnubeiras.ui.pantallas.PantallaRexistro
import com.aprengal.lendasnubeiras.ui.reutilizables.estruturas.EstruturaBase
import com.aprengal.lendasnubeiras.ui.reutilizables.estruturas.EstruturaSuperior
import com.aprengal.lendasnubeiras.ui.pantallas.lector.PantallaActividade
import com.aprengal.lendasnubeiras.ui.pantallas.lector.PantallaActividadeDetalle
import com.aprengal.lendasnubeiras.ui.pantallas.lector.PantallaBuscador
import com.aprengal.lendasnubeiras.ui.pantallas.lector.PantallaInicio
import com.aprengal.lendasnubeiras.ui.pantallas.lector.actividadesDixitais.XogoDados
import com.aprengal.lendasnubeiras.ui.reutilizables.LocalAviso
import com.aprengal.lendasnubeiras.ui.reutilizables.LocalIdioma
import com.aprengal.lendasnubeiras.ui.reutilizables.LocalRuta
import com.aprengal.lendasnubeiras.ui.reutilizables.estruturas.EstruturaApertura

import com.aprengal.lendasnubeiras.ui.navegacion.Navegacion.TipoPantalla.SOSUPERIOR
import com.aprengal.lendasnubeiras.ui.navegacion.Navegacion.TipoPantalla.COMPLETA
import com.aprengal.lendasnubeiras.ui.navegacion.Navegacion.TipoPantalla.APERTURA
import com.aprengal.lendasnubeiras.ui.navegacion.Navegacion.datosRutas
import com.aprengal.lendasnubeiras.ui.navegacion.Navegacion.haiTransicion
import com.aprengal.lendasnubeiras.ui.reutilizables.LocalControlador
import kotlin.reflect.KClass

private fun NavBackStackEntry.tipo(): Navegacion.TipoPantalla {
    return datosRutas.entries.first { ( clase, _ ) -> destination.hasRoute( clase ) }.value.tipo
}

private inline fun <reified T : Ruta> NavGraphBuilder.ruta( noinline contido: @Composable (T ) -> Unit ) {

    val datos = datosRutas[ T::class ]!!
    val dominio = "nubeiras"
    val enlaces = datos.enlace?.let { ruta -> listOf( navDeepLink { uriPattern = "$dominio://$ruta" } ) } ?: emptyList()

    composable<T>(
        deepLinks = enlaces,
        enterTransition = {
            val orixe = initialState.tipo()
            val destino = targetState.tipo()
            if ( haiTransicion( orixe, destino ) ) destino.entrada else EnterTransition.None
        },
        exitTransition = {
            val orixe = initialState.tipo()
            val destino = targetState.tipo()
            if ( haiTransicion( orixe, destino ) ) orixe.saida else ExitTransition.None
        },
        popEnterTransition = {
            val orixe = initialState.tipo()
            val destino = targetState.tipo()
            if ( haiTransicion( orixe, destino ) ) destino.atrasEntrada else EnterTransition.None
        },
        popExitTransition = {
            val orixe = initialState.tipo()
            val destino = targetState.tipo()
            if ( haiTransicion( orixe, destino ) ) orixe.atrasSaida else ExitTransition.None
        }
    ) { entrada ->

        val controlador = LocalControlador.current

        if ( !comprobarAcceso( controlador, T::class ) ) return@composable

        val ruta = entrada.toRoute<T>()

        if ( !verificarRuta( controlador, ruta  ) ) return@composable

        CompositionLocalProvider( LocalRuta provides ruta ) {

            when ( datos.tipo ) {
                COMPLETA -> EstruturaBase( controlador ) { contido( ruta ) }
                SOSUPERIOR -> EstruturaSuperior { contido( ruta ) }
                APERTURA -> EstruturaApertura( controlador ) { contido( ruta ) }
            }

        }

    }

}

@Composable
fun verificarRuta( controlador: NavHostController, ruta: Ruta ): Boolean {

    val redirixir = when ( ruta ) {
        is Ruta.ActividadeDetalle -> if ( collerActividade( ruta.id ) == null ) Ruta.Actividades else null
        else -> null
    }

    if ( redirixir != null ) {

        LaunchedEffect( redirixir ) {
            controlador.popBackStack()
            controlador.navigate( redirixir ) { launchSingleTop = true }
        }

        return false

    }

    return true

}

@Composable
fun CargarNavegacion( idioma: Idioma, controlador: NavHostController ) {

    val rutaInicial = collerRutaInicial()
    val aviso = remember { SnackbarHostState() }

    LaunchedEffect( idioma ) {
        aviso.currentSnackbarData?.dismiss()
    }

    CompositionLocalProvider( LocalIdioma provides idioma, LocalControlador provides controlador, LocalAviso provides aviso ) {

        NavHost( navController = controlador, startDestination = rutaInicial ) {

            ruta<Ruta.Axustes> { PantallaAxustes() }

            if ( PodeRexistrarse() ) {
                ruta<Ruta.Benvida> { PantallaBenvida( controlador ) }
                ruta<Ruta.Acceso> { PantallaAcceso( controlador ) }
                ruta<Ruta.Rexistro> { PantallaRexistro( controlador ) }
            }

            if ( PodeLer() ) {

                ruta<Ruta.Actividades> { PantallaActividade() }
                ruta<Ruta.ActividadeDetalle> { datos -> PantallaActividadeDetalle( datos.id ) }
                ruta<Ruta.Buscar> { datos -> PantallaBuscador( datos.termo ) }

                ruta<Ruta.Inicio> { PantallaInicio() }
                ruta<Ruta.Idioma> { XogoDados() }

            }

            if ( PodeCrear() ) {
                ruta<Ruta.ListarActividades> { TODO() }
                ruta<Ruta.CrearActividade> { NovaActividade() }
                ruta<Ruta.ModificarActividade> { TODO() }
            }

            if ( PodeAdministrar() ) {
                ruta<Ruta.Administrar> { TODO() }
            }

        }

    }

}

private fun collerRutaInicial(): Ruta {

    val ruta = when {
        PodeLer() -> Ruta.Inicio
        PodeRexistrarse() -> Ruta.Benvida
        else -> error( "Non se puido determinar a ruta inicial" )
    }

    return ruta

}

private fun verificarAcceso(ruta: KClass<out Ruta> ): Boolean {

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
private fun comprobarAcceso( controlador: NavHostController, ruta: KClass<out Ruta> ): Boolean {

    if ( verificarAcceso( ruta ) ) return true

    Log.wtf( "PERMISO", "Tratouse de realizar un acceso indebido" )
    val rutaInicial = collerRutaInicial()

    LaunchedEffect( Unit ) {
        controlador.navigate( rutaInicial ) { popUpTo( 0 ) }
    }

    return false

}