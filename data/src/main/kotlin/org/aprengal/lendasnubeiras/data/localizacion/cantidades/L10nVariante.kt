package org.aprengal.lendasnubeiras.data.localizacion.cantidades

import org.aprengal.lendasnubeiras.data.localizacion.clases.Dominio
import org.aprengal.lendasnubeiras.data.localizacion.clases.L10n
import org.aprengal.lendasnubeiras.data.localizacion.clases.Localizacion

enum class L10nVariante( internal val clave: String, internal val dominio: Dominio ) : L10n {

    MensaxesFallos( "mensaxe_fallos", Dominio.BASE );

    fun texto( num: Int ): String = Localizacion.l10nVariante( this, num )

}