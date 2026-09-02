package com.aprengal.lendasnubeiras.data.localizacion

import com.aprengal.lendasnubeiras.data.localizacion.claves.singular.L10nSingular

interface ElementoL10n {
    val clave: String
    val nome: L10nSingular
}