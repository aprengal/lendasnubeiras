package org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import org.aprengal.lendasnubeiras.data.bd.operacions.Actividades.collerActividade
import org.aprengal.lendasnubeiras.data.localizacion.claves.L10nPlural
import org.aprengal.lendasnubeiras.data.localizacion.claves.singular.L10nSingular
import org.aprengal.lendasnubeiras.ui.navegacion.Locais.LocalRuta
import org.aprengal.lendasnubeiras.ui.navegacion.Locais.LocalTitulo
import org.aprengal.lendasnubeiras.ui.navegacion.Ruta

@Composable
fun Texto( elemento: L10nSingular, modifier: Modifier = Modifier, estilo: TextStyle = LocalTextStyle.current ) {
    Text( elemento.texto(), modifier, style = estilo )
}

//Por se hai un texto que apareza de golpe (mensaxes de erro en formularios)
@Composable
fun TextoAnunciable( elemento: L10nSingular, modifier: Modifier = Modifier ) {
    Text( text = elemento.texto(), modifier = modifier.semantics { liveRegion = LiveRegionMode.Polite } )
}

@Composable
fun TextoPlural( elemento: L10nPlural, cantidade: Number, modifier: Modifier = Modifier, estilo: TextStyle = LocalTextStyle.current ) {
    Text( elemento.texto( cantidade ), modifier, style = estilo )
}

@Composable
fun AmosarTitulo() {

    val ruta = LocalRuta.current

    val titulo = when ( ruta ) {
        is Ruta.ActividadeDetalle -> collerActividade( ruta.id )?.titulo
        else -> LocalTitulo.current!!.texto()
    }

    requireNotNull( titulo ) { "A ruta ${ ruta::class.simpleName } non ten título asignado" }

    Text( titulo, style = MaterialTheme.typography.headlineLarge )

}

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