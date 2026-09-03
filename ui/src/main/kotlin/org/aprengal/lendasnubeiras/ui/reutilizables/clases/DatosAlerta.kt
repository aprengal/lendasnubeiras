package org.aprengal.lendasnubeiras.ui.reutilizables.clases

import androidx.compose.runtime.Composable
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nSingular

data class DatosAlerta(
    val titulo: L10nSingular,
    val cancelado: () -> Unit,
    val descartado: () -> Unit = cancelado,
    val contido: ( @Composable () -> Unit )? = null,
    val confirmado: ( () -> Unit )? = null,
    val icona: Icona? = null
)
