package org.aprengal.lendasnubeiras.data.actividades.datos.dixitais

import org.aprengal.lendasnubeiras.data.localizacion.clases.ElementoL10n
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nActividades

/**
 * Define os niveis de dificultade dispoñibles para as actividades.
 *
 * Permite establecer diferentes graos de dificultade para unha actividade,
 * identificando cada nivel mediante unha clave única e un nome localizado.
 *
 * Estes niveis utilízanse, entre outras cousas, para diferenciar e organizar
 * as puntuacións obtidas polos xogadores segundo a dificultade coa que
 * realizaron unha actividade.
 *
 * @property clave Identificador único do nivel de dificultade.
 * @property nome Recurso de localización co nome da dificultade que se
 *                amosará na interface de usuario.
 */
enum class Dificultade( override val clave: String, override val nome: L10nActividades ): ElementoL10n {

    /** Nivel de dificultade máis baixo. */
    Facil( "facil", L10nActividades.DificultadeFacil ),

    /** Nivel de dificultade intermedio. */
    Normal( "normal", L10nActividades.DificultadeMedia ),

    /** Nivel de dificultade elevado. */
    Dificil( "dificil", L10nActividades.DificultadeDificil ),

    /** Nivel de dificultade máis elevado. */
    Pesadelo( "pesadelo", L10nActividades.DificultadePesadelo );

}