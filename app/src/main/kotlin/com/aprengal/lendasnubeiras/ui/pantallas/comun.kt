package com.aprengal.lendasnubeiras.ui.pantallas

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.aprengal.lendasnubeiras.PantallaDetalle
import com.aprengal.lendasnubeiras.R
import com.aprengal.lendasnubeiras.localizacion.Localizacion.idiomaActual
import com.aprengal.lendasnubeiras.localizacion.Localizacion.l10n
import com.aprengal.lendasnubeiras.navegacion.Navegacion
import com.aprengal.lendasnubeiras.ui.tema.Iconas
import com.aprengal.lendasnubeiras.ui.tema.MirarAnimacions
import com.aprengal.lendasnubeiras.ui.tema.ProbaActividade
import com.aprengal.lendasnubeiras.ui.tema.ProbaTraducions
import com.aprengal.lendasnubeiras.ui.tema.XogoDados

@Composable
fun Contido( controlador: NavHostController, pantallaInicial: Pantalla, pantallas: List<Pantalla> ) {

    val backStackEntry by controlador.currentBackStackEntryAsState()
    val pantallaActual = pantallas.find { pantalla -> pantalla.ruta == backStackEntry?.destination?.route } ?: pantallaInicial

    val amosarSuperior = pantallaActual.tipo != Pantalla.TIPO.SEN_MENUS
    val amosarInferior = pantallaActual.tipo == Pantalla.TIPO.SCAFFOLD

    PantallaScaffold( controlador, pantallaActual.ruta, amosarSuperior, amosarInferior ) {
        CargarNavegacion( controlador, pantallaInicial, pantallas )
    }

}

@OptIn( ExperimentalMaterial3Api::class )
@Composable
fun PantallaScaffold( controlador: NavHostController, rutaActual: String, amosarSuperior: Boolean, amosarInferior: Boolean, navegacionContido: @Composable () -> Unit ) {

    Scaffold(
        topBar = { if ( amosarSuperior ) NavegacionSuperior( rutaActual, controlador ) },
        bottomBar = { if ( amosarInferior ) NavegacionInferior( rutaActual, controlador ) },
    ) { recheoInterno ->

        Column( modifier = Modifier.fillMaxSize().padding( recheoInterno ).padding( start = 10.dp, end = 10.dp ) ) {
            navegacionContido()
        }

    }

}

@Composable
private fun CargarNavegacion( controlador: NavHostController, pantallaInicial: Pantalla, listaPantallas: List<Pantalla> ) {

    val dominio = "nubeiras"

    NavHost( navController = controlador, startDestination = pantallaInicial.ruta ) {

        for ( pantalla in listaPantallas ) {

            val argumentos = pantalla.ruta.split( "/{" ).drop( 1 ).map {
                argumento -> navArgument( argumento.removeSuffix( "}" ) ) { type = NavType.StringType }
            }

            val enlaces = if ( pantalla.enlaces ) listOf( navDeepLink { uriPattern = "$dominio://${ pantalla.ruta }" } ) else emptyList()

            composable(
                route = pantalla.ruta,
                arguments = argumentos,
                enterTransition = { transicionEntrada( pantalla ) },
                exitTransition = { transicionSaida( pantalla ) },
                popEnterTransition = { transicionAtrasEntrada( pantalla ) },
                popExitTransition = { transicionAtrasSaida( pantalla ) },
                deepLinks = enlaces
            ) { entrada -> CollerContido( pantalla, controlador, entrada ) }

        }

    }

}

@OptIn( ExperimentalMaterial3Api::class )
@Composable
fun NavegacionSuperior(
    rutaActual: String,
    controlador: NavHostController
) {

    val densidade = LocalDensity.current
    val insetSuperior = WindowInsets.statusBars.getTop( densidade )
    val alturaTotal = 86.dp + with(densidade) { insetSuperior.toDp() }

    val elementosSuperior: List<Pantalla> = remember { Navegacion.menuSuperior }

    //Igual se podería prescindir desta barra se só se pon unha ó final
    val elementos = elementosSuperior.filter { pantalla -> pantalla.ruta != rutaActual }

    Column {

        TopAppBar(
            title = { Logo() },
            modifier = Modifier.heightIn( max = alturaTotal ),
            actions = {

                for ( elemento in elementos ) {

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
                        CollerIconaMenu( elemento )
                        //elemento.icono()
                    }

                }

            }
        )

        HorizontalDivider( thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface )

    }

}

