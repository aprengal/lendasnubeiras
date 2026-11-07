package org.aprengal.lendasnubeiras.data.actividades.datos.dixitais

import org.aprengal.lendasnubeiras.data.bd.clases.BD.ElementoBD
import org.aprengal.lendasnubeiras.data.bd.clases.Taboa.XOGADORES

/**
 * Representa un xogador dentro da aplicación.
 *
 * Permite identificar individualmente os participantes que poden obter
 * puntuacións nas distintas actividades.
 *
 * Os datos almacénanse na táboa [XOGADORES] da base de datos SQLite.
 *
 * @property id Identificador único do xogador na base de datos.
 * @property nome Nome do xogador.
 */
data class Xogador(
    val id: Long,
    val nome: String
): ElementoBD