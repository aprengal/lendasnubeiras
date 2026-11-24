package org.aprengal.lendasnubeiras.data.actividades.datos.dixitais

import org.aprengal.lendasnubeiras.data.localizacion.clases.ElementoL10n
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nMetaActividades


/**
 * Define os niveis de dificultade dispoñibles para as actividades.
 *
 * Permite clasificar as actividades segundo o grao de dificultade,
 * facilitando a súa organización e a clasificación das puntuacións
 * obtidas en función deste criterio.
 *
 * @property clave Identificador textual único do nivel de dificultade, independente do idioma.
 * @property nome Denominación localizada do nivel de dificultade, utilizada para obter
 * o texto que se presentará ao usuario.
 */
enum class Dificultade( override val clave: String, override val nome: L10nMetaActividades ): ElementoL10n {

    /** Nivel de dificultade máis baixo. */
    Facil( "facil", L10nMetaActividades.DificultadeFacil ),

    /** Nivel de dificultade medio. */
    Media( "media", L10nMetaActividades.DificultadeMedia ),

    /** Nivel de dificultade alto. */
    Dificil( "dificil", L10nMetaActividades.DificultadeDificil ),

    /** Nivel de dificultade máis alto. */
    Pesadelo( "pesadelo", L10nMetaActividades.DificultadePesadelo );

    companion object {

        /**
         * Obtén o nivel de dificultade correspondente a unha clave.
         *
         * Se non existe ningún nivel coa clave indicada, devolve [Media].
         *
         * @param clave Identificador do nivel de dificultade que se quere obter.
         * @return O nivel correspondente á clave ou [Media] se non existe.
         */
        fun escollerDificultade( clave: String ): Dificultade {
            return entries.find { dificultade -> dificultade.clave == clave } ?: Media
        }

    }

}