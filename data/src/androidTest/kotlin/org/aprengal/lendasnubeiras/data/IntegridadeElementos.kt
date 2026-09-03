package org.aprengal.lendasnubeiras.data

import org.junit.Test

//Como esta clase emprega tando data como ui, igual é mellor movela a data
class IntegridadeElementos {

    @Test
    fun integridadeElementos() {

        val hashLocalizacion = "7a64c54acdaacf4fe8eaf2763190ad6d5ef6eb94ace9adc70c5eb2006e772a37"

        revisarHashElemento( "Localizacion", hashLocalizacion, TipoIdentificador.OBJECT )

        val hashL10nSingular = "ad5fc049987dd80296a90d5321c398e2c07a5edbda46750e9a65c258f3a16475"

        revisarHashElemento( "L10nSingular", hashL10nSingular, TipoIdentificador.SEALED_INTERFACE )

        val hashCondicion = "d14dbcd5af527022dada6399232be2345504e8cbd180c41fdd1f3541fa3b63fa"

        revisarHashElemento( "Condicion", hashCondicion, TipoIdentificador.SEALED_INTERFACE )

    }

}