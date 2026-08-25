package com.aprengal.lendasnubeiras.ui.pantallas.lector

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aprengal.lendasnubeiras.data.localizacion.L10nPlural
import com.aprengal.lendasnubeiras.data.localizacion.L10nSingular
import com.aprengal.lendasnubeiras.ui.reutilizables.Texto
import com.aprengal.lendasnubeiras.ui.reutilizables.TextoPlural

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


    Column {

        Texto( L10nSingular.CARLA )
        Texto( L10nSingular.NATASHA )
        TextoPlural( L10nPlural.MENSAXES_NOVAS, 1 )
        TextoPlural( L10nPlural.MENSAXES_NOVAS, 5 )
        TextoPlural( L10nPlural.MENSAXES_NOVAS, 0 )
        TextoPlural( L10nPlural.MENSAXES_NOVAS, 100 )
        TextoPlural( L10nPlural.MENSAXES_NOVAS, 10000000 )

        HorizontalDivider( modifier = Modifier.padding( top = 10.dp ) )

    }

}