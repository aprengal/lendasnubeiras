package com.aprengal.lendasnubeiras.data.actividades.dixitais.elementos

import com.aprengal.lendasnubeiras.data.localizacion.ElementoL10n
import com.aprengal.lendasnubeiras.data.localizacion.claves.singular.L10nActividades

enum class Dificultade( override val clave: String, override val nome: L10nActividades): ElementoL10n {

    Facil( "facil", L10nActividades.DificultadeFacil ),
    Normal( "normal", L10nActividades.DificultadeMedia ),
    Dificil( "dificil", L10nActividades.DificultadeDificil ),
    Pesadelo( "pesadelo", L10nActividades.DificultadePesadelo );

}