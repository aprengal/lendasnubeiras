package org.aprengal.lendasnubeiras.data.actividades.datos

import org.aprengal.lendasnubeiras.data.localizacion.clases.ElementoL10n
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nActividades

enum class Destinatario( override val clave: String, override val nome: L10nActividades): ElementoL10n {

    Xeral( "xeral", L10nActividades.DestinatarioXeral ),
    Peques( "peques", L10nActividades.DestinatarioPeques ),
    Xuvenil( "xuvenil", L10nActividades.DestinatarioXuvenil ),
    Adultos( "adultos", L10nActividades.DestinatarioAdultos ),
    Maiores( "maiores", L10nActividades.DestinatarioMaiores ),
    Mixto( "mixto", L10nActividades.DestinatarioMixto );

    companion object {

        fun escollerDestinatario( clave: String ): Destinatario {
            return entries.find { destinario -> destinario.clave == clave } ?: Xeral
        }

    }

}