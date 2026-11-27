package org.aprengal.lendasnubeiras.data.api.resposta

import android.util.Patterns.EMAIL_ADDRESS
import org.json.JSONObject
import org.aprengal.lendasnubeiras.data.api.RutasApi
import org.aprengal.lendasnubeiras.data.usuarios.Rol
import org.aprengal.lendasnubeiras.data.usuarios.Rol.Companion.buscarRol

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
 */
class RespostaValidacionSesion( datos: JSONObject ) : RespostaApi( datos.optInt( "codigo" ) ) {

    val idUsuario: Long = datos.optLong( "id" )

    val correo: String = datos.optString( "correo" )

    val rol: Rol = if ( correo.isNotBlank() && EMAIL_ADDRESS.matcher( correo ).matches() ) Rol.MONITOR else Rol.LECTOR

}