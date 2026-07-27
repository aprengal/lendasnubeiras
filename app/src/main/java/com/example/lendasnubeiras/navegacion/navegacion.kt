package com.example.lendasnubeiras.navegacion

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.lendasnubeiras.mapa.MapaMundial
import com.example.lendasnubeiras.tema.ContenidoPrueba
import com.example.lendasnubeiras.tema.Iconas
import com.example.lendasnubeiras.tema.Logo
import com.example.lendasnubeiras.tema.MirarAnimacions
import com.example.lendasnubeiras.tema.ProbaActividade
import com.example.lendasnubeiras.tema.ProbaTraducions
import com.example.lendasnubeiras.tema.RevisarIdioma

sealed class Pantalla(

    val nome: String,
    val icono: @Composable () -> Unit,
    val contido: @Composable (NavBackStackEntry?) -> Unit = {},
    val deepLinks: List<NavDeepLink> = emptyList() ) {

    open val ruta: String = this::class.simpleName?.lowercase() ?: ""

    //TODO: traducir nome da pantalla
    object Inicio : Pantalla("Inicio" ,{ Iconas.Inicio() }, contido = { ProbaTraducions() }, deepLinks = listOf( navDeepLink { uriPattern = "nubeiras://inicio" } ) )
    object Perfil : Pantalla("Perfil", { Iconas.Perfil() }, contido = { ProbaActividade() }, deepLinks = listOf( navDeepLink { uriPattern = "nubeiras://perfil" } ) )
    object Axustes : Pantalla("Axustes", { Iconas.Axustes() }, contido = { ContenidoPrueba() } )
    object Mapa : Pantalla("Mapa", { Iconas.Mapa() }, contido = { MapaMundial() } )
    object Idioma : Pantalla("Dados", { Iconas.Idioma() }, contido = { RevisarIdioma() } )
    object Animacions: Pantalla("Animacións", { Iconas.OlloAberto() }, contido = { MirarAnimacions() } )


    //Test
    object Detalle : Pantalla(
        nome = "Detalles",
        icono = { Iconas.OlloPechado() },
        contido = { entry ->
            val id = entry?.arguments?.getString( "id" )!!.toInt()
            val test = entry.arguments?.getString( "test" )!!
            PantallaDetalle( id = id, test = test )
        },
        deepLinks = listOf( navDeepLink { uriPattern = "nubeiras://detalle/{id}/{test}" } )
    ) {
        override val ruta: String = "${super.ruta}/{id}/{test}"
    }

    fun crearRuta( vararg valores: Any ): String {

        var rutaFinal = ruta

        valores.forEach { valor ->

            val inicio = rutaFinal.indexOf( "{" )
            val fin = rutaFinal.indexOf( "}" )

            if ( inicio != -1 && fin != -1 ) {
                rutaFinal = rutaFinal.replaceRange( inicio, fin + 1, valor.toString() )
            }

        }

        return rutaFinal

    }

}

@Composable
fun PantallaDetalle( id: Int, test: String ) {

    var contador by remember { mutableIntStateOf( 0 ) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Detalle del Elemento $id cun bo $test",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Veces que has pulsado: $contador",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer( modifier = Modifier.height( 24.dp ) )

        Button( onClick = { contador++ } ) {
            Text(text = "Incrementar contador")
        }
    }
}

//TODO: Mirar se as transicións son normais ou se está disparando o consumo de memoria ou hai lagazos
@OptIn( ExperimentalMaterial3Api::class )
@Composable
fun PantallaPrincipal() {

    val controlador = rememberNavController()
    val pantallaActual by controlador.currentBackStackEntryAsState()
    val rutaActual = pantallaActual?.destination?.route ?: Pantalla.Inicio.ruta

    val elementosSuperior = listOf(
        Pantalla.Axustes, Pantalla.Detalle
    )

    val elementosInferior = listOf(
        Pantalla.Inicio,
        Pantalla.Perfil,
        Pantalla.Mapa,
        Pantalla.Idioma,
        Pantalla.Animacions
    )

    Scaffold(
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets( 0, 0, 0, 0 ),
                title  = { Logo() },
                actions = {

                    elementosSuperior.forEach { elemento ->

                        val seleccionado = rutaActual == elemento.ruta

                        val colorFondo by animateColorAsState(
                            targetValue = if ( seleccionado ) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
                            label = "colorFondo"
                        )

                        val colorTextoIcono by animateColorAsState(
                            targetValue = if ( seleccionado ) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimary,
                            label = "colorTextoIcono"
                        )

                        IconButton(
                            onClick = {
                                if ( !seleccionado ) {

                                    val rutaDestino = when ( elemento ) {
                                        is Pantalla.Detalle -> elemento.crearRuta("666", "resacón" )
                                        else -> elemento.ruta
                                    }

                                    controlador.navigate( rutaDestino ) {
                                        launchSingleTop = true
                                    }

                                }
                            },
                            modifier = Modifier.clip(RoundedCornerShape( 12.dp ) ).background( colorFondo )
                        ) {
                            CompositionLocalProvider(LocalContentColor provides colorTextoIcono) {
                                elemento.icono()
                            }
                        }

                    }

                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar( windowInsets = WindowInsets( 0, 0, 0, 0 ) ) {
                elementosInferior.forEach { elemento ->
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
                        label = { Text( elemento.nome ) }
                    )
                }
            }
        },
    ) { paddingInterno ->

        //TODO: revisar padding de start
        Box( modifier = Modifier.padding( paddingInterno ).padding( start = 10.dp, end = 10.dp ) ) {

            NavHost(
                navController = controlador,
                startDestination = Pantalla.Inicio.ruta,
            ) {

                Pantalla::class.sealedSubclasses.mapNotNull { it.objectInstance }.forEach { pantalla ->

                    val argumentos = pantalla.ruta.split( "/{" ).drop( 1 ).map { it.removeSuffix( "}" ) }

                    composable(
                        route = pantalla.ruta,
                        arguments = argumentos.map { nome ->
                            navArgument( nome ) { type = NavType.StringType }
                        },
                        deepLinks = pantalla.deepLinks
                    ) { backStackEntry ->
                        pantalla.contido( backStackEntry )
                    }

                }

            }

        }

    }

}