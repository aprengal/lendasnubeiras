package org.aprengal.lendasnubeiras.ui.pantallas.monitor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import org.aprengal.lendasnubeiras.data.actividades.datos.base.Categoria
import org.aprengal.lendasnubeiras.data.actividades.datos.base.Destinatario
import org.aprengal.lendasnubeiras.data.actividades.datos.buscador.CampoBusca
import org.aprengal.lendasnubeiras.data.actividades.datos.buscador.CriteriosBusca
import org.aprengal.lendasnubeiras.data.bd.clases.BD.OrdeBusca
import org.aprengal.lendasnubeiras.data.bd.operacions.Actividades.collerActividadesBuscables
import org.aprengal.lendasnubeiras.data.localizacion.clases.ElementoL10n
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nBase
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.ParametrosBusca
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.BotonPrincipal
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.BotonSecundario
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.CampoBuscador
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.SelectorFiltro
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.Texto
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.EspazadorAlto
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.compostos.TarxetaActividade

/**
 * Composable que presenta a pantalla de busca de actividades, permitindo realizar
 * buscas por texto libre e aplicar filtros adicionais mediante unha busca avanzada.
 *
 * A pantalla ofrece os seguintes criterios de busca:
 * - Texto libre, aplicado ao título, á descrición ou a ambos os campos.
 * - Categoría da actividade.
 * - Destinatario ao que vai dirixida a actividade.
 * - Orde de presentación dos resultados, segundo a súa antigüidade.
 *
 * Os filtros adicionais están inicialmente ocultos e poden amosarse ou ocultarse
 * mediante o botón correspondente. Os valores seleccionados consérvanse mentres
 * non se modifiquen e aplícanse cando se executa unha nova busca.
 *
 * A busca non se realiza automaticamente ao modificar os criterios. É necesario
 * premer o botón de buscar para confirmar os parámetros seleccionados e executar
 * unha nova consulta.
 *
 * Os parámetros da última busca almacénanse mediante [rememberSaveable], de xeito
 * que os resultados e os criterios empregados se manteñen durante a recomposición
 * e a restauración do estado da pantalla.
 *
 * Os resultados obtéñense mediante [collerActividadesBuscables], empregando
 * [CriteriosBusca] para aplicar os filtros, o termo de busca e a ordenación.
 * A busca por texto libre realízase sobre os campos indicados en [CampoBusca].
 *
 * A presentación dos resultados incorpora un sistema de carga progresiva:
 * - Inicialmente solicítanse 11 resultados, pero só se amosan os 10 primeiros.
 * - Solicítase sempre un resultado adicional ao número que se pretende amosar,
 *   permitindo determinar se existen máis actividades sen necesidade de coñecer
 *   o número total de coincidencias.
 * - Se existen máis resultados dos que se están amosando, aparece o botón
 *   de cargar máis.
 * - Cada vez que se preme ese botón, increméntase en 10 o número de resultados
 *   que se amosan e actualízase a consulta para obter os resultados adicionais.
 * - Cando se executa unha nova busca, o número de resultados amosados volve
 *   ao valor inicial de 10.
 *
 * Se a busca non devolve ningunha actividade, amósase a mensaxe correspondente
 * de ausencia de resultados. Se existen máis coincidencias das que se están
 * amosando, ofrécese a posibilidade de continuar cargándoas.
 *
 * O teclado virtual ocúltase ao executar unha busca e a sección de busca avanzada
 * péchase automaticamente, facilitando a visualización dos resultados.
 *
 * A función non recibe parámetros nin devolve ningún valor, xa que toda a súa
 * interacción e presentación se xestionan mediante o estado interno de Compose.
 */
