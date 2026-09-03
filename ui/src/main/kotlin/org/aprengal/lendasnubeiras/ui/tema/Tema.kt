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

object Tema {

    internal var temaActual by mutableStateOf( Predeterminado )
        private set

    private var temaCargado = false

    fun arrancar() {

        if ( temaCargado ) return
        temaCargado = true

        val claveGardada = collerTema()
        temaActual = Variante.buscar( claveGardada )

    }

    internal suspend fun cambiarTema( novoTema: Variante ): Boolean {

        if ( temaActual == novoTema ) return true
        if ( !gardarTema( novoTema ) ) return false

        temaActual = novoTema
        return true

    }

    private fun collerCoresTema( temaEscuro: Boolean ): ColorScheme {

        val esquemaCores = when( temaActual ) {
            Claro -> collerTemaClaro()
            Escuro -> collerTemaEscuro()
            Predeterminado -> if ( temaEscuro ) collerTemaEscuro() else collerTemaClaro()
        }

        return esquemaCores

    }

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

