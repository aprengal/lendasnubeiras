package org.aprengal.lendasnubeiras.data.actividades.datos.base

/**
 * Representa unha lista estruturada (ordenada ou non ordenada) asociada a unha actividade,
 * contendo unha cabeceira opcional e unha colección de elementos de texto.
 *
 * Emprégase para mostrar contidos informativos complexos ou opcións estruturadas
 * dentro da ficha dunha actividade de forma clara e accesible.
 *
 * @property tipo Indica se a lista é ordenada ou non ordenada (por exemplo, "ordenada" ou "desordenada").
 * @property cabeceira Texto opcional que encabeza a lista.
 * @property elementos Lista de elementos de texto que compoñen o contido da lista.
 */
data class DatosListaActividade(
    val tipo: String,
    val cabeceira: String?,
    val elementos: List<String>
)