package org.aprengal.lendasnubeiras.ui.reutilizables.elementos.compostos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.aprengal.lendasnubeiras.data.localizacion.clases.Localizacion.obterTexto
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nBase
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.DatosElementoLista
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.DatosListaOpcions
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.Icona.Companion.DebuxarIcona
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.Texto
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.EspazadorAlto
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.EspazadorAncho

/**
 * Mostra unha lista de opcións seleccionables mediante botóns de selección.
 *
 * Presenta as opcións contidas en [DatosListaOpcions] nunha lista vertical
 * desprazable, permitindo seleccionar unha delas.
 *
 * Cada opción móstrase acompañada dun botón de selección que indica se
 * está seleccionada. Ao premer sobre unha opción, actualízase a selección
 * mediante a función [DatosListaOpcions.escoller].
 *
 * O texto de cada opción obtense mediante [DatosListaOpcions.texto], que por defecto
 * usa [obterTexto]. Isto permite amosar opcións que non son elementos localizados,
 * como a opción "Todas", representada por `null`.
 *
 * Agrupa os elementos mediante [Modifier.selectableGroup] e emprega
 * [Role.RadioButton] para identificar semanticamente o tipo de selección.
 *
 * @param datos Datos que conteñen as opcións dispoñibles, a opción
 * actualmente seleccionada, a función encargada de actualizar a selección
 * e a función que obtén o texto de cada opción.
 */
@Composable
fun <T> ListaOpcions( datos: DatosListaOpcions<T> ) {

    LazyColumn( Modifier.selectableGroup() ) {

        items( datos.opcions ) { opcion ->

            val texto = datos.texto( opcion )
            val seleccionado = datos.escollido == opcion

            val modificador = Modifier
                .fillMaxWidth()
                .selectable( seleccionado, onClick = { datos.escoller( opcion ) }, role = Role.RadioButton )
                .padding( vertical = 8.dp )

            Row( modifier = modificador, verticalAlignment = Alignment.CenterVertically ) {
                RadioButton( seleccionado, null )
                EspazadorAncho()
                Text( texto )
            }

        }

    }

}

/**
 * Amosa un elemento dunha lista mediante un compoñente [ListItem].
 *
 * Presenta o título e o contido asociado ao elemento.
 *
 * Se [DatosElementoLista.accion] está definida, o elemento é pulsable e identifícase
 * como un botón. En caso contrario, só amosa información.
 *
 * Pode incluír unha icona na parte esquerda, un contido adicional na parte
 * central e un contido complementario na parte dereita.
 *
 * A icona móstrase unicamente cando está definida e debúxase mediante
 * [DebuxarIcona].
 *
 * @param datos Datos do elemento que se vai mostrar, incluíndo o título,
 * o contido asociado, a acción opcional que se executará ao seleccionalo
 * e os elementos visuais adicionais.
 */
@Composable
fun ElementoLista( datos: DatosElementoLista ) {

    val modificador = datos.accion?.let { accion -> Modifier.clickable( role = Role.Button, onClick = accion ) } ?: Modifier

    ListItem( { Texto( datos.titulo, estilo = MaterialTheme.typography.titleLarge ) }, modificador,
        supportingContent = { datos.contido() },
        leadingContent = { datos.icona?.let{ icona -> DebuxarIcona( icona.clave, icona.nome, 20.sp ) } },
        trailingContent = datos.contidoExtra
    )

}


/**
 * Mostra unha lista vertical de elementos de texto, permitindo escoller
 * entre unha lista ordenada, con numeración, ou unha lista non ordenada,
 * con viñetas.
 *
 * A lista preséntase cun espazado uniforme entre os seus elementos e
 * utiliza o estilo tipográfico `bodyLarge` de Material Design.
 *
 * Desde o punto de vista da accesibilidade, agrupa os elementos da lista
 * nunha única descrición semántica, identificando o tipo de lista mediante
 * a súa correspondente descrición localizada.
 *
 * @param elementos Lista de elementos de tipo [String] que se van mostrar.
 * @param ordenada Indica o tipo de lista que se vai mostrar:
 * `true` para unha lista ordenada con numeración e
 * `false` para unha lista non ordenada con viñetas.
 */

@Composable
fun ListaTexto( elementos: List<String>, ordenada: Boolean ) {

    val descricionTipoLista = if ( ordenada ) L10nBase.ListaOrdenada else L10nBase.ListaNonOrdenada
    val modificadorColumna = Modifier.semantics( mergeDescendants = true ) { contentDescription = descricionTipoLista.texto() }

    Column( modificadorColumna, Arrangement.spacedBy( 6.dp ) ) {

        elementos.forEachIndexed { indice, elemento ->

            val textoOrde = if ( ordenada ) "${indice + 1}." else "•"
            val estilo = MaterialTheme.typography.bodyLarge
            val pesoFonte = FontWeight.Bold
            val modificadorOrde = Modifier.clearAndSetSemantics {}

            Column( Modifier.fillMaxWidth() ) {

                Row( verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy( 8.dp ) ) {
                    Text( textoOrde, modificadorOrde, fontWeight = pesoFonte, style = estilo )
                    Text( text = elemento, style = MaterialTheme.typography.bodyLarge )
                }

                EspazadorAlto()

            }

        }

    }

}