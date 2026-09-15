package org.aprengal.lendasnubeiras.data.localizacion.singulares

import org.aprengal.lendasnubeiras.data.localizacion.clases.Localizacion
import org.aprengal.lendasnubeiras.data.localizacion.clases.L10n

sealed interface L10nSingular : L10n {
    fun texto(): String = Localizacion.l10n( this )
}