package org.aprengal.lendasnubeiras.ui.pantallas.monitor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.aprengal.lendasnubeiras.data.bd.operacions.Actividades.collerActividadesPublicas
import org.aprengal.lendasnubeiras.data.bd.operacions.Actividades.contarActividadesPublicas
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nBase
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.AmosarTituloCentrado
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.BotonPrincipal
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.EspazadorAlto
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.compostos.TarxetaActividade

@Composable
fun PantallaCatalogo() {

    val limitePorPaxina = 10
    val totalActividades by rememberSaveable { mutableIntStateOf( contarActividadesPublicas() ) }
    var totalCargados by rememberSaveable { mutableIntStateOf( limitePorPaxina.coerceAtMost( totalActividades ) ) }

    val actividades = remember( totalCargados ) { collerActividadesPublicas( 0, totalCargados ) }
    val haiMais = totalCargados < totalActividades

    LazyColumn( Modifier.fillMaxSize() ) {

        item {

            Column( Modifier.fillMaxWidth().padding( top = 10.dp ) ) {
                AmosarTituloCentrado()
                EspazadorAlto()
            }

        }

        items( actividades ) { actividade -> TarxetaActividade( actividade ) }

        if ( haiMais ) {

            item {

                val accion = { totalCargados = ( totalCargados + limitePorPaxina ).coerceAtMost( totalActividades ) }

                EspazadorAlto()
                BotonPrincipal( L10nBase.CargarMais, accion, Modifier.fillMaxWidth() )
                EspazadorAlto()

            }

        }

    }

}