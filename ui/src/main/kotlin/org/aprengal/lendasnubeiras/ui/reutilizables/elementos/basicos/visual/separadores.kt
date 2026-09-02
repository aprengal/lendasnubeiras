package org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EspazadorAlto( multiplicador: Int = 1 ) {
    Spacer( modifier = Modifier.height( 10.dp * multiplicador ) )
}

@Composable
fun EspazadorAncho( multiplicador: Int = 1 ) {
    Spacer( modifier = Modifier.width( 10.dp * multiplicador ) )
}