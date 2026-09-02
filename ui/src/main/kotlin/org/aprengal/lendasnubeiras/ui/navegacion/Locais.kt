package org.aprengal.lendasnubeiras.ui.navegacion

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.staticCompositionLocalOf
import org.aprengal.lendasnubeiras.data.localizacion.Idioma
import org.aprengal.lendasnubeiras.data.localizacion.claves.singular.L10nSingular


//Se se empezan a poñer locais que non teñen absolutamente nada de relación coa navegación,
// igual habería que movelo a utilidades
object Locais {

    val LocalAviso = staticCompositionLocalOf<SnackbarHostState> { error( "Aviso non proporcionado" ) }

    val LocalIdioma = staticCompositionLocalOf<Idioma> { error( "Idioma non proporcionado" ) }

    val LocalNavegacion = staticCompositionLocalOf<Navegacion> { error("Navegación non proporcionada" ) }

    val LocalRuta = staticCompositionLocalOf<Ruta> { error( "Ruta non proporcionada" ) }

    val LocalTitulo = staticCompositionLocalOf<L10nSingular?> { error( "Título non proporcionado" ) }

}