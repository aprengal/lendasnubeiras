package com.aprengal.lendasnubeiras.data.localizacion.claves.singular

import com.aprengal.lendasnubeiras.data.localizacion.Dominio
import com.aprengal.lendasnubeiras.data.localizacion.Localizacion
import com.aprengal.lendasnubeiras.data.localizacion.claves.L10n

sealed interface L10nSingular : L10n {

    fun texto(): String = Localizacion.l10n( this )

}