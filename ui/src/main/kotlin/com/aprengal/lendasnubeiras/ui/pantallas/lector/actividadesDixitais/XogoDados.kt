package com.aprengal.lendasnubeiras.ui.pantallas.lector.actividadesDixitais

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aprengal.lendasnubeiras.data.actividades.dixitais.ActividadeDados
import com.aprengal.lendasnubeiras.ui.reutilizables.DebuxarIcona
import com.aprengal.lendasnubeiras.ui.reutilizables.EspazadorAlto

@Composable
fun XogoDados() {

    val tarefaDatos = remember { ActividadeDados() }
    var listaSeleccionados by rememberSaveable { mutableStateOf<List<String>>( emptyList() ) }
    var cantidadeDados by rememberSaveable { mutableIntStateOf( 3 ) }

    val todasIconas = remember { tarefaDatos.iconasActividades.toList() }
    var amosarLista by rememberSaveable { mutableStateOf( false ) }

    LazyVerticalGrid(
        columns = GridCells.Adaptive( minSize = 64.dp ),
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy( 8.dp ),
        verticalArrangement = Arrangement.spacedBy( 8.dp )
    ) {

        item( span = { GridItemSpan( maxLineSpan ) } ) { EspazadorAlto() }

        //Modificador de dados
        item( span = { GridItemSpan( maxLineSpan ) } ) {

            Row( modifier = Modifier.fillMaxWidth(), Arrangement.SpaceEvenly, Alignment.CenterVertically ) {
                Button( onClick = { cantidadeDados-- }, enabled = cantidadeDados > 3 ) { Text( "-" ) }//E qué significa para un lector de pantalla?
                Text( cantidadeDados.toString() )
                Button( onClick = { cantidadeDados++ }, enabled = cantidadeDados < 10 ) { Text( "+" ) }//E qué significa para un lector de pantalla?
            }

        }

        //Botón de lanzamento de dados
        item( span = { GridItemSpan( maxLineSpan ) } ) {

            val recheoInferior = if ( listaSeleccionados.isEmpty() ) 0.dp else 10.dp
            val modificador = Modifier.fillMaxWidth().padding( bottom = recheoInferior )
            val accion = { listaSeleccionados = tarefaDatos.lanzarDados( cantidadeDados ) }

            Button( onClick = accion, modifier = modificador ) { Text( text = "Lanzar dados" ) }//Localizar isto

        }

        //Resultados
        items( listaSeleccionados ) { icona -> CasillaIcono( icona ) }

        //Mostra de todas as alternativas posibles
        item( span = { GridItemSpan( maxLineSpan ) } ) {

            Column( modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally ) {

                val recheoDivisor = Modifier.padding( vertical = 10.dp )
                val corDivisor = MaterialTheme.colorScheme.primary
                val textoBoton = if ( amosarLista ) "Tirar lista completa" else "Ver lista completa"

                HorizontalDivider( modifier = recheoDivisor, color = corDivisor )
                Button( onClick = { amosarLista = !amosarLista } ) { Text( text = textoBoton ) }

            }

        }

        if ( amosarLista ) {
            items( todasIconas ) { ( _, icona ) -> ListarTodasIconas( icona ) }
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