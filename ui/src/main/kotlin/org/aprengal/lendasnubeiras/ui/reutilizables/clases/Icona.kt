package org.aprengal.lendasnubeiras.ui.reutilizables.clases

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
import org.aprengal.lendasnubeiras.data.localizacion.clases.ElementoL10n
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nIconas
import org.aprengal.lendasnubeiras.ui.R
import org.aprengal.lendasnubeiras.ui.navegacion.Ruta

/**
 * Enumerado das iconas dispoñibles na aplicación.
 *
 * Cada elemento define un carácter da fonte de iconas empregada pola interface
 * e o recurso de localización asociado, utilizado como descrición accesible.
 *
 * Implementa [ElementoL10n] para identificar cada icona mediante a súa clave
 * e proporcionar o nome localizado correspondente.
 */
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
    Correo( "\uDB80\uDDF0", L10nIconas.Correo ),
    Catalogo( "\uDB82\uDC2E", L10nIconas.Catalogo );


    companion object {

        /**
         * Familia tipográfica que contén os glifos das iconas da aplicación.
         */
        private val fonteIconas = FontFamily( Font( R.font.ubuntu_iconas_nerd, FontWeight.Bold ) )

        /**
         * Debuxa unha icona empregando a fonte específica da aplicación.
         *
         * Aplica a cor de contido actual e, se se proporciona unha descrición,
         * engádea á semántica do elemento para facilitar a súa identificación
         * mediante tecnoloxías de asistencia. Se non hai descrición, oculta
         * o elemento da árbore semántica.
         *
         * @param contido Carácter ou glifo que representa a icona.
         * @param descricion Recurso de localización coa descrición accesible da icona.
         * @param dimension Tamaño da icona expresado como unidade tipográfica.
         */
        @Composable
        fun DebuxarIcona( contido: String, descricion: L10nIconas? = null, dimension: TextUnit ) {

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

        /**
         * Relaciona os tipos de ruta coas iconas que os representan no menú
         * principal da aplicación.
         *
         * As rutas sen unha icona asignada non aparecen nesta correspondencia.
         */
        private val iconas = mapOf(
            Ruta.Axustes::class to Axustes,
            Ruta.Catalogo::class to Catalogo,
            Ruta.Inicio::class to Inicio,
            Ruta.Buscador::class to Buscar,
            Ruta.CrearActividade::class to Engadir
        )

        /**
         * Debuxa a icona asociada a unha ruta da aplicación.
         *
         * Consulta a correspondencia de rutas e iconas e delega o debuxado
         * en [DebuxarIcona]. Se a ruta non ten unha icona asignada, lanza
         * unha excepción.
         *
         * @param ruta Ruta da aplicación á que se lle quere asociar unha icona.
         * @param dimension Tamaño da icona expresado como unidade tipográfica.
         * @throws IllegalArgumentException Se a ruta non ten unha icona asignada.
         */
        @Composable
        internal fun DebuxarIconaMenu( ruta: Ruta, dimension: TextUnit ) {

            val icona = iconas[ ruta::class ]
            requireNotNull( icona ) { "A ruta ${ ruta::class.simpleName } non ten icona asignada" }

            DebuxarIcona( icona.clave, icona.nome, dimension = dimension )

        }

    }

}