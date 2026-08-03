package com.aprengal.lendasnubeiras.tema

import android.util.Patterns.EMAIL_ADDRESS
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.aprengal.lendasnubeiras.tema.Iconas.PantallaPruebaIconas
import com.aprengal.lendasnubeiras.tema.Iconas.lanzarDados
import com.aprengal.lendasnubeiras.tema.Iconas.listarIconasActividades
import com.aprengal.lendasnubeiras.R
import com.aprengal.lendasnubeiras.localizacion.Localizacion
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import com.aprengal.lendasnubeiras.accesibilidade.haiLector
import com.aprengal.lendasnubeiras.conexion.ConexionApi
import com.aprengal.lendasnubeiras.localizacion.Idioma
import com.aprengal.lendasnubeiras.navegacion.corrutina
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.json.JSONObject

// Colores que quedaron fuera del ColorScheme (comentados en tu archivo
// de colores). Se importan/declaran aquí para usarlos directamente.
// Sustituye este import por el real de tu proyecto cuando los
// descomentes en tu archivo de colores:

private val CorGhost = Color( 0xFFB6F29A )

/**
 * Botón principal — fondo = primary, texto = onPrimary.
 * Úsalo para LA acción más importante de la pantalla (una por pantalla,
 * idealmente).
 */
@Composable
fun BotonPrincipal(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = habilitado,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(texto)
    }
}

/**
 * Botón secundario — borde y texto = outline (no "secondary": ese color
 * se reserva para logo/menús y no siempre tiene contraste suficiente
 * aquí, según lo comprobado).
 * Úsalo para acciones alternativas (cancelar, volver, otra opción válida).
 */
@Composable
fun BotonSecundario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        enabled = habilitado,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.outline
        ),
        border = ButtonDefaults.outlinedButtonBorder(enabled = habilitado).copy(
            brush = SolidColor(MaterialTheme.colorScheme.outline)
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(texto)
    }
}

/**
 * Botón ghost — fondo CorGhost (verde), texto negro. Color fijo, igual
 * en ambos temas (no forma parte del ColorScheme, se aplica directo).
 * Úsalo para acciones de menor peso visual (omitir, opcional).
 */
@Composable
fun BotonGhost(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = habilitado,
        colors = ButtonDefaults.buttonColors(
            containerColor = CorGhost,
            contentColor = Color.Black
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(texto)
    }
}

@Composable
fun Logo( tamano: Dp = 30.dp ) {

    Image(
        painter = painterResource( R.drawable.logo ),
        modifier = Modifier.size( tamano ),
        contentDescription = stringResource( R.string.nome_app )
    )

}

@Composable
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
                enter = slideInHorizontally(animationSpec = tween(600)) { it },
                exit = slideOutHorizontally(animationSpec = tween(600)) { it }
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

        Espazador( 2 )

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
}

@Composable
fun ProbaTraducions() {

    var idiomaActual by remember { mutableStateOf(Localizacion.idiomaActual ) }
    val esGalego = idiomaActual.value.codigo == "gl"
    var mostrarDialogo by rememberSaveable { mutableStateOf(false) }
    val contexto = LocalContext.current

    val nuevoIdioma = if (esGalego) Idioma.CASTELAN else Idioma.GALEGO

    PantallaBase {

        item {


            Column {

                Text(
                    text = Localizacion.l10n("carla", "test")
                )

                Text(
                    text = Localizacion.l10n("natasha", "test"),
                    style = Fontes.cabeceira1
                )

                Text(
                    text = Localizacion.l10nPlural("mensajes_nuevos", 1, "test")
                )

                Text(
                    text = Localizacion.l10nPlural("mensajes_nuevos", 5, "test")
                )

                Text(
                    text = Localizacion.l10nPlural("mensajes_nuevos", 0, "test")
                )

                Text(
                    text = Localizacion.l10nPlural("mensajes_nuevos", 100, "test")
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = buildAnnotatedString {
                        append("Idioma actual: ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(if (esGalego) "Galego" else "Castellano")
                        }
                    },
                    style = MaterialTheme.typography.titleMedium
                )

                Button(
                    onClick = {
                        if ( haiLector( contexto ) ) {
                            mostrarDialogo = true
                        } else {

                            corrutina {
                                Localizacion.gardarIdioma( nuevoIdioma )
                                idiomaActual.value = nuevoIdioma
                            }

                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    //Para que compose actualice o contido deste botón
                    key(idiomaActual) {
                        Text(
                            text = Localizacion.l10n("cambio_idioma", "test")
                        )
                    }
                }
            }

        }

    }

}

@Composable
fun ProbaActividade() {

    PantallaBase {

        for ( i in 0..10 ) {

            val acertos = 10 - i

            item {

                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(fontWeight = FontWeight.Bold)
                        ) {
                            append( "$acertos - " )
                        }

                        append( Localizacion.l10nPlural("mensaxe_fallos", i , "test" ) )

                    },
                    style = Fontes.paragrafoNormal, modifier = Modifier.padding( bottom = 10.dp )
                )

            }

        }

    }

}

