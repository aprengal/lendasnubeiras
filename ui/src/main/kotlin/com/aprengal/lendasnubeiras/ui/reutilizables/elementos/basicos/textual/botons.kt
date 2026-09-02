package com.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual

import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.aprengal.lendasnubeiras.data.localizacion.claves.singular.L10nSingular

@Composable
fun BotonPrincipal( elemento: L10nSingular, accion: () -> Unit, modifier: Modifier = Modifier, habilitado: Boolean = true ) {
    Button( onClick = accion, modifier = modifier, enabled = habilitado ) {
        Texto( elemento )
    }
}

@Composable
fun BotonSecundario( elemento: L10nSingular, accion: () -> Unit, modifier: Modifier = Modifier, habilitado: Boolean = true ) {
    OutlinedButton( onClick = accion, enabled = habilitado, modifier = modifier ) {
        Texto( elemento )
    }
}

@Composable
fun BotonAuxiliar( elemento: L10nSingular, accion: () -> Unit, activado: Boolean = true ) {
    TextButton( enabled = activado, onClick = accion ) {
        Texto( elemento )
    }
}