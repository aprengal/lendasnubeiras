package com.aprengal.lendasnubeiras.ui.reutilizables.clases

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
import com.aprengal.lendasnubeiras.data.localizacion.ElementoL10n
import com.aprengal.lendasnubeiras.data.localizacion.claves.singular.L10nIconas
import com.aprengal.lendasnubeiras.ui.R
import com.aprengal.lendasnubeiras.ui.navegacion.Ruta

enum class Icona( override val clave: String, override val nome: L10nIconas ): ElementoL10n {

    //Poderían volver
    //val perfil      = "\uDB80\uDC04" // U+F0004

    Axustes( "\uDB82\uDCBB", L10nIconas.Axustes ),

    Atras( "\uDB83\uDCDE", L10nIconas.Atras ),
    Buscar( "\uDB80\uDF49", L10nIconas.Buscar ),
    Limpar( "\uDB80\uDD56", L10nIconas.Limpar ),
    Engadir( "\uDB81\uDC15", L10nIconas.Crear ),
    Idioma( "\uDB81\uDD9F", L10nIconas.Idioma ),
    Inicio( "\uDB80\uDEDC", L10nIconas.Inicio ),
    OlloAberto( "\uDB80\uDE08", L10nIconas.Amosar ),
    OlloPechado( "\uDB80\uDE09", L10nIconas.Agochar ),
    TemaClaro( "\uDB81\uDDA8", L10nIconas.TemaClaro ),
    TemaEscuro( "\uDB83\uDF65", L10nIconas.TemaEscuro ),

    Valido( "\uDB80\uDD2C", L10nIconas.Valido ),
    Invalido( "\uDB80\uDD56", L10nIconas.Invalido ),
    Correo( "\uDB80\uDDF0", L10nIconas.Correo );


    companion object {

        private val fonteIconas = FontFamily(Font(R.font.ubuntu_iconas_nerd, FontWeight.Bold))

        @Composable
        fun DebuxarIcona(contido: String, descricion: L10nIconas? = null, dimension: TextUnit ) {

            val cor = LocalContentColor.current
            val modificador = descricion?.let { Modifier.semantics { contentDescription = descricion.texto() } } ?: Modifier.clearAndSetSemantics { }

            Text(
                text = contido,
                modifier = modificador,
                fontSize = dimension,
                fontFamily = fonteIconas,
                color = cor,
                lineHeight = 1.sp
            )

        }

        private val iconas = mapOf(
            Ruta.Axustes::class to Axustes,
            Ruta.Actividades::class to Invalido,
            Ruta.Inicio::class to Inicio,
            Ruta.Idioma::class to Idioma,
            Ruta.Buscar::class to Buscar,
            Ruta.CrearActividade::class to Engadir
        )

        @Composable
        internal fun DebuxarIconaMenu( ruta: Ruta, dimension: TextUnit ) {

            val icona = iconas[ ruta::class ]
            requireNotNull( icona ) { "A ruta ${ ruta::class.simpleName } non ten icona asignada" }

            DebuxarIcona( icona.clave, icona.nome, dimension = dimension )

        }

    }

}