package org.aprengal.lendasnubeiras.data.localizacion.claves

import org.aprengal.lendasnubeiras.data.localizacion.Dominio
import org.aprengal.lendasnubeiras.data.localizacion.Localizacion

enum class L10nVariante( internal val clave: String, internal val dominio: Dominio ) : L10n {

    MensaxesFallos( "mensaxe_fallos", Dominio.BASE );

    fun texto( num: Int ): String = Localizacion.l10nVariante( this, num )

}