package org.aprengal.lendasnubeiras.ui.pantallas.monitor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.aprengal.lendasnubeiras.data.localizacion.cantidades.L10nVariante
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nIconas
import org.aprengal.lendasnubeiras.ui.navegacion.Locais.LocalNavegacion
import org.aprengal.lendasnubeiras.ui.navegacion.Ruta
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.Icona
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.TextoVariante
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.EspazadorAlto
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.compostos.TarxetaPulsable
import kotlin.random.Random

@Composable
fun PantallaInicio() {

    val navegacion = LocalNavegacion.current
    val numeroAleatorio = rememberSaveable { Random.nextInt( 0, 11 ) }
    val desprazamento = rememberScrollState()
    val modificadorCol = Modifier.fillMaxSize().verticalScroll( desprazamento )

    val accionCatalogo = { navegacion.engadir( Ruta.Catalogo ) }
    val accionBuscar = { navegacion.engadir( Ruta.Buscador ) }

    Column( modificadorCol, Arrangement.Center, Alignment.CenterHorizontally ) {

        TarxetaPulsable( Icona.Catalogo, accionCatalogo, L10nIconas.Catalogo )
        EspazadorAlto( 2 )
        TarxetaPulsable( Icona.Buscar, accionBuscar, L10nIconas.Buscar )
        EspazadorAlto( 3 )

        Card( Modifier.fillMaxWidth() ) {
            TextoVariante( L10nVariante.MensaxesCambiantes, numeroAleatorio, estilo = MaterialTheme.typography.bodyLarge )
        }

    }

}