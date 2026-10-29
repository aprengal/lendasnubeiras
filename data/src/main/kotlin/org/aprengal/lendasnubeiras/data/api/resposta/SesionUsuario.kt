package org.aprengal.lendasnubeiras.data.api.resposta

import org.json.JSONObject

/**
 * Resposta específica para a xestión de sesións de usuario ([RutasApi.ACCESO]).
 *
 * @property sesion Token de rastro cifrado para as peticións autenticadas.
 * @property idUsuario Identificador único do usuario na base de datos.
 * @property correo Enderezo de correo electrónico asociado.
 * @property rol Rol asignado dentro do sistema.
 * @property info Cadea resumida co formato "idUsuario|rol".
 */

class SesionUsuario( datos: JSONObject) : RespostaApi( datos.optInt( "codigo" ) ) {

    val sesion: String = datos.optString( "sesion" )

    val idUsuario: Long = datos.optLong( "id" )

    val correo: String = datos.optString( "correo" )

    val rol: String = datos.optString( "rol" )

    val info: String = "$idUsuario|${ rol }"

}