@Composable
fun NavegacionInferior(
    rutaActual: String,
    controlador: NavHostController
) {

    val densidade = LocalDensity.current
    val insetInferior = WindowInsets.navigationBars.getBottom( densidade )
    val alturaTotal = 64.dp + with( densidade ) { insetInferior.toDp() }

    val elementos: List<Pantalla> = remember { Navegacion.menuInferior }

    NavigationBar( modifier = Modifier.heightIn( max = alturaTotal ) ) {

        for ( elemento in elementos ) {

            NavigationBarItem(
                selected = rutaActual == elemento.ruta,
                onClick = {
                    if ( rutaActual != elemento.ruta ) {
                        controlador.navigate( elemento.ruta ) {
                            launchSingleTop = true
                        }
                    }
                },
                icon = { CollerIconaMenu( elemento ) /*elemento.icono() */ },
                label = {
                    key( idiomaActual.value ) {
                        Text( l10n( "menu_" + elemento.ruta, "test" ), maxLines = 1, overflow = TextOverflow.Ellipsis )
                    }
                }

            )

        }

    }

}

@Composable
fun CollerContido( pantalla: Pantalla, controlador: NavHostController, entrada: NavBackStackEntry ) {

    when( pantalla ) {

        //Autencicación
        Pantalla.Apertura -> PantallaApertura( controlador )
        Pantalla.IniciarSesion -> PantallaIniciarSesion()
        Pantalla.Rexistro -> PantallaRexistro()

        //Lector
        Pantalla.Actividades -> TODO()
        Pantalla.ActividadeDetalle -> TODO()
        Pantalla.Animacions -> MirarAnimacions()

        //Lector con argumentos
        Pantalla.Detalle -> {

            val id = entrada.arguments?.getString( "id" )?.toIntOrNull() ?: 0
            val test = entrada.arguments?.getString( "test" ) ?: ""
            PantallaDetalle( id = id, test = test )

        }

        Pantalla.Buscar -> {

            val termo = entrada.arguments?.getString( "termo" ) ?: ""
            PantallaBuscador( termo )

        }

        Pantalla.Axustes -> PantallaAxustes()
        Pantalla.Idioma -> XogoDados()
        Pantalla.Inicio -> ProbaTraducions()
        //Pantalla.Mapa -> MapaMundial()

        //Creación
        Pantalla.ListarActividades -> TODO()
        Pantalla.CrearActividade -> TODO()
        Pantalla.ModificarActividade -> TODO() //Ten argumentos

        //Administración
        Pantalla.Administrar -> ProbaActividade()

    }

}

@Composable
fun Logo( tamano: Dp = 30.dp ) {

    Image(
        painter = painterResource( R.drawable.logo ),
        modifier = Modifier.size( tamano ),
        contentDescription = stringResource( R.string.nome_app )
    )

}

@Composable
fun CollerIconaMenu( pantalla: Pantalla ) {

    when ( pantalla ) {
        Pantalla.Rexistro -> Iconas.OlloAberto()
        Pantalla.IniciarSesion -> Iconas.OlloAberto()
        Pantalla.Inicio -> Iconas.Inicio()
        Pantalla.Axustes -> Iconas.Axustes()
        Pantalla.Animacions -> Iconas.OlloAberto()
        Pantalla.Detalle -> Iconas.OlloPechado()
        Pantalla.Actividades -> Iconas.Idioma()
        else -> error( "A pantalla ${ pantalla.ruta } non ten icona asignada" )
    }

}

private fun pantallaCompleta( pantalla: Pantalla ) = pantalla.tipo == Pantalla.TIPO.SCAFFOLD

fun transicionEntrada( pantalla: Pantalla ): EnterTransition =
    if ( pantallaCompleta( pantalla ) ) fadeIn( tween() )
    else slideInHorizontally( tween() ) { ancho -> ancho }

fun transicionSaida( pantalla: Pantalla ): ExitTransition =
    if ( pantallaCompleta( pantalla ) ) fadeOut( tween() )
    else slideOutHorizontally( tween() ) { ancho -> -ancho }

fun transicionAtrasEntrada( pantalla: Pantalla ): EnterTransition =
    if ( pantallaCompleta( pantalla ) ) fadeIn( tween() )
    else slideInHorizontally( tween() ) { ancho -> -ancho }

fun transicionAtrasSaida( pantalla: Pantalla ): ExitTransition =
    if ( pantallaCompleta( pantalla ) ) fadeOut( tween() )
    else slideOutHorizontally( tween() ) { ancho -> ancho }