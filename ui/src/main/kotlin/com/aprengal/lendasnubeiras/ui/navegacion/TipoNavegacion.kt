package com.aprengal.lendasnubeiras.ui.navegacion

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

internal enum class TipoNavegacion(
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

    SEN_MENUS(
        entrada = slideInHorizontally( tween() ) { ancho -> ancho },
        saida = slideOutHorizontally( tween() ) { ancho -> -ancho },
        atrasEntrada = slideInHorizontally( tween() ) { ancho -> -ancho },
        atrasSaida = slideOutHorizontally( tween() ) { ancho -> ancho }
    )

}