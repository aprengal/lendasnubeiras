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
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeCrear
import com.aprengal.lendasnubeiras.data.usuarios.SesionActual.usuarioActual
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.ui.Alignment
import com.aprengal.lendasnubeiras.ui.navegacion.Navegacion
import com.aprengal.lendasnubeiras.ui.navegacion.Ruta
import com.aprengal.lendasnubeiras.ui.navegacion.Locais.LocalAviso
import com.aprengal.lendasnubeiras.ui.reutilizables.clases.Icona.Companion.DebuxarIconaMenu
import com.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.Logo

@Composable
internal fun EstruturaBase( navegacion: Navegacion, contido: @Composable () -> Unit ) {

    val aviso = LocalAviso.current
    val amosarAccion = PodeCrear( usuarioActual() )

    Scaffold(
        topBar = { NavegacionSuperior( navegacion ) },
        bottomBar = { NavegacionInferior( navegacion ) },
        snackbarHost = { SnackbarHost( aviso ) },
        floatingActionButton = { if ( amosarAccion ) BotonCrearActividade( navegacion ) }
    ) { recheoInterno ->

        Column( modifier = Modifier.fillMaxSize().padding( recheoInterno ).padding( 10.dp ) ) {
            contido()
        }

    }

}

@Composable
private fun NavegacionSuperior( navegacion: Navegacion ) {

    val modificadorFila = Modifier.fillMaxWidth()
        .windowInsetsPadding( WindowInsets.systemBars.only( WindowInsetsSides.Top + WindowInsetsSides.Horizontal ) )
        .height( 64.dp ).absolutePadding( left = 16.dp, right = 4.dp )

    Column {

        Row( modifier = modificadorFila, verticalAlignment = Alignment.CenterVertically ) {
            Logo()
            Spacer( Modifier.weight( 1f ) )
            IconaAxustes( navegacion )
        }

        HorizontalDivider()

    }

}

@Composable
private fun NavegacionInferior( navegacion: Navegacion ) {

    val densidade = LocalDensity.current
    val insetInferior = WindowInsets.navigationBars.getBottom( densidade )
    val alturaTotal = 64.dp + with( densidade ) { insetInferior.toDp() }

    val elementos = listOf(
        Ruta.Inicio,
        Ruta.Buscar,
        Ruta.Idioma,
        Ruta.Actividades
    )

    NavigationBar( modifier = Modifier.heightIn( max = alturaTotal ) ) {

        for ( elemento in elementos ) {

            val seleccionado = navegacion.rutaActiva( elemento::class ) || navegacion.ultimaMenu == elemento::class
            val accion = { navegacion.seleccionarInferior( elemento ) }
            val icona: @Composable () -> Unit = { DebuxarIconaMenu( elemento, 20.sp ) }

            NavigationBarItem( seleccionado, accion, icona )

        }

    }

}

@Composable
private fun BotonCrearActividade( navegacion: Navegacion ) {

    val ruta = Ruta.CrearActividade
    val accion = { navegacion.engadir( ruta ) }

    FloatingActionButton( onClick = accion, shape = CircleShape ) {
        DebuxarIconaMenu( ruta, 20.sp )
    }

}