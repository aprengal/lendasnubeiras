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

@Composable
fun PantallaBuscador() {

    val teclado = LocalSoftwareKeyboardController.current
    val busca = rememberTextFieldState()

    var avanzada by rememberSaveable { mutableStateOf( false ) }
    var campoBusca by rememberSaveable { mutableStateOf( CampoBusca.Ambos ) }
    var categoria by rememberSaveable { mutableStateOf<Categoria?>( null ) }
    var destinatario by rememberSaveable { mutableStateOf<Destinatario?>( null ) }
    var parametros by rememberSaveable { mutableStateOf<ParametrosBusca?>( null ) }

    val resultados = remember( parametros ) {

        parametros?.let { datos ->

            val filtros = buildMap<String, ElementoL10n> {
                datos.categoria?.let { categoria -> put( "id_categoria", categoria ) }
                datos.destinatario?.let { destinatario -> put( "id_destinatario", destinatario ) }
            }

            val criterios = CriteriosBusca( colOrdenable = "id", filtros = filtros, termo = datos.termo, campoBusca = datos.campoBusca )
            collerActividadesBuscables( criterios )

        }

    }

    val buscar = {
        teclado?.hide()
        avanzada = false
        parametros = ParametrosBusca( busca.text.toString(), campoBusca, categoria, destinatario )
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

                    }

                }

            }

        }

        item {
            EspazadorAlto( 2 )
            BotonPrincipal( L10nBase.BotonBuscar, buscar, Modifier.fillMaxWidth() )
            EspazadorAlto()
        }

        resultados?.let { lista ->

            if ( lista.isEmpty() ) {
                item {
                    EspazadorAlto()
                    Texto( L10nBase.SenResultados )
                }
            }

            items( lista ) { actividade -> TarxetaActividade( actividade ) }

        }

    }

}