package org.aprengal.lendasnubeiras.ui.reutilizables.clases

import androidx.compose.runtime.Composable
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nSingular
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.compostos.ElementoLista

/**
 * Contén os datos necesarios para configurar un elemento de lista [ElementoLista].
 *
 * Permite definir a acción asociada ao elemento, o seu título, unha icona
 * opcional e o contido principal e adicional que se amosará.
 *
 * @property accion Acción executada ao seleccionar o elemento.
 * @property titulo Recurso de localización co título do elemento.
 * @property icona Icona opcional que acompaña o título.
 * @property contido Contido composable principal do elemento.
 * @property contidoExtra Contido composable adicional opcional.
 */
data class DatosElementoLista(
    val accion: () -> Unit,
    val titulo: L10nSingular,
    val icona: Icona? = null,
    val contido: @Composable () -> Unit,
    val contidoExtra: ( @Composable () -> Unit )? = null
)
