package com.aprengal.lendasnubeiras.ui.reutilizables.estruturas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.aprengal.lendasnubeiras.data.localizacion.L10nSingular
import com.aprengal.lendasnubeiras.data.usuarios.PodeCrear
import com.aprengal.lendasnubeiras.data.usuarios.SesionActual.usuarioActual
import com.aprengal.lendasnubeiras.ui.R
import com.aprengal.lendasnubeiras.ui.reutilizables.Icona
import com.aprengal.lendasnubeiras.ui.reutilizables.LocalIdioma
import com.aprengal.lendasnubeiras.ui.reutilizables.Logo
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.Alignment
import com.aprengal.lendasnubeiras.ui.navegacion.Pantalla
import com.aprengal.lendasnubeiras.ui.navegacion.TipoNavegacion

@Composable
internal fun EstruturaBase( controlador: NavHostController, contido: @Composable () -> Unit ) {

    val aviso = remember { SnackbarHostState() }
    val amosarAccion = PodeCrear( usuarioActual() )

    LaunchedEffect( LocalIdioma.current ) {
        aviso.currentSnackbarData?.dismiss()
    }

    Scaffold(
        topBar = { NavegacionSuperior( controlador) },
        bottomBar = { NavegacionInferior( controlador ) },
        snackbarHost = { SnackbarHost( aviso ) },
        floatingActionButton = { if ( amosarAccion ) BotonCrearActividade( controlador ) }
    ) { recheoInterno ->

        Column( modifier = Modifier.fillMaxSize().padding( recheoInterno ).padding( start = 10.dp, end = 10.dp ) ) {
            contido()
        }

    }

}

@Composable
private fun NavegacionSuperior( controlador: NavHostController ) {

    val elementos = listOf( Pantalla.Axustes )
    val modificador = Modifier.fillMaxWidth()
        .windowInsetsPadding( WindowInsets.statusBars.only( WindowInsetsSides.Top ) )
        .height( 64.dp ).padding( start = 16.dp, end = 4.dp )

    val entradaNavegacion by controlador.currentBackStackEntryAsState()

    Column {

        Row( modifier = modificador, verticalAlignment = Alignment.CenterVertically ) {

            Logo()
            Spacer( Modifier.weight( 1f ) )

            for ( elemento in elementos ) {

                val seleccionado = entradaNavegacion?.destination?.hierarchy?.any { pantalla -> pantalla.hasRoute( elemento::class ) } == true
                val accion = { controlador.navigate( elemento ) { launchSingleTop = true } }

                IconButton( enabled = !seleccionado, onClick = accion ) {
                    DebuxarIconaMenu( elemento )
                }

            }

        }

        HorizontalDivider( thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface )

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

            NavigationBarItem( enabled = !seleccionado, selected = seleccionado, onClick = accion, icon = { DebuxarIconaMenu( elemento ) } )

        }

    }

}

@Composable
private fun BotonCrearActividade( controlador: NavHostController ) {

    val pantalla = Pantalla.CrearActividade

    FloatingActionButton( onClick = { controlador.navigate( pantalla ) },
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary, shape = CircleShape ) {
        DebuxarIconaMenu( pantalla )
    }

}

@Composable
private fun DebuxarIconaMenu( pantalla: Pantalla ) {

    val ( icona, descricion ) = when ( pantalla ) {
        Pantalla.Inicio -> Pair( Icona.INICIO, L10nSingular.MENU_INICIO )
        is Pantalla.Buscar -> Pair( Icona.BUSCAR, L10nSingular.MENU_BUSCAR )
        Pantalla.Idioma -> Pair( Icona.IDIOMA, L10nSingular.MENU_IDIOMA )
        Pantalla.Axustes -> Pair( Icona.AXUSTES, L10nSingular.MENU_AXUSTES )
        Pantalla.CrearActividade -> Pair( Icona.ENGADIR, L10nSingular.MENU_CREAR )
        Pantalla.Actividades -> Pair( Icona.IDIOMA, L10nSingular.MENU_ACTIVIDADES )
        else -> error( "A pantalla ${ pantalla::class.simpleName } non ten icona asignada" )
    }

    val fontFamily = FontFamily( Font( R.font.ubuntu_iconas_nerd, FontWeight.Bold ) )

    Text(
        text = icona.codigo,
        fontFamily = fontFamily,
        color = LocalContentColor.current,
        fontSize = 20.sp,
        lineHeight = 1.sp,
        modifier = Modifier.semantics {
            this.contentDescription = descricion.texto()
        }
    )

}