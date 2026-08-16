package com.aprengal.lendasnubeiras.localizacion

enum class Idioma( val nome: String, val codigo: String, val rexion: String, val pendente: String ) {

    GALEGO( "Galego", "gl", "ES", pendente = "PENDENTE!!" ),
    CASTELAN( "Español", "es", "ES", pendente = "¡PENDIENTE!" ),
    INGLES( "English", "en", "GB", pendente = "PENDING!!" ),

    NADA( "", "", "", "" );

    val codigoRexion: String get() = "${codigo}_$rexion"

    companion object {

        fun escollerIdiomaAplicacion( vararg codigos: String ): Idioma {
            codigos.forEach { codigoRexion -> entries.find { idioma -> idioma.codigoRexion == codigoRexion }?.let { resultado -> return resultado } }
            return CASTELAN
        }

        fun escollerIdioma( etiqueta: String ): Idioma {
            return entries.find { idioma -> idioma.codigoRexion == etiqueta } ?: NADA
        }

        fun buscarNome( nome: String ): Idioma? {
            return entries.find { idioma -> idioma.nome == nome }
        }

    }

}