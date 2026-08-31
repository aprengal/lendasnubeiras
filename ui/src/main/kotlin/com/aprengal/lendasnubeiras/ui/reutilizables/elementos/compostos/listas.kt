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
import com.aprengal.lendasnubeiras.data.localizacion.claves.L10nSingular
import com.aprengal.lendasnubeiras.ui.reutilizables.clases.DatosElementoLista
import com.aprengal.lendasnubeiras.ui.reutilizables.clases.DatosListaOpcions
import com.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.Texto
import com.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.EspazadorAncho
import com.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.iconas.DebuxarIcona

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
                RadioButton( datos.escollido == opcion, null )
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
        leadingContent = { datos.icona?.let{ icona -> DebuxarIcona( icona.codigo, icona.descricion, 20.sp ) } },
        trailingContent = datos.contidoExtra
    )

}