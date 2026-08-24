package com.aprengal.lendasnubeiras.ui.reutilizables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
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
import com.aprengal.lendasnubeiras.data.localizacion.L10nPlural
import com.aprengal.lendasnubeiras.data.localizacion.L10nSingular
import com.aprengal.lendasnubeiras.ui.navegacion.Pantalla
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
fun AlertaDialogo( titulo: L10nSingular, contido: @Composable () -> Unit, rexeitar: () -> Unit,
    confirmacion: @Composable () -> Unit, cancelacion: @Composable () -> Unit
) {

    AlertDialog( title = { Texto( titulo ) }, text = { contido() }, dismissButton = cancelacion,
        confirmButton = confirmacion, onDismissRequest = rexeitar
    )

}

@Composable
fun <T> OpcionsDialogo(
    clave: String, opcions: List<T>, seleccionado: T,
    nomeUI: ( T ) -> String, traducirOpcions: Boolean,
    procesando: Boolean, seleccionar: ( T ) -> Unit
) {

    Column( Modifier.selectableGroup() ) {

        for ( opcion in opcions ) {

            val textoUI = nomeUI( opcion )
            val claveBuscable = if ( textoUI !in listOf( "si", "non" ) ) "${ clave }_${ textoUI }" else textoUI
            val texto = if ( traducirOpcions ) L10nSingular.buscar( claveBuscable).texto() else textoUI

            val modificador = Modifier
                .fillMaxWidth()
                .selectable(
                    selected = seleccionado == opcion, enabled = !procesando,
                    onClick = { seleccionar( opcion ) }, role = Role.RadioButton
                )
                .padding( vertical = 8.dp )

            Row( modifier = modificador, verticalAlignment = Alignment.CenterVertically ) {
                RadioButton( selected = seleccionado == opcion, enabled = !procesando, onClick = null )
                Spacer( Modifier.width( 12.dp ) )
                Text( texto )
            }

        }

    }

}

suspend fun amosarAviso( aviso: SnackbarHostState, claveMensaxe: L10nSingular, repetir: Boolean ): SnackbarResult {

    aviso.currentSnackbarData?.dismiss()

    val reintentar = if ( repetir ) L10nSingular.buscar( "reintentar_accion" ).texto() else null
    val duracion = if ( repetir ) SnackbarDuration.Long else SnackbarDuration.Short

    return aviso.showSnackbar( claveMensaxe.texto(), reintentar, repetir, duracion )

}

@Composable
fun Texto( elemento: L10nSingular, modifier: Modifier = Modifier, estilo: TextStyle = LocalTextStyle.current ) {
    Text( elemento.texto(), modifier, style = estilo )
}

@Composable
fun TextoPlural( elemento: L10nPlural, cantidade: Int, modifier: Modifier = Modifier, estilo: TextStyle = LocalTextStyle.current ) {
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
fun BotonTerciario( elemento: L10nSingular, accion: () -> Unit, modifier: Modifier = Modifier, habilitado: Boolean = true ) {

    val cores = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.tertiary,
        contentColor = MaterialTheme.colorScheme.onTertiary
    )

    Button( onClick = accion, enabled = habilitado, colors = cores, modifier = modifier ) {
        Texto( elemento )
    }

}

@Composable
fun BotonAuxiliar( elemento: L10nSingular, accion: () -> Unit, activado: Boolean ) {
    TextButton( enabled = activado, onClick = accion ) {
        Texto( elemento )
    }
}

@Composable
internal fun DebuxarIconaMenu( pantalla: Pantalla, dimension: TextUnit ) {

    val icona = when ( pantalla ) {
        Pantalla.Inicio -> Icona.INICIO
        is Pantalla.Buscar -> Icona.BUSCAR
        Pantalla.Idioma -> Icona.IDIOMA
        Pantalla.Axustes -> Icona.AXUSTES
        Pantalla.CrearActividade -> Icona.ENGADIR
        Pantalla.Actividades -> Icona.IDIOMA
        else -> error( "A pantalla ${ pantalla::class.simpleName } non ten icona asignada" )
    }

    DebuxarIcona( icona.codigo, icona.descricion.texto(), dimension = dimension )

}

private val fonteIconas = FontFamily( Font( R.font.ubuntu_iconas_nerd, FontWeight.Bold ) )

@Composable
fun DebuxarIcona( contido: String, descricion: String? = null, dimension: TextUnit ) {

    val cor = LocalContentColor.current
    val modificador = descricion?.let { Modifier.semantics { contentDescription = descricion } } ?: Modifier.clearAndSetSemantics { }

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

        for ( ( enlace, inicio ) in posicions ) {

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