package com.aprengal.lendasnubeiras.data.configuracion.api

import org.json.JSONObject

abstract class RespostaApi( val codigo: Int ) {
    val exito: Boolean = codigo in 200..299
}

class SesionUsuario( datos: JSONObject ): RespostaApi( datos.optInt( "codigo" ) ) {

    val sesion: String = datos.optString( "sesion" )

    val idUsuario: Long = datos.optLong( "id" )

    val correo: String = datos.optString( "correo" )

    val rol: String = datos.optString( "rol" )

    val info: String = "$idUsuario|${ rol }"

}

class RespostaXenerica( datos: JSONObject ): RespostaApi( datos.optInt( "codigo" ) )