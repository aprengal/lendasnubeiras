package org.aprengal.lendasnubeiras.data.localizacion.cantidades

import org.aprengal.lendasnubeiras.data.localizacion.clases.Dominio
import org.aprengal.lendasnubeiras.data.localizacion.clases.L10n
import org.aprengal.lendasnubeiras.data.localizacion.clases.Localizacion

enum class L10nPlural( internal val clave: String, internal val dominio: Dominio ) : L10n {

    MensaxesNovas( "mensaxes_novas", Dominio.BASE ),
    ResultadosBusca( "resultado_busca", Dominio.BASE ),
    ResultadosBuscaMais( "resultado_busca_mais", Dominio.BASE ),
    Duracion( "duracion_minutos", Dominio.BASE ),
    TempoRestante( "tempo_restante", Dominio.META_ACTIVIDADES );

    fun texto( num: Number ): String = Localizacion.l10nPlural( this, num )

}