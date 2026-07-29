package com.aprengal.lendasnubeiras.navegacion

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aprengal.lendasnubeiras.tema.Logo

//TODO: Mirar se as transicións son normais ou se está disparando o consumo de memoria ou hai lagazos
@OptIn( ExperimentalMaterial3Api::class )
@Composable
fun PantallaPrincipal() {

    val controlador = rememberNavController()
    val pantallaActual by controlador.currentBackStackEntryAsState()
    val rutaActual = pantallaActual?.destination?.route ?: Pantalla.Inicio.ruta

    val elementosSuperior = remember { listOf(
        Pantalla.Axustes, Pantalla.Detalle
    ) }

    //Igual se podería prescindir desta barra se só se pon unha ó final
    val elementosSuperiorFiltrados = elementosSuperior.filter { it.ruta != rutaActual }

    val elementosInferior = remember { listOf(
        Pantalla.Inicio,
        Pantalla.Perfil,
        Pantalla.Mapa,
        Pantalla.Idioma,
        Pantalla.Animacions
    ) }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val configuracion = LocalConfiguration.current
    val horizontal = configuracion.orientation == Configuration.ORIENTATION_LANDSCAPE
    val pantallaMapa = rutaActual == Pantalla.Mapa.ruta

    AnimarNavegacionSuperior(
        desprazamento = scrollBehavior,
        clave = rutaActual,
        ocultar = pantallaMapa && horizontal
    )

    val amosarInferior = elementosInferior.any { it.ruta == rutaActual } && !( horizontal && pantallaMapa )
    val progresoAgochar = animarNavegacionInferior( clave = rutaActual, agochar = !amosarInferior )

    Scaffold(
        modifier = Modifier.nestedScroll( scrollBehavior.nestedScrollConnection ),
        topBar = { NavegacionSuperior( scrollBehavior, elementosSuperiorFiltrados, rutaActual, controlador ) },
        bottomBar = { NavegacionInferior( elementosInferior, rutaActual, controlador, progresoAgochar ) },
    ) { recheoInterno ->

        Column( modifier = Modifier.fillMaxSize().padding( recheoInterno ).padding( start = 10.dp, end = 10.dp ) ) {
            NavegacionPrincipal( controlador )
        }

    }

}

@Composable
fun NavegacionPrincipal( controlador: NavHostController ) {

    NavHost(
        navController = controlador,
        startDestination = Pantalla.Inicio.ruta
    ) {

        Pantalla.todas.forEach { pantalla ->

            val argumentos = pantalla.ruta.split( "/{" ).drop( 1 ).map { it.removeSuffix( "}" ) }

            composable(
                route = pantalla.ruta,
                arguments = argumentos.map { nome ->
                    navArgument( nome ) { type = NavType.StringType }
                },
                deepLinks = pantalla.enlaces
            ) { backStackEntry ->
                pantalla.contido( backStackEntry )
            }

        }

    }

}

@OptIn( ExperimentalMaterial3Api::class )
@Composable
fun NavegacionSuperior(
    scroll: TopAppBarScrollBehavior,
    elementos: List<Pantalla>,
    rutaActual: String,
    controlador: NavHostController
) {

    Column {

        TopAppBar(
            title = { Logo() },
            scrollBehavior = scroll,
            colors = TopAppBarDefaults.topAppBarColors( //Cor asignada ó scroll ó chegar arriba de todo
                scrolledContainerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.heightIn( max = 86.dp ),
            actions = {

                elementos.forEach { elemento ->

                    val seleccionado = rutaActual == elemento.ruta

                    val colorFondo by animateColorAsState( //Este bloque igual se pode quitar se só hai un icono ao final
                        targetValue = if (seleccionado) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
                        label = "colorFondo"
                    )

                    IconButton(
                        onClick = {

                            if ( !seleccionado ) {

                                val rutaDestino = when (elemento) {
                                    is Pantalla.Detalle -> elemento.crearRuta(
                                        "666",
                                        "resacón"
                                    )

                                    else -> elemento.ruta
                                }

                                controlador.navigate(rutaDestino) {
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

        HorizontalDivider( thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface )

    }

}

@Composable
fun NavegacionInferior(
    elementos: List<Pantalla>,
    rutaActual: String,
    controlador: NavHostController,
    progresoAgochar: Float,
) {

    var alturaBarra by remember { mutableStateOf<Dp?>( null ) }

    val densidade = LocalDensity.current
    val bottomInsetDp = with( densidade ) {
        WindowInsets.navigationBars.getBottom( densidade ).toDp()
    }

    val modificadorExterior = alturaBarra?.let { altura ->
        Modifier.heightIn( max = altura * ( 1f - progresoAgochar ) )
    } ?: Modifier

    val modificadorInterior = Modifier.height( 64.dp + bottomInsetDp )
        .onSizeChanged { tamano ->
            if ( progresoAgochar == 0f ) {
                alturaBarra = with( densidade ) { tamano.height.toDp() }
            }
        }
        .graphicsLayer { translationY = size.height * progresoAgochar }

    Box( modifier = modificadorExterior.clipToBounds() ) {

        NavigationBar( modifier = modificadorInterior ) {
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