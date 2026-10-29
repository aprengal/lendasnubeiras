package org.aprengal.lendasnubeiras.data.actividades.datos

import org.aprengal.lendasnubeiras.data.localizacion.clases.Idioma
import org.aprengal.lendasnubeiras.data.localizacion.clases.Localizacion.collerTextoActividade

data class Actividade(
    override val id: Long,
    override val claveTitulo: String,
    val idAutoria: Long,
    override val categoria: Categoria,
    override val destinatario: Destinatario,
    override val idioma: Idioma,
    override val estado: Estado,
    val duracion: Int,
    val dataModificado: Long
) : DatosActividade {

    override val titulo: String
    val descricion: String
    val textos: Map<String, String>

    init {

        val datos = collerTextoActividade( claveTitulo )
        val textosJson = datos.optJSONObject( "textos" )

        titulo = datos.optString( "titulo" )
        descricion = datos.optString( "descricion" )
        textos = textosJson?.let { json -> json.keys().asSequence().associateWith { chave -> json.optString( chave ) } } ?: emptyMap()

    }

}