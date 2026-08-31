package com.aprengal.lendasnubeiras.ui.reutilizables.clases

data class DatosListaOpcions<T>( val clave: String, val opcions: List<T>, val escollido: T,
    val obterNome: ( T)  -> String, val localizar: Boolean, val escoller: ( T ) -> Unit
)