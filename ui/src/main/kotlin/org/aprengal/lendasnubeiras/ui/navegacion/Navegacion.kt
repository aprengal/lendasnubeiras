package org.aprengal.lendasnubeiras.ui.navegacion

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.ui.NavDisplay
import org.aprengal.lendasnubeiras.data.localizacion.clases.Idioma
import org.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeLer
import org.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeRexistrarse
import org.aprengal.lendasnubeiras.ui.navegacion.Locais.LocalAviso
import org.aprengal.lendasnubeiras.ui.navegacion.Locais.LocalIdioma
import org.aprengal.lendasnubeiras.ui.navegacion.Locais.LocalNavegacion
import org.aprengal.lendasnubeiras.ui.navegacion.Transicions.avanceTransicion
import org.aprengal.lendasnubeiras.ui.navegacion.Transicions.retrocesoTransicion
import kotlin.reflect.KClass

/**
 * Xestiona a navegación entre pantallas da aplicación.
 *
 * Envolve a pila de navegación ([NavBackStack]) de Navigation 3 e ofrece
 * operacións sinxelas para engadir, quitar e substituír pantallas, ademais de
 * lembrar cal foi a última opción escollida na barra de navegación inferior.
 *
 * @property traza Pila de rutas visitadas. A última é a pantalla actual.
 * @param seleccionada Clase da última ruta escollida no menú inferior, se a hai.
 */
class Navegacion ( private val traza: NavBackStack<Ruta>, seleccionada: KClass<out Ruta>? = null ) {

    /** Lista de rutas da pila, da máis antiga á pantalla actual. */
    val lista: List<Ruta> get() = traza

    /** Clase da última ruta escollida no menú inferior. Só se modifica desde esta clase. */
    var ultimaMenu: KClass<out Ruta>? = seleccionada
        private set

    /**
     * Engade unha ruta á pila.
     *
     * Non fai nada se a ruta indicada é xa a pantalla actual, para evitar
     * duplicados consecutivos.
     *
     * @param elemento Ruta que se quere abrir.
     */
    fun engadir( elemento: Ruta ) {

        if ( traza.lastOrNull() != elemento ) {
            traza.add( elemento )
        }

    }

    /**
     * Abre unha ruta escollida na barra de navegación inferior e lembra a súa
     * clase como a última opción seleccionada.
     *
     * @param elemento Ruta asociada á opción do menú.
     */
    fun seleccionarInferior( elemento: Ruta ) {
        ultimaMenu = elemento::class
        engadir( elemento )
    }

    /** Quita a pantalla actual da pila, se hai algunha. */
    fun quitarUltimo() {
        traza.removeLastOrNull()
    }

    /**
     * Indica se a pantalla actual é dun tipo de ruta determinado.
     *
     * @param claseRuta Clase da ruta que se quere comprobar.
     * @return `true` se a última ruta da pila é desa clase.
     */
    fun rutaActiva( claseRuta: KClass<out Ruta> ): Boolean {
        return traza.lastOrNull()?.let { ruta -> ruta::class == claseRuta } == true
    }

    /** Elimina todas as rutas da pila agás a primeira. */
    fun reiniciar() {
        traza.subList( 1, traza.size ).clear()
    }

    /**
     * Substitúe a pantalla actual por outra, de modo que non quede na pila.
     *
     * @param redirixir Ruta á que se redirixe.
     */
    fun redirixir( redirixir: Ruta ) {
        quitarUltimo()
        engadir( redirixir )
    }

    companion object {

        /**
         * Determina a primeira pantalla que se mostra ao abrir a aplicación.
         *
         * Se o usuario ten permiso de lectura, vai a [Ruta.Inicio]. Se non, pero
         * pode rexistrarse, vai a [Ruta.Benvida].
         *
         * @return Ruta inicial.
         * @throws IllegalStateException Se non se cumpre ningunha das dúas condicións.
         */
        fun rutaInicial(): Ruta {

            val ruta = when {
                PodeLer() -> Ruta.Inicio
                PodeRexistrarse() -> Ruta.Benvida
                else -> error( "Non se puido determinar a ruta inicial" )
            }

            return ruta

        }

        /**
         * Crea un [Saver] para conservar o estado da navegación (por exemplo,
         * ao rotar a pantalla).
         *
         * Só se garda o nome da clase da última opción do menú inferior. A pila
         * en si non se garda aquí, senón que se recibe de novo ao restaurar.
         *
         * @param traza Pila de navegación coa que se reconstrúe a [Navegacion].
         * @return Obxecto que garda e restaura a navegación.
         */
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

        /**
         * Mostra a pantalla activa e fornece aos compoñentes fillos o idioma, a
         * navegación e a barra de avisos mediante `CompositionLocal`.
         *
         * Cando cambia o idioma, pecha o aviso que se estea mostrando.
         *
         * @param idioma Idioma actual da aplicación.
         * @param navegacion Xestor de navegación que controla a pila de pantallas.
         */
        @Composable
        fun RexistrarNavegacion( idioma: Idioma, navegacion: Navegacion ) {

            val aviso = remember { SnackbarHostState() }

            LaunchedEffect( idioma ) {
                aviso.currentSnackbarData?.dismiss()
            }

            val entradas = remember { DatosNavegacion() }

            CompositionLocalProvider( LocalIdioma provides idioma, LocalNavegacion provides navegacion, LocalAviso provides aviso ) {
                NavDisplay( navegacion.lista, transitionSpec = avanceTransicion(), popTransitionSpec = retrocesoTransicion(), entryProvider = entradas.entradasNavegacion )
            }

        }

    }

}