package com.aprengal.lendasnubeiras.ui.reutilizables

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import com.aprengal.lendasnubeiras.data.localizacion.Idioma
import com.aprengal.lendasnubeiras.ui.navegacion.Ruta

val LocalAviso = staticCompositionLocalOf<SnackbarHostState> { error( "Aviso non proporcionado" ) }

val LocalControlador = staticCompositionLocalOf<NavHostController> { error( "Controlador non proporcionado" ) }

val LocalIdioma = staticCompositionLocalOf<Idioma> { error( "Idioma non proporcionado" ) }

val LocalRuta = staticCompositionLocalOf<Ruta> { error( "Ruta non proporcionada" ) }