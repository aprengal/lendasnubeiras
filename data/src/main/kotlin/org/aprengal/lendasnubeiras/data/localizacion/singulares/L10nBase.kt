package org.aprengal.lendasnubeiras.data.localizacion.singulares

import org.aprengal.lendasnubeiras.data.localizacion.clases.Dominio

enum class L10nBase( internal val clave: String ) : L10nSingular {

    NomeApp( "nome_app" ),
    Aceptar( "aceptar" ),
    Cancelar( "cancelar" ),
    Si( "si" ),
    Non( "non" ),
    Reintentar( "reintentar_accion" );

    internal val dominio: Dominio = Dominio.BASE

}