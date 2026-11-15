package org.aprengal.lendasnubeiras.ui.pantallas.monitor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.unit.dp
import org.aprengal.lendasnubeiras.data.actividades.datos.base.Actividade
import org.aprengal.lendasnubeiras.data.actividades.datos.base.TipoLista
import org.aprengal.lendasnubeiras.ui.navegacion.Ruta
import org.aprengal.lendasnubeiras.data.bd.operacions.Actividades.collerActividade
import org.aprengal.lendasnubeiras.data.localizacion.cantidades.L10nPlural
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nActividades
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nSingular
import org.aprengal.lendasnubeiras.ui.pantallas.monitor.actividadesDixitais.PrepararXogo
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.DatosElementoLista
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.BotonSecundario
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.EspazadorAlto
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.compostos.ElementoLista
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.compostos.ListaTexto

/**
 * Mostra a pantalla co detalle dunha actividade, permitindo consultar a súa ficha de datos xerais.
 *
 * Se a actividade contén textos adicionais, permite despregar ou ocultar a ficha mediante un botón.
 * Se non contén textos adicionais, a ficha móstrase directamente.
 *
 * A existencia da actividade compróbase antes de acceder a esta pantalla, polo que se dá por feito
 * que [collerActividade] devolve unha actividade válida.
 *
 * @param clave Clave identificadora da actividade que se vai mostrar, correspondente á propiedade
 * clave de [Ruta.ActividadeDetalle].
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

        if ( tenDescricion ) {
            PrepararXogo( actividade )
        }

    }

}

/**
 * Mostra unha tarxeta coa información xeral dunha actividade.
 *
 * Inclúe a súa categoría, o destinatario e a duración, organizados en filas separadas por divisores.
 * A continuación, mostra os parágrafos da descrición e, se existe, unha lista de elementos que pode
 * incluír unha cabeceira.
 *
 * A duración obtense mediante [L10nPlural.Duracion], adaptando o texto ao valor correspondente
 * para empregar a forma singular ou plural axeitada.
 *
 * @param actividade Actividade da que se quere mostrar a información.
 */
@Composable
private fun FichaActividade( actividade: Actividade ) {

    val duracionTexto = L10nPlural.Duracion.texto( actividade.duracion )

    Card( Modifier.fillMaxWidth().padding( vertical = 4.dp ) ) {

        Column( Modifier.fillMaxWidth() ) {

            FilaFicha( L10nActividades.Categoria, actividade.categoria.nome.texto() )
            HorizontalDivider()
            FilaFicha( L10nActividades.Destinatario, actividade.destinatario.nome.texto() )
            HorizontalDivider()
            FilaFicha( L10nActividades.Duracion, duracionTexto )
            HorizontalDivider()

            Column( Modifier.fillMaxWidth().padding( horizontal = 16.dp, vertical = 12.dp ) ) {

                actividade.descricion.forEach { paragrafo ->
                    Text( paragrafo, style = MaterialTheme.typography.bodyLarge )
                    EspazadorAlto( 2 )
                }

                actividade.lista?.let { lista ->

                    Text( lista.cabeceira )
                    EspazadorAlto( 1 )

                    val ordenada = lista.tipo == TipoLista.ORDENADA
                    ListaTexto( lista.elementos, ordenada )

                }

            }

        }

    }

}

/**
 * Mostra unha fila informativa dunha ficha, composta por unha etiqueta e o seu valor.
 *
 * Utiliza [ElementoLista] sen acción asociada, polo que presenta a información sen permitir
 * interaccións coa fila.
 *
 * O valor recíbese como texto xa localizado, polo que pode representar distintos tipos de datos.
 *
 * @param etiqueta Etiqueta localizada que identifica o dato.
 * @param valor Texto do valor, xa localizado.
 */
@Composable
private fun FilaFicha( etiqueta: L10nSingular, valor: String ) {
    ElementoLista( DatosElementoLista( titulo = etiqueta, contido = { Text( valor ) } ) )
}