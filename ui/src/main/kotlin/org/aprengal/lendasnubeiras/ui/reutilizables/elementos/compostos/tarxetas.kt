package org.aprengal.lendasnubeiras.ui.reutilizables.elementos.compostos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nSingular
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.Icona
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.Icona.Companion.DebuxarIcona
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.Texto
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.EspazadorAncho

/**
 * Crea unha tarxeta pulsable que presenta unha icona xunto cun texto descritivo.
 *
 * A tarxeta ocupa todo o ancho dispoñible e organiza os seus elementos horizontalmente,
 * aliñándoos verticalmente no centro. Ao premela, execútase a acción indicada.
 *
 * Para mellorar a accesibilidade, evita que os lectores de pantalla lean dúas veces
 * a mesma información: se o nome da icona coincide co texto da tarxeta,
 * omítese a descrición da icona.
 *
 * @param icona Icona que se mostra na tarxeta, incluíndo a súa clave gráfica e o seu nome
 * descritivo para os lectores de pantalla.
 * @param accion Acción que se executa cando a persoa usuaria preme a tarxeta.
 * @param texto Texto principal que se mostra xunto coa icona.
 * @param dimension Tamaño da icona expresado como [TextUnit]. Por defecto, establécese en 36.sp.
 */
@Composable
fun TarxetaPulsable( icona: Icona, accion: () -> Unit, texto: L10nSingular, dimension: TextUnit = 36.sp ) {

    val modificadorFila = Modifier.fillMaxWidth().padding( vertical = 20.dp, horizontal = 16.dp )
    val descricion = if ( icona.nome.texto() == texto.texto() ) null else { icona.nome }

    Card( accion, Modifier.fillMaxWidth() ) {

        Row( modificadorFila, Arrangement.Start, Alignment.CenterVertically ) {

            DebuxarIcona( icona.clave, descricion, dimension )
            EspazadorAncho( 2 )
            Texto( texto, estilo = MaterialTheme.typography.titleLarge )

        }

    }

}