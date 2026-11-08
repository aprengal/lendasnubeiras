package org.aprengal.lendasnubeiras.ui.reutilizables.clases

import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nOpcions

/**
 * Contén os datos necesarios para configurar unha opción na páxina de axustes.
 *
 * Define a súa identificación, información visual, tipo de control, valor inicial
 * e acción de almacenamento, así como os datos adicionais necesarios segundo
 * o tipo de opción.
 *
 * @param T Tipo de dato do valor da opción.
 * @property clave Identificador único da opción.
 * @property titulo Recurso de localización co título da opción.
 * @property gardadoFallido Mensaxe que se amosa cando falla o almacenamento do valor.
 * @property tipo Tipo de control utilizado para representar a opción.
 * @property icona Icona asociada á opción.
 * @property valorInicial Valor inicial da opción.
 * @property accion Acción suspendida que almacena un novo valor e indica se a operación se completou correctamente.
 * @property tituloAlerta Título opcional do diálogo de alerta asociado á opción.
 * @property opcions Lista opcional de valores dispoñibles para a opción.
 * @property descricionExtra Descrición adicional opcional da opción.
 */
data class DatosOpcion<T>(
    val clave: String,
    val titulo: L10nOpcions,
    val gardadoFallido: L10nOpcions,
    val tipo: TipoOpcion,
    val icona: Icona,
    val valorInicial: T,
    val accion: suspend ( T ) -> Boolean,
    val tituloAlerta: L10nOpcions? = null,
    val opcions: List<T>? = null,
    val descricionExtra: L10nOpcions? = null,
)