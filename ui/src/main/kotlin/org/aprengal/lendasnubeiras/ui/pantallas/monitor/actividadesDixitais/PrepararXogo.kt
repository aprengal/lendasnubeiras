package org.aprengal.lendasnubeiras.ui.pantallas.monitor.actividadesDixitais

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import org.aprengal.lendasnubeiras.data.actividades.datos.dixitais.Dificultade
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nActividades
import org.aprengal.lendasnubeiras.ui.navegacion.Locais.LocalNavegacion
import org.aprengal.lendasnubeiras.ui.navegacion.Ruta
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.BotonPrincipal
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.BotonSecundario
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.SelectorFiltro
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.EspazadorAlto

/**
 * Mostra a configuración previa dun xogo, que se pode despregar ou ocultar mediante un botón:
 * a dificultade, coa súa descrición, e o botón para comezar.
 *
 * As dificultades dispoñibles son as que ten a actividade ([Actividade.dificultades]). Se só hai unha,
 * non se amosa o selector. O botón de comezar está dentro da configuración e leva á pantalla do xogo
 * coa dificultade escollida; polo tanto, cando a configuración está oculta, non se pode comezar.
 *
 * @param actividade Actividade da que se prepara o xogo.
 */
@Composable
fun PrepararXogo( actividade: Actividade ) {

    val navegacion = LocalNavegacion.current
    val opcions = remember( actividade ) { actividade.dificultades.keys.sortedBy { dificultade -> dificultade.ordinal } }
    var dificultade by rememberSaveable { mutableStateOf( opcions.firstOrNull() ?: Dificultade.Normal ) }
    var configuracionAberta by rememberSaveable { mutableStateOf( true ) }

    val comezar = { navegacion.engadir( Ruta.Xogo( actividade.claveTitulo, dificultade.clave ) ) }
    val textoConfiguracion = if ( configuracionAberta ) L10nActividades.OcultarConfiguracion else L10nActividades.AmosarConfiguracion

    EspazadorAlto( 2 )

    Box( Modifier.fillMaxWidth(), contentAlignment = Alignment.Center ) {
        BotonSecundario( textoConfiguracion, { configuracionAberta = !configuracionAberta } )
    }

    if ( configuracionAberta ) {

        EspazadorAlto( 2 )

        Card( Modifier.fillMaxWidth().padding( vertical = 4.dp ) ) {

            Column( Modifier.fillMaxWidth() ) {

                if ( opcions.size > 1 ) {
                    SelectorFiltro( L10nActividades.Dificultade, opcions, dificultade, null ) { escollido -> dificultade = escollido ?: dificultade }
                    HorizontalDivider()
                }

                actividade.dificultades[ dificultade ]?.let { descricionDificultade ->
                    Text( descricionDificultade, Modifier.padding( horizontal = 16.dp, vertical = 12.dp ), style = MaterialTheme.typography.bodyLarge )
                }

                BotonPrincipal( L10nActividades.Xogar, comezar, Modifier.fillMaxWidth().padding( horizontal = 16.dp, vertical = 12.dp ) )

            }

        }

    }

    EspazadorAlto( 2 )

}