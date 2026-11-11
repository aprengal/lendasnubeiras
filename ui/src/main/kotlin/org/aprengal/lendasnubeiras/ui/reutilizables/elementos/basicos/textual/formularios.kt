package org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldLabelPosition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.aprengal.lendasnubeiras.data.localizacion.clases.ElementoL10n
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nSingular
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.DatosAlerta
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.DatosCampoTexto
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.DatosElementoLista
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.Icona
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.Icona.Companion.DebuxarIcona
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.EspazadorAncho
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.compostos.AlertaDialogo
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.compostos.ElementoLista

@Composable
fun CampoTexto( datos: DatosCampoTexto ) {

    var enfocado by rememberSaveable { mutableStateOf( false ) }
    var perdeuFoco by rememberSaveable { mutableStateOf( false ) }
    val modificador = if ( datos.amosarTexto ) datos.modifier else datos.modifier.semantics { contentDescription = datos.texto.texto() }
    val haiErro = perdeuFoco && !datos.erro?.texto().isNullOrEmpty()

    OutlinedTextField(
        state = datos.estado,
        modifier = modificador.onFocusChanged { elemento ->
            if ( enfocado && !elemento.isFocused ) perdeuFoco = true
            enfocado = elemento.isFocused
        },
        enabled = datos.activado,
        label = if ( datos.amosarTexto ) { { Texto( datos.texto ) } } else null,
        labelPosition = TextFieldLabelPosition.Above(),
        lineLimits = datos.altura,
        keyboardOptions = datos.teclado,
        inputTransformation = datos.sanitizacion,
        //trailingIcon = { datos.icona.takeIf { enfocado }?.let { icona -> DebuxarIcona( icona.clave, icona.nome, 20.sp ) } },
        supportingText = if ( haiErro ) { { Texto( datos.erro ) } } else null
    )

}

@Composable
fun CampoBuscador( estado: TextFieldState, buscar: () -> Unit ) {

    val iconaBuscar = Icona.Buscar
    val limpar = Icona.Limpar

    val reiniciar = @Composable {
        if ( estado.text.isNotEmpty() ) {
            IconButton( onClick = { estado.clearText() } ) {
                DebuxarIcona( limpar.clave, limpar.nome, 20.sp )
            }
        }
    }

    OutlinedTextField(
        state = estado,
        modifier = Modifier.fillMaxWidth(),
        lineLimits = TextFieldLineLimits.SingleLine,
        keyboardOptions = KeyboardOptions( imeAction = ImeAction.Search ),
        inputTransformation = InputTransformation.maxLength( 100 ),
        onKeyboardAction = { buscar() },
        leadingIcon = { DebuxarIcona( iconaBuscar.clave, iconaBuscar.nome, 20.sp ) },
        placeholder = { Text( "Buscar" ) },
        trailingIcon = reiniciar,
        shape = CircleShape
    )

}

@Composable
internal fun <T : ElementoL10n> SelectorFiltro( titulo: L10nSingular, opcions: List<T>, escollido: T?, textoTodas: L10nSingular?, escoller: ( T? ) -> Unit ) {

    var aberto by rememberSaveable { mutableStateOf( false ) }
    var temporal by rememberSaveable( escollido ) { mutableStateOf( escollido ) }

    val valor = escollido?.nome?.texto() ?: textoTodas?.texto().orEmpty()
    val accion = {
        temporal = escollido
        aberto = true
    }

    ElementoLista( DatosElementoLista( titulo = titulo, accion = accion, contido = { Text( valor ) } ) )

    if ( aberto ) {

        val pechar = { aberto = false }
        val confirmado = {
            escoller( temporal )
            pechar()
        }

        val contidoAlerta = @Composable { ListaFiltro( opcions, temporal, textoTodas ) { opcion -> temporal = opcion } }
        val datosAlerta = DatosAlerta( titulo = titulo, cancelado = pechar, confirmado = confirmado, contido = contidoAlerta )

        AlertaDialogo( datosAlerta )

    }

}

@Composable
private fun <T : ElementoL10n> ListaFiltro( opcions: List<T>, escollido: T?, textoTodas: L10nSingular?, escoller: ( T? ) -> Unit ) {

    val elementos = if ( textoTodas != null ) listOf<T?>( null ) + opcions else opcions

    LazyColumn( Modifier.selectableGroup() ) {

        items( elementos ) { opcion ->

            val texto = opcion?.nome?.texto() ?: textoTodas?.texto().orEmpty()
            val seleccionado = escollido == opcion

            val modificador = Modifier.fillMaxWidth().selectable( seleccionado, onClick = { escoller( opcion ) }, role = Role.RadioButton ).padding( vertical = 8.dp )

            Row( modifier = modificador, verticalAlignment = Alignment.CenterVertically ) {
                RadioButton( seleccionado, null )
                EspazadorAncho()
                Text( texto )
            }

        }

    }

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