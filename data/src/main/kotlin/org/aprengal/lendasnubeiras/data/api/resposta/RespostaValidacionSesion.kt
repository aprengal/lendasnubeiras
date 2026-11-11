package org.aprengal.lendasnubeiras.data.api.resposta

import org.json.JSONObject
import org.aprengal.lendasnubeiras.data.api.RutasApi

/**
 * Representa a resposta da API ao validar unha sesión de usuario ([RutasApi.VALIDACION]).
 *
 * Contén a información do usuario asociado á sesión, incluíndo
 * o seu identificador, enderezo de correo electrónico e rol.
 * Tamén proporciona unha cadea cos datos básicos do usuario
 * nun formato compacto.
 *
 * @property idUsuario Identificador único do usuario.
 * @property correo Enderezo de correo electrónico asociado ao usuario.
 * @property rol Rol asignado ao usuario dentro do sistema.
 * @property info Cadea cos datos do usuario no formato "idUsuario|rol".
 */
class RespostaValidacionSesion( datos: JSONObject ) : RespostaApi( datos.optInt( "codigo" ) ) {

    val idUsuario: Long = datos.optLong( "id" )

    val correo: String = datos.optString( "correo" )

    val rol: String = datos.optString( "rol" )

    val info: String = "$idUsuario|${ rol }"

}