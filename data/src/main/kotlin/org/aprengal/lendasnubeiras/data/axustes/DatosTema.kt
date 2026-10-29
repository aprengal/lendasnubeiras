package org.aprengal.lendasnubeiras.data.axustes

import org.aprengal.lendasnubeiras.data.axustes.Axustes.collerOpcion
import org.aprengal.lendasnubeiras.data.axustes.Axustes.gardarOpcion
import org.aprengal.lendasnubeiras.data.axustes.DatosTema.Variante.Claro
import org.aprengal.lendasnubeiras.data.axustes.DatosTema.Variante.Escuro
import org.aprengal.lendasnubeiras.data.axustes.DatosTema.Variante.Predeterminado
import org.aprengal.lendasnubeiras.data.localizacion.clases.ElementoL10n
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nOpcions

/**
 * Obxecto encargado de xestionar a configuración, persistencia e selección de recursos
 * asociados ao tema visual da aplicación (claro, escuro ou predeterminado).
 */
object DatosTema {

    /**
     * Define as variantes de tema dispoñibles na aplicación, asociando unha clave en texto
     * cunha opción de tradución (L10n).
     */
    enum class Variante( override val clave: String, override val nome: L10nOpcions ): ElementoL10n {

        /** Tema claro fixo. */
        Claro( "claro", L10nOpcions.TemaClaro ),

        /** Tema escuro fixo. */
        Escuro( "escuro", L10nOpcions.TemaEscuro ),

        /** Tema predeterminado (herda o comportamento do sistema). */
        Predeterminado( "predeterminado", L10nOpcions.TemaPredeterminado );

        companion object {
            /**
             * Busca e devolve unha variante a partir da súa clave en texto.
             * Se non se atopa, devolve por defecto [Predeterminado].
             *
             * @param clave A cadea de texto que identifica a variante.
             * @return A [Variante] correspondente ou [Predeterminado].
             */
            fun buscar( clave: String ): Variante {
                return entries.find { variante -> variante.clave == clave } ?: Predeterminado
            }

        }

    }

    /**
     * Recupera o tema actual almacenado nas opcións da aplicación.
     *
     * @return Unha cadea de texto coa clave do tema actual.
     */
    fun collerTema(): String {
        return collerOpcion( Opcion.Tema )
    }

    /**
     * Garda unha nova variante de tema nas opcións da aplicación de forma asíncrona.
     *
     * @param variante A [Variante] que se desexa gardar.
     * @return `true` se se gardou correctamente, `false` no caso contrario.
     */
    suspend fun gardarTema( variante: Variante ): Boolean {
        return gardarOpcion( Opcion.Tema, variante.clave )
    }

    /**
     * Selecciona un recurso (como unha imaxe ou elemento visual) en función do tema actual
     * e do estado do modo escuro do sistema.
     *
     * @param T O tipo de obxecto ou recurso a escoller.
     * @param temaActual A [Variante] de tema seleccionada polo usuario.
     * @param temaEscuro Indica se o contexto actual está en modo escuro.
     * @param claro O recurso correspondente ao tema claro.
     * @param escuro O recurso correspondente ao tema escuro.
     * @return O recurso axeitado (`claro` ou `escuro`) segundo corresponda.
     */
    fun <T> escollerVarianteImaxe( temaActual: Variante, temaEscuro: Boolean, claro: T, escuro: T ): T {

        val imaxe = when ( temaActual ) {
            Claro -> claro
            Escuro -> escuro
            Predeterminado -> if ( temaEscuro ) escuro else claro
        }

        return imaxe

    }

}