@Composable
fun PantallaBuscador() {

    val teclado = LocalSoftwareKeyboardController.current
    val busca = rememberTextFieldState()
    val limitePorPaxina = 10

    var avanzada by rememberSaveable { mutableStateOf( false ) }
    var campoBusca by rememberSaveable { mutableStateOf( CampoBusca.Ambos ) }
    var categoria by rememberSaveable { mutableStateOf<Categoria?>( null ) }
    var destinatario by rememberSaveable { mutableStateOf<Destinatario?>( null ) }
    var orde by rememberSaveable { mutableStateOf( OrdeBusca.Recentes ) }
    var parametros by rememberSaveable { mutableStateOf<ParametrosBusca?>( null ) }

    //Cada nova busca (novos parámetros) volve a pór o límite en 10
    var totalCargados by rememberSaveable( parametros ) { mutableIntStateOf( limitePorPaxina ) }

    val resultados = remember( parametros, totalCargados ) {

        parametros?.let { datos ->

            val filtros = buildMap<String, ElementoL10n> {
                datos.categoria?.let { categoria -> put( "id_categoria", categoria ) }
                datos.destinatario?.let { destinatario -> put( "id_destinatario", destinatario ) }
            }

            //Pídese unha actividade máis ca as que se amosan: se chega, sábese que hai máis sen contalas
            val criterios = CriteriosBusca(
                colOrdenable = "id", dirOrdenable = datos.orde.orde, filtros = filtros,
                termo = datos.termo, campoBusca = datos.campoBusca, limite = totalCargados + 1
            )

            collerActividadesBuscables( criterios )

        }

    }

    val amosadas = resultados?.take( totalCargados ).orEmpty()
    val haiMais = ( resultados?.size ?: 0 ) > totalCargados

    val buscar = {

        if ( busca.text.isNotBlank() ) {
            teclado?.hide()
            avanzada = false
            parametros = ParametrosBusca(busca.text.toString(), campoBusca, categoria, destinatario, orde)
        }

    }

    val textoAvanzada = if ( avanzada ) L10nBase.OcultarBuscaAvanzada else L10nBase.BuscaAvanzada

    LazyColumn( Modifier.fillMaxSize() ) {

        item {

            EspazadorAlto( 2 )
            CampoBuscador( busca, buscar )
            EspazadorAlto( 2 )

            Box( Modifier.fillMaxWidth(), contentAlignment = Alignment.Center ) {
                BotonSecundario( textoAvanzada, { avanzada = !avanzada } )
            }

        }

        if ( avanzada ) {

            item {

                EspazadorAlto( 2 )

                Card( Modifier.fillMaxWidth().padding( vertical = 4.dp ) ) {

                    Column( Modifier.fillMaxWidth() ) {

                        SelectorFiltro( L10nBase.FiltroBuscarEn, CampoBusca.entries, campoBusca, null ) {
                            escollido -> campoBusca = escollido ?: CampoBusca.Ambos
                        }

                        HorizontalDivider()

                        SelectorFiltro( L10nBase.FiltroCategoria, Categoria.entries, categoria, L10nBase.FiltroTodas ) {
                            escollido -> categoria = escollido
                        }

                        HorizontalDivider()

                        SelectorFiltro( L10nBase.FiltroDestinatario, Destinatario.entries, destinatario, L10nBase.FiltroTodos ) {
                            escollido -> destinatario = escollido
                        }

                        HorizontalDivider()

                        SelectorFiltro( L10nBase.FiltroOrde, OrdeBusca.entries, orde, null ) {
                            escollido -> orde = escollido ?: OrdeBusca.Recentes
                        }

                    }

                }

            }

        }

        item {
            EspazadorAlto( 2 )
            BotonPrincipal( L10nBase.BotonBuscar, buscar, Modifier.fillMaxWidth(), busca.text.isNotBlank() )
            EspazadorAlto()
        }

        resultados?.let {

            if ( amosadas.isEmpty() ) {
                item {
                    EspazadorAlto()
                    Texto( L10nBase.SenResultados )
                }
            }

            items( amosadas ) { actividade -> TarxetaActividade( actividade ) }

            if ( haiMais ) {

                item {

                    val accion = { totalCargados += limitePorPaxina }

                    EspazadorAlto()
                    BotonPrincipal( L10nBase.CargarMais, accion, Modifier.fillMaxWidth() )
                    EspazadorAlto()

                }

            }

        }

    }

}