package com.aprengal.lendasnubeiras.ui.pantallas.monitor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aprengal.lendasnubeiras.data.localizacion.claves.L10nPlural
import com.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.CampoBuscador
import com.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.Texto
import com.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.TextoPlural
import com.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.EspazadorAlto

@Composable
fun PantallaBuscador() {

    //Esta páxina serviría para realizar as buscas e configuralas?
    //Igual Atributo e os seus fillos precisan de clave externo traducido

    val busca = rememberTextFieldState()

    CampoBuscador( busca )

}

@Composable
fun PantallaResultadoBusca( termo: String ) {

    val busca = rememberTextFieldState( termo )

    CampoBuscador( busca )

}

@Composable
fun PantallaActividadeDetalle( id: Long ) {

    Text( "Detalle de $id" )

}

@Composable
fun PantallaActividade() {

    Text( "EHHHHHHHHHHHHHHHHHHHHHHH" )

}

@Composable
fun PantallaInicio() {

    val scrollState = rememberScrollState()
    val modificadorCol = Modifier.fillMaxSize().verticalScroll( scrollState )

    Column( modificadorCol ) {

        Text( "F por todas as veces que apareceu Carla e Natasha nesta pantalla" )
        Text( "A aplicación segue sen funcionar e Natasha aínda non volveu de festa" )
        /*Texto( L10nXeral.Carla )
        Texto( L10nXeral.Natasha )
        TextoPlural( L10nPlural.MensaxesNovas, 1 )
        TextoPlural( L10nPlural.MensaxesNovas, 5 )
        TextoPlural( L10nPlural.MensaxesNovas, 0 )
        TextoPlural( L10nPlural.MensaxesNovas, 100 )
        TextoPlural( L10nPlural.MensaxesNovas, 10000000 )

        HorizontalDivider( modifier = Modifier.padding( top = 10.dp ) )

        repeat( 30 ) { indice ->
            EspazadorAlto( 2 )
            TextoPlural( L10nPlural.MensaxesNovas, indice + 1 )
            HorizontalDivider( modifier = Modifier.padding( top = 10.dp ) )
        }*/

    }

}