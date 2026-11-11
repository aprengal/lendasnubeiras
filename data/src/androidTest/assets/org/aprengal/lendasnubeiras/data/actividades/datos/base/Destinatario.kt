package org.aprengal.lendasnubeiras.data.actividades.datos.base

import org.aprengal.lendasnubeiras.data.localizacion.clases.ElementoL10n
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nActividades

/**
 * Define os grupos de persoas aos que vai dirixida unha actividade.
 *
 * Permite clasificar as actividades segundo o seu público destinatario,
 * facilitando a súa identificación e filtrado en función do perfil das
 * persoas ás que están dirixidas.
 *
 * As categorías inclúen grupos definidos por franxas de idade, así como
 * opcións para actividades dirixidas ao público xeral ou a varios grupos.
 *
 * Cada valor dispón dunha clave única para a súa identificación e dun nome
 * localizado para a súa presentación na interface de usuario.
 *
 * @property clave Identificador único do destinatario.
 * @property nome Recurso de localización co nome do destinatario que se
 *                amosará na interface de usuario.
 */
enum class Destinatario( override val clave: String, override val nome: L10nActividades ): ElementoL10n {

    /** Actividades dirixidas ao público xeral, sen unha franxa de idade específica. */
    Xeral( "xeral", L10nActividades.DestinatarioXeral ),

    /** Actividades dirixidas a nenos e nenas de entre 6 e 11 anos. */
    Peques( "peques", L10nActividades.DestinatarioPeques ),

    /** Actividades dirixidas a mozos e mozas de entre 12 e 18 anos. */
    Xuvenil( "xuvenil", L10nActividades.DestinatarioXuvenil ),

    /** Actividades dirixidas a persoas adultas, sen unha franxa de idade específica. */
    Adultos( "adultos", L10nActividades.DestinatarioAdultos ),

    /** Actividades dirixidas a persoas que teñen 65 anos ou máis. */
    Maiores( "maiores", L10nActividades.DestinatarioMaiores ),

    /** Actividades dirixidas a persoas de diferentes grupos de idade. */
    Mixto( "mixto", L10nActividades.DestinatarioMixto );

    companion object {

        /**
         * Obtén o destinatario correspondente a unha clave.
         *
         * Se non existe ningún destinatario coa clave indicada, devolve
         * [Xeral] como valor por defecto.
         *
         * @param clave Identificador do destinatario que se quere obter.
         * @return O destinatario correspondente á clave ou [Xeral] se non existe.
         */
        fun escollerDestinatario( clave: String ): Destinatario {
            return entries.find { destinario -> destinario.clave == clave } ?: Xeral
        }

    }

}