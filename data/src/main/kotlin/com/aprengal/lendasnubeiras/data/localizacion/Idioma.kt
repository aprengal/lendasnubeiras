package com.aprengal.lendasnubeiras.data.localizacion

enum class Idioma( val nome: String, val codigo: String, val rexion: String, val pendente: String ) {

    Galego( "Galego", "gl", "ES", pendente = "PENDENTE!!" ),
    Castelan( "Español", "es", "ES", pendente = "¡PENDIENTE!" ),
    Ingles( "English", "en", "GB", pendente = "PENDING!!" ),

    Nada( "", "", "", "" );

    val codigoRexion: String get() = "${codigo}_$rexion"

    companion object {

        fun escollerIdiomaAplicacion( vararg codigos: String ): Idioma {
            codigos.forEach { codigoRexion -> entries.find { idioma -> idioma.codigoRexion == codigoRexion }?.let { resultado -> return resultado } }
            return Castelan
        }

        fun escollerIdioma( etiqueta: String ): Idioma {
            return entries.find { idioma -> idioma.codigoRexion == etiqueta } ?: Nada
        }

    }

}