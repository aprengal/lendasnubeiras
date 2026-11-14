package org.aprengal.lendasnubeiras.ui.pantallas.monitor.actividadesDixitais

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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import org.aprengal.lendasnubeiras.data.actividades.xogos.ActividadeDados
import org.aprengal.lendasnubeiras.data.actividades.datos.base.Actividade
import org.aprengal.lendasnubeiras.data.localizacion.clases.Localizacion.collerTextoXogo
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.Icona.Companion.DebuxarIcona
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.Texto
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.EspazadorAlto

/**
 * Mostra o xogo "Dados historiosos": lánzanse varios dados e a persoa ten que contar unha historia
 * coas iconas que saen.
 *
 * A dificultade determina cantos dados se poden lanzar ([ActividadeDados.rangoDados]) e de que
 * iconas se escolle ([ActividadeDados.iconasDoNivel]). Se cambia a dificultade, reiníciase o xogo.
 *
 * @param dificultade Nivel de dificultade do xogo.
 * @param textos Mapa cos textos da interface, o de [Actividade.textos].
 */
@Composable
fun XogoDados( dificultade: Dificultade, textos: Map<String, String> ) {

    val tarefaDatos = remember { ActividadeDados() }
    val rango = tarefaDatos.rangoDados( dificultade )

    var listaSeleccionados by rememberSaveable( dificultade ) { mutableStateOf<List<String>>( emptyList() ) }
    var cantidadeDados by rememberSaveable( dificultade ) { mutableIntStateOf( rango.first ) }
    var amosarLista by rememberSaveable { mutableStateOf( false ) }

    val texto = { clave: String -> collerTextoXogo( textos, clave ) }

    LazyVerticalGrid(
        columns = GridCells.Adaptive( minSize = 64.dp ),
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy( 8.dp ),
        verticalArrangement = Arrangement.spacedBy( 8.dp )
    ) {

        item( span = { GridItemSpan( maxLineSpan ) } ) { EspazadorAlto() }

        //Modificador de dados
        item( span = { GridItemSpan( maxLineSpan ) } ) {

            val descricionQuitar = Modifier.semantics { contentDescription = texto( "quitar_dado" ) }
            val descricionEngadir = Modifier.semantics { contentDescription = texto( "engadir_dado" ) }

            Row( modifier = Modifier.fillMaxWidth(), Arrangement.SpaceEvenly, Alignment.CenterVertically ) {
                Button( onClick = { cantidadeDados-- }, modifier = descricionQuitar, enabled = cantidadeDados > rango.first ) { Text( "-" ) }
                Text( cantidadeDados.toString() )
                Button( onClick = { cantidadeDados++ }, modifier = descricionEngadir, enabled = cantidadeDados < rango.last ) { Text( "+" ) }
            }

        }

        //Botón de lanzamento de dados
        item( span = { GridItemSpan( maxLineSpan ) } ) {

            val recheoInferior = if ( listaSeleccionados.isEmpty() ) 0.dp else 10.dp
            val modificador = Modifier.fillMaxWidth().padding( bottom = recheoInferior )
            val accion = { listaSeleccionados = tarefaDatos.lanzarDados( cantidadeDados, dificultade ) }

            Button( onClick = accion, modifier = modificador ) { Text( text = texto( "lanzar_dados" ) ) }

        }

        //Resultados
        items( listaSeleccionados ) { icona -> CasillaIcono( icona ) }

        //Mostra de todas as alternativas posibles do nivel
        item( span = { GridItemSpan( maxLineSpan ) } ) {

            Column( modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally ) {

                val recheoDivisor = Modifier.padding( vertical = 10.dp )
                val corDivisor = MaterialTheme.colorScheme.primary
                val textoBoton = if ( amosarLista ) texto( "agochar_lista" ) else texto( "ver_lista" )

                HorizontalDivider( modifier = recheoDivisor, color = corDivisor )
                Button( onClick = { amosarLista = !amosarLista } ) { Text( text = textoBoton ) }

            }

        }

        if ( amosarLista ) {

            val iconasPorNivel = tarefaDatos.iconasNivel()

            iconasPorNivel.forEach { ( dificultade, mapaIconas ) ->

                // 1. Cabeceira
                item( span = { GridItemSpan( maxLineSpan ) } ) {
                    Texto( dificultade.nome, Modifier.padding( top = 16.dp, bottom = 8.dp ), MaterialTheme.typography.titleMedium )
                }

                // 2. Os iconos correspondentes a este nivel
                items( mapaIconas.values.toList() ) { icona -> ListarTodasIconas( icona ) }

            }

        }

    }

}

@Composable
private fun CasillaIcono( icona: String, modifier: Modifier = Modifier ) {

    val corExterna = MaterialTheme.colorScheme.secondary.copy( alpha = 0.15f )
    val modificador = modifier.aspectRatio( 1f ).background( color = corExterna, shape = RoundedCornerShape( 8.dp ) )

    BoxWithConstraints( contentAlignment = Alignment.Center, modifier = modificador ) {
        val dimension = with( LocalDensity.current ) { ( maxWidth * 0.625f ).toSp() }
        DebuxarIcona( icona, dimension = dimension )
    }

}

@Composable
private fun ListarTodasIconas( textoIcona: String ) {

    val modificador = Modifier
        .aspectRatio( 1f )
        .border( 1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape( 8.dp ) )
        .padding( 8.dp )

    Column( modificador, Arrangement.Center, Alignment.CenterHorizontally ) {
        DebuxarIcona( textoIcona, dimension = 36.sp )
    }

}