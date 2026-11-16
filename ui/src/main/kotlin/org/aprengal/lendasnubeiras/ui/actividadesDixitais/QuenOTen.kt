package org.aprengal.lendasnubeiras.ui.actividadesDixitais

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import org.aprengal.lendasnubeiras.data.actividades.datos.base.Actividade
import org.aprengal.lendasnubeiras.data.actividades.datos.dixitais.Dificultade
import org.aprengal.lendasnubeiras.data.localizacion.clases.Localizacion.collerTextoActividadeDixital
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.CabeceiraActividadeDixital
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.EspazadorAlto
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.compostos.ContaAtras

/**
 * Mostra a actividade dixital «Quen o ten?»: unha persoa ponse no centro e as demais pasan un obxecto
 * en segredo; a do centro ten que adiviñar quen o ten antes de que remate o tempo.
 *
 * O tempo de cada ronda depende da dificultade e móstrase nunha conta atrás ([ContaAtras]). A conta atrás
 * pódese parar e continuar. Ao premer «Acertou / Seguinte» comeza unha ronda nova, en pausa, para que dea
 * tempo a colocarse e pasar o obxecto antes de continuar.
 *
 * Cando se esgota o tempo anúnciase e a conta atrás queda en 0 ata que se comece a seguinte ronda.
 *
 * @param dificultade Nivel de dificultade, que determina o tempo de cada ronda.
 * @param textos Mapa cos textos da interface ([Actividade.textos]).
 */
@Composable
fun QuenOTen( dificultade: Dificultade, textos: Map<String, String> ) {

    val segundos = when ( dificultade ) {
        Dificultade.Facil -> 120
        Dificultade.Media -> 90
        Dificultade.Dificil -> 60
        Dificultade.Pesadelo -> 30
    }

    val texto = { clave: String -> collerTextoActividadeDixital( textos, clave ) }
    var iniciada by rememberSaveable { mutableStateOf( false ) }
    var enPausa by rememberSaveable { mutableStateOf( false ) }
    var ronda by rememberSaveable { mutableIntStateOf( 0 ) }
    var esgotado by rememberSaveable( ronda ) { mutableStateOf( false ) }

    val comezar = {
        ronda++
        enPausa = false
        iniciada = true
    }

    //Cada ronda nova comeza en pausa, para poder colocarse e pasar o obxecto antes de continuar
    val seguinte = {
        ronda++
        enPausa = true
    }

    val alternarPausa = { enPausa = !enPausa }
    val esgotarTempo = { esgotado = true }
    val rematar = { iniciada = false }

    val textoPausa = if ( enPausa ) texto( "continuar_tempo" ) else texto( "parar_tempo" )

    val aviso = when {
        esgotado -> texto( "tempo_esgotado" )
        enPausa -> texto( "cron_pausado" )
        else -> ""
    }

    Column( Modifier.fillMaxSize().verticalScroll( rememberScrollState() ), horizontalAlignment = Alignment.CenterHorizontally ) {

        EspazadorAlto()
        CabeceiraActividadeDixital( texto( "instrucions" ), dificultade )

        if ( !iniciada ) {

            EspazadorAlto()
            Button( onClick = comezar, modifier = Modifier.fillMaxWidth() ) { Text( texto( "comezar" ) ) }

        } else {

            EspazadorAlto( 2 )
            ContaAtras( segundos, ronda, enPausa, esgotarTempo )

            if ( aviso.isNotEmpty() ) {
                EspazadorAlto()
                Text( aviso, Modifier.semantics { liveRegion = LiveRegionMode.Polite }, style = MaterialTheme.typography.titleMedium )
            }

            EspazadorAlto( 2 )
            OutlinedButton( onClick = alternarPausa, modifier = Modifier.fillMaxWidth(), enabled = !esgotado ) { Text( textoPausa ) }
            EspazadorAlto()
            Button( onClick = seguinte, modifier = Modifier.fillMaxWidth() ) { Text( texto( "acertou" ) ) }
            EspazadorAlto()
            OutlinedButton( onClick = rematar, modifier = Modifier.fillMaxWidth() ) { Text( texto( "rematar" ) ) }

        }

        EspazadorAlto( 2 )

    }

}