package org.aprengal.lendasnubeiras.ui.actividadesDixitais

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.aprengal.lendasnubeiras.data.actividades.datos.dixitais.Dificultade
import org.aprengal.lendasnubeiras.data.actividades.clases.ActividadeDados
import org.aprengal.lendasnubeiras.data.actividades.datos.base.Actividade
import org.aprengal.lendasnubeiras.data.localizacion.clases.Localizacion.collerTextoActividadeDixital
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.Icona.Companion.DebuxarIcona
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.CabeceiraActividadeDixital
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.Texto
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.EspazadorAlto

/**
 * Mostra a actividade dixital «Dados historiosos», na que a persoa lanza varios dados e crea
 * unha historia a partir das iconas obtidas.
 *
 * A dificultade determina o número de dados que se poden lanzar ([ActividadeDados.rangoDados])
 * e as iconas dispoñibles ([ActividadeDados.iconasDoNivel]).
 *
 * @param dificultade Nivel de dificultade da actividade dixital.
 * @param textos Mapa cos textos da interface ([Actividade.textos]).
 */
@Composable
fun DadosHistoriosos( dificultade: Dificultade, textos: Map<String, String> ) {

    val tarefaDatos = remember { ActividadeDados() }
    val rango = tarefaDatos.rangoDados( dificultade )

    var listaSeleccionados by rememberSaveable( dificultade ) { mutableStateOf<List<String>>( emptyList() ) }
    var cantidadeDados by rememberSaveable( dificultade ) { mutableIntStateOf( rango.first ) }
    var amosarLista by rememberSaveable { mutableStateOf( false ) }

    val texto = { clave: String -> collerTextoActividadeDixital( textos, clave ) }

    LazyVerticalGrid(
        columns = GridCells.Adaptive( minSize = 70.dp ),
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy( 10.dp ),
        verticalArrangement = Arrangement.spacedBy( 10.dp )
    ) {

        item( span = { GridItemSpan( maxLineSpan ) } ) { EspazadorAlto() }

        item( span = { GridItemSpan( maxLineSpan ) } ) { CabeceiraActividadeDixital( texto( "instrucions" ), dificultade ) }

        //Modificador de dados
        item( span = { GridItemSpan( maxLineSpan ) } ) {

            val descricionQuitar = Modifier.semantics { contentDescription = texto( "quitar_dado" ) }
            val descricionEngadir = Modifier.semantics { contentDescription = texto( "engadir_dado" ) }

            EspazadorAlto()
            Row( modifier = Modifier.fillMaxWidth(), Arrangement.SpaceEvenly, Alignment.CenterVertically ) {
                Button( onClick = { cantidadeDados-- }, modifier = descricionQuitar, enabled = cantidadeDados > rango.first ) { Text( "-" ) }
                Text( cantidadeDados.toString() )
                Button( onClick = { cantidadeDados++ }, modifier = descricionEngadir, enabled = cantidadeDados < rango.last ) { Text( "+" ) }
            }

        }

        //Botón de lanzamento de dados
        item( span = { GridItemSpan( maxLineSpan ) } ) {

            val modificador = Modifier.fillMaxWidth()
            val accion = { listaSeleccionados = tarefaDatos.lanzarDados( cantidadeDados, dificultade ) }

            Button( onClick = accion, modifier = modificador ) { Text( text = texto( "lanzar_dados" ) ) }

            if ( listaSeleccionados.isEmpty() ) {
                EspazadorAlto()
            }

        }

        //Resultados
        items( listaSeleccionados ) { icona -> CasillaIcono( icona ) }

        //Mostra de todas as alternativas posibles do nivel
        item( span = { GridItemSpan( maxLineSpan ) } ) {

            Column( modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally ) {

                val corDivisor = MaterialTheme.colorScheme.primary
                val textoBoton = if ( amosarLista ) texto( "agochar_lista" ) else texto( "ver_lista" )

                EspazadorAlto()
                HorizontalDivider( color = corDivisor )

                //Ponse 2 porque cada fila engade 10.dp de altura do grid
                EspazadorAlto( 2 )
                Button( onClick = { amosarLista = !amosarLista } ) { Text( text = textoBoton ) }

            }

        }

        if ( amosarLista ) {

            val iconasPorNivel = tarefaDatos.todasIconasNivel()

            iconasPorNivel.forEach { ( dificultade, mapaIconas ) ->

                // 1. Cabeceira
                item( span = { GridItemSpan( maxLineSpan ) } ) {
                    Texto( dificultade.nome, Modifier.padding( vertical = 10.dp ), MaterialTheme.typography.titleMedium )
                }

                // 2. Os iconos correspondentes a este nivel
                items( mapaIconas.values.toList() ) { icona -> ListarTodasIconas( icona ) }

            }

            item( span = { GridItemSpan( maxLineSpan ) } ) { EspazadorAlto() }

        }

    }

}

/**
 * Mostra unha icona nunha casiña cadrada cun fondo de cor secundaria.
 *
 * @param icona Clave da icona que se quere debuxar.
 * @param modifier Modificador que se aplica á casiña.
 */
@Composable
private fun CasillaIcono( icona: String, modifier: Modifier = Modifier ) {

    val corExterna = MaterialTheme.colorScheme.secondary.copy( alpha = 0.15f )
    val modificador = modifier.aspectRatio( 1f ).background( color = corExterna, shape = MaterialTheme.shapes.medium )

    BoxWithConstraints( contentAlignment = Alignment.Center, modifier = modificador ) {
        val dimension = with( LocalDensity.current ) { ( maxWidth * 0.625f ).toSp() }
        DebuxarIcona( icona, dimension = dimension )
    }

}


/**
 * Mostra unha icona nunha casiña cadrada co seu bordo.
 *
 * Utilízase para visualizar as iconas dispoñibles durante as probas da actividade dixital.
 *
 * @param textoIcona Clave da icona que se quere debuxar.
 */
@Composable
private fun ListarTodasIconas( textoIcona: String ) {

    val modificador = Modifier
        .aspectRatio( 1f )
        .border( 1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium )
        .padding( 8.dp )

    Column( modificador, Arrangement.Center, Alignment.CenterHorizontally ) {
        DebuxarIcona( textoIcona, dimension = 36.sp )
    }

}