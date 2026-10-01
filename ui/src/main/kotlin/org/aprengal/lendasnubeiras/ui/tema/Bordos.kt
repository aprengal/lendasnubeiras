package org.aprengal.lendasnubeiras.ui.tema

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp


/**
 * Bordos da aplicación.
 *
 * Define os radios de curvatura dos cantos dos compoñentes, segundo os
 * tamaños de [Shapes] de Material 3.
 */
internal object Bordos {

    /**
     * Formas dos compoñentes, de máis pequena a máis grande.
     *
     * - `extraSmall`: 4 dp.
     * - `small`: 6 dp.
     * - `medium`: 8 dp (empregado nos botóns principal e secundario).
     * - `large`: 12 dp.
     * - `extraLarge`: 50 dp.
     */
    private val bordos = Shapes(
        extraSmall = RoundedCornerShape( 4.dp ),
        small = RoundedCornerShape( 6.dp ),
        medium = RoundedCornerShape( 8.dp ), // botóns colPrincipal/secundario
        large = RoundedCornerShape( 12.dp ),
        extraLarge = RoundedCornerShape( 50.dp )
    )

    /**
     * Devolve as formas da aplicación.
     *
     * @return O conxunto de [Shapes] que se pasa ao tema da aplicación.
     */
    internal fun collerBordos(): Shapes {
        return bordos
    }

}