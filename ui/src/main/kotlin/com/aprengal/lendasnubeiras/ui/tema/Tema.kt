package com.aprengal.lendasnubeiras.ui.tema

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.aprengal.lendasnubeiras.data.axustes.Axustes.collerOpcion
import com.aprengal.lendasnubeiras.data.axustes.Axustes.gardarOpcion
import com.aprengal.lendasnubeiras.data.axustes.Opcion
import com.aprengal.lendasnubeiras.data.localizacion.ElementoL10n
import com.aprengal.lendasnubeiras.data.localizacion.claves.singular.L10nOpcions
import com.aprengal.lendasnubeiras.ui.tema.Bordos.collerBordos
import com.aprengal.lendasnubeiras.ui.tema.Cores.collerTemaClaro
import com.aprengal.lendasnubeiras.ui.tema.Cores.collerTemaEscuro
import com.aprengal.lendasnubeiras.ui.tema.Tema.Variante.Claro
import com.aprengal.lendasnubeiras.ui.tema.Tema.Variante.Escuro
import com.aprengal.lendasnubeiras.ui.tema.Tema.Variante.Predeterminado
import com.aprengal.lendasnubeiras.ui.tema.Tipografias.collerTipografias

object Tema {

    enum class Variante( override val clave: String, override val nome: L10nOpcions ): ElementoL10n {

        Claro( "claro", L10nOpcions.TemaClaro ),
        Escuro( "escuro", L10nOpcions.TemaEscuro ),
        Predeterminado( "predeterminado", L10nOpcions.TemaPredeterminado );

        companion object {
            fun buscar( clave: String ): Variante {
                return entries.find { variante -> variante.clave == clave } ?: Predeterminado
            }
        }

    }

    internal var temaActual by mutableStateOf( Predeterminado )
        private set

    private var temaCargado = false

    fun arrancar() {

        if ( temaCargado ) return
        temaCargado = true

        val claveGardada = collerOpcion( Opcion.Tema )
        temaActual = Variante.buscar( claveGardada )

    }

    internal suspend fun gardarTema( novoTema: Variante ): Boolean {

        if ( temaActual == novoTema ) return true
        if ( !gardarOpcion( Opcion.Tema, novoTema.clave ) ) return false

        temaActual = novoTema
        return true

    }

    fun <T> escollerVarianteImaxe( temaEscuro: Boolean, claro: T, escuro: T ): T {

        val imaxe = when ( temaActual ) {
            Claro -> claro
            Escuro -> escuro
            Predeterminado -> if ( temaEscuro ) escuro else claro
        }

        return imaxe

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

