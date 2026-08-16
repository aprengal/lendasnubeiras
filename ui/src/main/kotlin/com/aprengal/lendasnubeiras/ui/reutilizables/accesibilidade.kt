package com.aprengal.lendasnubeiras.ui.reutilizables

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import com.aprengal.lendasnubeiras.data.localizacion.Localizacion.l10n


//Por se hai un texto que apareza de golpe (mensaxes de erro en formularios)
@Composable
fun TextoAnunciable( clave: String, dominio: String, modifier: Modifier = Modifier ) {

    Text(
        text = l10n( clave, dominio ),
        modifier = modifier.semantics { liveRegion = LiveRegionMode.Polite }
    )

}