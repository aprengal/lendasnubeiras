package org.aprengal.lendasnubeiras.data.api.resposta

import org.json.JSONObject

/**
 * Resposta para o endpoint de actualización e listaxe de actividades ([RutasApi.ACTUALIZAR]).
 * Procesa o vector JSON de actividades e convérteo nunha lista de mapas clave-valor.
 *
 * @property lista Colección de mapas que representan os datos de cada actividade.
 */
class DatosActividades( datos: JSONObject) : RespostaApi( datos.optInt( "codigo" ) ) {

    val lista = collerActividades( datos )

    /**
     * Extrae e transforma o JSONArray de actividades nunha lista plana de atributos.
     */
    private fun collerActividades( datos: JSONObject): List<Map<String, Any>> {

        val actividades = datos.optJSONArray( "actividades" ) ?: return emptyList()

        val saida = ( 0 until actividades.length() ).map { i ->
            val obxecto = actividades.getJSONObject( i )
            obxecto.keys().asSequence().associateWith { clave -> obxecto.get( clave ) }
        }

        return saida

    }

}