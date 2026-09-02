package com.aprengal.lendasnubeiras.data.localizacion.claves.singular

import com.aprengal.lendasnubeiras.data.localizacion.Dominio

enum class L10nTitulos( override val clave: String ) : L10nSingular {

    Benvida( "benvida" ),
    Acceso( "acceso" ),
    Rexistro( "rexistro" ),
    Axustes( "axustes" );

    override val dominio: Dominio = Dominio.TITULOS

}