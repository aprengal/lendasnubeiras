package com.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldLabelPosition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import com.aprengal.lendasnubeiras.ui.reutilizables.clases.DatosCampoTexto
import com.aprengal.lendasnubeiras.ui.reutilizables.clases.Icona
import com.aprengal.lendasnubeiras.ui.reutilizables.clases.Icona.Companion.DebuxarIcona

@Composable
fun CampoTexto( datos: DatosCampoTexto ) {

    val modificador = if ( datos.amosarTexto ) datos.modifier else datos.modifier.semantics { contentDescription = datos.texto.texto() }
    val haiErro = !datos.erro?.texto().isNullOrEmpty()

    OutlinedTextField(
        state = datos.estado,
        modifier = modificador,
        enabled = datos.activado,
        label = if ( datos.amosarTexto ) { { Texto( datos.texto ) } } else null,
        labelPosition = TextFieldLabelPosition.Above(),
        lineLimits = datos.altura,
        keyboardOptions = datos.teclado,
        inputTransformation = datos.sanitizacion,
        trailingIcon = { datos.icona?.let { icona -> DebuxarIcona( icona.clave, icona.nome, 20.sp ) } },
        supportingText = if ( haiErro ) { { Texto( datos.erro ) } } else null
    )

}

@Composable
fun CampoBuscador( state: TextFieldState ) {

    val buscar = Icona.Buscar
    val limpar = Icona.Limpar
    var amosar by rememberSaveable { mutableStateOf( false ) }

    OutlinedTextField(
        state = state,
        modifier = Modifier.fillMaxWidth(),
        lineLimits = TextFieldLineLimits.SingleLine,
        keyboardOptions = KeyboardOptions( imeAction = ImeAction.Search ),
        inputTransformation = InputTransformation.maxLength( 100 ),
        onKeyboardAction = { amosar = true }, //Aquí igual habería que cambiar isto pola acción que actualizaría a pantalla?
        leadingIcon = { DebuxarIcona( buscar.clave, buscar.nome, 20.sp ) },
        placeholder = { Text( "Buscar" ) }, //Cambio por Texto_busca o valorar outro
        trailingIcon = {
            IconButton( onClick = { state.clearText() } ) {
                DebuxarIcona( limpar.clave, limpar.nome, 20.sp )
            }
        },
        shape = CircleShape
    )

    if ( amosar ) {
        Buscar( state.text.toString(), { amosar = false } )
    }

}

@Composable
fun Buscar( termo: String, accion: () -> Unit ) {

    val titulo = @Composable { Text( "Simulouse unha busca e atopouse..." ) }
    val contido = @Composable { Text( "Nada con $termo" ) }

    AlertDialog(
        title = titulo,
        text = contido,
        onDismissRequest = accion,
        confirmButton = {}
    )

}

//Igual se podería aplicar nun xogo de amosar ou agochar resposta
//Funciona o campo de contrasinal, pero igual é innecesario
/*@Composable
fun CampoContrasinal( datos: DatosCampoTexto ) {

    var oculta by rememberSaveable { mutableStateOf( true ) }
    val ocultarTexto = if ( oculta ) OutputTransformation { replace( 0, length, "•".repeat( length ) ) } else null

    val localFocusManager = LocalFocusManager.current
    val icona = if ( oculta ) Icona.OLLO_ABERTO else Icona.OLLO_PECHADO

    val debuxoIcona = @Composable {
        IconButton( { oculta = !oculta; localFocusManager.clearFocus() } ) { DebuxarIcona( icona.clave, icona.clave, 20.sp ) }
    }

    CampoTexto( datos, ocultarTexto, debuxoIcona )

}*/