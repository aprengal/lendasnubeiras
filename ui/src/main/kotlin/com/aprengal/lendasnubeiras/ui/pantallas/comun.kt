package com.aprengal.lendasnubeiras.ui.pantallas

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.aprengal.lendasnubeiras.data.localizacion.Localizacion.idiomaActual
import com.aprengal.lendasnubeiras.data.localizacion.Localizacion.l10n
import com.aprengal.lendasnubeiras.ui.navegacion.Navegacion
import com.aprengal.lendasnubeiras.ui.navegacion.Navegacion.collerPantallas
import com.aprengal.lendasnubeiras.ui.navegacion.Pantalla
import com.aprengal.lendasnubeiras.ui.reutilizables.LocalAviso
import com.aprengal.lendasnubeiras.ui.reutilizables.LocalControlador
import com.aprengal.lendasnubeiras.ui.reutilizables.LocalPantallaActual
import com.aprengal.lendasnubeiras.ui.reutilizables.Logo
import com.aprengal.lendasnubeiras.ui.reutilizables.comprobarPermisos
import com.aprengal.lendasnubeiras.ui.tema.MirarAnimacions
import com.aprengal.lendasnubeiras.ui.tema.ProbaActividade
import com.aprengal.lendasnubeiras.ui.tema.ProbaTraducions
import com.aprengal.lendasnubeiras.data.usuarios.Permisos.podeCrear
import com.aprengal.lendasnubeiras.data.usuarios.SesionActual.collerUsuarioActual
import com.aprengal.lendasnubeiras.ui.R
import com.aprengal.lendasnubeiras.ui.pantallas.lector.PantallaActividade
import com.aprengal.lendasnubeiras.ui.pantallas.lector.PantallaActividadeDetalle
import com.aprengal.lendasnubeiras.ui.pantallas.lector.PantallaBuscador
import com.aprengal.lendasnubeiras.ui.pantallas.lector.actividadesDixitais.XogoDados
import com.aprengal.lendasnubeiras.ui.reutilizables.Icona

@OptIn( ExperimentalMaterial3Api::class )
@Composable
fun PantallaBase() {

    val controlador = rememberNavController()

    val ( pantallaInicial, pantallas ) = collerPantallas()
    val backStackEntry by controlador.currentBackStackEntryAsState()
    val pantallaActual = pantallas.find { pantalla -> pantalla.ruta == backStackEntry?.destination?.route } ?: pantallaInicial

    val aviso = remember { SnackbarHostState() }
    val amosarSuperior = pantallaActual.tipo != Pantalla.TIPO.SEN_MENUS
    val amosarInferior = pantallaActual.tipo == Pantalla.TIPO.SCAFFOLD
    val amosarAccion = podeCrear( collerUsuarioActual() ) && amosarInferior

    LaunchedEffect( idiomaActual ) {
        aviso.currentSnackbarData?.dismiss()
    }

    CompositionLocalProvider( LocalPantallaActual provides pantallaActual, LocalControlador provides controlador, LocalAviso provides aviso ) {

        Scaffold(
            topBar = { if ( amosarSuperior ) NavegacionSuperior( pantallaActual, controlador) },
            bottomBar = { if ( amosarInferior ) NavegacionInferior( pantallaActual, controlador ) },
            snackbarHost = { SnackbarHost( aviso ) },
            floatingActionButton = { if ( amosarAccion ) BotonCrearActividade( controlador ) }
        ) { recheoInterno ->

            Column( modifier = Modifier.fillMaxSize().padding( recheoInterno ).padding( start = 10.dp, end = 10.dp ) ) {
                CargarNavegacion( controlador, pantallaInicial, pantallas )
            }

        }

    }

}

@Composable
private fun CargarNavegacion( controlador: NavHostController, pantallaInicial: Pantalla, pantallas: Set<Pantalla> ) {

    val dominio = "nubeiras"

    NavHost( navController = controlador, startDestination = pantallaInicial.ruta ) {

        for ( pantalla in pantallas ) {

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
            ) { entrada -> CollerContido( pantalla, entrada ) }

        }

    }

}

@OptIn( ExperimentalMaterial3Api::class )
@Composable
private fun NavegacionSuperior(
    pantallaActual: Pantalla,
    controlador: NavHostController
) {

    val densidade = LocalDensity.current
    val insetSuperior = WindowInsets.statusBars.getTop( densidade )
    val alturaTotal = 86.dp + with(densidade) { insetSuperior.toDp() }

    val elementosSuperior: List<Pantalla> = remember { Navegacion.menuSuperior }

    //Igual se podería prescindir desta barra se só se pon unha ó final
    val elementos = elementosSuperior.filter { pantalla -> pantalla != pantallaActual }

    Column {

        TopAppBar(
            title = { Logo() },
            modifier = Modifier.heightIn( max = alturaTotal ),
            actions = {

                for ( elemento in elementos ) {

                    val seleccionado = pantallaActual == elemento

                    //Igual isto se ten que ir se só hai 1 elemento no menú superior e se quita o foreach?
                    val colorFondo by animateColorAsState(
                        targetValue = if ( seleccionado ) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
                        label = "colorFondo"
                    )

                    IconButton(
                        onClick = {

                            if ( !seleccionado ) {

                                /*val rutaDestino = when ( elemento ) {
                                    /*is Pantalla.Detalle -> elemento.crearRuta(
                                        "666",
                                        "resacón"
                                    )*/

                                    else -> elemento.ruta
                                }*/

                                controlador.navigate( elemento.ruta ) {
                                    launchSingleTop = true
                                }

                            }

                        },
                        modifier = Modifier.clip( RoundedCornerShape( 12.dp ) ).background( colorFondo )
                    ) {
                        DebuxarIconaMenu( elemento )
                    }

                }

            }
        )

        HorizontalDivider( thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface )

    }

}

