package org.aprengal.lendasnubeiras.ui.actividadesDixitais

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.aprengal.lendasnubeiras.data.actividades.datos.base.Actividade
import org.aprengal.lendasnubeiras.data.actividades.datos.dixitais.Dificultade
import org.aprengal.lendasnubeiras.data.localizacion.clases.Localizacion.collerTextoActividadeDixital
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.CabeceiraActividadeDixital
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.EspazadorAlto
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.compostos.ContaAtras
import kotlin.time.Duration.Companion.milliseconds

/**
 * Mostra a actividade dixital «A aplicación sinala»: a aplicación indica ordes e accións que as persoas
 * participantes deben realizar antes de que remate o tempo.
 *
 * As ordes están no mapa de textos, nas claves `ordes_facil`, `ordes_media` e `ordes_dificil`, separadas
 * por «|». En cada ronda escóllese unha ao chou. Cando se esgota o tempo pasa automaticamente á seguinte
 * orde, e o botón permite adiantala.
 *
 * Este xogo non ten nivel pesadelo.
 *
 * @param dificultade Nivel de dificultade, que determina as ordes e o tempo por acción.
 * @param textos Mapa cos textos da interface ([Actividade.textos]).
 */
@Composable
fun AplicacionSinala( dificultade: Dificultade, textos: Map<String, String> ) {

    val segundos = when ( dificultade ) {
        Dificultade.Facil -> 6
        Dificultade.Media -> 5
        Dificultade.Dificil -> 4
        Dificultade.Pesadelo -> error( "Aplicación sinala non ten nivel pesadelo" )
    }

    val texto = { clave: String -> collerTextoActividadeDixital( textos, clave ) }
    val ordes = remember( textos, dificultade ) {

        Dificultade.entries
            .filter { nivel -> nivel.ordinal <= dificultade.ordinal }
            .flatMap { nivel -> texto( "ordes_${ nivel.clave }" ).split( "|" ) }
            .map { orde -> orde.trim() }
            .filter { orde -> orde.isNotEmpty() }

    }

    var iniciada by rememberSaveable { mutableStateOf( false ) }
    var ronda by rememberSaveable { mutableIntStateOf( 0 ) }
    var esgotado by rememberSaveable( ronda ) { mutableStateOf( false ) }
    val orde = rememberSaveable( ronda ) { ordes.randomOrNull().orEmpty() }

    // Retardo suave cando se esgota o tempo para evitar o salto brusco de 0 ao máximo
    LaunchedEffect( esgotado ) {
        if ( esgotado ) {
            delay( 250.milliseconds )
            ronda++
            esgotado = false
        }
    }

    Column( Modifier.fillMaxSize().verticalScroll( rememberScrollState() ), horizontalAlignment = Alignment.CenterHorizontally ) {

        EspazadorAlto()
        CabeceiraActividadeDixital( texto( "instrucions" ), dificultade )

        if ( !iniciada ) {

            EspazadorAlto()
            Button( onClick = { ronda++; iniciada = true }, modifier = Modifier.fillMaxWidth() ) { Text( texto( "comezar" ) ) }

        } else {

            Card( Modifier.fillMaxWidth() ) {
                Text( orde, Modifier.fillMaxWidth().padding( 16.dp ), style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center )
            }

            EspazadorAlto( 2 )
            ContaAtras( segundos, ronda ) { esgotado = true }

            EspazadorAlto( 2 )
            Button( onClick = { ronda++ }, modifier = Modifier.fillMaxWidth() ) { Text( texto( "seguinte_orde" ) ) }
            EspazadorAlto()
            OutlinedButton( onClick = { iniciada = false }, modifier = Modifier.fillMaxWidth() ) { Text( texto( "rematar" ) ) }

        }

        EspazadorAlto( 2 )

    }

}