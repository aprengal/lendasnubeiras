package com.aprengal.lendasnubeiras.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.navDeepLink
import com.aprengal.lendasnubeiras.PantallaDetalle
//import com.aprengal.lendasnubeiras.mapa.MapaMundial
import com.aprengal.lendasnubeiras.pantallas.PantallaAxustes
import com.aprengal.lendasnubeiras.tema.Iconas
import com.aprengal.lendasnubeiras.tema.MirarAnimacions
import com.aprengal.lendasnubeiras.tema.PantallaApertura
import com.aprengal.lendasnubeiras.tema.PantallaIniciarSesion
import com.aprengal.lendasnubeiras.tema.PantallaRexistro
import com.aprengal.lendasnubeiras.tema.ProbaActividade
import com.aprengal.lendasnubeiras.tema.ProbaTraducions
import com.aprengal.lendasnubeiras.tema.XogoDados

sealed class Pantalla(

    //val nome: String,
    val icono: @Composable () -> Unit = {},
    val tipo: TIPO = TIPO.SCAFFOLD,
    //val navSuperior: Boolean = true,
    //val navInferior: Boolean = true,
    val contido: @Composable (NavBackStackEntry?) -> Unit = {},
    val enlaces: List<NavDeepLink> = emptyList() ) {

    open val ruta: String = this::class.simpleName!!.replaceFirstChar { it.lowercase() }

    companion object {

        val todas: List<Pantalla> by lazy {
            listOf( Inicio, Perfil, /*Mapa,*/ Idioma, Animacions, Axustes, Detalle )
        }

        val autenticacion: List<Pantalla> by lazy {
            listOf( Apertura, Rexistro, IniciarSesion )
        }

        enum class TIPO { SCAFFOLD, SOSUPERIOR, SEN_MENUS }

    }

    //TODO: traducir nome da pantalla
    object Apertura: Pantalla( {}, tipo = TIPO.SEN_MENUS, contido = { PantallaApertura() } )
    object Rexistro: Pantalla( { Iconas.OlloAberto() }, tipo = TIPO.SEN_MENUS, contido = { PantallaRexistro() } )
    object IniciarSesion: Pantalla( { Iconas.OlloAberto() }, tipo = TIPO.SEN_MENUS, contido = { PantallaIniciarSesion() } )

    object Inicio: Pantalla( { Iconas.Inicio() }, contido = { ProbaTraducions() } )
    object Perfil: Pantalla( { Iconas.Perfil() }, contido = { ProbaActividade() }, enlaces = listOf( navDeepLink { uriPattern = "nubeiras://perfil" } ) )
    object Axustes: Pantalla( { Iconas.Axustes() }, tipo = TIPO.SOSUPERIOR, contido = { PantallaAxustes() }, enlaces = listOf( navDeepLink { uriPattern = "nubeiras://axustes" } ) )
    //object Mapa: Pantalla("Mapa", { Iconas.Mapa() }, contido = { MapaMundial() }, enlaces = listOf( navDeepLink { uriPattern = "nubeiras://mapa" } ) )
    object Idioma: Pantalla( { Iconas.Idioma() }, contido = { XogoDados() } )
    object Animacions: Pantalla( { Iconas.OlloAberto() }, contido = { MirarAnimacions() } )


    //Test
    object Detalle : Pantalla(
        //nome = "Detalles",
        icono = { Iconas.OlloPechado() },
        //navSuperior = false,
        //navInferior = false,
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