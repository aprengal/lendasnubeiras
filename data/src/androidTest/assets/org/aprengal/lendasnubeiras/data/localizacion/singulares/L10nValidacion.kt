package org.aprengal.lendasnubeiras.data.localizacion.singulares

import org.aprengal.lendasnubeiras.data.localizacion.clases.Dominio

enum class L10nValidacion( internal val clave: String ) : L10nSingular {

    ErroFaltaArroba( "erro_arroba" ),
    ErroFormatoCorreo( "erro_formato_correo" );

    internal val dominio: Dominio = Dominio.VALIDACION

}