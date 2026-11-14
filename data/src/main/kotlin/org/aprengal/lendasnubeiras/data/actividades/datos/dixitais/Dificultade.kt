package org.aprengal.lendasnubeiras.data.actividades.datos.dixitais

import org.aprengal.lendasnubeiras.data.localizacion.clases.ElementoL10n
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nActividades


/**
 * Define os niveis de dificultade dispoñibles para as actividades.
 *
 * Permite establecer distintos graos de dificultade para cada actividade,
 * identificados mediante unha clave única e un nome localizado.
 *
 * Estes niveis utilízanse, entre outras cousas, para clasificar e organizar
 * as puntuacións obtidas en función da dificultade da actividade.
 *
 * @property clave Identificador único do nivel de dificultade.
 * @property nome Recurso de localización co nome do nivel de dificultade
 * que se amosará na interface de usuario.
 */
enum class Dificultade( override val clave: String, override val nome: L10nActividades ): ElementoL10n {

    /** Nivel de dificultade máis baixo. */
    Facil( "facil", L10nActividades.DificultadeFacil ),

    /** Nivel de dificultade medio. */
    Normal( "normal", L10nActividades.DificultadeMedia ),

    /** Nivel de dificultade alto. */
    Dificil( "dificil", L10nActividades.DificultadeDificil ),

    /** Nivel de dificultade máis alto. */
    Pesadelo( "pesadelo", L10nActividades.DificultadePesadelo );

    companion object {

        /**
         * Obtén o nivel de dificultade correspondente a unha clave.
         *
         * Se non existe ningún nivel coa clave indicada, devolve [Normal].
         *
         * @param clave Identificador do nivel de dificultade que se quere obter.
         * @return O nivel correspondente á clave ou [Normal] se non existe.
         */
        fun escollerDificultade( clave: String ): Dificultade {
            return entries.find { dificultade -> dificultade.clave == clave } ?: Normal
        }

    }

}