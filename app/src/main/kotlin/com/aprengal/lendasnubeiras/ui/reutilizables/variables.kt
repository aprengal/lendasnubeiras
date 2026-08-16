package com.aprengal.lendasnubeiras.ui.reutilizables

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.staticCompositionLocalOf
import com.aprengal.lendasnubeiras.navegacion.Pantalla

val LocalAviso = staticCompositionLocalOf<SnackbarHostState> {
    error( "Aviso non proporcionado" )
}

val LocalPantallas = staticCompositionLocalOf<Set<Pantalla>> {
    error( "Conxunto de pantallas non proporcionado" )
}

val LocalPantallaInicial = staticCompositionLocalOf<Pantalla> {
    error( "Pantalla inicial non proporcionada" )
}