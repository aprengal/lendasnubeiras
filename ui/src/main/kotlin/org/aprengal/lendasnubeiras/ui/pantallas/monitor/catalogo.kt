package org.aprengal.lendasnubeiras.ui.pantallas.monitor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import org.aprengal.lendasnubeiras.ui.navegacion.Locais.LocalNavegacion
import org.aprengal.lendasnubeiras.ui.navegacion.Ruta
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.AmosarTituloCentrado
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.BotonPrincipal
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.EspazadorAlto

@Composable
fun PantallaCatalogo() {

    val navegacion = LocalNavegacion.current

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

        items( actividades ) { actividade ->

            Card( Modifier.fillMaxWidth().padding( vertical = 4.dp ) ) {

                val modificadorActividade = Modifier.fillMaxWidth().clickable {
                    navegacion.engadir( Ruta.ActividadeDetalle( actividade.claveTitulo ) )
                }.padding( horizontal = 16.dp, vertical = 12.dp )

                Column( modificadorActividade )  {
                    Text( text = actividade.titulo, style = MaterialTheme.typography.titleLarge )
                    EspazadorAlto()
                    Text( text = actividade.resumo, style = MaterialTheme.typography.bodySmall )
                }

            }

            EspazadorAlto()

        }

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