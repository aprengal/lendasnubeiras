package org.aprengal.lendasnubeiras.data

import org.junit.Test

//Como esta clase emprega tando data como ui, igual é mellor movela a data
class IntegridadeElementos {

    @Test
    fun integridadeElementos() {

        val hashLocalizacion = "b5629b57dd2d72ccaccb42126ad537c602f78b0a50185f5b1674b0388f54d9ea"

        revisarHashElemento( "Localizacion", hashLocalizacion, TipoIdentificador.OBJECT )

        val hashL10nSingular = "ad5fc049987dd80296a90d5321c398e2c07a5edbda46750e9a65c258f3a16475"

        revisarHashElemento( "L10nSingular", hashL10nSingular, TipoIdentificador.SEALED_INTERFACE )

        val hashCondicion = "aba983db04e3341f584d117f548f75f19667027747305b0683e9c7f97d44e5ba"

        revisarHashElemento( "Condicion", hashCondicion, TipoIdentificador.SEALED_INTERFACE )

    }

}