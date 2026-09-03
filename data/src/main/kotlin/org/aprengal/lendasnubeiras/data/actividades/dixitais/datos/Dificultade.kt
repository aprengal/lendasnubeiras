package org.aprengal.lendasnubeiras.data.actividades.dixitais.datos

import org.aprengal.lendasnubeiras.data.localizacion.clases.ElementoL10n
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nActividades

enum class Dificultade( override val clave: String, override val nome: L10nActividades ): ElementoL10n {

    Facil( "facil", L10nActividades.DificultadeFacil ),
    Normal( "normal", L10nActividades.DificultadeMedia ),
    Dificil( "dificil", L10nActividades.DificultadeDificil ),
    Pesadelo( "pesadelo", L10nActividades.DificultadePesadelo );

}