@Composable
fun XogoDados() {

    var resultadoDados by rememberSaveable { mutableStateOf<List<String>>(emptyList()) }
    var cantidadeDados by rememberSaveable { mutableIntStateOf( 3 ) }

    val iconos = remember { listarIconasActividades().toList() }

    var amosarLista by rememberSaveable { mutableStateOf(false) }

    LazyVerticalGrid(
        columns = GridCells.Adaptive( minSize = 64.dp ),
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy( 8.dp ),
        verticalArrangement = Arrangement.spacedBy( 8.dp )
    ) {

        item(
            span = { GridItemSpan(maxLineSpan) }
        ) {
            Espazador()
        }

        item(
            span = { GridItemSpan(maxLineSpan) }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { cantidadeDados-- },
                    enabled = cantidadeDados > 3
                ) {
                    Text("-")
                }

                Text( cantidadeDados.toString() )

                Button(
                    onClick = { cantidadeDados++ },
                    enabled = cantidadeDados < 10
                ) {
                    Text("+")
                }
            }
        }

        item(
            span = { GridItemSpan(maxLineSpan) }
        ) {

            val paddingInferior = if ( resultadoDados.isEmpty() ) 0.dp else 10.dp

            Button(
                onClick = {
                    resultadoDados = lanzarDados( cantidadeDados )
                },
                modifier = Modifier.fillMaxWidth().padding( bottom = paddingInferior )
            ) {

                Text( text = "Lanzar dados" )

            }

        }

        items( resultadoDados ) { icono ->
            Iconas.CasillaIcono( icono )
        }

        // Separador / botón desplegable
        item(
            span = { GridItemSpan( maxLineSpan ) }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = MaterialTheme.colorScheme.primary
                )

                Button(
                    onClick = { amosarLista = !amosarLista }
                ) {

                    Text(
                        text = if ( amosarLista )
                            "Tirar lista completa"
                        else
                            "Ver lista completa"
                    )

                }

            }
        }

        if ( amosarLista ) {
            items( iconos ) { icono ->
                PantallaPruebaIconas( icono )
            }
        }

    }

}

@Composable
fun Espazador( multiplicador: Int = 1 ) {
    Spacer( modifier = Modifier.height( 10.dp * multiplicador ) )
}

@Composable
fun PantallaBase( contido: LazyListScope.() -> Unit ) {

    LazyColumn( modifier = Modifier.fillMaxSize() ) {

        item {
            Espazador()
        }

        contido()

    }

}

@Composable
private fun PantallaAcceso(
    tituloBoton: String,
    amosarCheckbox: Boolean,
    botonPulsado: ( correo: String ) -> String
) {

    var correo by rememberSaveable { mutableStateOf( "" ) }
    var aceptaTerminos by rememberSaveable { mutableStateOf( false ) }
    var texto by rememberSaveable { mutableStateOf( "" ) }

    val correoValido = EMAIL_ADDRESS.matcher( correo ).matches()
    val podeContinuar = correoValido && ( !amosarCheckbox || aceptaTerminos )

    Column(
        modifier = Modifier.fillMaxSize()
            .windowInsetsPadding( WindowInsets.safeDrawing )
            .verticalScroll( rememberScrollState() )
            .padding( horizontal = 5.dp, vertical = 10.dp ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Logo( 90.dp )

        Espazador( 2 )

        if ( texto != "" ) {
            Text( texto )
            Espazador()
        }

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text( "Correo electrónico" ) },
            singleLine = true,
            keyboardOptions = KeyboardOptions( keyboardType = KeyboardType.Email ),
            modifier = Modifier.fillMaxWidth()
        )

        Espazador()

        if ( amosarCheckbox ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = aceptaTerminos,
                    onCheckedChange = { aceptaTerminos = it }
                )
                Text( "Acepto os termos e condicións" )
            }

            Espazador()

        }

        Button(
            onClick = { texto = botonPulsado( correo ) },
            enabled = podeContinuar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text( tituloBoton )
        }

    }

}

@Composable
fun PantallaRexistro() {

    PantallaAcceso(
        tituloBoton = "Rexistrarse",
        amosarCheckbox = true,
        botonPulsado = { correo ->
            when {
                correo.contains( "erd248@", ignoreCase = true ) -> "O furacán C7w6so acaba de pasar por aquí"
                correo.contains( "n" ) -> "N de ninguén"
                else -> "As malas linguas din que usar o teu correo trae un bo regalo"
            }
        }
    )

}

@Composable
fun PantallaIniciarSesion() {

    PantallaAcceso(
        tituloBoton = "Iniciar sesión",
        amosarCheckbox = false,
        botonPulsado = { correo ->
            "Comprobando correo..." // aquí irá a lóxica real de login
        }
    )

}

@Composable
fun PantallaApertura() {

    var peido: String by rememberSaveable { mutableStateOf( "Esperando resposta..." ) }

    LaunchedEffect( Unit ) {

        val resultado = ConexionApi.get( "peido.php", emptyMap() )

        //Isto vale para indicar que a operación foi exitosa
        //if ( resultado.optBoolean( "exito" ) )

        peido = resultado.optString( "mensaxe", "Escachou o servidor" ).toString()

    }

    PantallaAcceso(
        tituloBoton = peido,
        amosarCheckbox = false,
        botonPulsado = { correo ->

            if ( peido.startsWith( "Esperando" ) ) "Meh" else "Chi"

        }

    )

}