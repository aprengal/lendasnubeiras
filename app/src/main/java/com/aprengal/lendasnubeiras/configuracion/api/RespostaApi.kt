package com.aprengal.lendasnubeiras.configuracion.api

import com.aprengal.lendasnubeiras.usuarios.Rol
import com.aprengal.lendasnubeiras.usuarios.Rol.Companion.buscarRol
import org.json.JSONObject

abstract class RespostaApi( val exito: Boolean )

class SesionUsuario( datos: JSONObject ): RespostaApi( datos.optBoolean( "exito" ) ) {

    val sesion: String = datos.optString( "sesion" )

    val idUsuario: Long = datos.optLong( "id" )

    val correo: String = datos.optString( "correo" )

    val rol: Rol = buscarRol( datos.optString( "rol" ) )

    val info: String = "$idUsuario|${ rol.name }"

}

class RespostaXenerica( datos: JSONObject ): RespostaApi( datos.optBoolean( "exito" ) )