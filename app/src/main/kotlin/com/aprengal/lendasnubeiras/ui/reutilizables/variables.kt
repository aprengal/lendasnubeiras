package com.aprengal.lendasnubeiras.ui.reutilizables

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.staticCompositionLocalOf

val LocalAviso = staticCompositionLocalOf<SnackbarHostState> {
    error( "EstadoAviso non proporcionado" )
}