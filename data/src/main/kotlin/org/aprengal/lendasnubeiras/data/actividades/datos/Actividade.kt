package org.aprengal.lendasnubeiras.data.actividades.datos

import org.aprengal.lendasnubeiras.data.localizacion.clases.Localizacion.collerTextoActividade

data class Actividade(
    override val id: Long,
    override val claveTitulo: String,
    val idAutoria: Long,
    override val categoria: Categoria,
    override val destinatario: Destinatario,
    override val estado: Estado,
    val duracion: Int,
    val dataModificado: Long
) : DatosActividade {

    override val titulo: String
    val descricion: List<String>
    val textos: Map<String, String>

    init {

        val datos = collerTextoActividade( claveTitulo )
        titulo = datos.optString( "titulo" )

        descricion = buildList {
            val array = datos.optJSONArray( "descricion" )
            if ( array != null ) {
                for ( i in 0 until array.length() ) {
                    add( array.getString( i ) )
                }
            }
        }

        val textosJson = datos.optJSONObject( "textos" )
        textos = textosJson?.let { json -> json.keys().asSequence().associateWith { chave -> json.optString( chave ) } } ?: emptyMap()

    }

}