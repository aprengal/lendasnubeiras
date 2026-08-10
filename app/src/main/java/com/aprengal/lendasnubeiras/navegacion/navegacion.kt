package com.aprengal.lendasnubeiras.navegacion

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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.aprengal.lendasnubeiras.Axustes.collerOpcion
import com.aprengal.lendasnubeiras.localizacion.Localizacion.idiomaActual
import com.aprengal.lendasnubeiras.tema.Logo
import com.aprengal.lendasnubeiras.localizacion.Localizacion.l10n
import com.aprengal.lendasnubeiras.elementos.usuarios.Rol
import com.aprengal.lendasnubeiras.elementos.usuarios.Usuario
import com.aprengal.lendasnubeiras.elementos.usuarios.UsuarioActual

@Composable
fun IniciarAplicacion() {

    var usuarioActual by remember { mutableStateOf(Usuario( id = 0L, correo = "", rol = Rol.LECTOR ) ) }
    var sesionAnonima by rememberSaveable { mutableStateOf( false ) }

    LaunchedEffect( Unit ) {
        usuarioActual = UsuarioActual.coller()
        sesionAnonima = collerOpcion( "sesion_anonima", false )
    }

    key( usuarioActual ) {

        val controlador = rememberNavController()

        if ( usuarioActual.id > 0L || sesionAnonima ) { //Iniciouse sesión?

            Contido( controlador, Pantalla.Inicio, Pantalla.todas )

        } else {

            Contido( controlador, Pantalla.Apertura, Pantalla.autenticacion )

        }

    }

}

@Composable
fun Contido( controlador: NavHostController, pantallaInicial: Pantalla, pantallas: List<Pantalla> ) {

    val backStackEntry by controlador.currentBackStackEntryAsState()
    val pantallaActual = Pantalla.todas.find { pantalla -> pantalla.ruta == backStackEntry?.destination?.route } ?: pantallaInicial

    val amosarSuperior = pantallaActual.tipo != Pantalla.Companion.TIPO.SEN_MENUS
    val amosarInferior = pantallaActual.tipo == Pantalla.Companion.TIPO.SCAFFOLD

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

    NavHost( navController = controlador, startDestination = pantallaInicial.ruta ) {

        for ( pantalla in listaPantallas ) {

            val argumentos = pantalla.ruta.split( "/{" ).drop( 1 ).map { ruta -> ruta.removeSuffix( "}" ) }

            composable(
                route = pantalla.ruta,
                arguments = argumentos.map { nome -> navArgument( nome ) { type = NavType.StringType } },
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

@OptIn( ExperimentalMaterial3Api::class )
@Composable
fun NavegacionSuperior(
    rutaActual: String,
    controlador: NavHostController
) {

    val densidade = LocalDensity.current
    val insetSuperior = WindowInsets.statusBars.getTop( densidade )
    val alturaTotal = 86.dp + with(densidade) { insetSuperior.toDp() }

    val elementosSuperior = remember { listOf(
        Pantalla.Axustes, Pantalla.Detalle
    ) }

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
    rutaActual: String,
    controlador: NavHostController
) {

    val densidade = LocalDensity.current
    val insetInferior = WindowInsets.navigationBars.getBottom( densidade )
    val alturaTotal = 64.dp + with( densidade ) { insetInferior.toDp() }

    val elementos = remember { listOf(
        Pantalla.Inicio,
        Pantalla.Perfil,
        //Pantalla.Idioma,
        Pantalla.Animacions
    ) }

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
                icon = { elemento.icono() },
                label = {
                    key( idiomaActual.value ) {
                        Text( l10n( "menu_" + elemento.ruta, "test" ), maxLines = 1, overflow = TextOverflow.Ellipsis ) }
                    }

            )

        }

    }

}

private fun esCompleta( pantalla: Pantalla ) = pantalla.tipo == Pantalla.Companion.TIPO.SCAFFOLD

fun transicionEntrada( pantalla: Pantalla ): EnterTransition =
    if ( esCompleta( pantalla ) ) fadeIn( tween() )
    else slideInHorizontally( tween() ) {  ancho -> ancho }

fun transicionSaida( pantalla: Pantalla ): ExitTransition =
    if ( esCompleta( pantalla ) ) fadeOut( tween() )
    else slideOutHorizontally( tween() ) { ancho -> -ancho }

fun transicionAtrasEntrada( pantalla: Pantalla ): EnterTransition =
    if ( esCompleta( pantalla ) ) fadeIn( tween() )
    else slideInHorizontally( tween() ) { ancho -> -ancho }

fun transicionAtrasSaida( pantalla: Pantalla ): ExitTransition =
    if ( esCompleta( pantalla ) ) fadeOut( tween() )
    else slideOutHorizontally( tween() ) { ancho -> ancho }