@Composable
private fun NavegacionInferior( pantallaActual: Pantalla, controlador: NavHostController ) {

    val densidade = LocalDensity.current
    val insetInferior = WindowInsets.navigationBars.getBottom( densidade )
    val alturaTotal = 64.dp + with( densidade ) { insetInferior.toDp() }

    val elementos: List<Pantalla> = remember { Navegacion.menuInferior }

    NavigationBar( modifier = Modifier.heightIn( max = alturaTotal ) ) {

        for ( elemento in elementos ) {

            NavigationBarItem(
                selected = pantallaActual == elemento,
                onClick = {
                    if ( pantallaActual != elemento ) {
                        controlador.navigate( elemento.ruta ) {
                            launchSingleTop = true
                        }
                    }
                },
                icon = { DebuxarIconaMenu( elemento ) }
            )

        }

    }

}

@Composable
private fun BotonCrearActividade( controlador: NavHostController ) {

    val pantalla = Pantalla.CrearActividade

    FloatingActionButton( onClick = { controlador.navigate(pantalla.ruta ) }, shape = CircleShape) {
        DebuxarIconaMenu( pantalla )
    }

}

@Composable
private fun CollerContido( pantalla: Pantalla, entrada: NavBackStackEntry ) {

    if ( !comprobarPermisos() ) return

    when( pantalla ) {

        //Autencicación
        Pantalla.Apertura -> PantallaApertura()
        Pantalla.IniciarSesion -> PantallaIniciarSesion()
        Pantalla.Rexistro -> PantallaRexistro()

        //Lector
        Pantalla.Actividades -> PantallaActividade()
        Pantalla.ActividadeDetalle -> {

            val id = entrada.arguments?.getString( "id" )?.toLongOrNull() ?: 0
            PantallaActividadeDetalle( id )

        }
        Pantalla.Animacions -> MirarAnimacions()

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
        Pantalla.CrearActividade -> NovaActividade()
        Pantalla.ModificarActividade -> TODO() //Ten argumentos

        //Administración
        Pantalla.Administrar -> ProbaActividade()

    }

}

@Composable
private fun DebuxarIconaMenu( pantalla: Pantalla ) {

    val icona = when ( pantalla ) {
        Pantalla.Inicio -> Icona.INICIO
        Pantalla.Buscar -> Icona.BUSCAR
        Pantalla.Idioma -> Icona.IDIOMA
        Pantalla.Axustes -> Icona.AXUSTES
        Pantalla.CrearActividade -> Icona.ENGADIR
        Pantalla.Actividades -> Icona.IDIOMA
        else -> error( "A pantalla ${ pantalla.ruta } non ten icona asignada" )
    }

    val fontFamily = FontFamily( Font( R.font.ubuntu_iconas_nerd, FontWeight.Bold ) )
    val descricion = l10n( "menu_" + pantalla.ruta, "menu" )

    Text(
        text = icona.codigo,
        fontFamily = fontFamily,
        color = LocalContentColor.current,
        fontSize = 20.sp,
        lineHeight = 1.sp,
        modifier = Modifier.semantics {
            this.contentDescription = descricion
        }
    )

}

private fun pantallaCompleta( pantalla: Pantalla ) = pantalla.tipo == Pantalla.TIPO.SCAFFOLD

private fun transicionEntrada( pantalla: Pantalla ): EnterTransition =
    if ( pantallaCompleta( pantalla ) ) fadeIn( tween() )
    else slideInHorizontally( tween() ) { ancho -> ancho }

private fun transicionSaida( pantalla: Pantalla ): ExitTransition =
    if ( pantallaCompleta( pantalla ) ) fadeOut( tween() )
    else slideOutHorizontally( tween() ) { ancho -> -ancho }

private fun transicionAtrasEntrada( pantalla: Pantalla ): EnterTransition =
    if ( pantallaCompleta( pantalla ) ) fadeIn( tween() )
    else slideInHorizontally( tween() ) { ancho -> -ancho }

private fun transicionAtrasSaida( pantalla: Pantalla ): ExitTransition =
    if ( pantallaCompleta( pantalla ) ) fadeOut( tween() )
    else slideOutHorizontally( tween() ) { ancho -> ancho }