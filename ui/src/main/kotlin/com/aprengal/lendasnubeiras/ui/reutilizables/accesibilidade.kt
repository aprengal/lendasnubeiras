package com.aprengal.lendasnubeiras.ui.reutilizables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aprengal.lendasnubeiras.data.localizacion.L10nSingular
import com.aprengal.lendasnubeiras.ui.R


//Por se hai un texto que apareza de golpe (mensaxes de erro en formularios)
@Composable
fun TextoAnunciable( elemento: L10nSingular, modifier: Modifier = Modifier ) {
    Text( text = elemento.texto(), modifier = modifier.semantics { liveRegion = LiveRegionMode.Polite } )
}

@Composable
private fun CasillaIconoAccesible( icona: String, descricion: String, modifier: Modifier = Modifier ) {

    val corExterna = MaterialTheme.colorScheme.secondary.copy( alpha = 0.15f )
    val modificador = modifier
        .aspectRatio( 1f )
        .background( color = corExterna, shape = RoundedCornerShape( 8.dp ) )
        .semantics { contentDescription = descricion }

    BoxWithConstraints( contentAlignment = Alignment.Center, modifier = modificador ) {

        val fonte = FontFamily( Font( R.font.ubuntu_iconas_nerd, FontWeight.Bold ) )
        val densidade = with( LocalDensity.current ) { ( maxWidth * 0.625f ).toSp() }
        val corTexto = LocalContentColor.current

        Text( text = icona, fontFamily = fonte, fontSize = densidade, color = corTexto, modifier = Modifier.clearAndSetSemantics {} )

    }

}