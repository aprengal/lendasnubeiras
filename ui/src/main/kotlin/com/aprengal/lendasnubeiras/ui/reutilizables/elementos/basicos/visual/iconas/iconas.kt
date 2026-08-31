package com.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.iconas

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.aprengal.lendasnubeiras.data.localizacion.claves.L10nSingular
import com.aprengal.lendasnubeiras.ui.R
import com.aprengal.lendasnubeiras.ui.navegacion.Ruta


private val fonteIconas = FontFamily( Font( R.font.ubuntu_iconas_nerd, FontWeight.Bold ) )

@Composable
fun DebuxarIcona( contido: String, descricion: L10nSingular? = null, dimension: TextUnit ) {

    val cor = LocalContentColor.current
    val modificador = descricion?.let { Modifier.semantics { contentDescription = descricion.texto() } } ?: Modifier.clearAndSetSemantics { }

    Text( text = contido, modifier = modificador, fontSize = dimension, fontFamily = fonteIconas, color = cor, lineHeight = 1.sp )

}

private val iconas = mapOf(
    Ruta.Axustes::class to Icona.AXUSTES,
    Ruta.Actividades::class to Icona.INVALIDO,
    Ruta.Inicio::class to Icona.INICIO,
    Ruta.Idioma::class to Icona.IDIOMA,
    Ruta.Buscar::class to Icona.BUSCAR,
    Ruta.CrearActividade::class to Icona.ENGADIR
)

@Composable
internal fun DebuxarIconaMenu( ruta: Ruta, dimension: TextUnit ) {

    val icona = iconas[ ruta::class ]
    requireNotNull( icona ) { "A ruta ${ ruta::class.simpleName } non ten icona asignada" }

    DebuxarIcona( icona.codigo, icona.descricion, dimension = dimension )

}
