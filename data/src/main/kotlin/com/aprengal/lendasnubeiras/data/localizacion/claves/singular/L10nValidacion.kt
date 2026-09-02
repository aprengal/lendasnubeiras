package com.aprengal.lendasnubeiras.data.localizacion.claves.singular

import com.aprengal.lendasnubeiras.data.localizacion.Dominio

enum class L10nValidacion( override val clave: String ) : L10nSingular {

    ErroFaltaArroba( "erro_arroba" ),
    ErroFormatoCorreo( "erro_formato_correo" );

    override val dominio: Dominio = Dominio.VALIDACION

}