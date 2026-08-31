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
import com.aprengal.lendasnubeiras.ui.tema.Bordos.collerBordos
import com.aprengal.lendasnubeiras.ui.tema.Cores.collerTemaClaro
import com.aprengal.lendasnubeiras.ui.tema.Cores.collerTemaEscuro
import com.aprengal.lendasnubeiras.ui.tema.Tipografias.collerTipografias

object Tema {

    enum class Variante( val nome: String ) {

        CLARO( "claro" ),
        ESCURO( "escuro" ),
        PREDETERMINADO( "predeterminado" );

        companion object {
            fun buscar( clave: String ): Variante {
                return entries.find { variante -> variante.nome == clave } ?: PREDETERMINADO
            }
        }

    }

    internal var temaActual by mutableStateOf( Variante.PREDETERMINADO )
        private set

    private var temaCargado = false

    fun arrancar() {

        if ( temaCargado ) return
        temaCargado = true

        val claveGuardada = collerOpcion( Opcion.Tema )
        temaActual = Variante.buscar( claveGuardada )

    }

    internal suspend fun gardarTema( novoTema: Variante ): Boolean {

        if ( temaActual == novoTema ) return true
        if ( !gardarOpcion( Opcion.Tema, novoTema.nome ) ) return false

        temaActual = novoTema
        return true

    }

    fun <T> escollerVarianteImaxe( temaEscuro: Boolean, claro: T, escuro: T ): T {

        val imaxe = when ( temaActual ) {
            Variante.CLARO -> claro
            Variante.ESCURO -> escuro
            Variante.PREDETERMINADO -> if ( temaEscuro ) escuro else claro
        }

        return imaxe

    }

    private fun collerCoresTema( temaEscuro: Boolean ): ColorScheme {

        val esquemaCores = when( temaActual ) {
            Variante.CLARO -> collerTemaClaro()
            Variante.ESCURO -> collerTemaEscuro()
            Variante.PREDETERMINADO -> if ( temaEscuro ) collerTemaEscuro() else collerTemaClaro()
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

