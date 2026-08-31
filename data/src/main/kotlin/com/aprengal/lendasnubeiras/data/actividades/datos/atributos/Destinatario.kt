package com.aprengal.lendasnubeiras.data.actividades.datos.atributos

enum class Destinatario( override val clave: String ): Atributo {

    XERAL( "xeral" ),
    PEQUES( "peques" ),
    XUVENIL( "xuvenil" ),
    ADULTOS( "adultos" ),
    MAIORES( "maiores" ),
    MIXTO( "mixto" );

    companion object {

        fun escollerDestinatario( clave: String ): Destinatario {
            return entries.find { destinario -> destinario.clave == clave } ?: XERAL
        }

    }

}