package org.aprengal.lendasnubeiras.data.api.resposta

import org.json.JSONObject
import org.aprengal.lendasnubeiras.data.api.RutasApi

/**
 * Representa a resposta da API a unha solicitude de inicio de sesión ([RutasApi.ACCESO]).
 *
 * Contén o identificador da sesión devolto polo servidor, necesario
 * para continuar co proceso de autenticación.
 *
 * @property sesion Token ou identificador da sesión devolto pola API.
 */
class RespostaAcceso( datos: JSONObject ) : RespostaApi( datos.optInt( "codigo" ) ) {
    val sesion: String = datos.optString( "sesion" )
}