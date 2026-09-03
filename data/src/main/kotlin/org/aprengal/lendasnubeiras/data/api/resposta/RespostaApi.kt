package org.aprengal.lendasnubeiras.data.api.resposta

sealed class RespostaApi( val codigo: Int ) {
    val exito: Boolean = codigo in 200..299
}