package org.aprengal.lendasnubeiras.data.bd.clasesAxuda

import org.aprengal.lendasnubeiras.data.bd.clases.BD.Orde

data class SeleccionSQL(
    val columnas: Set<String>,
    val distinto: Boolean = false,
    val alias: String = "",
    val combinacions: List<CombinacionSQL> = emptyList(),
    val onde: Map<String, Condicion> = emptyMap(),
    val ordenar: Map<String, Orde> = emptyMap(),
    val limite: List<Int> = emptyList()
)