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

    val lista = collerActividades( datos )

    private fun collerActividades( datos: JSONObject ): List<Map<String, Any>> {

        //TODO: se a lista está baleira ou non está especificada, habería que mandar un erro?
        val actividades = datos.optJSONArray( "actividades" ) ?: return emptyList()

        val saida = ( 0 until actividades.length() ).map { i ->
            val obxecto = actividades.getJSONObject( i )
            obxecto.keys().asSequence().associateWith { clave -> obxecto.get( clave ) }
        }

        return saida

    }

}