package org.aprengal.lendasnubeiras.ui.tema

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.aprengal.lendasnubeiras.data.axustes.DatosTema.Variante
import org.aprengal.lendasnubeiras.data.axustes.DatosTema.collerTema
import org.aprengal.lendasnubeiras.data.axustes.DatosTema.gardarTema
import org.aprengal.lendasnubeiras.data.axustes.DatosTema.Variante.Claro
import org.aprengal.lendasnubeiras.data.axustes.DatosTema.Variante.Escuro
import org.aprengal.lendasnubeiras.data.axustes.DatosTema.Variante.Predeterminado
import org.aprengal.lendasnubeiras.ui.tema.Bordos.collerBordos
import org.aprengal.lendasnubeiras.ui.tema.Cores.collerTemaClaro
import org.aprengal.lendasnubeiras.ui.tema.Cores.collerTemaEscuro
import org.aprengal.lendasnubeiras.ui.tema.Tipografias.collerTipografias

/**
 * Xestiona o tema visual da aplicación (claro, escuro ou predeterminado).
 *
 * Garda o tema escollido, cárgao ao arrancar a aplicación e aplícao a toda a
 * interface mediante [TemaNubeiro].
 */
object Tema {

    /**
     * Tema que se está aplicando actualmente.
     *
     * Ao ser un estado de Compose, a interface actualízase automaticamente
     * cando cambia. Só se pode modificar desde esta clase.
     */
    internal var temaActual by mutableStateOf( Predeterminado )
        private set

    /** Indica se o tema gardado xa se cargou, para facelo só unha vez. */
    private var temaCargado = false

    /**
     * Carga o tema gardado no dispositivo.
     *
     * Só actúa a primeira vez que se chama; as seguintes non fan nada. Se non
     * hai ningún tema gardado, o resultado depende de [Variante.buscar].
     */
    fun arrancar() {

        if ( temaCargado ) return
        temaCargado = true

        val claveGardada = collerTema()
        temaActual = Variante.buscar( claveGardada )

    }

    /**
     * Cambia o tema da aplicación e gárdao para as próximas execucións.
     *
     * O tema só se actualiza se se consegue gardar. Se o tema indicado xa é o
     * actual, non fai nada.
     *
     * @param novoTema Tema que se quere aplicar.
     * @return `true` se o tema queda aplicado (ou xa o estaba); `false` se non
     * se puido gardar e, polo tanto, non se cambiou.
     */
    internal suspend fun cambiarTema( novoTema: Variante ): Boolean {

        if ( temaActual == novoTema ) return true
        if ( !gardarTema( novoTema ) ) return false

        temaActual = novoTema
        return true

    }

    /**
     * Escolle o esquema de cores que corresponde ao tema actual.
     *
     * Co tema [Predeterminado], segue o do sistema: usa o escuro se o
     * dispositivo está en modo escuro e o claro en caso contrario.
     *
     * @param temaEscuro Indica se o sistema está en modo escuro.
     * @return O [ColorScheme] que se debe aplicar.
     */
    private fun collerCoresTema( temaEscuro: Boolean ): ColorScheme {

        val esquemaCores = when( temaActual ) {
            Claro -> collerTemaClaro()
            Escuro -> collerTemaEscuro()
            Predeterminado -> if ( temaEscuro ) collerTemaEscuro() else collerTemaClaro()
        }

        return esquemaCores

    }

    /**
     * Aplica o tema da aplicación ao contido que recibe.
     *
     * Combina as cores do tema actual coas formas ([Bordos]) e as tipografías
     * ([Tipografias]) da aplicación, e envolve o contido nun [MaterialTheme].
     *
     * @param contido Interface á que se aplica o tema.
     */
    @Composable
    fun TemaNubeiro( contido: @Composable () -> Unit ) {

        val cores = collerCoresTema( isSystemInDarkTheme() )
        val bordos = collerBordos()
        val tipografias = collerTipografias()

        MaterialTheme( cores, bordos, tipografias ) {
            contido()
        }

    }

}