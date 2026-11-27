package org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import org.aprengal.lendasnubeiras.data.actividades.datos.dixitais.Dificultade
import org.aprengal.lendasnubeiras.data.bd.operacions.Actividades.collerActividade
import org.aprengal.lendasnubeiras.data.localizacion.cantidades.L10nPlural
import org.aprengal.lendasnubeiras.data.localizacion.cantidades.L10nVariante
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nMetaActividades
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nSingular
import org.aprengal.lendasnubeiras.ui.navegacion.Locais.LocalRuta
import org.aprengal.lendasnubeiras.ui.navegacion.Locais.LocalTitulo
import org.aprengal.lendasnubeiras.ui.navegacion.Ruta
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.EspazadorAlto

/**
 * Mostra un texto localizado a partir dun elemento de localización singular.
 *
 * @param elemento Elemento de localización que contén o texto que se desexa mostrar.
 * @param modifier Modificador que permite personalizar a disposición e o aspecto do texto.
 * @param estilo Estilo tipográfico aplicado ao texto. Por defecto, utiliza o estilo local actual.
 */
@Composable
fun Texto( elemento: L10nSingular, modifier: Modifier = Modifier, estilo: TextStyle = LocalTextStyle.current ) {
    Text( elemento.texto(), modifier, style = estilo )
}

/**
 * Mostra un texto localizado en función da cantidade indicada, utilizando
 * as formas plurais correspondentes.
 *
 * @param elemento Elemento de localización que contén as formas plurais do texto.
 * @param cantidade Cantidade utilizada para determinar a forma plural que se mostrará.
 * @param modifier Modificador que permite personalizar a disposición e o aspecto do texto.
 * @param estilo Estilo tipográfico aplicado ao texto. Por defecto, utiliza o estilo local actual.
 */
@Composable
fun TextoPlural( elemento: L10nPlural, cantidade: Number, modifier: Modifier = Modifier, estilo: TextStyle = LocalTextStyle.current ) {
    Text( elemento.texto( cantidade ), modifier, style = estilo )
}

/**
 * Mostra un texto localizado en función da cantidade indicada, utilizando
 * a variante correspondente definida no elemento de localización.
 *
 * @param elemento Elemento de localización que contén as variantes do texto.
 * @param cantidade Cantidade utilizada para determinar a variante que se mostrará.
 * @param modifier Modificador que permite personalizar a disposición e o aspecto do texto.
 * @param estilo Estilo tipográfico aplicado ao texto. Por defecto, utiliza o estilo local actual.
 */
@Composable
fun TextoVariante( elemento: L10nVariante, cantidade: Int, modifier: Modifier = Modifier, estilo: TextStyle = LocalTextStyle.current ) {
    Text( elemento.texto( cantidade ), modifier, style = estilo )
}

/**
 * Mostra o título correspondente á [Ruta] actual obtido desde [LocalRuta] ou desde
 * [LocalTitulo] no resto de pantallas.
 *
 * @throws IllegalArgumentException Se a ruta actual non ten un título dispoñible.
 */
@Composable
fun AmosarTitulo() {

    val ruta = LocalRuta.current

    val titulo = when ( ruta ) {
        is Ruta.ActividadeDetalle -> collerActividade( ruta.clave )?.titulo
        is Ruta.ActividadeDixital -> collerActividade( ruta.clave )?.titulo
        else -> LocalTitulo.current!!.texto()
    }

    requireNotNull( titulo ) { "A ruta ${ ruta::class.simpleName } non ten título asignado" }

    Text( titulo, style = MaterialTheme.typography.headlineLarge )

}

/**
 * Mostra o título da pantalla actual centrado horizontalmente dentro dunha columna
 * que ocupa todo o ancho dispoñible.
 */
@Composable
fun AmosarTituloCentrado() {

    Column( Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally ) {
        AmosarTitulo()
    }

}

/**
 * Mostra un texto localizado con un ou varios treitos interactivos destacados e
 * subliñados que executan a acción asociada en [enlaces].
 *
 * @param T Tipo de elemento de localización singular empregado para o texto e as ligazóns.
 * @param elemento Elemento de localización que contén o texto completo.
 * @param enlaces Mapa entre cada segmento clicable e o callback executado ao premer nel.
 * @throws IllegalArgumentException Se [enlaces] está baleiro ou algún enlace non aparece no texto localizado.
 */
@Composable
fun <T: L10nSingular> TextoEnlazado( elemento: T, enlaces: Map<T, () -> Unit> ) {

    require( enlaces.isNotEmpty() ) { "TextoEnlazado require polo menos un enlace" }

    val texto = elemento.texto()
    val posicions = enlaces.keys
        .map { enlace -> enlace to texto.indexOf( enlace.texto() ) }
        .sortedBy { enlaceConPosicion -> enlaceConPosicion.second }

    require( posicions.all { enlaceConPosicion -> enlaceConPosicion.second >= 0 } ) {
        "TextoEnlazado: hai enlaces que non aparecen no texto"
    }

    val estiloEnlace = SpanStyle( color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline )

    val saida = buildAnnotatedString {

        var posicion = 0

        posicions.forEach { ( enlace, inicio ) ->

            val enlaceDestacado = LinkAnnotation.Clickable( tag = enlace.texto(), linkInteractionListener = { enlaces[ enlace ]?.invoke() } )
            append( texto.substring( posicion, inicio ) )
            withLink( enlaceDestacado ) { withStyle( estiloEnlace ) { append( enlace.texto() ) } }

            posicion = inicio + enlace.texto().length

        }

        if ( posicion < texto.length ) {
            append( texto.substring( posicion ) )
        }

    }

    Text( saida, Modifier.fillMaxWidth(), textAlign = TextAlign.Center )

}

/**
 * Mostra a cabeceira dunha actividade dixital coas súas instrucións e a dificultade escollida.
 *
 * @param instrucions Instrucións da actividade dixital.
 * @param dificultade Dificultade escollida.
 */
@Composable
fun CabeceiraActividadeDixital( instrucions: String, dificultade: Dificultade ) {

    val textoDificultade = L10nMetaActividades.DificultadeActual.texto().format( dificultade.nome.texto() )

    Column( Modifier.fillMaxWidth().padding( horizontal = 8.dp ) ) {
        Text( instrucions, style = MaterialTheme.typography.bodyLarge )
        EspazadorAlto( 2 )
        Text( textoDificultade, Modifier.align( Alignment.CenterHorizontally ), style = MaterialTheme.typography.labelLarge )
        EspazadorAlto( 2 )
    }

}