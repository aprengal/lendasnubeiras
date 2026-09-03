package org.aprengal.lendasnubeiras.data.axustes

import org.aprengal.lendasnubeiras.data.axustes.Axustes.collerOpcion
import org.aprengal.lendasnubeiras.data.axustes.Axustes.gardarOpcion
import org.aprengal.lendasnubeiras.data.axustes.DatosTema.Variante.Claro
import org.aprengal.lendasnubeiras.data.axustes.DatosTema.Variante.Escuro
import org.aprengal.lendasnubeiras.data.axustes.DatosTema.Variante.Predeterminado
import org.aprengal.lendasnubeiras.data.localizacion.clases.ElementoL10n
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nOpcions


object DatosTema {

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

    fun collerTema(): String {
        return collerOpcion( Opcion.Tema )
    }

    suspend fun gardarTema( variante: Variante ): Boolean {
        return gardarOpcion( Opcion.Tema, variante.clave )
    }

    fun <T> escollerVarianteImaxe( temaActual: Variante, temaEscuro: Boolean, claro: T, escuro: T ): T {

        val imaxe = when ( temaActual ) {
            Claro -> claro
            Escuro -> escuro
            Predeterminado -> if ( temaEscuro ) escuro else claro
        }

        return imaxe

    }

}