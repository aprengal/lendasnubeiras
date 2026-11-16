package org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual

import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nSingular


/**
 * Botón principal da aplicación, destinado a executar a acción máis relevante
 * dentro dunha pantalla ou contexto.
 *
 * @param elemento Elemento de localización que contén o texto do botón.
 * @param accion Acción que se executará ao premer o botón.
 * @param modifier Modificador que permite personalizar a disposición e o aspecto do botón.
 * @param habilitado Indica se o botón está habilitado para recibir interaccións.
 */
@Composable
fun BotonPrincipal( elemento: L10nSingular, accion: () -> Unit, modifier: Modifier = Modifier, habilitado: Boolean = true ) {
    Button( onClick = accion, modifier = modifier, enabled = habilitado ) {
        Texto( elemento )
    }
}

/**
 * Botón secundario da aplicación, destinado a executar accións alternativas
 * ou complementarias respecto da acción principal.
 *
 * @param elemento Elemento de localización que contén o texto do botón.
 * @param accion Acción que se executará ao premer o botón.
 * @param modifier Modificador que permite personalizar a disposición e o aspecto do botón.
 * @param habilitado Indica se o botón está habilitado para recibir interaccións.
 */
@Composable
fun BotonSecundario( elemento: L10nSingular, accion: () -> Unit, modifier: Modifier = Modifier, habilitado: Boolean = true ) {
    OutlinedButton( onClick = accion, enabled = habilitado, modifier = modifier ) {
        Texto( elemento )
    }
}

/**
 * Botón auxiliar da aplicación, destinado a executar accións complementarias
 * ou de menor relevancia visual.
 *
 * @param elemento Elemento de localización que contén o texto do botón.
 * @param accion Acción que se executará ao premer o botón.
 * @param activado Indica se o botón está habilitado para recibir interaccións.
 */
@Composable
fun BotonAuxiliar( elemento: L10nSingular, accion: () -> Unit, activado: Boolean = true ) {
    TextButton( enabled = activado, onClick = accion ) {
        Texto( elemento )
    }
}
