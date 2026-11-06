package org.aprengal.lendasnubeiras.ui.pantallas.monitor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.aprengal.lendasnubeiras.data.localizacion.cantidades.L10nVariante
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nIconas
import org.aprengal.lendasnubeiras.ui.navegacion.Locais.LocalNavegacion
import org.aprengal.lendasnubeiras.ui.navegacion.Ruta
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.Icona
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.Icona.Companion.DebuxarIcona
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.Texto
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.TextoVariante
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.EspazadorAlto
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.EspazadorAncho
import kotlin.random.Random

@Composable
fun PantallaInicio() {

    val navegacion = LocalNavegacion.current
    val numeroAleatorio = rememberSaveable { Random.nextInt( 0, 11 ) }
    val desprazamento = rememberScrollState()

    val modificadorCol = Modifier.fillMaxSize().verticalScroll( desprazamento )
    val modificadorTarxeta = Modifier.fillMaxWidth()
    val modificadorContido = Modifier.fillMaxWidth().padding( vertical = 20.dp, horizontal = 16.dp )

    Column( modificadorCol, Arrangement.Center, Alignment.CenterHorizontally ) {

        Card( modifier = modificadorTarxeta.clickable( onClick = { navegacion.engadir( Ruta.Catalogo ) } ) ) {

            Column( modificadorContido, Arrangement.Center, Alignment.CenterHorizontally ) {

                Row( modifier = Modifier.fillMaxWidth(), Arrangement.Start, Alignment.CenterVertically ) {

                    val iconaCatalogo = Icona.Catalogo

                    //Non se indica o texto descritivo para lectores porque xa se emprega no texto e iso faría que se lese o mesmo dúas veces
                    DebuxarIcona( iconaCatalogo.clave, dimension = 36.sp )
                    EspazadorAncho( 2 )
                    Texto( L10nIconas.Catalogo, estilo = MaterialTheme.typography.titleLarge )

                }

            }

        }

        EspazadorAlto( 2 )

        Card( modificadorTarxeta.clickable( onClick = { navegacion.engadir( Ruta.Buscar ) } ) ) {

            Column( modificadorContido, Arrangement.Center, Alignment.CenterHorizontally ) {

                Row( modifier = Modifier.fillMaxWidth(), Arrangement.Start, Alignment.CenterVertically ) {

                    val iconaCatalogo = Icona.Buscar

                    //Non se indica o texto descritivo para lectores porque xa se emprega no texto e iso faría que se lese o mesmo dúas veces
                    DebuxarIcona( iconaCatalogo.clave, iconaCatalogo.nome, 36.sp )
                    EspazadorAncho( 2 )
                    Texto( L10nIconas.Buscar, estilo = MaterialTheme.typography.titleLarge )

                }

            }

        }

        EspazadorAlto( 3 )

        Card( modifier = modificadorTarxeta ) {

            Column( Modifier.fillMaxWidth().padding( 16.dp ), Arrangement.Center, Alignment.CenterHorizontally ) {
                TextoVariante( L10nVariante.MensaxesCambiantes, numeroAleatorio, estilo = MaterialTheme.typography.bodyLarge )
            }

        }

    }

}