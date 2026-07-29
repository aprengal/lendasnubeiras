package com.aprengal.lendasnubeiras.navegacion

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember

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