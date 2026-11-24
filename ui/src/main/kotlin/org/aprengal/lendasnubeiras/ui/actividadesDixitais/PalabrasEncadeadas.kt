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
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.aprengal.lendasnubeiras.data.actividades.datos.base.Actividade
import org.aprengal.lendasnubeiras.data.actividades.datos.dixitais.Dificultade
import org.aprengal.lendasnubeiras.data.localizacion.clases.Localizacion.collerTextoActividadeDixital
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.CabeceiraActividadeDixital
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.EspazadorAlto
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.compostos.ContaAtras
import kotlin.time.Duration.Companion.milliseconds

/**
 * Mostra a actividade dixital «Palabras encadeadas»: a aplicación amosa unha letra e cada xogador
 * ten un tempo limitado para dicir a súa palabra.
 *
 * Ao comezar, escóllese unha letra aleatoria da clave `letras` do mapa de textos, con valores separados
 * por comas. Cada xogador ten un tempo limitado, que se mostran nunha conta atrás
 * ([ContaAtras]). O botón de seguinte xogador reinicia a conta atrás.
 *
 * Se se esgota o tempo, amósase que o xogador está eliminado. Nos niveis fácil, medio e difícil a conta
 * atrás queda en 0 ata que se prema o botón de seguinte xogador. No nivel pesadelo o reloxo non se
 * detén: despois dun cuarto de segundo pasa automaticamente ao seguinte xogador.
 *
 * Non se leva a conta dos xogadores nin das palabras: iso queda para as persoas participantes.
 *
 * @param dificultade Nivel de dificultade, que determina o tempo de cada xogador.
 * @param textos Mapa cos textos da interface ([Actividade.textos]).
 */
@Composable
fun PalabrasEncadeadas( dificultade: Dificultade, textos: Map<String, String> ) {

    val segundos = when ( dificultade ) {
        Dificultade.Facil -> 10
        Dificultade.Media -> 7
        Dificultade.Dificil -> 5
        Dificultade.Pesadelo -> 3
    }

    val texto = { clave: String -> collerTextoActividadeDixital( textos, clave ) }
    val letras = remember( textos ) { texto( "letras" ).split( "," ).map { letra -> letra.trim() }.filter { letra -> letra.isNotEmpty() } }

    var iniciada by rememberSaveable { mutableStateOf( false ) }
    var letra by rememberSaveable { mutableStateOf( "" ) }
    var ronda by rememberSaveable { mutableIntStateOf( 0 ) }
    var esgotado by rememberSaveable( ronda ) { mutableStateOf( false ) }

    //No nivel pesadelo o reloxo non se detén tras unha eliminación
    LaunchedEffect( esgotado ) {

        if ( esgotado && dificultade == Dificultade.Pesadelo ) {
            delay( 250.milliseconds )
            ronda++
        }

    }

    val comezar = {
        letra = letras.randomOrNull().orEmpty()
        ronda++
        iniciada = true
    }

    Column( Modifier.fillMaxSize().verticalScroll( rememberScrollState() ), horizontalAlignment = Alignment.CenterHorizontally ) {

        EspazadorAlto()
        CabeceiraActividadeDixital( texto( "instrucions" ), dificultade )

        if ( !iniciada ) {

            EspazadorAlto()
            Button( onClick = comezar, modifier = Modifier.fillMaxWidth() ) { Text( texto( "comezar" ) ) }

        } else {

            Card( Modifier.fillMaxWidth() ) {

                Column( Modifier.fillMaxWidth().padding( 16.dp ), horizontalAlignment = Alignment.CenterHorizontally ) {
                    Text( texto( "letra_inicial" ), style = MaterialTheme.typography.labelLarge )
                    Text( letra, style = MaterialTheme.typography.headlineLarge.copy( fontSize = 72.sp, lineHeight = 80.sp ) )
                }

            }

            EspazadorAlto( 2 )
            ContaAtras( segundos, ronda ) { esgotado = true }

            if ( esgotado && dificultade != Dificultade.Pesadelo ) {

                EspazadorAlto()
                Text(
                    texto( "eliminado" ), Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                    style = MaterialTheme.typography.titleLarge
                )

            }

            EspazadorAlto( 2 )
            Button( onClick = { ronda++ }, modifier = Modifier.fillMaxWidth() ) { Text( texto( "seguinte_xogador" ) ) }
            EspazadorAlto()
            OutlinedButton( onClick = { iniciada = false }, modifier = Modifier.fillMaxWidth() ) { Text( texto( "rematar" ) ) }

        }

        EspazadorAlto( 2 )

    }

}