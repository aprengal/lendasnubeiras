package org.aprengal.lendasnubeiras.ui.navegacion

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
import org.aprengal.lendasnubeiras.ui.navegacion.DatosNavegacion.Tipo
import org.aprengal.lendasnubeiras.ui.navegacion.DatosNavegacion.TipoPantalla
import org.aprengal.lendasnubeiras.ui.navegacion.DatosNavegacion.TipoPantalla.APERTURA
import org.aprengal.lendasnubeiras.ui.navegacion.DatosNavegacion.TipoPantalla.COMPLETA
import org.aprengal.lendasnubeiras.ui.navegacion.DatosNavegacion.TipoPantalla.TITULO_SUPERIOR

/**
 * Define as animacións que se aplican ao cambiar de pantalla.
 *
 * A animación elíxese segundo o [TipoPantalla] da pantalla de orixe e o da
 * pantalla de destino ([COMPLETA], [TITULO_SUPERIOR] ou [APERTURA]). Se as
 * dúas son de tipo [COMPLETA], non se aplica ningunha animación para evitar
 * parpadeos entre pantallas coa mesma estrutura.
 */
object Transicions {

    /**
     * Tipos de animación dispoñibles.
     *
     * Cada tipo define catro animacións: entrada e saída ao avanzar, e entrada
     * e saída ao retroceder.
     *
     * @property entrada Animación da pantalla que aparece ao avanzar.
     * @property saida Animación da pantalla que desaparece ao avanzar.
     * @property atrasEntrada Animación da pantalla que aparece ao retroceder.
     * @property atrasSaida Animación da pantalla que desaparece ao retroceder.
     */
    private enum class Transicion( val entrada: EnterTransition, val saida: ExitTransition, val atrasEntrada: EnterTransition, val atrasSaida: ExitTransition ) {

        /** Fundido: unha pantalla desaparece mentres a outra aparece gradualmente. */
        Disolver( fadeIn( tween() ), fadeOut( tween() ), fadeIn( tween() ), fadeOut( tween() ) ),

        /**
         * Desprazamento horizontal (`slideInHorizontally` e
         * `slideOutHorizontally`, importados como `moverDH` e `moverFH`).
         * O sentido invértese ao retroceder.
         */
        Deslizar( moverDH( tween() ) { w -> w }, moverFH( tween() ) { w -> -w }, moverDH( tween() ) { w -> -w }, moverFH( tween() ) { w -> w } )

    }

    /**
     * Indica se debe haber animación entre dúas pantallas.
     *
     * @param orixe Tipo da pantalla da que se sae.
     * @param destino Tipo da pantalla á que se vai.
     * @return `false` se ambas son de tipo [COMPLETA]; `true` en calquera outro caso.
     */
    private fun haiTransicion( orixe: TipoPantalla, destino: TipoPantalla ): Boolean {
        return orixe != COMPLETA || destino != COMPLETA
    }

    /**
     * Escolle a animación que corresponde a un tipo de pantalla.
     *
     * @param tipo Tipo de pantalla.
     * @return [Transicion.Disolver] para [COMPLETA]; [Transicion.Deslizar] para
     * [TITULO_SUPERIOR] e [APERTURA].
     */
    private fun collerTransicion( tipo: TipoPantalla ): Transicion {

        val transicion = when( tipo ) {
            COMPLETA -> Transicion.Disolver
            TITULO_SUPERIOR, APERTURA -> Transicion.Deslizar
        }

        return transicion

    }

    /**
     * Crea a transición que se usa ao avanzar a unha pantalla nova.
     *
     * Combina a entrada da pantalla de destino coa saída da de orixe. Se non
     * debe haber animación (ver [haiTransicion]), non se aplica ningunha.
     *
     * @return Función que `NavDisplay` usa como `transitionSpec`.
     */
    fun avanceTransicion(): AnimatedContentTransitionScope<Scene<Ruta>>.() -> ContentTransform = {

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

    /**
     * Crea a transición que se usa ao volver á pantalla anterior.
     *
     * Funciona como [avanceTransicion], pero usa as animacións de retroceso.
     *
     * @return Función que `NavDisplay` usa como `popTransitionSpec`.
     */
    fun retrocesoTransicion(): AnimatedContentTransitionScope<Scene<Ruta>>.() -> ContentTransform = {

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