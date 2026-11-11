package org.aprengal.lendasnubeiras.data.localizacion.clases

/**
 * Enumera os idiomas dispoñibles na aplicación e os seus datos de identificación
 * e localización.
 *
 * Cada idioma dispón dun nome, un código lingüístico, un código de rexión
 * e un texto empregado para identificar contidos de tradución pendente.
 *
 * Inclúe tamén o valor especial Nada, utilizado como estado inicial ou
 * ausencia de idioma seleccionado, por exemplo durante a creación dun
 * obxecto antes de establecer o idioma configurado nas opcións da aplicación.
 */
enum class Idioma( val nome: String, val codigo: String, val rexion: String, val pendente: String ) {

    Galego( "Galego", "gl", "ES", pendente = "PENDENTE!!" ),
    Castelan( "Español", "es", "ES", pendente = "¡PENDIENTE!" ),
    Ingles( "English", "en", "GB", pendente = "PENDING!!" ),

    Nada( "", "", "", "" );

    /**
     * Obtén o código lingüístico combinado co código de rexión,
     * seguindo o formato código_rexión.
     */
    val codigoRexion: String get() = "${codigo}_$rexion"

    companion object {

        /**
         * Selecciona o idioma da aplicación a partir dunha lista de códigos
         * lingüísticos e rexionais, respectando a orde de preferencia recibida.
         *
         * Se ningún dos códigos coincide cun idioma dispoñible, utiliza
         * o castelán como idioma predeterminado.
         *
         * @param codigos Códigos lingüísticos e rexionais ordenados por preferencia.
         * @return Primeiro idioma coincidente ou castelán se non se atopa ningún.
         */
        fun escollerIdiomaAplicacion( vararg codigos: String ): Idioma {
            codigos.forEach { codigoRexion -> entries.find { idioma -> idioma.codigoRexion == codigoRexion }?.let { resultado -> return resultado } }
            return Castelan
        }

        /**
         * Obtén o idioma correspondente a un código lingüístico e rexional.
         *
         * A diferenza de escollerIdiomaAplicacion, se non se atopa ningunha
         * coincidencia devolve o valor especial Nada en lugar de utilizar
         * un idioma predeterminado.
         *
         * @param etiqueta Código lingüístico e rexional que se quere identificar.
         * @return Idioma correspondente ou Nada se non existe ningunha coincidencia.
         */
        fun escollerIdioma( etiqueta: String ): Idioma {
            return entries.find { idioma -> idioma.codigoRexion == etiqueta } ?: Nada
        }

    }

}