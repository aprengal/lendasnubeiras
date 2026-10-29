package org.aprengal.lendasnubeiras.data.actividades.datos

import org.aprengal.lendasnubeiras.data.localizacion.clases.Idioma
import org.aprengal.lendasnubeiras.data.localizacion.clases.Localizacion.collerTextoActividade

data class ActividadeBuscable(
    override val id: Long,
    override val claveTitulo: String,
    override val categoria: Categoria,
    override val destinatario: Destinatario,
    override val idioma: Idioma,
    override val estado: Estado
) : DatosActividade {

    override val titulo: String
    val resumo: String

    init {

        val datos = collerTextoActividade( claveTitulo )

        titulo = datos.optString( "titulo" )
        resumo = datos.optString( "descricion" )

    }

}