package org.aprengal.lendasnubeiras.ui.reutilizables.elementos.compostos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.aprengal.lendasnubeiras.data.localizacion.clases.Localizacion.obterTexto
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.DatosElementoLista
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.DatosListaOpcions
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.Icona.Companion.DebuxarIcona
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.Texto
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
 * O texto mostrado para cada opción obtense mediante [obterTexto].
 *
 * Agrupa os elementos mediante [Modifier.selectableGroup] e emprega
 * [Role.RadioButton] para identificar semanticamente o tipo de selección.
 *
 * @param datos Datos que conteñen as opcións dispoñibles, a opción
 * actualmente seleccionada e a función encargada de actualizar a selección.
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
 * Mostra un elemento dunha lista mediante un compoñente [ListItem].
 *
 * Presenta o título e o contido asociado ao elemento, permitindo executar
 * unha acción ao premer sobre el.
 *
 * Pode incluír unha icona na parte esquerda, un contido adicional na parte
 * central e un contido complementario na parte dereita.
 *
 * A icona móstrase unicamente cando está definida e debúxase mediante
 * [DebuxarIcona].
 *
 * O contido principal e os elementos complementarios obtéñense a partir
 * dos datos proporcionados, permitindo adaptar a presentación ás
 * necesidades de cada elemento da lista.
 *
 * @param datos Datos do elemento que se vai mostrar, incluíndo o título,
 * o contido asociado, a acción que se executará ao seleccionalo
 * e os elementos visuais adicionais.
 */
@Composable
fun ElementoLista( datos: DatosElementoLista ) {

    ListItem( { Texto( datos.titulo ) }, Modifier.clickable( onClick = datos.accion ),
        supportingContent = { datos.contido() },
        leadingContent = { datos.icona?.let{ icona -> DebuxarIcona( icona.clave, icona.nome, 20.sp ) } },
        trailingContent = datos.contidoExtra
    )

}