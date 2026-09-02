package com.aprengal.lendasnubeiras.ui.reutilizables.clases

import androidx.compose.runtime.Composable
import com.aprengal.lendasnubeiras.data.localizacion.claves.singular.L10nSingular

data class DatosElementoLista(
    val accion: () -> Unit,
    val titulo: L10nSingular,
    val icona: Icona? = null,
    val contido: @Composable () -> Unit,
    val contidoExtra: ( @Composable () -> Unit )? = null
)
