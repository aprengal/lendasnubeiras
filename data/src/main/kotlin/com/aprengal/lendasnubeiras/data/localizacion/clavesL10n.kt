package com.aprengal.lendasnubeiras.data.localizacion

internal interface L10n {

    val clave: String
    val dominio: Dominio

}

enum class L10nSingular( override val clave: String, override val dominio: Dominio ): L10n {

    // Base
    NOME_APP( "nome_app", Dominio.Base ),
    ACEPTAR( "aceptar", Dominio.Base ),
    CANCELAR( "cancelar", Dominio.Base ),
    SI( "si", Dominio.Base ),
    NON( "non", Dominio.Base ),
    REINTENTAR( "reintentar_accion", Dominio.Base ),

    //Títulos
    TITULO_BENVIDA( "titulo_benvida", Dominio.Titulos ),
    TITULO_ACCESO( "titulo_acceso", Dominio.Titulos ),
    TITULO_REXISTRO( "titulo_rexistro", Dominio.Titulos ),
    TITULO_AXUSTES( "titulo_axustes", Dominio.Titulos ),

    //Autenticacion
    BOTON_ACCESO( "boton_acceso", Dominio.Autenticacion ),
    BOTON_REXISTRO( "boton_rexistro", Dominio.Autenticacion ),
    BOTON_ANONIMO( "boton_anonimo", Dominio.Autenticacion ),

    // Menu
    MENU_AXUSTES( "menu_axustes", Dominio.Menu ),

    MENU_CREAR( "menu_crear", Dominio.Menu ),

    MENU_INICIO( "menu_inicio", Dominio.Menu ),
    MENU_BUSCAR( "menu_buscar", Dominio.Menu ),
    MENU_IDIOMA( "menu_idioma", Dominio.Menu ),
    MENU_ACTIVIDADES( "menu_actividades", Dominio.Menu ),

    //Opcions
    DIALOGO_CAMBIO_IDIOMA( "dialogo_cambio_idioma", Dominio.Opcions ),
    DIALOGO_CAMBIO_TEMA( "dialogo_cambio_tema", Dominio.Opcions ),
    DIALOGO_PECHE_SESION( "dialogo_peche_sesion", Dominio.Opcions ),
    BOTON_CAMBIO_IDIOMA( "boton_cambio_idioma", Dominio.Opcions ),
    BOTON_CAMBIO_TEMA( "boton_cambio_tema", Dominio.Opcions ),
    BOTON_PECHE_SESION( "boton_peche_sesion", Dominio.Opcions ),
    CAMBIO_TEMA_CLARO( "cambio_tema_claro", Dominio.Opcions ),
    CAMBIO_TEMA_ESCURO( "cambio_tema_escuro", Dominio.Opcions ),
    CAMBIO_TEMA_PREDETERMINADO( "cambio_tema_predeterminado", Dominio.Opcions ),
    GARDADO_FALLIDO_CAMBIO_IDIOMA( "gardado_fallido_cambio_idioma", Dominio.Opcions ),
    GARDADO_FALLIDO_CAMBIO_TEMA( "gardado_fallido_cambio_tema", Dominio.Opcions ),
    GARDADO_FALLIDO_PECHE_SESION( "gardado_fallido_peche_sesion", Dominio.Opcions ),

    // Test
    CARLA( "carla", Dominio.Test ),
    NATASHA( "natasha", Dominio.Test );

    // Actividade
    //TITULO_ACTIVIDADE( "titulo", Dominio.Actividade ),
    //DESCRIPCION_ACTIVIDADE( "descripcion" );

    fun texto(): String = Localizacion.l10n( this )

    companion object {

        fun buscar( clave: String ): L10nSingular {
            return entries.find { elemento -> elemento.clave == clave } ?: error( "Problema coa clave $clave" )
        }

    }

}

enum class L10nPlural( override val clave: String, override val dominio: Dominio ): L10n {

    // Test
    MENSAXES_NOVAS( "mensaxes_novas", Dominio.Test );

    fun texto( num: Int ): String = Localizacion.l10nPlural( this, num )

}

enum class L10nVariante( override val clave: String, override val dominio: Dominio ): L10n {

    // Test
    MENSAXE_FALLOS( "mensaxe_fallos", Dominio.Test );

    fun texto( num: Int ): String = Localizacion.l10nVariante( this, num )

}