package org.aprengal.lendasnubeiras.data

import org.junit.Test

//Como esta clase emprega tando data como ui, igual é mellor movela a data
class IntegridadeElementos {

    @Test
    fun integridadeElementos() {

        val hashLocalizacion = "8be742e9914812051b93a1653c475d1bf563a85ff85db3a1d5d23d801a5627b3"

        revisarHashElemento( "Localizacion", hashLocalizacion, TipoIdentificador.OBJECT )

        val hashL10nSingular = "ad5fc049987dd80296a90d5321c398e2c07a5edbda46750e9a65c258f3a16475"

        revisarHashElemento( "L10nSingular", hashL10nSingular, TipoIdentificador.SEALED_INTERFACE )

        val hashCondicion = "aba983db04e3341f584d117f548f75f19667027747305b0683e9c7f97d44e5ba"

        revisarHashElemento( "Condicion", hashCondicion, TipoIdentificador.SEALED_INTERFACE )

    }

}