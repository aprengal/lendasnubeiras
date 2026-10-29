package org.aprengal.lendasnubeiras.data.api.resposta

/**
 * Clase base selada para representar calquera resposta recibida da API.
 *
 * @property codigo O código de estado HTTP recibido na resposta.
 * @property exito Indica se a resposta foi exitosa (código HTTP entre 200 e 299).
 */
sealed class RespostaApi( val codigo: Int ) {
    val exito: Boolean = codigo in 200..299
}