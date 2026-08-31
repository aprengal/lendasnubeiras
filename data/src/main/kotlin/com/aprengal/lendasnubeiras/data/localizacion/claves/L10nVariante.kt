package com.aprengal.lendasnubeiras.data.localizacion.claves

import com.aprengal.lendasnubeiras.data.localizacion.Dominio
import com.aprengal.lendasnubeiras.data.localizacion.Localizacion

enum class L10nVariante( override val clave: String, override val dominio: Dominio): L10n {

    // Test
    MENSAXE_FALLOS( "mensaxe_fallos", Dominio.Test );

    fun texto( num: Int ): String = Localizacion.l10nVariante( this, num )

}