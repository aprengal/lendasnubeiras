package org.aprengal.lendasnubeiras.ui.reutilizables.elementos.compostos

import androidx.compose.animation.core.Animatable
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
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
 * Mostra unha conta atrás circular: un indicador de progreso que se vai baleirando mentres baixa o
 * tempo, co número de segundos restantes no centro e, debaixo, o texto do tempo restante.
 *
 * A conta atrás comeza de novo cada vez que cambia [ronda]. Cando chega a 0 execútase
 * [tempoEsgotado] unha soa vez e a conta queda en 0 ata que cambie [ronda].
 *
 * O número do centro non se le nos lectores de pantalla, porque xa o len no texto de debaixo.
 *
 * @param segundos Duración da conta atrás, en segundos.
 * @param ronda Identifica a conta atrás actual: ao cambiar, reiníciase.
 * @param tempoEsgotado Acción que se executa cando o tempo chega a 0.
 */
@Composable
fun ContaAtras( segundos: Int, ronda: Int, tempoEsgotado: () -> Unit ) {

    val progreso = remember { Animatable( 1f ) }
    val accionFinal by rememberUpdatedState( tempoEsgotado )

    //Só se recompón cando cambia o número, non en cada fotograma da animación
    val segundosRestantes by remember( segundos ) { derivedStateOf { ceil( progreso.value * segundos ).toInt() } }

    LaunchedEffect( ronda, segundos ) {

        progreso.snapTo( 1f )
        progreso.animateTo( 0f, tween( segundos * 1000, easing = LinearEasing ) )
        accionFinal()

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