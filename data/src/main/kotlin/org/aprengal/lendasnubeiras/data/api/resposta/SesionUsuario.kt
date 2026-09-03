package org.aprengal.lendasnubeiras.data.api.resposta

import org.json.JSONObject

class SesionUsuario( datos: JSONObject) : RespostaApi( datos.optInt( "codigo" ) ) {

    val sesion: String = datos.optString( "sesion" )

    val idUsuario: Long = datos.optLong( "id" )

    val correo: String = datos.optString( "correo" )

    val rol: String = datos.optString( "rol" )

    val info: String = "$idUsuario|${ rol }"

}