package com.aprengal.lendasnubeiras.ui.navegacion

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.get
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay
import com.aprengal.lendasnubeiras.data.localizacion.Idioma
import com.aprengal.lendasnubeiras.ui.navegacion.DatosNavegacion.Tipo
import com.aprengal.lendasnubeiras.ui.navegacion.DatosNavegacion.TipoPantalla
import com.aprengal.lendasnubeiras.ui.navegacion.DatosNavegacion.TipoPantalla.COMPLETA
import com.aprengal.lendasnubeiras.ui.reutilizables.LocalAviso
import com.aprengal.lendasnubeiras.ui.reutilizables.LocalIdioma
import com.aprengal.lendasnubeiras.ui.reutilizables.LocalNavegacion
import kotlin.reflect.KClass

object Navegacion {

    @Composable
    fun CargarNavegacion( idioma: Idioma, navegacion: NavBackStack<NavKey> ) {

        val aviso = remember { SnackbarHostState() }

        LaunchedEffect( idioma ) {
            aviso.currentSnackbarData?.dismiss()
        }

        CompositionLocalProvider( LocalIdioma provides idioma, LocalNavegacion provides navegacion, LocalAviso provides aviso ) {
            NavDisplay( navegacion, transitionSpec = avance(), popTransitionSpec = retroceso(), entryProvider = ListaNavegacion( navegacion ).entradas )
        }

    }

    private fun haiTransicion( orixe: TipoPantalla, destino: TipoPantalla ): Boolean {
        return orixe != COMPLETA || destino != COMPLETA
    }

    fun NavBackStack<NavKey>.comprobarRutaActiva( claseRuta: KClass<out Ruta> ): Boolean {
        return lastOrNull()?.let { clase -> clase::class == claseRuta } == true
    }

    private fun avance(): AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform = {

        val orixe = initialState.entries.last().metadata[ Tipo ]!!
        val destino = targetState.entries.last().metadata[ Tipo ]!!

        if ( haiTransicion( orixe, destino ) ) {
            destino.entrada togetherWith orixe.saida
        } else {
            EnterTransition.None togetherWith ExitTransition.None
        }

    }

    private fun retroceso(): AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform = {

        val orixe = initialState.entries.last().metadata[ Tipo ]!!
        val destino = targetState.entries.last().metadata[ Tipo ]!!

        if ( haiTransicion( orixe, destino ) ) {
            destino.atrasEntrada togetherWith orixe.atrasSaida
        } else {
            EnterTransition.None togetherWith ExitTransition.None
        }

    }

}