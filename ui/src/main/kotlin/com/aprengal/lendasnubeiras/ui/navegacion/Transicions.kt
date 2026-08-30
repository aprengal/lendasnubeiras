package com.aprengal.lendasnubeiras.ui.navegacion

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally as moverDH
import androidx.compose.animation.slideOutHorizontally as moverFH
import androidx.compose.animation.togetherWith
import androidx.navigation3.runtime.get
import androidx.navigation3.scene.Scene
import com.aprengal.lendasnubeiras.ui.navegacion.DatosNavegacion.Tipo
import com.aprengal.lendasnubeiras.ui.navegacion.DatosNavegacion.TipoPantalla
import com.aprengal.lendasnubeiras.ui.navegacion.DatosNavegacion.TipoPantalla.APERTURA
import com.aprengal.lendasnubeiras.ui.navegacion.DatosNavegacion.TipoPantalla.COMPLETA
import com.aprengal.lendasnubeiras.ui.navegacion.DatosNavegacion.TipoPantalla.SOSUPERIOR

class Transicions {

    enum class Transicion( val entrada: EnterTransition, val saida: ExitTransition, val atrasEntrada: EnterTransition, val atrasSaida: ExitTransition ) {

        DISOLVER( fadeIn( tween() ), fadeOut( tween() ), fadeIn( tween() ), fadeOut( tween() ) ),

        DESLIZAR( moverDH( tween() ) { w -> w }, moverFH( tween() ) { w -> -w }, moverDH( tween() ) { w -> -w }, moverFH( tween() ) { w -> w } )

    }

    private fun haiTransicion( orixe: TipoPantalla, destino: TipoPantalla ): Boolean {
        return orixe != COMPLETA || destino != COMPLETA
    }

    private fun collerTransicion( tipo: TipoPantalla ): Transicion {

        val transicion = when( tipo ) {
            COMPLETA -> Transicion.DISOLVER
            SOSUPERIOR, APERTURA -> Transicion.DESLIZAR
        }

        return transicion

    }

    fun avance(): AnimatedContentTransitionScope<Scene<Ruta>>.() -> ContentTransform = {

        val orixe = initialState.entries.last().metadata[ Tipo ]!!
        val destino = targetState.entries.last().metadata[ Tipo ]!!

        if ( !haiTransicion( orixe, destino ) ) {
            EnterTransition.None togetherWith ExitTransition.None
        } else {

            val transicionOrixe = collerTransicion( orixe )
            val transicionDestino = collerTransicion( destino )

            transicionDestino.entrada togetherWith transicionOrixe.saida

        }

    }

    fun retroceso(): AnimatedContentTransitionScope<Scene<Ruta>>.() -> ContentTransform = {

        val orixe = initialState.entries.last().metadata[ Tipo ]!!
        val destino = targetState.entries.last().metadata[ Tipo ]!!

        if ( !haiTransicion( orixe, destino ) ) {
            EnterTransition.None togetherWith ExitTransition.None
        } else {

            val transicionOrixe = collerTransicion( orixe )
            val transicionDestino = collerTransicion( destino )

            transicionDestino.atrasEntrada togetherWith transicionOrixe.atrasSaida

        }

    }

}