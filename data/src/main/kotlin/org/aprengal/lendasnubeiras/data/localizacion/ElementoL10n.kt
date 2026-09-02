package org.aprengal.lendasnubeiras.data.localizacion

import org.aprengal.lendasnubeiras.data.localizacion.claves.singular.L10nSingular

interface ElementoL10n {
    val clave: String
    val nome: L10nSingular
}