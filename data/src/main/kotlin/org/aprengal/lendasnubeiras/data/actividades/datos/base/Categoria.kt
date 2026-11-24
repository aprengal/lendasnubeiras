package org.aprengal.lendasnubeiras.data.actividades.datos.base

import org.aprengal.lendasnubeiras.data.localizacion.clases.ElementoL10n
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nMetaActividades

/**
 * Define as categorías nas que se poden clasificar as actividades.
 *
 * Permite identificar a temática ou o contexto dunha actividade,
 * facilitando a súa organización e clasificación dentro da aplicación.
 *
 * Inclúe unha categoría xenérica ([Outros]), utilizada como valor por
 * defecto cando non se atopa unha categoría correspondente á clave indicada.
 *
 * @property clave Identificador textual único da categoría, independente do idioma.
 * @property nome Denominación localizada da categoría, utilizada para obter
 * o texto que se presentará ao usuario.
 */
enum class Categoria( override val clave: String, override val nome: L10nMetaActividades ): ElementoL10n {

    /** Actividades que non encaixan en ningunha das outras categorías. */
    Outros( "outros", L10nMetaActividades.CategoriaOutros ),

    /** Actividades relacionadas co ámbito dixital e as novas tecnoloxías. */
    Dixital( "dixital", L10nMetaActividades.CategoriaDixital ),

    /** Actividades que se desenvolven principalmente ao aire libre. */
    AireLibre( "aire-libre", L10nMetaActividades.CategoriaAireLibre ),

    /** Actividades que se desenvolven en espazos interiores. */
    Interior( "interior", L10nMetaActividades.CategoriaInterior ),

    /** Actividades relacionadas coa lectura. */
    Lectura( "lectura", L10nMetaActividades.CategoriaLectura );

    companion object {

        /**
         * Obtén a categoría correspondente a unha clave.
         *
         * Se non existe ningunha categoría coa clave indicada, devolve
         * [Outros] como categoría por defecto.
         *
         * @param clave Identificador da categoría que se quere obter.
         * @return A categoría correspondente á clave ou [Outros] se non existe.
         */
        fun escollerCategoria( clave: String ): Categoria {
            return entries.find { categoria -> categoria.clave == clave } ?: Outros
        }

    }

}