package org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Engade un espazamento vertical mediante un elemento baleiro.
 *
 * @param multiplicador Factor polo que se multiplica a separación base de 10 dp.
 * Por defecto, utiliza un multiplicador de 1.
 */
@Composable
fun EspazadorAlto( multiplicador: Int = 1 ) {
    Spacer( modifier = Modifier.height( 10.dp * multiplicador ) )
}

/**
 * Engade un espazamento horizontal mediante un elemento baleiro.
 *
 * @param multiplicador Factor polo que se multiplica a separación base de 10 dp.
 * Por defecto, utiliza un multiplicador de 1.
 */
@Composable
fun EspazadorAncho( multiplicador: Int = 1 ) {
    Spacer( modifier = Modifier.width( 10.dp * multiplicador ) )
}