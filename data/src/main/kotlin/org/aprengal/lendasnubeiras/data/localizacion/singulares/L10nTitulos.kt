package org.aprengal.lendasnubeiras.data.localizacion.singulares

import org.aprengal.lendasnubeiras.data.localizacion.clases.Dominio

enum class L10nTitulos( internal val clave: String ) : L10nSingular {

    Benvida( "benvida" ),
    Acceso( "acceso" ),
    Rexistro( "rexistro" ),
    Axustes( "axustes" ),
    Actividades( "actividades" ),
    CrearActividade( "crear_actividade" );

    internal val dominio: Dominio = Dominio.TITULOS

}