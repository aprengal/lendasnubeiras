package org.aprengal.lendasnubeiras.data.api.resposta

import org.json.JSONObject
import org.aprengal.lendasnubeiras.data.api.RutasApi

/**
 * Representa a resposta da API tras crear unha actividade ([RutasApi.CREARACTIVIDADE]).
 *
 * Ademais do código de resposta herdado de [RespostaApi], contén
 * os datos identificativos e o estado inicial da actividade creada.
 *
 * @property id Identificador único da actividade creada.
 * @property estado Estado asignado á actividade tras a súa creación.
 */
class ActividadeCreada( datos: JSONObject ) : RespostaApi( datos.optInt( "codigo" ) ) {
    val id: Long = datos.optLong( "id" )
    val estado: Int = datos.optInt( "estado" )
}