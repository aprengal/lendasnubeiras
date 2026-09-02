package com.aprengal.lendasnubeiras.ui.reutilizables.estruturas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.aprengal.lendasnubeiras.ui.navegacion.Navegacion
import com.aprengal.lendasnubeiras.ui.navegacion.Ruta
import com.aprengal.lendasnubeiras.ui.reutilizables.clases.Icona.Companion.DebuxarIconaMenu
import com.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.AmosarTitulo
import com.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.EspazadorAlto
import com.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.Logo

@Composable
fun EstruturaApertura( navegacion: Navegacion, contido: @Composable () -> Unit ) {

    val modFila = Modifier.height( 64.dp ).absolutePadding( left = 16.dp, right = 4.dp )
    val modCol = Modifier.padding( horizontal = 24.dp, vertical = 16.dp )

    ColocarExtras {

        Row( modifier = modFila.align( Alignment.TopEnd ), verticalAlignment = Alignment.CenterVertically ) {
            IconaAxustes( navegacion )
        }

        Column( modCol.align( Alignment.Center ), Arrangement.Center, Alignment.CenterHorizontally ) {
            Logo( 90.dp )
            EspazadorAlto( 2 )
            AmosarTitulo()
            EspazadorAlto()
            contido()
        }

    }

}

@Composable
fun IconaAxustes( navegacion: Navegacion ) {

    val elemento = Ruta.Axustes
    val dimension = with( LocalDensity.current ) { 30.dp.toSp() }
    val accion = { navegacion.engadir( elemento ) }

    IconButton( accion ) {
        DebuxarIconaMenu( elemento, dimension )
    }

}