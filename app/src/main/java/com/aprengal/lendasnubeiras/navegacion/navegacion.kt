package com.aprengal.lendasnubeiras.navegacion


import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aprengal.lendasnubeiras.tema.Logo
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@OptIn( ExperimentalMaterial3Api::class )
@Composable
fun PantallaScaffold( controlador: NavHostController, pantallaActual: Pantalla, navegacionContido: @Composable () -> Unit ) {

    val rutaActual = pantallaActual.ruta

    val elementosSuperior = remember { listOf(
        Pantalla.Axustes, Pantalla.Detalle
    ) }

    //Igual se podería prescindir desta barra se só se pon unha ó final
    val elementosSuperiorFiltrados = elementosSuperior.filter { it.ruta != rutaActual }

    val elementosInferior = remember { listOf(
        Pantalla.Inicio,
        Pantalla.Perfil,
        Pantalla.Rexistro,
        Pantalla.IniciarSesion,
        Pantalla.Idioma,
        Pantalla.Animacions
    ) }

    Scaffold(
        topBar = { NavegacionSuperior( elementosSuperiorFiltrados, rutaActual, controlador, pantallaActual.navSuperior ) },
        bottomBar = { NavegacionInferior( elementosInferior, rutaActual, controlador, pantallaActual.navInferior ) },
    ) { recheoInterno ->

        Column( modifier = Modifier.fillMaxSize().padding( recheoInterno ).padding( start = 10.dp, end = 10.dp ) ) {
            navegacionContido()
        }

    }

}

@Composable
fun NavegacionPrincipal() {

    val controlador = rememberNavController()
    val backStackEntry by controlador.currentBackStackEntryAsState()
    val pantallaActual = Pantalla.todas.find { it.ruta == backStackEntry?.destination?.route } ?: Pantalla.Inicio

    PantallaScaffold( controlador, pantallaActual ) {
        NavHostContido( controlador )
    }

}

@OptIn( ExperimentalMaterial3Api::class )
@Composable
fun NavegacionSuperior(
    elementos: List<Pantalla>,
    rutaActual: String,
    controlador: NavHostController,
    amosar: Boolean
) {

    val densidade = LocalDensity.current
    val insetSuperior = WindowInsets.statusBars.getTop( densidade )
    val alturaInsetDp = with(densidade) { insetSuperior.toDp() }
    val alturaTotal = 86.dp

    val valorInicial = if (amosar) alturaTotal else alturaInsetDp

    var alturaGuardadaValue by rememberSaveable {
        mutableFloatStateOf( valorInicial.value )
    }

    LaunchedEffect(amosar, alturaTotal ) {
        alturaGuardadaValue = ( if ( amosar ) alturaTotal else alturaInsetDp ).value
    }

    val alturaDeseada = alturaGuardadaValue.dp

    val altura by animateDpAsState(
        targetValue = alturaDeseada,
        animationSpec = tween(),
        label = "alturaTopBar"
    )

    Column {

        Box( modifier = Modifier.heightIn( max = altura ).clipToBounds() ) {

            TopAppBar(
                title = { Logo() },
                modifier = Modifier.heightIn( max = 86.dp ).background( Color.Cyan ),
                actions = {

                    elementos.forEach { elemento ->

                        val seleccionado = rutaActual == elemento.ruta

                        val colorFondo by animateColorAsState(
                            targetValue = if ( seleccionado ) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
                            label = "colorFondo"
                        )

                        IconButton(
                            onClick = {

                                if ( !seleccionado ) {

                                    val rutaDestino = when ( elemento ) {
                                        is Pantalla.Detalle -> elemento.crearRuta(
                                            "666",
                                            "resacón"
                                        )

                                        else -> elemento.ruta
                                    }

                                    controlador.navigate( rutaDestino ) {
                                        launchSingleTop = true
                                    }

                                }

                            },
                            modifier = Modifier.clip( RoundedCornerShape( 12.dp ) ).background( colorFondo )
                        ) {
                            elemento.icono()
                        }

                    }

                }
            )

        }

        HorizontalDivider( thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface )

    }

}

@Composable
fun NavegacionInferior(
    elementos: List<Pantalla>,
    rutaActual: String,
    controlador: NavHostController,
    amosar: Boolean
) {

    val densidade = LocalDensity.current
    val insetInferior = WindowInsets.navigationBars.getBottom( densidade )
    val alturaTotal = 64.dp + with( densidade ) { insetInferior.toDp() }

    val valorInicial = if (amosar) alturaTotal else 0.dp

    var alturaGuardadaValue by rememberSaveable { mutableFloatStateOf( valorInicial.value ) }

    // Sincronizamos se o parámetro externo cambia (por exemplo, ao navegar)
    LaunchedEffect( amosar, alturaTotal ) {
        alturaGuardadaValue = ( if ( amosar ) alturaTotal else 0.dp ).value
    }

    val alturaDeseada = alturaGuardadaValue.dp

    val altura by animateDpAsState(
        targetValue = alturaDeseada,
        animationSpec = tween(),
        label = "alturaBottomBar"
    )

    Box( modifier = Modifier.height( altura ).clipToBounds() ) {

        NavigationBar {
            elementos.forEach { elemento ->
                NavigationBarItem(
                    selected = rutaActual == elemento.ruta,
                    onClick = {
                        if ( rutaActual != elemento.ruta ) {
                            controlador.navigate( elemento.ruta ) {
                                launchSingleTop = true
                            }
                        }
                    },
                    icon = { elemento.icono() },
                    label = { Text( elemento.nome, maxLines = 1, overflow = TextOverflow.Ellipsis ) }
                )
            }
        }

    }

}

@Composable
private fun NavHostContido( controlador: NavHostController ) {

    NavHost( navController = controlador, startDestination = Pantalla.Inicio.ruta ) {

        Pantalla.todas.forEach { pantalla ->

            val argumentos = pantalla.ruta.split( "/{" ).drop( 1 ).map { it.removeSuffix( "}" ) }

            composable(
                route = pantalla.ruta,
                arguments = argumentos.map { nome ->
                    navArgument( nome ) { type = NavType.StringType }
                },
                enterTransition = { transicionEntrada( pantalla ) },
                exitTransition = { transicionSaida( pantalla ) },
                popEnterTransition = { transicionAtrasEntrada( pantalla ) },
                popExitTransition = { transicionAtrasSaida( pantalla ) },
                deepLinks = pantalla.enlaces
            ) { backStackEntry ->
                pantalla.contido( backStackEntry )
            }

        }
    }

}

fun transicionEntrada( pantalla: Pantalla ): EnterTransition {

    val transicion = if ( !pantalla.navInferior ) {
        slideInHorizontally( tween() ) { ancho -> ancho }
    } else {
        fadeIn( tween() )
    }

    return transicion

}

fun transicionSaida( pantalla: Pantalla ): ExitTransition {

    val transicion = if ( !pantalla.navInferior ) {
        slideOutHorizontally( tween() ) { ancho -> -ancho }
    } else {
        fadeOut( tween() )
    }

    return transicion

}

fun transicionAtrasEntrada( pantalla: Pantalla ): EnterTransition {

    val transicion = if ( !pantalla.navInferior ) {
        slideInHorizontally( tween() ) { ancho -> -ancho }
    } else {
        fadeIn( tween() )
    }

    return transicion

}

fun transicionAtrasSaida( pantalla: Pantalla ): ExitTransition {

    val transicion = if ( !pantalla.navInferior ) {
        slideOutHorizontally( tween() ) { ancho -> ancho }
    } else {
        fadeOut( tween() )
    }

    return transicion

}