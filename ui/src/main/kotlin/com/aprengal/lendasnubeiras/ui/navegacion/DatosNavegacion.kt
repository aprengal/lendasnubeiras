package com.aprengal.lendasnubeiras.ui.navegacion

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation3.runtime.NavMetadataKey
import com.aprengal.lendasnubeiras.data.localizacion.L10nSingular
import com.aprengal.lendasnubeiras.ui.navegacion.DatosNavegacion.TipoPantalla.APERTURA
import com.aprengal.lendasnubeiras.ui.navegacion.DatosNavegacion.TipoPantalla.COMPLETA
import com.aprengal.lendasnubeiras.ui.navegacion.DatosNavegacion.TipoPantalla.SOSUPERIOR
import com.aprengal.lendasnubeiras.ui.reutilizables.Icona

internal object DatosNavegacion {

    object Tipo : NavMetadataKey<TipoPantalla>

    enum class TipoPantalla( private val transicion: Transicion ) {
        COMPLETA( Transicion.DISOLVER ),
        SOSUPERIOR( Transicion.DESLIZAR ),
        APERTURA( Transicion.DESLIZAR );

        val entrada get() = transicion.entrada
        val saida get() = transicion.saida
        val atrasEntrada get() = transicion.atrasEntrada
        val atrasSaida get() = transicion.atrasSaida

    }

    private enum class Transicion( val entrada: EnterTransition, val saida: ExitTransition,
        val atrasEntrada: EnterTransition, val atrasSaida: ExitTransition
    ) {

        DISOLVER(
            entrada = fadeIn( tween() ),
            saida = fadeOut( tween() ),
            atrasEntrada = fadeIn( tween() ),
            atrasSaida = fadeOut( tween() )
        ),

        DESLIZAR(
            entrada = slideInHorizontally( tween() ) { ancho -> ancho },
            saida = slideOutHorizontally( tween() ) { ancho -> -ancho },
            atrasEntrada = slideInHorizontally( tween() ) { ancho -> -ancho },
            atrasSaida = slideOutHorizontally( tween() ) { ancho -> ancho }
        )

    }

    data class DatosPantalla( val tipo: TipoPantalla, val titulo: L10nSingular? = null, val icona: Icona? = null )

    val datosRutas = mapOf(

        //Común
        Ruta.Axustes::class to DatosPantalla( SOSUPERIOR, L10nSingular.TITULO_AXUSTES, Icona.AXUSTES ),

        //Apertura
        Ruta.Benvida::class to DatosPantalla( APERTURA, L10nSingular.TITULO_BENVIDA ),
        Ruta.Acceso::class to DatosPantalla( APERTURA, L10nSingular.TITULO_ACCESO ),
        Ruta.Rexistro::class to DatosPantalla( APERTURA, L10nSingular.TITULO_REXISTRO ),

        //Lectura
        Ruta.Actividades::class to DatosPantalla( SOSUPERIOR, L10nSingular.SI, icona = Icona.INVALIDO ),
        Ruta.ActividadeDetalle::class to DatosPantalla( SOSUPERIOR ),
        Ruta.Buscar::class to DatosPantalla( COMPLETA, icona = Icona.BUSCAR ),
        Ruta.Inicio::class to DatosPantalla( COMPLETA, icona = Icona.INICIO ),
        Ruta.Idioma::class to DatosPantalla( COMPLETA, icona = Icona.IDIOMA ),

        //Crear
        Ruta.ListarActividades::class to DatosPantalla( SOSUPERIOR ),
        Ruta.CrearActividade::class to DatosPantalla( SOSUPERIOR, icona = Icona.ENGADIR ),
        Ruta.ModificarActividade::class to DatosPantalla( SOSUPERIOR ),

        //Administrar
        Ruta.Administrar::class to DatosPantalla( SOSUPERIOR )

    )

}