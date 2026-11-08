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

/**
 * Estrutura visual das páxinas que amosan o título na cabeceira superior.
 *
 * Dispón o logotipo e o título da aplicación nunha cabeceira horizontal,
 * separada do contido principal mediante unha liña divisoria.
 * O contido proporcionado colócase debaixo da cabeceira cunha marxe interior.
 *
 * @param contido Contido composable específico da páxina que se amosará baixo a cabeceira.
 */
@Composable
fun EstruturaSuperior( contido: @Composable () -> Unit ) {

    val modificador = Modifier.fillMaxWidth().height( 64.dp )

    ColocarExtras {

        Column( Modifier.fillMaxSize() ) {

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