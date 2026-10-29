package org.aprengal.lendasnubeiras.data.api.resposta

import org.json.JSONObject

/**
 * Resposta específica para o endpoint de creación dunha actividade ([RutasApi.CREARACTIVIDADE]).
 *
 * @property id Identificador único da actividade creada.
 * @property estado Estado actual da actividade tras a creación.
 */
class ActividadeCreada( datos: JSONObject ) : RespostaApi( datos.optInt( "codigo" ) ) {

    val id: Long = datos.optLong( "id" )

    val estado: Int = datos.optInt( "estado" )

}