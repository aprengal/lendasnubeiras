package org.aprengal.lendasnubeiras.ui.navegacion

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.staticCompositionLocalOf
import org.aprengal.lendasnubeiras.data.localizacion.clases.Idioma
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nSingular


//Se se empezan a poñer locais que non teñen absolutamente nada de relación coa navegación,
// igual habería que movelo a utilidades
/**
 * Agrupa os `CompositionLocal` da aplicación.
 *
 * Permiten pasar datos comúns (idioma, navegación, avisos...) a calquera
 * compoñente da interface sen ter que enviarlos como parámetro en cada nivel.
 * Se se le un valor que ninguén proporcionou, lánzase un erro co nome do
 * dato que falta.
 */
object Locais {

    /** Estado da barra de avisos (`Snackbar`) onde se mostran mensaxes ao usuario. */
    val LocalAviso = staticCompositionLocalOf<SnackbarHostState> { error( "Aviso non proporcionado" ) }

    /** Idioma actual da aplicación. */
    val LocalIdioma = staticCompositionLocalOf<Idioma> { error( "Idioma non proporcionado" ) }

    /** Xestor de navegación para moverse entre pantallas. */
    val LocalNavegacion = staticCompositionLocalOf<Navegacion> { error( "Navegación non proporcionada" ) }

    /** Ruta correspondente á pantalla que se está mostrando. */
    val LocalRuta = staticCompositionLocalOf<Ruta> { error( "Ruta non proporcionada" ) }

    /** Título da pantalla actual, ou nulo se non ten. */
    val LocalTitulo = staticCompositionLocalOf<L10nSingular?> { error( "Título non proporcionado" ) }

}