package org.aprengal.lendasnubeiras.data.api.resposta

/**
 * Clase base selada das respostas recibidas da API.
 *
 * Centraliza a información común a todas as respostas, permitindo
 * consultar o código devolto polo servidor e determinar se a operación
 * foi satisfactoria.
 *
 * @property codigo Código de resposta devolto pola API.
 * @property exito Indica se a operación foi satisfactoria, considerando
 *                 exitosos os códigos comprendidos entre 200 e 299.
 */
sealed class RespostaApi( val codigo: Int ) {
    val exito: Boolean = codigo in 200..299
}