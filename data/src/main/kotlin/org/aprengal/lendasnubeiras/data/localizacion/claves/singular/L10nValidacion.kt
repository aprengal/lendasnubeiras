package org.aprengal.lendasnubeiras.data.localizacion.claves.singular

import org.aprengal.lendasnubeiras.data.localizacion.Dominio

enum class L10nValidacion( internal val clave: String ) : L10nSingular {

    ErroFaltaArroba( "erro_arroba" ),
    ErroFormatoCorreo( "erro_formato_correo" );

    internal val dominio: Dominio = Dominio.VALIDACION

}