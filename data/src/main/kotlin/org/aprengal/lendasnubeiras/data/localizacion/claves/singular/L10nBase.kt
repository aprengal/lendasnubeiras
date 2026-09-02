package org.aprengal.lendasnubeiras.data.localizacion.claves.singular

import org.aprengal.lendasnubeiras.data.localizacion.Dominio

enum class L10nBase( internal val clave: String ) : L10nSingular {

    NomeApp( "nome_app" ),
    Aceptar( "aceptar" ),
    Cancelar( "cancelar" ),
    Si( "si" ),
    Non( "non" ),
    Reintentar( "reintentar_accion" );

    internal val dominio: Dominio = Dominio.BASE

}