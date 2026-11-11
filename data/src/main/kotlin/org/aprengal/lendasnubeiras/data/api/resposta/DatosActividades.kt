package org.aprengal.lendasnubeiras.data.api.resposta

import org.json.JSONObject
import org.aprengal.lendasnubeiras.data.api.RutasApi

/**
 * Representa a resposta da API que contén información sobre as actividades ([RutasApi.ACTUALIZAR]).
 *
 * Extrae o array JSON de actividades e transforma cada un dos seus elementos
 * nun mapa de pares clave-valor, almacenándoos nunha lista.
 *
 * @property lista Lista de mapas cos atributos das actividades recibidas.
 *                 Se a resposta non contén o array de actividades,
 *                 devolve unha lista baleira.
 */
class DatosActividades( datos: JSONObject ) : RespostaApi( datos.optInt( "codigo" ) ) {

    val lista = collerActividades( datos )

    /**
     * Extrae e transforma o JSONArray de actividades nunha lista plana de atributos.
     */
    private fun collerActividades( datos: JSONObject ): List<Map<String, Any>> {

        val actividades = datos.optJSONArray( "actividades" ) ?: return emptyList()

        val saida = ( 0 until actividades.length() ).map { i ->
            val obxecto = actividades.getJSONObject( i )
            obxecto.keys().asSequence().associateWith { clave -> obxecto.get( clave ) }
        }

        return saida

    }

}