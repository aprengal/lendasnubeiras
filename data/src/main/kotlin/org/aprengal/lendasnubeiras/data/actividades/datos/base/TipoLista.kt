package org.aprengal.lendasnubeiras.data.actividades.datos.base

import org.aprengal.lendasnubeiras.data.localizacion.clases.Idioma
import org.aprengal.lendasnubeiras.data.localizacion.clases.Idioma.Nada


/**
 * Define os tipos de lista admitidos para representar coleccións
 * de elementos textuais asociados a unha actividade.
 *
 * Permite distinguir entre listas ordenadas, nas que os elementos
 * seguen unha secuencia determinada, e listas desordenadas, nas que
 * os elementos se presentan sen establecer unha orde específica.
 *
 * Cada tipo dispón dunha clave textual propia que permite identificalo
 * independentemente do nome da constante. Inclúe un método para
 * obter o tipo correspondente a partir dunha destas claves,
 * utilizando [DESORDENADA] como valor predeterminado cando
 * a clave non coincide con ningún dos tipos admitidos.
 *
 * @property clave Clave textual que identifica o tipo de lista.
 */
enum class TipoLista( val clave: String ) {

    /**
     * Representa unha lista na que os elementos seguen
     * unha orde ou secuencia determinada e se presentan
     * mediante numeración.
     */
    ORDENADA( "ordenada" ),

    /**
     * Representa unha lista na que os elementos non seguen
     * unha orde específica e se presentan mediante viñetas.
     */
    DESORDENADA( "desordenada" );

    companion object {

        /**
         * Obtén o tipo de lista correspondente á clave indicada.
         *
         * Se a clave non coincide con ningún dos valores admitidos,
         * devolve [DESORDENADA] como tipo predeterminado.
         *
         * @param clave Clave textual que identifica o tipo de lista.
         * @return O tipo de lista correspondente ou [DESORDENADA]
         * se a clave non é recoñecida.
         */
        fun escollerTipoLista( clave: String ): TipoLista {
            return entries.find { elemento -> elemento.clave == clave } ?: DESORDENADA
        }

    }

}
