package com.aprengal.lendasnubeiras.elementos.actividades

data class Actividade(
    val id: Long,
    val titulo: String,
    val idAutoria: Long,
    val idCategoria: Categoria,
    val idDestinatario: Destinatario,
    val idIdioma: Idioma,
    val estado: Int,
    val duracion: Int,
    val descricion: String,
    val obxectivo: String,
    val materiais: String,
    val dataModificado: Long
)

enum class Idioma( val nome: String, val codigo: String, val rexion: String, val pendente: String ) {

    GALEGO( "Galego", "gl", "ES", pendente = "PENDENTE!!" ),
    CASTELAN( "Español", "es", "ES", pendente = "¡PENDIENTE!" ),
    INGLES( "English", "en", "GB", pendente = "PENDING!!" ),

    NADA( "", "", "", "" );

    val codigoRexion: String get() = "{$codigo}_$rexion"

    companion object {

        fun escollerIdiomaAplicacion( vararg codigos: String ): Idioma {
            codigos.forEach { codigoRexion -> entries.find { idioma -> idioma.codigoRexion == codigoRexion }?.let { resultado -> return resultado } }
            return CASTELAN
        }

        fun escollerIdioma( etiqueta: String ): Idioma {
            return entries.find { idioma -> idioma.codigoRexion == etiqueta } ?: NADA
        }

    }

}

enum class Categoria( val clave: String ) {

    OUTROS( "outros" ),
    DIXITAL( "dixital" ),
    AIRE_LIBRE( "aire-libre" ),
    INTERIOR( "interior" ),
    LECTURA( "lectura" );

    companion object {

        fun escollerCategoria( clave: String ): Categoria {
            return entries.find { categoria -> categoria.clave == clave } ?: OUTROS
        }

    }

}

enum class Destinatario( val clave: String ) {

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