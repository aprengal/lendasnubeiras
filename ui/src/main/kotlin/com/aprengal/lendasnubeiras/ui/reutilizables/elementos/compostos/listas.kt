package com.aprengal.lendasnubeiras.ui.reutilizables.elementos.compostos

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
import com.aprengal.lendasnubeiras.data.localizacion.Localizacion.obterTexto
import com.aprengal.lendasnubeiras.ui.reutilizables.clases.DatosElementoLista
import com.aprengal.lendasnubeiras.ui.reutilizables.clases.DatosListaOpcions
import com.aprengal.lendasnubeiras.ui.reutilizables.clases.Icona.Companion.DebuxarIcona
import com.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.Texto
import com.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.EspazadorAncho

@Composable
fun <T> ListaOpcions( datos: DatosListaOpcions<T> ) {

    LazyColumn( Modifier.selectableGroup() ) {

        items( datos.opcions ) { opcion ->

            val texto = obterTexto( opcion )
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

@Composable
fun ElementoLista( datos: DatosElementoLista ) {

    ListItem( { Texto( datos.titulo ) }, Modifier.clickable( onClick = datos.accion ),
        supportingContent = { datos.contido() },
        leadingContent = { datos.icona?.let{ icona -> DebuxarIcona( icona.clave, icona.nome, 20.sp ) } },
        trailingContent = datos.contidoExtra
    )

}