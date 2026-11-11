package org.aprengal.lendasnubeiras.data.actividades.datos.base

import org.aprengal.lendasnubeiras.data.localizacion.clases.ElementoL10n
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nActividades

/**
 * Define as categorías nas que se poden clasificar as actividades.
 *
 * Cada categoría permite identificar a temática ou o contexto no que se
 * desenvolve unha actividade, facilitando a súa organización e clasificación
 * dentro da aplicación.
 *
 * Cada valor dispón dunha clave única para a súa identificación e dun nome
 * localizado para a súa presentación na interface de usuario.
 *
 * Inclúe unha categoría xenérica ([Outros]) que se utiliza como valor por
 * defecto cando non se atopa unha categoría correspondente á clave indicada.
 *
 * @property clave Identificador único da categoría.
 * @property nome Recurso de localización co nome da categoría que se amosará
 *                na interface de usuario.
 */
enum class Categoria( override val clave: String, override val nome: L10nActividades ): ElementoL10n {

    /** Actividades que non encaixan en ningunha das outras categorías. */
    Outros( "outros", L10nActividades.CategoriaOutros ),

    /** Actividades relacionadas co ámbito dixital e as novas tecnoloxías. */
    Dixital( "dixital", L10nActividades.CategoriaDixital ),

    /** Actividades que se desenvolven principalmente ao aire libre. */
    AireLibre( "aire-libre", L10nActividades.CategoriaAireLibre ),

    /** Actividades que se desenvolven en espazos interiores. */
    Interior( "interior", L10nActividades.CategoriaInterior ),

    /** Actividades relacionadas coa lectura. */
    Lectura( "lectura", L10nActividades.CategoriaLectura );

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