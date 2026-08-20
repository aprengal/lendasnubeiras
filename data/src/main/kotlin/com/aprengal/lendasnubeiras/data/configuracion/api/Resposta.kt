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

class DatosActividades( datos: JSONObject ): RespostaApi( datos.optInt( "codigo" ) ) {

    val lista: List<Map<String, Any>>

    init {

        val actividades = datos.getJSONArray( "actividades" )

        lista = ( 0 until actividades.length() ).map { i ->
            val obxecto = actividades.getJSONObject( i )
            actividades.getJSONObject( i ).keys().asSequence().associateWith { clave -> obxecto.get( clave ) }
        }

    }

}