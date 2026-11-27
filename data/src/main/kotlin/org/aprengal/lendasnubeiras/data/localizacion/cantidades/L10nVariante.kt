package org.aprengal.lendasnubeiras.data.localizacion.cantidades

import org.aprengal.lendasnubeiras.data.localizacion.clases.Dominio
import org.aprengal.lendasnubeiras.data.localizacion.clases.L10n
import org.aprengal.lendasnubeiras.data.localizacion.clases.Localizacion

/**
 * Define textos localizados que dispoñen de diferentes variantes numéricas.
 *
 * Cada valor identifica unha clave de localización e o dominio ao que pertence.
 * Permite obter a variante correspondente ao número indicado mediante [texto].
 *
 * @property clave Identificador do texto no sistema de localización.
 * @property dominio Dominio de localización ao que pertence o texto.
 */
enum class L10nVariante( internal val clave: String, internal val dominio: Dominio ) : L10n {

    MensaxesCambiantes( "mensaxes_cambiantes", Dominio.BASE );

    /**
     * Obtén a variante do texto correspondente ao número indicado
     * e ao idioma actual.
     *
     * @param num Número utilizado para seleccionar a variante.
     * @return Texto localizado correspondente á variante.
     */
    fun texto( num: Int ): String = Localizacion.l10nVariante( this, num )

}