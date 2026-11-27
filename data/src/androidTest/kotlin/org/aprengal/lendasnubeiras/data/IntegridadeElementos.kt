package org.aprengal.lendasnubeiras.data

import org.junit.Test

//Como esta clase emprega tando data como ui, igual é mellor movela a data
class IntegridadeElementos {

    @Test
    fun integridadeElementos() {

        val hashLocalizacion = "bd774ef036b592a4a091806936fb8dc66199e3e16fc1ac51e9e29c5d37e36520"

        revisarHashElemento( "Localizacion", hashLocalizacion, TipoIdentificador.OBJECT )

        val hashL10nSingular = "02cfa5fa080d782fcd634eb25bb71fee0a8a38e60c62fedf93a542f12fe205ee"

        revisarHashElemento( "L10nSingular", hashL10nSingular, TipoIdentificador.SEALED_INTERFACE )

        val hashCondicion = "667389c0e707f4be9ed91439a4c8d9d6da32012410812065bb19816fc5e480a1"

        revisarHashElemento( "Condicion", hashCondicion, TipoIdentificador.SEALED_INTERFACE )

    }

}