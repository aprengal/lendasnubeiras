package org.aprengal.lendasnubeiras.ui.reutilizables.estruturas

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
import org.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeCrear
import org.aprengal.lendasnubeiras.data.usuarios.SesionActual.usuarioActual
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.ui.Alignment
import org.aprengal.lendasnubeiras.ui.navegacion.Navegacion
import org.aprengal.lendasnubeiras.ui.navegacion.Ruta
import org.aprengal.lendasnubeiras.ui.navegacion.Locais.LocalAviso
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.Icona.Companion.DebuxarIconaMenu
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.Logo

/**
 * Estrutura principal das páxinas da aplicación mediante [Scaffold].
 *
 * Integra as barras de navegación superior e inferior, o sistema de avisos
 * mediante [SnackbarHost] e o botón flotante para crear actividades cando
 * o usuario actual dispón do permiso correspondente.
 *
 * O contido da páxina colócase na área central, respectando os espazos
 * reservados polos compoñentes do [Scaffold].
 *
 * @param navegacion Controlador de navegación empregado polas barras e accións da interface.
 * @param contido Contido composable específico da páxina.
 */
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

        Column( modifier = Modifier.fillMaxSize().padding( recheoInterno ).padding( horizontal = 10.dp ) ) {
            contido()
        }

    }

}

/**
 * Debuxa a cabeceira superior da navegación principal.
 *
 * Amosa o logotipo da aplicación e a icona de acceso aos axustes,
 * respectando os espazos do sistema na parte superior da pantalla.
 *
 * @param navegacion Controlador de navegación empregado para acceder aos axustes.
 */
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

/**
 * Debuxa a barra de navegación inferior coas rutas principais da aplicación.
 *
 * Inclúe os accesos ao inicio, ao buscador e ao catálogo, indicando
 * visualmente a ruta seleccionada e permitindo cambiar de sección.
 *
 * @param navegacion Controlador de navegación empregado para seleccionar as rutas.
 */
@Composable
private fun NavegacionInferior( navegacion: Navegacion ) {

    val densidade = LocalDensity.current
    val insetInferior = WindowInsets.navigationBars.getBottom( densidade )
    val alturaTotal = 64.dp + with( densidade ) { insetInferior.toDp() }

    val elementos = listOf(
        Ruta.Inicio,
        Ruta.Buscador,
        Ruta.Catalogo
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

/**
 * Debuxa o botón flotante para acceder á creación dunha actividade.
 *
 * Ao premelo, engade a ruta de creación de actividades á navegación.
 *
 * @param navegacion Controlador de navegación empregado para abrir a pantalla de creación.
 */
@Composable
private fun BotonCrearActividade( navegacion: Navegacion ) {

    val ruta = Ruta.CrearActividade
    val accion = { navegacion.engadir( ruta ) }

    FloatingActionButton( onClick = accion, shape = CircleShape ) {
        DebuxarIconaMenu( ruta, 20.sp )
    }

}