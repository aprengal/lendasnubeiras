package com.aprengal.lendasnubeiras.ui.navegacion

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.ui.NavDisplay
import com.aprengal.lendasnubeiras.data.localizacion.Idioma
import com.aprengal.lendasnubeiras.data.usuarios.PodeLer
import com.aprengal.lendasnubeiras.data.usuarios.PodeRexistrarse
import com.aprengal.lendasnubeiras.ui.reutilizables.LocalAviso
import com.aprengal.lendasnubeiras.ui.reutilizables.LocalIdioma
import com.aprengal.lendasnubeiras.ui.reutilizables.LocalNavegacion
import kotlin.reflect.KClass

class Navegacion ( private val traza: NavBackStack<Ruta>, seleccionada: KClass<out Ruta>? = null ) {

    val lista: List<Ruta> get() = traza

    var ultimaMenu: KClass<out Ruta>? = seleccionada
        private set

    fun engadir( elemento: Ruta ) {

        if ( traza.lastOrNull() != elemento ) {
            traza.add( elemento )
        }

    }

    fun seleccionarInferior( elemento: Ruta ) {
        ultimaMenu = elemento::class
        engadir( elemento )
    }

    fun quitarUltimo() {
        traza.removeLastOrNull()
    }

    fun rutaActiva( claseRuta: KClass<out Ruta> ): Boolean {
        return traza.lastOrNull()?.let { ruta -> ruta::class == claseRuta } == true
    }

    fun reiniciar() {
        traza.subList( 1, traza.size ).clear()
    }

    fun redirixir( redirixir: Ruta ) {
        quitarUltimo()
        engadir( redirixir )
    }

    companion object {

        fun rutaInicial(): Ruta {

            val ruta = when {
                PodeLer() -> Ruta.Inicio
                PodeRexistrarse() -> Ruta.Benvida
                else -> error( "Non se puido determinar a ruta inicial" )
            }

            return ruta

        }

        fun gardarNavegacion( traza: NavBackStack<Ruta> ): Saver<Navegacion, String> {

            val gardado = Saver<Navegacion, String>(
                save = { navegacion -> navegacion.ultimaMenu?.java?.name },
                restore = { className ->
                    @Suppress( "UNCHECKED_CAST" )
                    val ruta = className.let { nome -> Class.forName( nome ).kotlin as KClass<out Ruta> }
                    Navegacion( traza, ruta )
                }
            )

            return gardado

        }

        @Composable
        fun RexistrarNavegacion( idioma: Idioma, navegacion: Navegacion ) {

            val aviso = remember { SnackbarHostState() }

            LaunchedEffect( idioma ) {
                aviso.currentSnackbarData?.dismiss()
            }

            val entradas = DatosNavegacion( navegacion ).entradas
            val transicions = Transicions()

            CompositionLocalProvider( LocalIdioma provides idioma, LocalNavegacion provides navegacion, LocalAviso provides aviso ) {
                NavDisplay( navegacion.lista, transitionSpec = transicions.avance(), popTransitionSpec = transicions.retroceso(), entryProvider = entradas )
            }

        }

    }

}