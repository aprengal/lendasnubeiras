package com.aprengal.lendasnubeiras.ui.reutilizables.clases

import androidx.compose.runtime.Composable
import com.aprengal.lendasnubeiras.data.localizacion.claves.L10nSingular
import com.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.iconas.Icona

data class DatosAlerta(
    val titulo: L10nSingular,
    val cancelado: () -> Unit,
    val descartado: () -> Unit = cancelado,
    val contido: ( @Composable () -> Unit )? = null,
    val confirmado: ( () -> Unit )? = null,
    val icona: Icona? = null
)
