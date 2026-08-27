package com.aprengal.lendasnubeiras.ui.navegacion

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import com.aprengal.lendasnubeiras.data.localizacion.L10nSingular
import com.aprengal.lendasnubeiras.ui.navegacion.Navegacion.TipoPantalla.APERTURA
import com.aprengal.lendasnubeiras.ui.navegacion.Navegacion.TipoPantalla.COMPLETA
import com.aprengal.lendasnubeiras.ui.navegacion.Navegacion.TipoPantalla.SOSUPERIOR
import com.aprengal.lendasnubeiras.ui.reutilizables.Icona

internal object Navegacion {

    internal enum class TipoPantalla(
        val entrada: EnterTransition, val saida: ExitTransition,
        val atrasEntrada: EnterTransition, val atrasSaida: ExitTransition
    ) {

        COMPLETA(
            entrada = fadeIn( tween() ),
            saida = fadeOut( tween() ),
            atrasEntrada = fadeIn( tween() ),
            atrasSaida = fadeOut( tween() )
        ),

        SOSUPERIOR(
            entrada = slideInHorizontally( tween() ) { ancho -> ancho },
            saida = slideOutHorizontally( tween() ) { ancho -> -ancho },
            atrasEntrada = slideInHorizontally( tween() ) { ancho -> -ancho },
            atrasSaida = slideOutHorizontally( tween() ) { ancho -> ancho }
        ),

        APERTURA(
            entrada = slideInHorizontally( tween() ) { ancho -> ancho },
            saida = slideOutHorizontally( tween() ) { ancho -> -ancho },
            atrasEntrada = slideInHorizontally( tween() ) { ancho -> -ancho },
            atrasSaida = slideOutHorizontally( tween() ) { ancho -> ancho }
        )

    }

    internal fun haiTransicion(orixe: TipoPantalla, destino: TipoPantalla ): Boolean {
        return orixe != COMPLETA || destino != COMPLETA
    }

    internal data class DatosPantalla(
        val tipo: TipoPantalla, val enlace: String? = null,
        val titulo: L10nSingular? = null, val icona: Icona? = null
    )

    internal val datosRutas = mapOf(

        //Común
        Ruta.Axustes::class to DatosPantalla( SOSUPERIOR, "axustes", titulo = L10nSingular.TITULO_AXUSTES, icona = Icona.AXUSTES ),

        //Apertura
        Ruta.Benvida::class to DatosPantalla( APERTURA, titulo = L10nSingular.TITULO_BENVIDA ),
        Ruta.Acceso::class to DatosPantalla( APERTURA, titulo = L10nSingular.TITULO_ACCESO ),
        Ruta.Rexistro::class to DatosPantalla( APERTURA, titulo = L10nSingular.TITULO_REXISTRO ),

        //Lectura
        Ruta.Actividades::class to DatosPantalla( SOSUPERIOR, "actividades", icona = Icona.INVALIDO ),
        Ruta.ActividadeDetalle::class to DatosPantalla(
            SOSUPERIOR,
            "actividade/detalle/{id}"
        ),

        Ruta.Buscar::class to DatosPantalla( COMPLETA, "buscar/{termo}", icona = Icona.BUSCAR ),

        Ruta.Inicio::class to DatosPantalla( COMPLETA, icona = Icona.INICIO ),
        Ruta.Idioma::class to DatosPantalla( COMPLETA, "idioma", icona = Icona.IDIOMA ),

        //Crear
        Ruta.ListarActividades::class to DatosPantalla( SOSUPERIOR ),
        Ruta.CrearActividade::class to DatosPantalla( SOSUPERIOR, icona = Icona.ENGADIR ),
        Ruta.ModificarActividade::class to DatosPantalla( SOSUPERIOR ),

        //Administrar
        Ruta.Administrar::class to DatosPantalla( SOSUPERIOR )

    )


}