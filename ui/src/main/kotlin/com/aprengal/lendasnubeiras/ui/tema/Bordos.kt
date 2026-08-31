package com.aprengal.lendasnubeiras.ui.tema

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp


internal object Bordos {

    private val bordos = Shapes(
        extraSmall = RoundedCornerShape( 4.dp ),
        small = RoundedCornerShape( 6.dp ),
        medium = RoundedCornerShape( 8.dp ),   // botóns colPrincipal/secundario
        large = RoundedCornerShape( 12.dp ),
        extraLarge = RoundedCornerShape( 50.dp )
    )

    internal fun collerBordos(): Shapes {
        return bordos
    }

}