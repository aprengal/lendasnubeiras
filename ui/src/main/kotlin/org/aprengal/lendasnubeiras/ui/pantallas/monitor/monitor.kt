package org.aprengal.lendasnubeiras.ui.pantallas.monitor

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.CampoBuscador

@Composable
fun PantallaBuscador() {

    val busca = rememberTextFieldState()

    CampoBuscador( busca )

}

@Composable
fun PantallaActividadeDetalle( id: String ) {

    Text( "Detalle de $id" )

}