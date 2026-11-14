package org.aprengal.lendasnubeiras.ui.reutilizables.clases

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.ui.Modifier
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nSingular
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.CampoTexto

/**
 * Agrupa a configuración e o estado dun campo de entrada de texto.
 *
 * Utilízase en [CampoTexto] para definir o contido, as restricións de entrada,
 * a presentación e o comportamento do teclado.
 *
 * @property estado Estado que controla o texto introducido no campo.
 * @property etiqueta Recurso de localización co texto que identifica o campo. Móstrase sobre o campo
 * se [amosarEtiqueta] é true; se non, úsase como descrición para os lectores de pantalla.
 * @property lonxitudeMax Número máximo de caracteres permitidos.
 * @property modifier Modificador de Compose aplicado ao campo.
 * @property amosarEtiqueta Indica se [etiqueta] se amosa de forma visible sobre o campo. Se é false (por exemplo,
 * nun buscador), o campo non ten etiqueta visible, pero os lectores de pantalla len igualmente [etiqueta].
 * @property activado Indica se o campo está habilitado para recibir interaccións.
 * @property altura Límites de liñas de texto permitidos no campo.
 * @property teclado Configuración do teclado virtual que se amosará ao editar o campo.
 * @property erro Recurso de localización coa mensaxe de erro, se existe. Só se amosa despois de que
 * o campo perda o foco por primeira vez.
 * @property sanitizacion Transformación de entrada calculada a partir de [lonxitudeMax],
 * que limita o texto á lonxitude máxima.
 */
data class DatosCampoTexto(
    val estado: TextFieldState,
    val etiqueta: L10nSingular,
    val lonxitudeMax: Int,
    val modifier: Modifier = Modifier,
    val amosarEtiqueta: Boolean = true,
    val activado: Boolean = true,
    val altura: TextFieldLineLimits = TextFieldLineLimits.SingleLine,
    val teclado: KeyboardOptions = KeyboardOptions.Default,
    val erro: L10nSingular? = null
) {
    val sanitizacion: InputTransformation = InputTransformation.maxLength( lonxitudeMax )
}