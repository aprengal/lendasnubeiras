package com.aprengal.lendasnubeiras.ui


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.aprengal.lendasnubeiras.data.localizacion.L10nVariante
import com.aprengal.lendasnubeiras.ui.reutilizables.EspazadorAlto

// Colores que quedaron fuera del ColorScheme (comentados en tu archivo
// de colores). Se importan/declaran aquí para usarlos directamente.
// Sustituye este import por el real de tu proyecto cuando los
// descomentes en tu archivo de colores:

/**
 * Botón ghost — fondo CorGhost (verde), texto negro. Color fijo, igual
 * en ambos temas (no forma parte del ColorScheme, se aplica directo).
 * Úsalo para acciones de menor peso visual (omitir, opcional).
 */
/*@Composable
fun MirarAnimacions() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        SeccionAnimacion("AnimatedVisibility — fadeIn/fadeOut") { activo ->
            AnimatedVisibility(
                visible = activo,
                enter = fadeIn(tween(600)),
                exit = fadeOut(tween(600))
            ) { CasillaDemo("Fade") }
        }

        SeccionAnimacion("AnimatedVisibility — scaleIn") { activo ->
            AnimatedVisibility(
                visible = activo,
                enter = scaleIn(initialScale = 0.3f, animationSpec = tween(600)),
                exit = scaleOut(targetScale = 0.3f, animationSpec = tween(600))
            ) { CasillaDemo("Scale") }
        }

        SeccionAnimacion("AnimatedVisibility — slideInHorizontally") { activo ->
            AnimatedVisibility(
                visible = activo,
                enter = slideInHorizontally(animationSpec = tween(600)) { ancho -> ancho },
                exit = slideOutHorizontally(animationSpec = tween(600)) { ancho -> ancho }
            ) { CasillaDemo("Slide") }
        }

        SeccionAnimacion("AnimatedVisibility — expandVertically") { activo ->
            AnimatedVisibility(
                visible = activo,
                enter = expandVertically(animationSpec = tween(600)),
                exit = shrinkVertically(animationSpec = tween(600))
            ) { CasillaDemo("Expand") }
        }

        SeccionAnimacion("AnimatedVisibility — combinada") { activo ->
            AnimatedVisibility(
                visible = activo,
                enter = fadeIn(tween(600)) + scaleIn(initialScale = 0.5f, animationSpec = tween(600)),
                exit = fadeOut(tween(600)) + scaleOut(targetScale = 0.5f, animationSpec = tween(600))
            ) { CasillaDemo("Combo") }
        }

        SeccionAnimacion("animateFloatAsState — opacidad") { activo ->
            val alpha by animateFloatAsState(
                targetValue = if (activo) 1f else 0f,
                animationSpec = tween(800), label = "alpha"
            )
            CasillaDemo("Alpha", modifier = Modifier.graphicsLayer { this.alpha = alpha })
        }

        SeccionAnimacion("animateColorAsState") { activo ->
            val color by animateColorAsState(
                targetValue = if (activo) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.secondary,
                animationSpec = tween(800), label = "color"
            )
            CasillaDemo("Color", colorFondo = color)
        }

        SeccionAnimacion("animateDpAsState — spring (rebote)") { activo ->
            val tamano by animateDpAsState(
                targetValue = if (activo) 80.dp else 40.dp,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "tamano"
            )
            Box(
                modifier = Modifier
                    .size(tamano)
                    .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp))
            )
        }

        SeccionAnimacion("updateTransition — color + tamaño coordinados") { activo ->
            val transicion = updateTransition(activo, label = "coordinada")
            val tamano by transicion.animateDp(label = "tamano_t") { if (it) 80.dp else 40.dp }
            val color by transicion.animateColor(label = "color_t") {
                if (it) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            }
            Box(
                modifier = Modifier
                    .size(tamano)
                    .background(color, RoundedCornerShape(12.dp))
            )
        }

        SeccionAnimacion("Cascada con delay (5 iconos)") { activo ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(5) { index ->
                    AnimatedVisibility(
                        visible = activo,
                        enter = fadeIn(tween(400, delayMillis = index * 150)),
                        exit = fadeOut(tween(200))
                    ) { CasillaDemo("${index + 1}") }
                }
            }
        }

        ProbarSwitch()

    }

}

@Composable
fun ProbarSwitch() {
    var activado by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (activado) "Activado" else "Desactivado",
                style = MaterialTheme.typography.titleMedium
            )

            Switch(
                checked = activado,
                onCheckedChange = {
                    activado = it
                }
            )
        }

        // Algo que fuerce recomposición al cambiar
        AnimatedVisibility(visible = activado) {
            Card {
                Text(
                    text = "Contenido visible",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        EspazadorAlto( 2 )

    }
}

@Composable
private fun SeccionAnimacion(titulo: String, contenido: @Composable (activo: Boolean) -> Unit) {
    var activo by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(titulo, style = MaterialTheme.typography.labelLarge, color = Color.Gray)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = { activo = !activo }) {
                Text(if (activo) "◀ Volver" else "▶ Reproducir")
            }
            contenido(activo)
        }
    }
}

@Composable
private fun CasillaDemo(
    texto: String,
    modifier: Modifier = Modifier,
    colorFondo: Color = MaterialTheme.colorScheme.secondary
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(60.dp)
            .background(colorFondo, RoundedCornerShape(8.dp))
    ) {
        Text(texto, color = MaterialTheme.colorScheme.onSecondary)
    }
}*/

@Composable
fun ProbaActividade() {

    PantallaBase {

        for ( i in 0..10 ) {

            val acertos = 10 - i

            item {

                Text(
                    text = buildAnnotatedString {
                        withStyle( SpanStyle( fontWeight = FontWeight.Bold ) ) { append( acertos.toString() ) }
                        append( " - " )
                        append( L10nVariante.MENSAXE_FALLOS.texto( i ) )
                    },
                    style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding( bottom = 10.dp )
                )

            }

        }

    }

}

@Composable
fun PantallaBase( contido: LazyListScope.() -> Unit ) {

    LazyColumn( modifier = Modifier.fillMaxSize() ) {

        item {
            EspazadorAlto()
        }

        contido()

    }

}