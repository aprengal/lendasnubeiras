package org.aprengal.lendasnubeiras.data.actividades.datos.dixitais

import org.aprengal.lendasnubeiras.data.bd.clases.BD.ElementoBD
import org.aprengal.lendasnubeiras.data.bd.clases.Taboa.GRUPOS

/**
 * Representa un grupo de xogadores.
 *
 * Permite agrupar xogadores baixo un mesmo nome, facilitando a súa
 * organización e identificación dentro da aplicación.
 *
 * Os datos almacénanse na táboa [GRUPOS] da base de datos SQLite.
 *
 * @property id Identificador único do grupo na base de datos.
 * @property nome Nome do grupo.
 */
data class Grupo(
    val id: Long,
    val nome: String
): ElementoBD