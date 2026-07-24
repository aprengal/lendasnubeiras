package com.example.lendasnubeiras.ui.theme.partes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 2.4 Botóns — Botón principal
 * Activo: fondo gris claro + borde escuro.
 * Hover: fondo branco.
 * Pulsado: fondo branco + borde máis groso.
 * Desactivado: fondo gris + texto atenuado.
 */
/*@Composable
fun BotonPrincipal(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()

    val fondo = when {
        !enabled -> BotonPrincipalDesactivadoFondo
        isPressed || isHovered -> BotonPrincipalFondoHover
        else -> BotonPrincipalFondo
    }
    val bordeAncho = if (isPressed) 2.dp else 1.dp

    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(bordeAncho, BotonPrincipalBorde),
        colors = ButtonDefaults.buttonColors(
            containerColor = fondo,
            contentColor = if (enabled) MaterialTheme.colorScheme.onSurface else BotonPrincipalDesactivadoTexto,
            disabledContainerColor = BotonPrincipalDesactivadoFondo,
            disabledContentColor = BotonPrincipalDesactivadoTexto
        ),
        modifier = modifier
    ) {
        Text(text = texto)
    }
}

/**
 * 2.4 Botóns — Botón secundario
 * Fondo escuro (FondoPrincipal), texto/contido en cor de logo.
 * Hover/Pulsado: fondo lixeiramente máis claro.
 */
@Composable
fun BotonSecundario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()

    val fondo = when {
        !enabled -> BotonSecundarioDesactivadoFondo
        isPressed || isHovered -> BotonSecundarioFondoHover
        else -> BotonSecundarioFondo
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = fondo,
            contentColor = BotonSecundarioTexto,
            disabledContainerColor = BotonSecundarioDesactivadoFondo,
            disabledContentColor = BotonSecundarioTexto.copy(alpha = 0.6f)
        ),
        modifier = modifier
    ) {
        Text(text = texto)
    }
}

/**
 * 2.4 Botóns — Botón ghost
 * Circular, fondo verde claro, texto negro.
 */
@Composable
fun BotonGhost(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()

    val fondo = when {
        !enabled -> BotonGhostDesactivadoFondo
        isPressed || isHovered -> BotonGhostFondoHover
        else -> BotonGhostFondo
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = fondo,
            contentColor = BotonGhostTexto,
            disabledContainerColor = BotonGhostDesactivadoFondo,
            disabledContentColor = BotonGhostTexto.copy(alpha = 0.5f)
        ),
        modifier = modifier
    ) {
        Text(text = texto)
    }
}
*/