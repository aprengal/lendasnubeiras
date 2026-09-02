package org.aprengal.lendasnubeiras.data.localizacion.claves.singular

import org.aprengal.lendasnubeiras.data.localizacion.Localizacion
import org.aprengal.lendasnubeiras.data.localizacion.claves.L10n

sealed interface L10nSingular : L10n {
    fun texto(): String = Localizacion.l10n( this )
}