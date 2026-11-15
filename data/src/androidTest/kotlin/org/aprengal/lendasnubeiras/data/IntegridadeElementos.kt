package org.aprengal.lendasnubeiras.data

import org.junit.Test

//Como esta clase emprega tando data como ui, igual é mellor movela a data
class IntegridadeElementos {

    @Test
    fun integridadeElementos() {

        val hashLocalizacion = "93a58980b5970bea704b91498e3063af131fdb41672cb31507a5c834755c04bf"

        revisarHashElemento( "Localizacion", hashLocalizacion, TipoIdentificador.OBJECT )

        val hashL10nSingular = "ad5fc049987dd80296a90d5321c398e2c07a5edbda46750e9a65c258f3a16475"

        revisarHashElemento( "L10nSingular", hashL10nSingular, TipoIdentificador.SEALED_INTERFACE )

        val hashCondicion = "667389c0e707f4be9ed91439a4c8d9d6da32012410812065bb19816fc5e480a1"

        revisarHashElemento( "Condicion", hashCondicion, TipoIdentificador.SEALED_INTERFACE )

    }

}