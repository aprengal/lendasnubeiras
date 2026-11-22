package org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.maxLength
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import org.aprengal.lendasnubeiras.data.localizacion.clases.ElementoL10n
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nBase
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nSingular
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.DatosAlerta
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.DatosCampoTexto
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.DatosElementoLista
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.DatosListaOpcions
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.Icona
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.Icona.Companion.DebuxarIcona
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.compostos.AlertaDialogo
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.compostos.ElementoLista
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.compostos.ListaOpcions

/**
 * Campo de texto da aplicación que permite introducir e editar información,
 * xestionando o seu estado, validación e presentación de erros.
 *
 * @param datos Obxecto que contén a configuración e os datos necesarios para o campo de texto.
 */
@Composable
fun CampoTexto( datos: DatosCampoTexto ) {

    var enfocado by rememberSaveable { mutableStateOf( false ) }
    var perdeuFoco by rememberSaveable { mutableStateOf( false ) }
    val modificador = if ( datos.amosarEtiqueta ) datos.modifier else datos.modifier.semantics { contentDescription = datos.etiqueta.texto() }
    val haiErro = perdeuFoco && !datos.erro?.texto().isNullOrEmpty()

    OutlinedTextField(
        state = datos.estado,
        modifier = modificador.onFocusChanged { elemento ->
            if ( enfocado && !elemento.isFocused ) perdeuFoco = true
            enfocado = elemento.isFocused
        },
        enabled = datos.activado,
        label = if ( datos.amosarEtiqueta ) { { Texto( datos.etiqueta ) } } else null,
        labelPosition = TextFieldLabelPosition.Above(),
        lineLimits = datos.altura,
        keyboardOptions = datos.teclado,
        inputTransformation = datos.sanitizacion,
        //trailingIcon = { datos.icona.takeIf { enfocado }?.let { icona -> DebuxarIcona( icona.clave, icona.nome, 20.sp ) } },
        supportingText = if ( haiErro ) { { Texto( datos.erro ) } } else null
    )

}

/**
 * Campo de texto destinado á busca de contido, cunha icona identificativa
 * e unha opción para limpar o texto introducido.
 *
 * Permite iniciar a busca mediante a acción correspondente do teclado.
 *
 * @param estado Estado que almacena e xestiona o texto introducido.
 * @param buscar Acción que se executará ao iniciar a busca.
 */
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
        placeholder = { Texto( L10nBase.CampoBuscar, Modifier.clearAndSetSemantics {} ) },
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

        //O null representa a opción "Todas", que só se engade se hai texto para ela
        val elementos = if ( textoTodas != null ) listOf<T?>( null ) + opcions else opcions
        val texto = { opcion: T? -> opcion?.nome?.texto() ?: textoTodas?.texto().orEmpty() }

        val datosLista = DatosListaOpcions( elementos, temporal, { opcion -> temporal = opcion }, texto )
        val contidoAlerta = @Composable { ListaOpcions( datosLista ) }
        val datosAlerta = DatosAlerta( titulo = titulo, cancelado = pechar, confirmado = confirmado, contido = contidoAlerta )

        AlertaDialogo( datosAlerta )

    }

}