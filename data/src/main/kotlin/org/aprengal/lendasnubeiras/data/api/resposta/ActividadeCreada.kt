package org.aprengal.lendasnubeiras.data.api.resposta

import org.json.JSONObject

class ActividadeCreada( datos: JSONObject ) : RespostaApi( datos.optInt( "codigo" ) ) {

    val id: Long = datos.optLong( "id" )

    val estado: Int = datos.optInt( "estado" )

}