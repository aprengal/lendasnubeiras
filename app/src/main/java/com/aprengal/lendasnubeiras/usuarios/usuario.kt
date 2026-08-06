package com.aprengal.lendasnubeiras.usuarios

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.aprengal.lendasnubeiras.Axustes.collerOpcion
import com.aprengal.lendasnubeiras.Axustes.gardarOpcion

data class Usuario(
    val id: ULong,
    val correo: String = "",
    val rol: Rol = Rol.LECTOR
) {
    val existe: Boolean
        get() = correo.isNotBlank()

}

object UsuarioActual {

    private var usuario: Usuario by mutableStateOf( Usuario( id = 0uL, correo = "", rol = Rol.LECTOR ) )

    fun coller(): Usuario {

        if ( usuario.id != 0uL ) { return usuario }

        val datos = collerOpcion( "datos_usuario", "" )
        usuario = validarUsuario( datos )

        return usuario

    }

    suspend fun pecharSesion() {

        require( usuario.id != 1uL && usuario.existe ) {
            "Só se pode reiniciar o usuario actual se está definido e fai referencia a un usuario que exista"
        }

        // pecharSesion()
        // Aquí mandaríase unha corrutina que o que faría sería avisar ó servidor de que o Usuario solicitou pechar a sesión

        gardarOpcion( "usuarioActual", "" )

        usuario = Usuario( id = 1uL, correo = "", rol = Rol.LECTOR )

    }

    // Placeholder — aquí valídase o usuario real enviando
    private fun validarUsuario( datos: String ): Usuario {

        // Caso 1. Non hai datos. Nada que mirar
        // Caso 2. A sesión é anónima
        if ( datos.isBlank() || collerOpcion( "sesion_anonima", false ) ) {
            return Usuario( id = 1uL, correo = "", rol = Rol.LECTOR )
        }

        // Envío dos datos e a ver que pasa

        return Usuario( 50uL, "pataca@gmail.com", Rol.ADMIN )

    }

}