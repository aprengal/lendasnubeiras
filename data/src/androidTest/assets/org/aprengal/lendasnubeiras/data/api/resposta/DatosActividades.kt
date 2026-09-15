package org.aprengal.lendasnubeiras.data.api.resposta

import org.json.JSONObject

class DatosActividades( datos: JSONObject) : RespostaApi( datos.optInt( "codigo" ) ) {

    val lista = collerActividades( datos )

    private fun collerActividades( datos: JSONObject): List<Map<String, Any>> {

        //TODO: se a lista está baleira ou non está especificada, habería que mandar un erro?
        val actividades = datos.optJSONArray( "actividades" ) ?: return emptyList()

        val saida = ( 0 until actividades.length() ).map { i ->
            val obxecto = actividades.getJSONObject( i )
            obxecto.keys().asSequence().associateWith { clave -> obxecto.get( clave ) }
        }

        return saida

    }

}