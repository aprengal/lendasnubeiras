package com.aprengal.lendasnubeiras.usuarios

import com.aprengal.lendasnubeiras.configuracion.Axustes.collerOpcion
import com.aprengal.lendasnubeiras.configuracion.Opcion.SesionAnonima
import com.aprengal.lendasnubeiras.usuarios.Rol.Companion.buscarRol

data class Usuario(
    val id: Long,
    val correo: String,
    val rol: Rol
) {

    val existe = this.rol != Rol.NADA && correo.isNotBlank()

}

enum class Rol {

    NADA,
    LECTOR,
    COLABORADOR, //vaise permitir a colaboración de persoas alleas a Cruz Vermella?
    AUTOR,
    EDITOR,
    ADMIN;

    private val nome = name.lowercase()

    companion object {

        fun buscarRol( clave: String ): Rol {
            return entries.find { elemento -> elemento.nome == clave } ?: NADA
        }

    }

}

//Isto só valida o rol. A acción debe revisarse a nivel de servidor
object Permisos {

    //Permisos mira os permisos do usuario pasado como argumento
    //Pero en PecharSesion só mira o valor global dunha opción

    //Isto é un problema ou é unha opción para que un usuario anónimo poida cambiar de anónimo a iniciar sesión?

    fun podeRexistrarse( usuario: Usuario ): Boolean {
        return usuario.rol == Rol.NADA
    }

    fun podeIniciarSesion( usuario: Usuario ): Boolean {
        return usuario.rol == Rol.NADA
    }

    fun podePecharSesion( usuario: Usuario ): Boolean {
        return usuario.existe || collerOpcion( SesionAnonima )
    }

    fun podeLer( usuario: Usuario ): Boolean {
        return usuario.rol != Rol.NADA
    }

    fun podeBorrarConta( usuario: Usuario ): Boolean {
        return usuario.existe && usuario.rol != Rol.ADMIN
    }

    fun podeCrear( usuario: Usuario ): Boolean {
        return usuario.rol in listOf( Rol.COLABORADOR, Rol.AUTOR, Rol.EDITOR, Rol.ADMIN )
    }

    fun podeDescartar( usuario: Usuario ): Boolean {
        return usuario.rol in listOf( Rol.COLABORADOR, Rol.AUTOR, Rol.EDITOR, Rol.ADMIN )
    }

    fun podeEditar( usuario: Usuario ): Boolean {
        return usuario.rol in listOf( Rol.COLABORADOR, Rol.AUTOR, Rol.EDITOR, Rol.ADMIN )
    }

    fun podePublicar( usuario: Usuario ): Boolean {
        return usuario.rol in listOf( Rol.AUTOR, Rol.EDITOR, Rol.ADMIN )
    }

    fun podeBorrar( usuario: Usuario ): Boolean {
        return usuario.rol in listOf( Rol.AUTOR, Rol.EDITOR, Rol.ADMIN )
    }

    fun podeRestaurar( usuario: Usuario ): Boolean {
        return usuario.rol in listOf( Rol.AUTOR, Rol.EDITOR, Rol.ADMIN )
    }

    fun podeEditarOutras( usuario: Usuario ): Boolean {
        return usuario.rol in listOf( Rol.EDITOR, Rol.ADMIN )
    }

    fun podeBorrarOutras( usuario: Usuario ): Boolean {
        return usuario.rol in listOf( Rol.EDITOR, Rol.ADMIN )
    }

    fun podeRestaurarOutras( usuario: Usuario ): Boolean {
        return usuario.rol in listOf( Rol.EDITOR, Rol.ADMIN )
    }

    fun podeAdministrar( usuario: Usuario ): Boolean {
        return usuario.rol == Rol.ADMIN
    }

}