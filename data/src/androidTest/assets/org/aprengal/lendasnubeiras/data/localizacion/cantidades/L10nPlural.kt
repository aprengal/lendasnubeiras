package org.aprengal.lendasnubeiras.data.localizacion.cantidades

import org.aprengal.lendasnubeiras.data.localizacion.clases.Dominio
import org.aprengal.lendasnubeiras.data.localizacion.clases.L10n
import org.aprengal.lendasnubeiras.data.localizacion.clases.Localizacion

enum class L10nPlural( internal val clave: String, internal val dominio: Dominio ) : L10n {

    MensaxesNovas( "mensaxes_novas", Dominio.BASE );

    fun texto( num: Number ): String = Localizacion.l10nPlural( this, num )

}