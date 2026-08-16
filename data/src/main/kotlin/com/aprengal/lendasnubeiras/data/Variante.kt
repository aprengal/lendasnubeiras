package com.aprengal.lendasnubeiras.data

enum class Variante {
    CLARO,
    ESCURO,
    PREDETERMINADO;

    val nome = name.lowercase()

    companion object {
        fun buscar( clave: String ): Variante {
            return entries.find { variante -> variante.nome == clave } ?: PREDETERMINADO
        }
    }

}