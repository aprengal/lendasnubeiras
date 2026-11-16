package org.aprengal.lendasnubeiras.data.axustes

import org.aprengal.lendasnubeiras.data.axustes.Axustes.collerOpcion
import org.aprengal.lendasnubeiras.data.axustes.Axustes.gardarOpcion
import org.aprengal.lendasnubeiras.data.axustes.DatosTema.Variante.Claro
import org.aprengal.lendasnubeiras.data.axustes.DatosTema.Variante.Escuro
import org.aprengal.lendasnubeiras.data.axustes.DatosTema.Variante.Predeterminado
import org.aprengal.lendasnubeiras.data.localizacion.clases.ElementoL10n
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nOpcions


/**
 * Obxecto encargado de xestionar a configuración do tema visual da aplicación,
 * incluíndo a súa persistencia e a selección dos recursos visuais correspondentes.
 *
 * Permite utilizar un tema claro, escuro ou predeterminado, neste último caso
 * respectando a configuración de aparencia do sistema.
 */
object DatosTema {

    /**
     * Define as variantes de tema dispoñibles na aplicación.
     *
     * Permite identificar cada variante mediante unha clave textual e obter
     * a súa denominación correspondente a través dos mecanismos de localización.
     *
     * @property clave Identificador textual único da variante, independente do idioma.
     * @property nome Denominación localizada da variante, utilizada para obter
     * o texto que se presentará ao usuario.
     */
    enum class Variante( override val clave: String, override val nome: L10nOpcions ): ElementoL10n {

        /** Tema claro, independente da configuración do sistema. */
        Claro( "claro", L10nOpcions.TemaClaro ),

        /** Tema escuro, independente da configuración do sistema. */
        Escuro( "escuro", L10nOpcions.TemaEscuro ),

        /** Tema predeterminado, que respecta a configuración de aparencia do sistema. */
        Predeterminado( "predeterminado", L10nOpcions.TemaPredeterminado );

        companion object {
            /**
             * Busca unha variante a partir da súa clave textual.
             *
             * Se non existe ningunha variante coa clave indicada, devolve
             * [Predeterminado].
             *
             * @param clave Clave textual que identifica a variante de tema.
             * @return A [Variante] correspondente á clave ou [Predeterminado]
             * se non se atopa ningunha coincidencia.
             */
            fun buscar( clave: String ): Variante {
                return entries.find { variante -> variante.clave == clave } ?: Predeterminado
            }

        }

    }

    /**
     * Recupera a clave textual do tema actualmente almacenado nas opcións
     * da aplicación.
     *
     * @return A clave textual correspondente ao tema actual.
     */
    fun collerTema(): String {
        return collerOpcion( Opcion.Tema )
    }

    /**
     * Garda a variante de tema indicada nas opcións da aplicación.
     *
     * @param variante Variante de tema que se desexa almacenar.
     * @return `true` se a operación se realiza correctamente ou `false`
     * se non se consegue gardar a opción.
     */
    suspend fun gardarTema( variante: Variante ): Boolean {
        return gardarOpcion( Opcion.Tema, variante.clave )
    }

    /**
     * Selecciona un recurso en función da variante de tema configurada
     * e do estado do modo escuro do sistema.
     *
     * Se o tema seleccionado é [Variante.Claro] ou [Variante.Escuro],
     * utiliza o recurso correspondente independentemente da configuración
     * do sistema. Se é [Variante.Predeterminado], utiliza o recurso
     * correspondente ao modo de aparencia actual do sistema.
     *
     * @param temaActual Variante de tema seleccionada polo usuario.
     * @param temaEscuro `true` se o sistema está en modo escuro ou `false`
     * se está en modo claro.
     * @param claro Recurso que se utilizará no tema claro.
     * @param escuro Recurso que se utilizará no tema escuro.
     * @return O recurso correspondente á variante de tema seleccionada.
     */
    fun <T> escollerRecursoTema( temaActual: Variante, temaEscuro: Boolean, claro: T, escuro: T ): T {

        val imaxe = when ( temaActual ) {
            Claro -> claro
            Escuro -> escuro
            Predeterminado -> if ( temaEscuro ) escuro else claro
        }

        return imaxe

    }

}
