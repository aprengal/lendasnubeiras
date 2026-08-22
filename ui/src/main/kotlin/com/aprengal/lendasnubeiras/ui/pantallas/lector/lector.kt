package com.aprengal.lendasnubeiras.ui.pantallas.lector

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aprengal.lendasnubeiras.data.localizacion.L10nPlural
import com.aprengal.lendasnubeiras.data.localizacion.L10nSingular
import com.aprengal.lendasnubeiras.ui.PantallaBase

@Composable
fun PantallaBuscador( termo: String) {

    println( termo  )
    TODO("Not yet implemented")

}

@Composable
fun PantallaActividadeDetalle( id: Long ) {

    Text( "DEtalle de $id" )

}

@Composable
fun PantallaActividade() {

    Text( "EHHHHHHHHHHHHHHHHHHHHHHH" )

}

@Composable
fun PantallaInicio() {

    PantallaBase {

        item {

            Column {

                Text( text = L10nSingular.CARLA.texto() )

                Text( text = L10nSingular.NATASHA.texto(), style = MaterialTheme.typography.headlineLarge )

                Text( text = L10nPlural.MENSAXES_NOVAS.texto( 1 ) )

                Text( text = L10nPlural.MENSAXES_NOVAS.texto( 5 ) )

                Text( text = L10nPlural.MENSAXES_NOVAS.texto( 0 ) )

                Text( text = L10nPlural.MENSAXES_NOVAS.texto( 100 ) )

                HorizontalDivider(
                    modifier = Modifier.padding( vertical = 10.dp ),
                    color = MaterialTheme.colorScheme.primary
                )

            }

        }

    }

}