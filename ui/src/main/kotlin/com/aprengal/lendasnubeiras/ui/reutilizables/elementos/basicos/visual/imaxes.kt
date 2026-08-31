package com.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aprengal.lendasnubeiras.data.localizacion.claves.L10nSingular
import com.aprengal.lendasnubeiras.ui.R
import com.aprengal.lendasnubeiras.ui.tema.Tema.escollerVarianteImaxe

@Composable
fun Logo( medida: Dp = 30.dp ) {
    val logo = escollerVarianteImaxe( isSystemInDarkTheme(), R.drawable.logo_claro, R.drawable.logo_escuro )
    Image( painterResource( logo ), L10nSingular.NOME_APP.texto(), Modifier.size( medida ) )
}