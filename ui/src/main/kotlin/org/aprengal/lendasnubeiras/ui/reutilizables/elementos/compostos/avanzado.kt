package org.aprengal.lendasnubeiras.ui.reutilizables.elementos.compostos

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.aprengal.lendasnubeiras.data.localizacion.cantidades.L10nPlural
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.TextoPlural
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.EspazadorAlto
import kotlin.math.ceil

/**
 * Gardador do progreso da conta atrás ([ContaAtras]), para evitar que o círculo se reinicie
 * ao rotar a pantalla.
 *
 * Só garda o valor actual do progreso, un número entre 0 e 1. Ao restauralo, crea un novo
 * [Animatable] con ese valor.
 */
private val gardadorProgreso = Saver<Animatable<Float, AnimationVector1D>, Float>(
    save = { progreso -> progreso.value },
    restore = { valor -> Animatable( valor ) }
)

/**
 * Mostra unha conta atrás circular co tempo restante no centro e debaixo do indicador.
 *
 * A conta atrás reiníciase cando cambia [ronda]. Pode pausarse e retomarse conservando o
 * progreso. Ao chegar a cero, execútase [tempoEsgotado].
 *
 * O progreso consérvase ao restaurar o estado da pantalla, por exemplo, tras unha rotación.
 *
 * @param segundos Duración da conta atrás, en segundos.
 * @param ronda Identificador da rolda actual.
 * @param pausada Indica se a conta atrás está pausada.
 * @param tempoEsgotado Acción que se executa ao rematar a conta atrás.
 */
@Composable
fun ContaAtras( segundos: Int, ronda: Int, pausada: Boolean = false, tempoEsgotado: () -> Unit ) {

    val progreso = rememberSaveable( saver = gardadorProgreso ) { Animatable( 1f ) }
    var rondaAnimada by rememberSaveable { mutableIntStateOf( -1 ) }
    val accionFinal by rememberUpdatedState( tempoEsgotado )
    val segundosRestantes by remember( segundos ) { derivedStateOf { ceil( progreso.value * segundos ).toInt() } }

    LaunchedEffect( ronda, segundos, pausada ) {

        if ( rondaAnimada != ronda ) {
            progreso.snapTo( 1f )
            rondaAnimada = ronda
        }

        if ( !pausada && progreso.value > 0f ) {
            progreso.animateTo( 0f, tween( ( progreso.value * segundos * 1000 ).toInt(), easing = LinearEasing ) )
            accionFinal()
        }

    }

    Column( Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally ) {

        Box( contentAlignment = Alignment.Center ) {

            CircularProgressIndicator( progress = { progreso.value }, modifier = Modifier.size( 160.dp ), strokeWidth = 10.dp )
            Text( segundosRestantes.toString(), Modifier.clearAndSetSemantics { }, style = MaterialTheme.typography.headlineLarge.copy( fontSize = 48.sp, lineHeight = 56.sp ) )

        }

        EspazadorAlto()
        TextoPlural( L10nPlural.TempoRestante, segundosRestantes )

    }

}