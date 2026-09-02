package org.aprengal.lendasnubeiras.ui.reutilizables.clases

import org.aprengal.lendasnubeiras.data.localizacion.claves.singular.L10nOpcions

enum class TipoOpcion { ALERTA, INTERRUPTOR }

data class DatosOpcion<T>(
    val clave: String,
    val titulo: L10nOpcions,
    val gardadoFallido: L10nOpcions,
    val tipo: TipoOpcion,
    val icona: Icona,
    val valorInicial: T,
    val accion: suspend ( T ) -> Boolean,
    val tituloAlerta: L10nOpcions? = null,
    val opcions: List<T>? = null,
    val descricionExtra: L10nOpcions? = null,
)