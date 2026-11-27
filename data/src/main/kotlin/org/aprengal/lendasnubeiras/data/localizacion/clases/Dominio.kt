package org.aprengal.lendasnubeiras.data.localizacion.clases

/**
 * Dominios que identifican os distintos recursos e seccións da aplicación.
 *
 * Cada valor define o nome utilizado para identificar o dominio correspondente.
 */
enum class Dominio( val nome: String ) {

    /** Dominio dos datos meta das actividades. */
    META_ACTIVIDADES( "meta-actividades" ),

    /** Dominio relacionado coa autenticación dos usuarios. */
    AUTENTICACION( "autenticacion" ),

    /** Dominio de recursos xerais da aplicación. */
    BASE( "base" ),

    /** Dominio dos recursos gráficos das iconas. */
    ICONAS( "iconas" ),

    /** Dominio dos títulos das pantallas. */
    TITULOS( "titulos" ),

    /** Dominio das opcións e preferencias da aplicación. */
    OPCIONS( "opcions" )

}