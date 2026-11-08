package org.aprengal.lendasnubeiras.ui.reutilizables.clases

import androidx.compose.runtime.Composable
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nSingular
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.compostos.AlertaDialogo

/**
 * Contén os datos e as accións necesarios para configurar un diálogo de alerta [AlertaDialogo].
 *
 * Permite definir o título, o contido opcional, a icona e as accións
 * asociadas á cancelación, ao descarte e á confirmación do diálogo.
 *
 * @property titulo Recurso de localización co título do diálogo.
 * @property cancelado Acción executada ao cancelar o diálogo.
 * @property descartado Acción executada ao descartar o diálogo. Por defecto,
 *                      utiliza a mesma acción que [cancelado].
 * @property contido Contido composable opcional que se amosa no diálogo.
 * @property confirmado Acción opcional executada ao confirmar o diálogo.
 * @property icona Icona opcional que acompaña o título do diálogo.
 */
data class DatosAlerta(
    val titulo: L10nSingular,
    val cancelado: () -> Unit,
    val descartado: () -> Unit = cancelado,
    val contido: ( @Composable () -> Unit )? = null,
    val confirmado: ( () -> Unit )? = null,
    val icona: Icona? = null
)
