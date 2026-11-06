package org.aprengal.lendasnubeiras.ui.pantallas.monitor

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.CampoBuscador

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
fun PantallaActividadeDetalle( id: String ) {

    Text( "Detalle de $id" )

}