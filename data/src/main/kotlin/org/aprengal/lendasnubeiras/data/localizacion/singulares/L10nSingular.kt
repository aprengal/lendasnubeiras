package org.aprengal.lendasnubeiras.data.localizacion.singulares

import org.aprengal.lendasnubeiras.data.localizacion.clases.Localizacion
import org.aprengal.lendasnubeiras.data.localizacion.clases.L10n

/**
 * Representa un texto localizado que non depende de cantidades nin variantes.
 *
 * Permite obter o texto correspondente ao idioma actual mediante [texto].
 */
sealed interface L10nSingular : L10n {

    /**
     * Obtén o texto correspondente ao idioma actual.
     *
     * @return Texto localizado.
     */
    fun texto(): String = Localizacion.l10n( this )

}