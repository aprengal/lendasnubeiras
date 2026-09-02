package org.aprengal.lendasnubeiras.ui.reutilizables.clases

data class DatosListaOpcions<T>(
    val opcions: List<T>,
    val escollido: T,
    val escoller: ( T ) -> Unit
)