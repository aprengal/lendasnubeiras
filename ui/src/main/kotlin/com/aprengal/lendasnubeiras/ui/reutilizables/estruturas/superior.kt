package com.aprengal.lendasnubeiras.ui.reutilizables.estruturas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aprengal.lendasnubeiras.ui.pantallas.AmosarTitulo
import com.aprengal.lendasnubeiras.ui.reutilizables.EspazadorAncho
import com.aprengal.lendasnubeiras.ui.reutilizables.Logo

@Composable
fun EstruturaSuperior( contido: @Composable () -> Unit ) {

    val modificador = Modifier.fillMaxWidth().height( 64.dp )

    Column( Modifier.fillMaxSize().windowInsetsPadding( WindowInsets.systemBars ) ) {

        //Igual isto se pode mover a unha nova función
        Row( modificador, horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically ) {
            Logo()
            EspazadorAncho()
            AmosarTitulo()
        }

        HorizontalDivider( thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface )

        Column( Modifier.padding( 10.dp ) ) {
            contido()
        }

    }

}