package org.aprengal.lendasnubeiras.ui.reutilizables.estruturas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.AmosarTitulo
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.EspazadorAncho
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.Logo

@Composable
fun EstruturaSuperior( contido: @Composable () -> Unit ) {

    val modificador = Modifier.fillMaxWidth().height( 64.dp )

    ColocarExtras {

        Column( Modifier.fillMaxSize() ) {

            //Igual isto se pode mover a unha nova función
            Row( modificador, horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically ) {
                Logo()
                EspazadorAncho()
                AmosarTitulo()
            }

            HorizontalDivider()

            Column( Modifier.padding( 10.dp ) ) {
                contido()
            }

        }

    }

}