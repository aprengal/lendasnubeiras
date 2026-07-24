package com.example.lendasnubeiras.ui.theme.partes

import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

/**
 * 2.3 Campos de texto — estados posibles do campo de formulario:
 * Activo, Correcto, Incorrecto, Desactivado, Enfocado.
 * O estado visual (borde/cor) reflíctese mediante o parámetro `estado`.
 */
/*enum class EstadoCampo { ACTIVO, CORRECTO, INCORRECTO, DESACTIVADO }

@Composable
fun CampoFormulario(
    etiqueta: String,
    valor: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    estado: EstadoCampo = EstadoCampo.ACTIVO
) {
    val corBorde = when (estado) {
        EstadoCampo.CORRECTO -> CampoCorrecto
        EstadoCampo.INCORRECTO -> CampoIncorrecto
        EstadoCampo.DESACTIVADO -> CampoDesactivadoTexto
        EstadoCampo.ACTIVO -> CampoBordeNormal
    }

    OutlinedTextField(
        value = valor,
        onValueChange = onValueChange,
        label = { Text(etiqueta) },
        enabled = estado != EstadoCampo.DESACTIVADO,
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CampoEnfocadoBorde,
            unfocusedBorderColor = corBorde,
            disabledBorderColor = CampoDesactivadoTexto,
            focusedContainerColor = CampoFondoNormal,
            unfocusedContainerColor = CampoFondoNormal,
            disabledContainerColor = CampoDesactivadoFondo,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            disabledTextColor = CampoDesactivadoTexto
        ),
        modifier = modifier
    )
}

/**
 * Campo buscador (2.3) — versión simplificada con icona de lupa.
 */
@Composable
fun CampoBuscador(
    valor: String,
    modifier: Modifier = Modifier,
    placeholder: String = "Inserta texto a buscar...",
    onValueChange: (String) -> Unit

) {
    OutlinedTextField(
        value = valor,
        modifier = modifier,
        placeholder = { Text( placeholder ) },
        onValueChange = onValueChange,
        singleLine = true,
        trailingIcon = {
            Icon( imageVector = Icons.Default.Search, contentDescription = "Buscar" )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CampoEnfocadoBorde,
            unfocusedBorderColor = CampoBordeNormal,
            focusedContainerColor = CampoFondoNormal,
            unfocusedContainerColor = CampoFondoNormal
        )
    )
}

/**
 * Texto protexido (contrasinal) con botón para amosar/ocultar,
 * tal e como aparece na sección "Texto protexido" da folla de estilos.
 */
@Composable
fun CampoTextoProtexido(
    valor: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var mostrarTexto by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = valor,
        onValueChange = onValueChange,
        singleLine = true,
        visualTransformation = if (mostrarTexto) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = { mostrarTexto = !mostrarTexto }) {
                Icon(
                    imageVector = if (mostrarTexto) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (mostrarTexto) "Ocultar texto" else "Amosar texto"
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CampoEnfocadoBorde,
            unfocusedBorderColor = CampoBordeNormal,
            focusedContainerColor = CampoFondoNormal,
            unfocusedContainerColor = CampoFondoNormal
        ),
        modifier = modifier
    )
}*/