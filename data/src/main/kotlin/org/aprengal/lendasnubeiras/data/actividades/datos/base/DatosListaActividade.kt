package org.aprengal.lendasnubeiras.data.actividades.datos.base

/**
 * Representa unha lista de elementos de texto, ordenada ou desordenada,
 * asociada a unha actividade.
 *
 * Permite agrupar elementos baixo unha cabeceira opcional, facilitando
 * a presentación estruturada e accesible de información na ficha
 * dunha actividade.
 *
 * @property tipo Tipo de lista, que determina se os elementos se presentan
 * de forma ordenada ou desordenada.
 * @property cabeceira Texto que encabeza a lista. Pode ser unha cadea baleira
 * se non se precisa unha cabeceira.
 * @property elementos Lista de textos que constitúen os elementos da lista.
 */
data class DatosListaActividade(
    val tipo: TipoLista,
    val cabeceira: String,
    val elementos: List<String>
)