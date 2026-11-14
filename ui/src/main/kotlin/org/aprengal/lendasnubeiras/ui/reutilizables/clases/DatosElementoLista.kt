package org.aprengal.lendasnubeiras.ui.reutilizables.clases

import androidx.compose.runtime.Composable
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nSingular
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.compostos.ElementoLista

/**
 * Contén os datos necesarios para configurar un elemento de lista [ElementoLista].
 *
 * Permite definir o título, o contido que se amosa debaixo del, a acción asociada,
 * unha icona opcional e un contido adicional opcional.
 *
 * Se non se indica ningunha acción, o elemento só amosa información e non é pulsable.
 * Se se indica, o elemento é pulsable e identifícase como un botón.
 *
 * @property titulo Recurso de localización co título do elemento.
 * @property contido Contido composable que se amosa debaixo do título.
 * @property accion Acción opcional que se executa ao premer o elemento. Se é null, o elemento non é pulsable.
 * @property icona Icona opcional que se amosa á esquerda do elemento.
 * @property contidoExtra Contido composable opcional que se amosa á dereita do elemento.
 */
data class DatosElementoLista(
    val titulo: L10nSingular,
    val contido: @Composable () -> Unit,
    val accion: ( () -> Unit )? = null,
    val icona: Icona? = null,
    val contidoExtra: ( @Composable () -> Unit )? = null
)
