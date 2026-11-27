package org.aprengal.lendasnubeiras.data.localizacion.cantidades

import org.aprengal.lendasnubeiras.data.localizacion.clases.Dominio
import org.aprengal.lendasnubeiras.data.localizacion.clases.L10n
import org.aprengal.lendasnubeiras.data.localizacion.clases.Localizacion

/**
 * Define textos localizados cuxa forma depende dunha cantidade numérica.
 *
 * Cada valor identifica unha clave de localización e o dominio ao que pertence.
 * Permite obter o texto adaptado á cantidade indicada mediante [texto].
 *
 * @property clave Identificador do texto no sistema de localización.
 * @property dominio Dominio de localización ao que pertence o texto.
 */
enum class L10nPlural( internal val clave: String, internal val dominio: Dominio ) : L10n {

    ResultadosBusca( "resultado_busca", Dominio.BASE ),
    ResultadosBuscaMais( "resultado_busca_mais", Dominio.BASE ),
    Duracion( "duracion_minutos", Dominio.BASE ),
    TempoRestante( "tempo_restante", Dominio.META_ACTIVIDADES );

    /**
     * Obtén o texto adaptado á cantidade indicada e ao idioma actual.
     *
     * @param num Cantidade utilizada para determinar a forma do texto.
     * @return Texto localizado correspondente á cantidade.
     */
    fun texto( num: Number ): String = Localizacion.l10nPlural( this, num )

}