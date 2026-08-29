package com.aprengal.lendasnubeiras.ui.reutilizables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aprengal.lendasnubeiras.ui.R
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.aprengal.lendasnubeiras.data.configuracion.db.DB.collerActividade
import com.aprengal.lendasnubeiras.data.localizacion.L10nPlural
import com.aprengal.lendasnubeiras.data.localizacion.L10nSingular
import com.aprengal.lendasnubeiras.ui.navegacion.Ruta
import com.aprengal.lendasnubeiras.ui.navegacion.DatosNavegacion.datosRutas
import com.aprengal.lendasnubeiras.ui.tema.escollerVarianteImaxe

@Composable
fun EspazadorAlto(multiplicador: Int = 1 ) {
    Spacer( modifier = Modifier.height( 10.dp * multiplicador ) )
}

@Composable
fun EspazadorAncho( multiplicador: Int = 1 ) {
    Spacer( modifier = Modifier.width( 10.dp * multiplicador ) )
}

@Composable
fun Logo( medida: Dp = 30.dp ) {

    val logo = escollerVarianteImaxe( isSystemInDarkTheme(), R.drawable.logo_claro, R.drawable.logo_escuro )

    Image(
        painter = painterResource( logo ),
        modifier = Modifier.size( medida ),
        contentDescription = L10nSingular.NOME_APP.texto()
    )

}

@Composable
fun AlertaDialogo( titulo: L10nSingular, cancelado: () -> Unit, descartado: () -> Unit = cancelado,
    contido: @Composable ( () -> Unit )? = null, confirmado: ( () -> Unit )? = null, icona: Icona? = null,
) {

    AlertDialog(
        icon = { icona?.let { DebuxarIcona( icona.codigo, icona.descricion, dimension = 50.sp ) } },
        title = { Text( titulo.texto(), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center ) },
        text = { contido?.let { contido() } },
        onDismissRequest = descartado,
        dismissButton = { BotonAuxiliar( L10nSingular.CANCELAR, cancelado ) },
        confirmButton = { confirmado?.let { BotonPrincipal( L10nSingular.ACEPTAR, confirmado ) } }
    )

}

@Composable
fun <T> ListaOpcions( datos: DatosListaOpcions<T> ) {

    LazyColumn( Modifier.selectableGroup() ) {

        items( datos.opcions ) { opcion ->

            val textoUI = datos.obterNome( opcion )
            val texto = if ( datos.localizar ) L10nSingular.buscar( "${ datos.clave }_${ textoUI }" ).texto() else textoUI

            val modificador = Modifier
                .fillMaxWidth()
                .selectable( selected = datos.escollido == opcion, onClick = { datos.escoller( opcion ) }, role = Role.RadioButton )
                .padding( vertical = 8.dp )

            Row( modifier = modificador, verticalAlignment = Alignment.CenterVertically ) {
                RadioButton( selected = datos.escollido == opcion, onClick = null )
                EspazadorAncho()
                Text( texto )
            }

        }

    }

}

suspend fun amosarAviso( aviso: SnackbarHostState, claveMensaxe: L10nSingular, repetir: Boolean ): SnackbarResult {

    aviso.currentSnackbarData?.dismiss()

    val reintentar = if ( repetir ) L10nSingular.REINTENTAR.texto() else null
    val duracion = if ( repetir ) SnackbarDuration.Long else SnackbarDuration.Short

    return aviso.showSnackbar( claveMensaxe.texto(), reintentar, repetir, duracion )

}

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
    TextButton(enabled = activado, onClick = accion) {
        Texto(elemento)
    }
}

private val fonteIconas = FontFamily( Font( R.font.ubuntu_iconas_nerd, FontWeight.Bold ) )

@Composable
fun DebuxarIcona( contido: String, descricion: L10nSingular? = null, dimension: TextUnit ) {

    val cor = LocalContentColor.current
    val modificador = descricion?.let { Modifier.semantics { contentDescription = descricion.texto() } } ?: Modifier.clearAndSetSemantics { }

    Text( text = contido, modifier = modificador, fontSize = dimension, fontFamily = fonteIconas, color = cor, lineHeight = 1.sp )

}

@Composable
fun TextoEnlazado( elemento: L10nSingular, enlaces: Map<L10nSingular, () -> Unit> ) {

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

    Text( saida, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center )

}

@Composable
fun ElementoLista( accion: () -> Unit, titulo: L10nSingular, icona: Icona?,
    contido: @Composable ( () -> Unit ), contidoExtra: @Composable ( () -> Unit )? = null
) {

    ListItem(
        modifier = Modifier.clickable( onClick = accion ),
        headlineContent = { Texto( titulo ) },
        supportingContent = { contido() },
        leadingContent = { icona?.let{ DebuxarIcona( icona.codigo, icona.descricion, 20.sp ) } },
        trailingContent = contidoExtra
    )

}

@Composable
fun AmosarTitulo() {

    val ruta = LocalRuta.current
    val datos = datosRutas[ ruta::class ]!!

    val titulo = when ( ruta ) {
        is Ruta.ActividadeDetalle -> collerActividade( ruta.id )?.titulo
        else -> datos.titulo!!.texto()
    }

    requireNotNull( titulo ) { "A ruta ${ ruta::class.simpleName } non ten título asignado" }

    Text( titulo, style = MaterialTheme.typography.headlineLarge )

}

@Composable
internal fun DebuxarIconaMenu( ruta: Ruta, dimension: TextUnit ) {

    val claseRuta = ruta::class
    val icona = datosRutas[ claseRuta ]!!.icona
    requireNotNull( icona ) { "A ruta ${ ruta::class.simpleName } non ten icona asignada" }

    DebuxarIcona( icona.codigo, icona.descricion, dimension = dimension )

}