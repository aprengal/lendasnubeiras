package org.aprengal.lendasnubeiras.data.localizacion.claves.singular

import org.aprengal.lendasnubeiras.data.localizacion.Dominio

enum class L10nTitulos( internal val clave: String ) : L10nSingular {

    Benvida( "benvida" ),
    Acceso( "acceso" ),
    Rexistro( "rexistro" ),
    Axustes( "axustes" ),
    Actividades( "actividades" );

    internal val dominio: Dominio = Dominio.TITULOS

}