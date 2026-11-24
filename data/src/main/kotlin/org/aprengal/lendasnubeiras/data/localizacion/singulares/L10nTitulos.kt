package org.aprengal.lendasnubeiras.data.localizacion.singulares

import org.aprengal.lendasnubeiras.data.localizacion.clases.Dominio


/**
 * Textos localizados utilizados como títulos das pantallas da aplicación.
 */
enum class L10nTitulos( internal val clave: String ) : L10nSingular {

    /** Título da pantalla de benvida. */
    Benvida( "benvida" ),

    /** Título da pantalla de inicio de sesión. */
    Acceso( "acceso" ),

    /** Título da pantalla de rexistro dunha nova conta. */
    Rexistro( "rexistro" ),

    /** Título da pantalla de axustes da aplicación. */
    Axustes( "axustes" ),

    /** Título da pantalla do catálogo de actividades. */
    Catalogo( "catalogo" ),

    /** Título da pantalla de creación dunha actividade. */
    CrearActividade( "crear_actividade" );

    /** Dominio de localización ao que pertencen estes textos. */
    internal val dominio: Dominio = Dominio.TITULOS

}