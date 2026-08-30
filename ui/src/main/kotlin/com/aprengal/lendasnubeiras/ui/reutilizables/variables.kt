package com.aprengal.lendasnubeiras.ui.reutilizables

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.staticCompositionLocalOf
import com.aprengal.lendasnubeiras.data.localizacion.Idioma
import com.aprengal.lendasnubeiras.data.localizacion.L10nSingular
import com.aprengal.lendasnubeiras.ui.navegacion.Navegacion
import com.aprengal.lendasnubeiras.ui.navegacion.Ruta

val LocalAviso = staticCompositionLocalOf<SnackbarHostState> { error( "Aviso non proporcionado" ) }

val LocalNavegacion = staticCompositionLocalOf<Navegacion> { error("Navegación non proporcionada") }

val LocalIdioma = staticCompositionLocalOf<Idioma> { error( "Idioma non proporcionado" ) }

val LocalRuta = staticCompositionLocalOf<Ruta> { error( "Ruta non proporcionada" ) }

val LocalTitulo = staticCompositionLocalOf<L10nSingular?> { error( "Título non proporcionado" ) }