package com.aprengal.lendasnubeiras.navegacion

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.aprengal.lendasnubeiras.mapa.MapaMundial
import com.aprengal.lendasnubeiras.tema.ContenidoPrueba
import com.aprengal.lendasnubeiras.tema.Espazador
import com.aprengal.lendasnubeiras.tema.Iconas
import com.aprengal.lendasnubeiras.tema.Logo
import com.aprengal.lendasnubeiras.tema.MirarAnimacions
import com.aprengal.lendasnubeiras.tema.ProbaActividade
import com.aprengal.lendasnubeiras.tema.ProbaTraducions
import com.aprengal.lendasnubeiras.tema.XogoDados

sealed class Pantalla(

    val nome: String,
    val icono: @Composable () -> Unit,
    val contido: @Composable (NavBackStackEntry?) -> Unit = {},
    val enlaces: List<NavDeepLink> = emptyList() ) {

    open val ruta: String = this::class.simpleName?.lowercase() ?: ""

    companion object {

        val todas: List<Pantalla> by lazy {
            listOf( Inicio, Perfil, Mapa, Idioma, Animacions, Axustes, Detalle )
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

@Composable
fun PantallaBase( contido: LazyListScope.() -> Unit ) {

    LazyColumn( modifier = Modifier.fillMaxSize() ) {

        item {
            Espazador()
        }

        contido()

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimarNavegacionSuperior(
    desprazamento: TopAppBarScrollBehavior,
    clave: String,
    ocultar: Boolean
) {

    val heightOffsetAnimado = remember { Animatable(0f) }
    val objetivo = if ( ocultar ) desprazamento.state.heightOffsetLimit else 0f

    LaunchedEffect( clave, ocultar ) {
        heightOffsetAnimado.snapTo(desprazamento.state.heightOffset)
        heightOffsetAnimado.animateTo(
            targetValue = objetivo,
            animationSpec = tween()
        ) {
            desprazamento.state.heightOffset = value
            desprazamento.state.contentOffset = 0f
        }
    }

}

@Composable
fun animarNavegacionInferior(
    clave: String,
    agochar: Boolean
): Float {

    val offsetAnimado = remember { Animatable( 0f ) }

    LaunchedEffect( clave, agochar ) {
        offsetAnimado.animateTo(
            targetValue = if ( agochar ) 1f else 0f,
            animationSpec = tween()
        )
    }

    return offsetAnimado.value

}

//TODO: Mirar se as transicións son normais ou se está disparando o consumo de memoria ou hai lagazos
@OptIn(ExperimentalMaterial3Api::class)
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

    val modificadorAltura = alturaBarra?.let { altura ->
        Modifier.heightIn( max = altura * ( 1f - progresoAgochar ) )
    } ?: Modifier

    val densidade = LocalDensity.current
    val bottomInsetDp = with( densidade ) {
        WindowInsets.navigationBars.getBottom( densidade ).toDp()
    }

    val modificador = Modifier.height( 64.dp + bottomInsetDp )
        .onSizeChanged { tamano ->
            if ( progresoAgochar == 0f ) {
                alturaBarra = with( densidade ) { tamano.height.toDp() }
            }

        }
        .graphicsLayer { translationY = size.height * progresoAgochar }
        .then( modificadorAltura )
        .clipToBounds()

    NavigationBar( modifier = modificador ) {
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