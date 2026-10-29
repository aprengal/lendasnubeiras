package org.aprengal.lendasnubeiras.data.usuarios

/**
 * Enum que define os diferentes roles de usuario dispoñibles no sistema e as súas claves asociadas.
 *
 * Nota: Non estende [ElementoL10n] nin dispón dunha propiedade de tradución directa xa que,
 * aínda que o nome do rol se poida amosar puntualmente na UI, é innecesario cargar esa información
 * de internacionalización se na maioría dos casos só se emprega en pantallas de administración.
 */
enum class Rol( val clave: String ) {

    /** Sen rol ou usuario non autenticado. */
    NADA( "nada" ),

    /** Rol básico de monitor (antigo rol lector). */
    MONITOR( "monitor" ),

    /** Rol de colaborador (permitindo a colaboración de persoas alleas con permisos reducidos). */
    COLABORADOR( "colaborador" ),

    /** Rol de autor de contido. */
    AUTOR( "autor" ),

    /** Rol de editor con permisos ampliados. */
    EDITOR( "editor" ),

    /** Rol de administrador con control total do sistema. */
    ADMIN( "admin" );

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