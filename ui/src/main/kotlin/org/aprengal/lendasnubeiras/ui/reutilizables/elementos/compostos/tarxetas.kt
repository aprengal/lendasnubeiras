package org.aprengal.lendasnubeiras.ui.reutilizables.elementos.compostos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nSingular
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.Icona
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.Icona.Companion.DebuxarIcona
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.Texto
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.EspazadorAncho

@Composable
fun TarxetaPulsable( icona: Icona, accion: () -> Unit, texto: L10nSingular, dimension: TextUnit = 36.sp ) {

    val modificadorFila = Modifier.fillMaxWidth().padding( vertical = 20.dp, horizontal = 16.dp )

    //Se a descrición da icona é igual ao texto da tarxeta, suprímese a primeira para evitar ser lida dúas veces iguais en lectores de pantalla
    val descricion = if ( icona.nome.texto() == texto.texto() ) null else { icona.nome }

    Card( Modifier.fillMaxWidth().clickable( role = Role.Button, onClick = accion ) ) {

        Row( modificadorFila, Arrangement.Start, Alignment.CenterVertically ) {

            DebuxarIcona( icona.clave, descricion, dimension )
            EspazadorAncho( 2 )
            Texto( texto, estilo = MaterialTheme.typography.titleLarge )

        }

    }

}