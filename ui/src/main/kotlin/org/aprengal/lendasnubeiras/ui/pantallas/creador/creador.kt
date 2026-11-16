package org.aprengal.lendasnubeiras.ui.pantallas.creador

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.aprengal.lendasnubeiras.ui.reutilizables.estruturas.EstruturaBase

/**
 * Representa a pantalla de creación de actividades.
 *
 * Consérvase como implementación de proba porque o botón de acción do
 * Scaffold de [EstruturaBase] fai referencia a esta pantalla.
 * Deste xeito, evítase que o exemplo falle se o usuario dispón do permiso
 * para editar actividades.
 */
@Composable
fun NovaActividade() {
    Text( "Pantalla de creación de actividades" )
}