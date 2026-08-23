package com.aprengal.lendasnubeiras.ui.reutilizables

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.staticCompositionLocalOf
import com.aprengal.lendasnubeiras.data.localizacion.Idioma
import com.aprengal.lendasnubeiras.ui.navegacion.Pantalla

val LocalAviso = staticCompositionLocalOf<SnackbarHostState> { error( "Aviso non proporcionado" ) }

val LocalIdioma = staticCompositionLocalOf<Idioma> { error( "Idioma non proporcionado" ) }

val LocalPantalla = staticCompositionLocalOf<Pantalla> { error( "Pantalla non proporcionada" ) }