package com.example.lendasnubeiras.navegacion

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import com.example.lendasnubeiras.tema.Espazador
import com.example.lendasnubeiras.tema.Iconas
import com.example.lendasnubeiras.tema.Logo
import com.example.lendasnubeiras.tema.MirarAnimacions
import com.example.lendasnubeiras.tema.ProbaActividade
import com.example.lendasnubeiras.tema.ProbaTraducions
import com.example.lendasnubeiras.tema.XogoDados

sealed class Pantalla(

    val nome: String,
    val icono: @Composable () -> Unit,
    val contido: @Composable (NavBackStackEntry?) -> Unit = {},
    val enlaces: List<NavDeepLink> = emptyList() ) {

    open val ruta: String = this::class.simpleName?.lowercase() ?: ""

    companion object {
        val todas: List<Pantalla> by lazy {
            listOf(Inicio, Perfil, Axustes, Mapa, Idioma, Animacions, Detalle)
        }
    }

    //TODO: traducir nome da pantalla
    object Inicio : Pantalla("Inicio" ,{ Iconas.Inicio() }, contido = { ProbaTraducions() } )
    object Perfil : Pantalla("Perfil", { Iconas.Perfil() }, contido = { ProbaActividade() }, enlaces = listOf( navDeepLink { uriPattern = "nubeiras://perfil" } ) )
    object Axustes : Pantalla("Axustes", { Iconas.Axustes() }, contido = { ContenidoPrueba() }, enlaces = listOf( navDeepLink { uriPattern = "nubeiras://axustes" } ) )
    object Mapa : Pantalla("Mapa", { Iconas.Mapa() }, contido = { MapaMundial() }, enlaces = listOf( navDeepLink { uriPattern = "nubeiras://mapa" } ) )
    object Idioma : Pantalla("Dados", { Iconas.Idioma() }, contido = { XogoDados() } )
    object Animacions: Pantalla("Animacións", { Iconas.OlloAberto() }, contido = { MirarAnimacions() } )


    //Test
    object Detalle : Pantalla(
        nome = "Detalles",
        icono = { Iconas.OlloPechado() },
        contido = { entry ->
            val id = entry?.arguments?.getString( "id" )?.toIntOrNull() ?: 0
            val test = entry?.arguments?.getString( "test" ) ?: ""
            PantallaDetalle( id = id, test = test )
        },
        enlaces = listOf( navDeepLink { uriPattern = "nubeiras://detalle/{id}/{test}" } )
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

        Text(
            text = "Veces que has pulsado: $contador",
            style = MaterialTheme.typography.bodyLarge
        )

        Espazador( 2 )

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
            Column {
                TopAppBar(
                    windowInsets = WindowInsets(0, 0, 0, 0),
                    title = { Logo() },
                    actions = {

                        elementosSuperior.forEach { elemento ->

                            val seleccionado = rutaActual == elemento.ruta

                            val colorFondo by animateColorAsState(
                                targetValue = if (seleccionado) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
                                label = "colorFondo"
                            )

                            IconButton(
                                onClick = {
                                    if (!seleccionado) {

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
                                modifier = Modifier.clip(RoundedCornerShape(12.dp))
                                    .background(colorFondo)
                            ) {
                                elemento.icono()
                            }

                        }

                    }
                )
                HorizontalDivider( thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface )
            }
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
        Column( modifier = Modifier.padding( paddingInterno ).padding( start = 10.dp, end = 10.dp ) ) {

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

    }

}

@Composable
fun PantallaBase( contido: @Composable ColumnScope.() -> Unit ) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll( rememberScrollState() )
    ) {
        Espazador()
        contido()
    }

}