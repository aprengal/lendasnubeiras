package com.aprengal.lendasnubeiras.ui.reutilizables.estruturas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.aprengal.lendasnubeiras.data.usuarios.PodeCrear
import com.aprengal.lendasnubeiras.data.usuarios.SesionActual.usuarioActual
import com.aprengal.lendasnubeiras.ui.reutilizables.LocalIdioma
import com.aprengal.lendasnubeiras.ui.reutilizables.Logo
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.ui.Alignment
import com.aprengal.lendasnubeiras.ui.navegacion.Pantalla
import com.aprengal.lendasnubeiras.ui.reutilizables.DebuxarIconaMenu
import com.aprengal.lendasnubeiras.ui.reutilizables.LocalAviso

@Composable
internal fun EstruturaBase( controlador: NavHostController, contido: @Composable () -> Unit ) {

    val aviso = LocalAviso.current
    val amosarAccion = PodeCrear( usuarioActual() )

    Scaffold(
        topBar = { NavegacionSuperior( controlador) },
        bottomBar = { NavegacionInferior( controlador ) },
        snackbarHost = { SnackbarHost( aviso ) },
        floatingActionButton = { if ( amosarAccion ) BotonCrearActividade( controlador ) }
    ) { recheoInterno ->

        Column( modifier = Modifier.fillMaxSize().padding( recheoInterno ).padding( 10.dp ) ) {
            contido()
        }

    }

}

@Composable
private fun NavegacionSuperior( controlador: NavHostController ) {

    val modificadorFila = Modifier.fillMaxWidth()
        .windowInsetsPadding( WindowInsets.statusBars.only( WindowInsetsSides.Top ) )
        .height( 64.dp ).absolutePadding( left = 16.dp, right = 4.dp )

    Column {

        Row( modifier = modificadorFila, verticalAlignment = Alignment.CenterVertically ) {
            Logo()
            Spacer( Modifier.weight( 1f ) )
            IconaAxustes( controlador )
        }

        HorizontalDivider()

    }

}

@Composable
private fun NavegacionInferior( controlador: NavHostController ) {

    val densidade = LocalDensity.current
    val insetInferior = WindowInsets.navigationBars.getBottom( densidade )
    val alturaTotal = 64.dp + with( densidade ) { insetInferior.toDp() }

    val elementos = listOf(
        Pantalla.Inicio,
        Pantalla.Buscar( "" ),
        Pantalla.Idioma,
        Pantalla.Actividades
    )

    val entradaNavegacion by controlador.currentBackStackEntryAsState()

    NavigationBar( modifier = Modifier.heightIn( max = alturaTotal ) ) {

        for ( elemento in elementos ) {

            val seleccionado = entradaNavegacion ?.destination?.hierarchy?.any { pantalla -> pantalla.hasRoute( elemento::class ) } == true
            val accion = { controlador.navigate( elemento ) { launchSingleTop = true } }
            val icona: @Composable () -> Unit = { DebuxarIconaMenu( elemento, 20.sp ) }

            NavigationBarItem( seleccionado, accion, icona, enabled = !seleccionado )

        }

    }

}

@Composable
private fun BotonCrearActividade( controlador: NavHostController ) {

    val pantalla = Pantalla.CrearActividade
    val accion = { controlador.navigate( pantalla ) }
    val fondo = MaterialTheme.colorScheme.secondary
    val cor = MaterialTheme.colorScheme.onSecondary

    FloatingActionButton( onClick = accion, containerColor = fondo, contentColor = cor, shape = CircleShape ) {
        DebuxarIconaMenu( pantalla, 20.sp )
    }

}