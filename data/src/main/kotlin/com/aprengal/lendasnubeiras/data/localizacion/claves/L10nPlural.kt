package com.aprengal.lendasnubeiras.data.localizacion.claves

import com.aprengal.lendasnubeiras.data.localizacion.Dominio
import com.aprengal.lendasnubeiras.data.localizacion.Localizacion

enum class L10nPlural( override val clave: String, override val dominio: Dominio ) : L10n {

    ;
    //MensaxesNovas( "mensaxes_novas", Dominio.Test );

    fun texto( num: Number ): String = Localizacion.l10nPlural( this, num )

}