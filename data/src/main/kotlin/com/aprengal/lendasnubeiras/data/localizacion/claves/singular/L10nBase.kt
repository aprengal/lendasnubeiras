package com.aprengal.lendasnubeiras.data.localizacion.claves.singular

import com.aprengal.lendasnubeiras.data.localizacion.Dominio

enum class L10nBase( override val clave: String ) : L10nSingular {

    NomeApp( "nome_app" ),
    Aceptar( "aceptar" ),
    Cancelar( "cancelar" ),
    Si( "si" ),
    Non( "non" ),
    Reintentar( "reintentar_accion" );

    override val dominio: Dominio = Dominio.BASE

}