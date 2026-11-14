package org.aprengal.lendasnubeiras.ui.pantallas.monitor.actividadesDixitais

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.aprengal.lendasnubeiras.data.actividades.datos.base.Actividade

@Composable
fun PrepararXogo( actividade: Actividade ) {

    Text( "Preparando xogo de... ${actividade.titulo}" )

}