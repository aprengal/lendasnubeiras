package com.aprengal.lendasnubeiras.ui.navegacion

import android.os.Bundle
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.aprengal.lendasnubeiras.data.localizacion.Idioma
import com.aprengal.lendasnubeiras.data.usuarios.PodeAdministrar
import com.aprengal.lendasnubeiras.data.usuarios.PodeCrear
import com.aprengal.lendasnubeiras.data.usuarios.PodeLer
import com.aprengal.lendasnubeiras.data.usuarios.PodeRexistrarse
import com.aprengal.lendasnubeiras.ui.ProbaActividade
import com.aprengal.lendasnubeiras.ui.pantallas.NovaActividade
import com.aprengal.lendasnubeiras.ui.pantallas.PantallaAcceso
import com.aprengal.lendasnubeiras.ui.pantallas.PantallaAxustes
import com.aprengal.lendasnubeiras.ui.pantallas.PantallaBenvida
import com.aprengal.lendasnubeiras.ui.pantallas.PantallaRexistro
import com.aprengal.lendasnubeiras.ui.reutilizables.estruturas.EstruturaBase
import com.aprengal.lendasnubeiras.ui.reutilizables.estruturas.EstruturaSuperior
import com.aprengal.lendasnubeiras.ui.pantallas.lector.PantallaActividade
import com.aprengal.lendasnubeiras.ui.pantallas.lector.PantallaActividadeDetalle
import com.aprengal.lendasnubeiras.ui.pantallas.lector.PantallaBuscador
import com.aprengal.lendasnubeiras.ui.pantallas.lector.PantallaInicio
import com.aprengal.lendasnubeiras.ui.pantallas.lector.actividadesDixitais.XogoDados
import com.aprengal.lendasnubeiras.ui.reutilizables.LocalAviso
import com.aprengal.lendasnubeiras.ui.reutilizables.LocalIdioma
import com.aprengal.lendasnubeiras.ui.reutilizables.LocalPantalla
import com.aprengal.lendasnubeiras.ui.reutilizables.estruturas.EstruturaApertura
import kotlin.reflect.KType
import kotlin.reflect.typeOf

private val validadorID = object : NavType<Int>( isNullableAllowed = false ) {

    private val ID_INVALIDO = -1

    override fun get( bundle: Bundle, key: String ): Int {
        return bundle.getInt( key, ID_INVALIDO )
    }

    override fun put( bundle: Bundle, key: String, value: Int ) {
        bundle.putInt( key, value )
    }

    override fun parseValue( value: String ): Int {
        return value.toIntOrNull() ?: ID_INVALIDO
    }

    override fun serializeAsValue( value: Int ): String {
        return value.toString()
    }

}

private inline fun <reified T : Pantalla> NavGraphBuilder.pantalla(
    tipo: TipoNavegacion,
    controlador: NavHostController,
    enlace: String? = null,
    tipoMapa: Map<KType, NavType<*>> = emptyMap(),
    noinline contido: @Composable ( T ) -> Unit
) {

    val dominio = "nubeiras"
    val enlaces = enlace?.let { ruta -> listOf( navDeepLink { uriPattern = "$dominio://$ruta" } ) } ?: emptyList()

    composable<T>(
        typeMap = tipoMapa,
        deepLinks = enlaces,
        enterTransition = { tipo.entrada },
        exitTransition = { tipo.saida },
        popEnterTransition = { tipo.atrasEntrada },
        popExitTransition = { tipo.atrasSaida }
    ) { entrada ->

        if ( !comprobarAcceso( controlador, T::class ) ) return@composable

        val datos = entrada.toRoute<T>()

        CompositionLocalProvider( LocalPantalla provides datos ) {

            when ( tipo ) {
                TipoNavegacion.COMPLETA -> EstruturaBase( controlador ) { contido( datos ) }
                TipoNavegacion.SOSUPERIOR -> EstruturaSuperior { contido( datos ) }
                TipoNavegacion.APERTURA -> EstruturaApertura( controlador ) { contido( datos ) }
            }

        }

    }

}

@Composable
fun CargarNavegacion( idioma: Idioma ) {

    val controlador = rememberNavController()
    val pantallaInicial = collerPantallaInicial()
    val aviso = remember { SnackbarHostState() }

    CompositionLocalProvider( LocalIdioma provides idioma, LocalAviso provides aviso ) {

        NavHost( navController = controlador, startDestination = pantallaInicial ) {

            pantalla<Pantalla.Axustes>( TipoNavegacion.SOSUPERIOR, controlador, enlace = "axustes" ) {
                PantallaAxustes()
            }

            if ( PodeRexistrarse() ) {

                pantalla<Pantalla.Benvida>( TipoNavegacion.APERTURA, controlador ) {
                    PantallaBenvida( controlador )
                }

                pantalla<Pantalla.Acceso>( TipoNavegacion.APERTURA, controlador ) {
                    PantallaAcceso( controlador )
                }

                pantalla<Pantalla.Rexistro>( TipoNavegacion.APERTURA, controlador ) {
                    PantallaRexistro( controlador )
                }

            }

            if ( PodeLer() ) {

                pantalla<Pantalla.Actividades>( TipoNavegacion.SOSUPERIOR, controlador, enlace = "actividades" ) {
                    PantallaActividade()
                }

                pantalla<Pantalla.ActividadeDetalle>(
                    TipoNavegacion.SOSUPERIOR,
                    controlador,
                    enlace = "actividade/detalle/{id}",
                    tipoMapa = mapOf( typeOf<Int>() to validadorID )
                ) { datos ->
                    PantallaActividadeDetalle( datos.id.toLong() )
                }

                pantalla<Pantalla.Inicio>( TipoNavegacion.COMPLETA, controlador ) {
                    PantallaInicio()
                }

                pantalla<Pantalla.Buscar>( TipoNavegacion.COMPLETA, controlador, enlace = "buscar/{termo}" ) { datos ->
                    PantallaBuscador( datos.termo )
                }

                pantalla<Pantalla.Idioma>( TipoNavegacion.COMPLETA, controlador, enlace = "idioma" ) {
                    XogoDados()
                }

            }

            if ( PodeCrear() ) {

                pantalla<Pantalla.ListarActividades>( TipoNavegacion.SOSUPERIOR, controlador ) {
                    TODO()
                }

                pantalla<Pantalla.CrearActividade>( TipoNavegacion.SOSUPERIOR, controlador ) {
                    NovaActividade()
                }

                pantalla<Pantalla.ModificarActividade>( TipoNavegacion.SOSUPERIOR, controlador ) {
                    TODO()
                }

            }

            if ( PodeAdministrar() ) {

                pantalla<Pantalla.Administrar>( TipoNavegacion.SOSUPERIOR, controlador ) {
                    ProbaActividade()
                }

            }

        }

    }

}