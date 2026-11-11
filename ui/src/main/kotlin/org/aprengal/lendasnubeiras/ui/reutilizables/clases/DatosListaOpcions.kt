package org.aprengal.lendasnubeiras.ui.reutilizables.clases

import org.aprengal.lendasnubeiras.data.localizacion.clases.Localizacion.obterTexto
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.compostos.ListaOpcions

/**
 * Contén os datos necesarios para configurar unha lista de opcións [ListaOpcions].
 *
 * Permite proporcionar as opcións dispoñibles, identificar a opción seleccionada
 * e definir a acción que se executa ao escoller outra opción.
 *
 * @param T Tipo de dato das opcións.
 * @property opcions Lista de opcións dispoñibles.
 * @property escollido Opción seleccionada actualmente.
 * @property escoller Acción executada ao seleccionar unha opción.
 * @property texto Función que devolve o texto de cada opción.
 */
data class DatosListaOpcions<T>(
    val opcions: List<T>,
    val escollido: T,
    val escoller: ( T ) -> Unit,
    val texto: ( T ) -> String = { opcion -> obterTexto( opcion ) }
)