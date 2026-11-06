package org.aprengal.lendasnubeiras.data.localizacion.clases

import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nSingular

/**
 * Define a estrutura común dos elementos que dispoñen dunha clave identificativa
 * e dun nome localizado, destinado a obter o texto que se mostrará ao usuario
 * na interface da aplicación.
 *
 * Permite identificar os elementos independentemente do idioma e acceder
 * á súa denominación correspondente mediante os mecanismos de localización.
 *
 * @property clave Identificador textual único do elemento, independente do idioma.
 * @property nome Denominación localizada do elemento, utilizada para obter
 * o texto que se presentará ao usuario.
 */
interface ElementoL10n {
    val clave: String
    val nome: L10nSingular
}