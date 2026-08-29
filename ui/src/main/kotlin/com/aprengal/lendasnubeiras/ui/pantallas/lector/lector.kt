package com.aprengal.lendasnubeiras.ui.pantallas.lector

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aprengal.lendasnubeiras.data.localizacion.L10nPlural
import com.aprengal.lendasnubeiras.data.localizacion.L10nSingular
import com.aprengal.lendasnubeiras.ui.reutilizables.EspazadorAlto
import com.aprengal.lendasnubeiras.ui.reutilizables.Texto
import com.aprengal.lendasnubeiras.ui.reutilizables.TextoPlural

@Composable
fun PantallaBuscador( termo: String) {

    println( termo  )
    TODO("Not yet implemented")

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

        Texto( L10nSingular.CARLA )
        Texto( L10nSingular.NATASHA )
        TextoPlural( L10nPlural.MENSAXES_NOVAS, 1 )
        TextoPlural( L10nPlural.MENSAXES_NOVAS, 5 )
        TextoPlural( L10nPlural.MENSAXES_NOVAS, 0 )
        TextoPlural( L10nPlural.MENSAXES_NOVAS, 100 )
        TextoPlural( L10nPlural.MENSAXES_NOVAS, 10000000 )

        HorizontalDivider( modifier = Modifier.padding( top = 10.dp ) )

        repeat( 30 ) { indice ->
            EspazadorAlto( 2 )
            TextoPlural( L10nPlural.MENSAXES_NOVAS, indice + 1 )
            HorizontalDivider( modifier = Modifier.padding( top = 10.dp ) )
        }

    }

}