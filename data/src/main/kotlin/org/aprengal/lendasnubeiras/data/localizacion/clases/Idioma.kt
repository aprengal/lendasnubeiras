package org.aprengal.lendasnubeiras.data.localizacion.clases

/**
 * Define os idiomas dispoñibles na aplicación e os seus datos de identificación
 * e localización.
 *
 * Inclúe o valor especial Nada, que representa a ausencia dun idioma seleccionado
 * e permite establecer un estado inicial antes de aplicar o idioma configurado
 * nas opcións da aplicación.
 *
 * @property nome Nome do idioma.
 * @property codigo Código lingüístico do idioma.
 * @property rexion Código da rexión asociada ao idioma.
 * @property pendente Texto identificativo dos contidos pendentes de tradución.
 */
enum class Idioma( val nome: String, val codigo: String, val rexion: String, val pendente: String ) {

    /** Idioma galego. */
    Galego( "Galego", "gl", "ES", pendente = "PENDENTE!!" ),
    /** Idioma castelán. */
    Castelan( "Español", "es", "ES", pendente = "¡PENDIENTE!" ),
    /** Idioma inglés. */
    Ingles( "English", "en", "GB", pendente = "PENDING!!" ),

    /** Ausencia dun idioma seleccionado ou configurado. */
    Nada( "", "", "", "" );

    /**
     * Código lingüístico e rexional do idioma, no formato código_rexión.
     */
    val codigoRexion: String get() = "${codigo}_$rexion"

    companion object {

        /**
         * Determina o idioma da aplicación a partir dos códigos lingüísticos
         * e rexionais recibidos, respectando a súa orde de preferencia.
         *
         * Se ningún código coincide cun idioma dispoñible, devolve o castelán
         * como idioma predeterminado.
         *
         * @param codigos Códigos lingüísticos e rexionais ordenados por preferencia.
         * @return Primeiro idioma coincidente ou castelán se non existe coincidencia.
         */
        fun escollerIdiomaAplicacion( vararg codigos: String ): Idioma {
            codigos.forEach { codigoRexion -> entries.find { idioma -> idioma.codigoRexion == codigoRexion }?.let { resultado -> return resultado } }
            return Castelan
        }

        /*
        /**
         * Identifica o idioma correspondente a un código lingüístico e rexional.
         *
         * Se non existe ningunha coincidencia, devolve Nada en lugar de
         * seleccionar un idioma predeterminado.
         *
         * @param etiqueta Código lingüístico e rexional que se quere identificar.
         * @return Idioma correspondente ou Nada se non existe coincidencia.
         */
        fun escollerIdioma( etiqueta: String ): Idioma {
            return entries.find { idioma -> idioma.codigoRexion == etiqueta } ?: Nada
        }*/

    }

}