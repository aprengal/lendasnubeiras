package com.aprengal.lendasnubeiras.ui.reutilizables.estruturas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.aprengal.lendasnubeiras.ui.navegacion.Ruta
import com.aprengal.lendasnubeiras.ui.reutilizables.AmosarTitulo
import com.aprengal.lendasnubeiras.ui.reutilizables.DebuxarIconaMenu
import com.aprengal.lendasnubeiras.ui.reutilizables.EspazadorAlto
import com.aprengal.lendasnubeiras.ui.reutilizables.Logo

@Composable
fun EstruturaApertura( controlador: NavHostController, contido: @Composable () -> Unit ) {

    val modFila = Modifier.height( 64.dp ).absolutePadding( left = 16.dp, right = 4.dp )
    val modCol = Modifier.padding( horizontal = 24.dp, vertical = 16.dp )

    ColocarExtras {

        Row( modifier = modFila.align( Alignment.TopEnd ), verticalAlignment = Alignment.CenterVertically ) {
            IconaAxustes( controlador )
        }

        Column( modCol.align( Alignment.Center ), Arrangement.Center, Alignment.CenterHorizontally ) {
            Logo( 90.dp )
            EspazadorAlto( 2 )
            AmosarTitulo()
            EspazadorAlto()
            contido()
        }

    }

}

@Composable
fun IconaAxustes( controlador: NavHostController ) {

    val elemento = Ruta.Axustes
    val entradaNavegacion by controlador.currentBackStackEntryAsState()
    val dimension = with( LocalDensity.current ) { 30.dp.toSp() }
    val interacion = remember { MutableInteractionSource() }

    val seleccionado = entradaNavegacion?.destination?.hierarchy?.any { ruta -> ruta.hasRoute( elemento::class ) } == true
    val accion = { controlador.navigate( elemento ) { launchSingleTop = true } }
    val modIcona = Modifier.size( 48.dp ).clickable( interacion, null, !seleccionado, onClick = accion )

    Box( modIcona, Alignment.Center ) {
        DebuxarIconaMenu( elemento, dimension )
    }

}