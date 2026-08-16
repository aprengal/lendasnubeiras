package com.aprengal.lendasnubeiras.ui.reutilizables

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import com.aprengal.lendasnubeiras.ui.navegacion.Pantalla

val LocalAviso = staticCompositionLocalOf<SnackbarHostState> { error( "Aviso non proporcionado" ) }

val LocalPantallaActual = staticCompositionLocalOf<Pantalla> { error( "Non se definiu ningunha pantalla" ) }

val LocalControlador = compositionLocalOf<NavHostController> { error( "Non hai NavController" ) }