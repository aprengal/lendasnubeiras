package org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.aprengal.lendasnubeiras.data.axustes.DatosTema.escollerVarianteImaxe
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nBase
import org.aprengal.lendasnubeiras.ui.R
import org.aprengal.lendasnubeiras.ui.tema.Tema.temaActual

@Composable
fun Logo( medida: Dp = 30.dp ) {
    val logo = escollerVarianteImaxe( temaActual, isSystemInDarkTheme(), R.drawable.logo_claro, R.drawable.logo_escuro )
    Image( painterResource( logo ), L10nBase.NomeApp.texto(), Modifier.size( medida ) )
}