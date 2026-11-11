package org.aprengal.lendasnubeiras.ui.reutilizables.elementos.compostos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.aprengal.lendasnubeiras.data.actividades.datos.base.Actividade
import org.aprengal.lendasnubeiras.data.actividades.datos.buscador.ActividadeBuscable
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nSingular
import org.aprengal.lendasnubeiras.ui.navegacion.Locais.LocalNavegacion
import org.aprengal.lendasnubeiras.ui.navegacion.Ruta
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.Icona
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.Icona.Companion.DebuxarIcona
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.Texto
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.EspazadorAlto
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

/**
 * Representa unha actividade mediante unha tarxeta interactiva.
 *
 * Mostra o título e o resumo da actividade, permitindo acceder á súa
 * información detallada ao seleccionar a tarxeta.
 *
 * Ao premer nela, engádese á navegación a ruta [Ruta.ActividadeDetalle],
 * identificando a actividade mediante [ActividadeBuscable.claveTitulo].
 *
 * A tarxeta ocupa todo o ancho dispoñible e presenta o título e o resumo
 * empregando os estilos tipográficos correspondentes.
 *
 * @param actividade Actividade cuxo título e resumo se van mostrar na tarxeta.
 */
@Composable
fun TarxetaActividade( actividade: ActividadeBuscable ) {

    val navegacion = LocalNavegacion.current
    val accion = { navegacion.engadir( Ruta.ActividadeDetalle( actividade.claveTitulo ) ) }

    Card( onClick = accion, Modifier.fillMaxWidth().padding( vertical = 4.dp ) ) {

        Column( Modifier.fillMaxWidth().padding( horizontal = 16.dp, vertical = 12.dp ) )  {
            Text( text = actividade.titulo, style = MaterialTheme.typography.titleLarge )
            EspazadorAlto()
            Text( text = actividade.resumo, style = MaterialTheme.typography.bodySmall )
        }

    }

    EspazadorAlto()

}