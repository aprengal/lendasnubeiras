package org.aprengal.lendasnubeiras.ui.pantallas.monitor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import org.aprengal.lendasnubeiras.data.actividades.datos.base.Actividade
import org.aprengal.lendasnubeiras.ui.navegacion.Ruta
import org.aprengal.lendasnubeiras.data.bd.operacions.Actividades.collerActividade
import org.aprengal.lendasnubeiras.data.localizacion.cantidades.L10nPlural
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nActividades
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nBase
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nSingular
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.BotonSecundario
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.EspazadorAlto

/**
 * Pantalla co detalle dunha actividade, que permite consultar a súa ficha de datos xerais.
 *
 * A pantalla mostra un botón para despregar ou ocultar a ficha cando a actividade ten textos
 * adicionais. Se non ten textos, a ficha móstrase directamente.
 *
 * A existencia da actividade compróbase antes de navegar a esta pantalla, polo que aquí non se
 * contempla que non exista: [collerActividade] devolve a actividade e non se compara con null.
 *
 * @param clave Clave do título da actividade, a mesma que leva [Ruta.ActividadeDetalle].
 */
@Composable
fun PantallaActividadeDetalle( clave: String ) {

    val actividade = remember( clave ) { collerActividade( clave )!! }
    val tenDescricion = actividade.textos.isNotEmpty()

    var fichaAberta by rememberSaveable { mutableStateOf( !tenDescricion ) }
    val textoFicha = if ( fichaAberta ) L10nActividades.OcultarFicha else L10nActividades.AmosarFicha

    Column( modifier = Modifier.fillMaxSize().verticalScroll( rememberScrollState() ) ) {

        if ( tenDescricion ) {

            EspazadorAlto( 2 )

            Box( Modifier.fillMaxWidth(), contentAlignment = Alignment.Center ) {
                BotonSecundario( textoFicha, { fichaAberta = !fichaAberta } )
            }

        }

        if ( fichaAberta ) {
            EspazadorAlto( 2 )
            FichaActividade( actividade )
        }

    }

}

/**
 * Mostra a ficha cos datos xerais dunha actividade.
 *
 * A ficha inclúe a categoría, o destinatario, a duración e os parágrafos da descrición.
 * Os datos preséntanse dentro dunha tarxeta e os textos da descrición móstranse por separado.
 *
 * @param actividade Actividade da que se quere mostrar a información.
 */
@Composable
private fun FichaActividade( actividade: Actividade ) {

    Card( Modifier.fillMaxWidth().padding( vertical = 4.dp ) ) {

        Column( Modifier.fillMaxWidth().padding( horizontal = 16.dp, vertical = 12.dp ) ) {

            FilaFicha( L10nActividades.Categoria, actividade.categoria.nome )
            EspazadorAlto()
            FilaFicha( L10nActividades.Destinatario, actividade.destinatario.nome )
            EspazadorAlto()
            FilaFicha( L10nActividades.Duracion, L10nPlural.Duracion, actividade.duracion )
            EspazadorAlto( 3 )

            actividade.descricion.forEach { paragrafo ->
                Text( paragrafo, style = MaterialTheme.typography.bodyLarge )
                EspazadorAlto( 2 )
            }

        }

    }

}

/**
 * Mostra unha fila da ficha a partir dunha etiqueta e dun valor singular.
 *
 * Ambos os elementos son traducidos antes de delegar á representación visual en
 * [FilaFichaRender].
 *
 * @param etiqueta Etiqueta localizada que identifica o dato.
 * @param valor Valor localizado que se quere mostrar.
 */
@Composable
private fun FilaFicha( etiqueta: L10nSingular, valor: L10nSingular ) {
    FilaFichaRender( etiqueta.texto(), valor.texto() )
}

/**
 * Mostra unha fila da ficha cun valor plural que depende dunha cantidade.
 *
 * A tradución do valor realízase tendo en conta a cantidade indicada, para seleccionar
 * a forma gramatical correspondente.
 *
 * @param etiqueta Etiqueta localizada que identifica o dato.
 * @param valor Texto plural localizado que se quere mostrar.
 * @param cantidade Cantidade utilizada para obter a forma plural adecuada.
 */
@Composable
private fun FilaFicha( etiqueta: L10nSingular, valor: L10nPlural, cantidade: Number ) {
    FilaFichaRender( etiqueta.texto(), valor.texto( cantidade ) )
}

/**
 * Representa visualmente unha fila da ficha a partir de dous textos xa localizados.
 *
 * Combina a etiqueta e o valor utilizando o formato definido en [L10nBase.FormatoFicha].
 * A etiqueta resáltase cun estilo tipográfico en negra e co tamaño correspondente ao título
 * grande do tema. Se non se atopa a etiqueta no texto formatado, utilízase unha representación
 * alternativa coa etiqueta e o valor separados por dous puntos.
 *
 * @param textoEtiqueta Texto da etiqueta que identifica o dato.
 * @param textoValor Texto co valor correspondente á etiqueta.
 */
@Composable
private fun FilaFichaRender( textoEtiqueta: String, textoValor: String ) {

    val estilo = SpanStyle(
        fontFamily = MaterialTheme.typography.titleLarge.fontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = MaterialTheme.typography.titleLarge.fontSize
    )

    val formato = L10nBase.FormatoFicha.texto()
    val textoCompleto = String.format( formato, textoEtiqueta, textoValor )

    val texto = buildAnnotatedString {

        val inicio = textoCompleto.indexOf( textoEtiqueta )

        if ( inicio >= 0 ) {
            append( textoCompleto.substring( 0, inicio ) )
            withStyle( estilo ) { append( textoEtiqueta ) }
            append( textoCompleto.substring( inicio + textoEtiqueta.length ) )
        } else {
            // Fallback por si acaso la etiqueta no aparece exacta
            withStyle( estilo ) { append( textoEtiqueta ) }
            append( ": " )
            append( textoValor )
        }

    }

    Text( text = texto, style = MaterialTheme.typography.bodyLarge )

}