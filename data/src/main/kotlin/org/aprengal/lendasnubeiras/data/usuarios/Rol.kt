package org.aprengal.lendasnubeiras.data.usuarios

/**
 * Enum que define os diferentes roles de usuario dispoñibles no sistema e as súas claves asociadas.
 */
enum class Rol( val clave: String ) {

    /** Sen rol ou usuario non autenticado. */
    NADA( "nada" ),

    /** Rol de lector. */
    LECTOR( "lector" ),

    /** Rol de monitor. */
    MONITOR( "monitor" );

    companion object {

        /**
         * Busca e devolve un [Rol] a partir da súa clave en texto.
         * Se non se atopa ou non coincide, devolve por defecto [NADA].
         *
         * @param clave A cadea de texto que identifica o rol.
         * @return O [Rol] correspondente ou [NADA].
         */
        fun buscarRol( clave: String ): Rol {
            return entries.find { elemento -> elemento.clave == clave } ?: NADA
        }

    }

}