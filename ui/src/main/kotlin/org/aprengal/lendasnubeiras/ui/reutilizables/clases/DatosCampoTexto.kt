package org.aprengal.lendasnubeiras.ui.reutilizables.clases

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.ui.Modifier
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nSingular

data class DatosCampoTexto(
    val estado: TextFieldState,
    val texto: L10nSingular,
    val lonxitudeMax: Int,
    val modifier: Modifier = Modifier,
    val amosarTexto: Boolean = true,
    val activado: Boolean = true,
    val altura: TextFieldLineLimits = TextFieldLineLimits.SingleLine,
    val teclado: KeyboardOptions = KeyboardOptions.Default,
    val erro: L10nSingular? = null,
    val icona: Icona? = if ( erro != null ) Icona.Invalido else null,
) {
    val sanitizacion: InputTransformation = InputTransformation.maxLength( lonxitudeMax )
}