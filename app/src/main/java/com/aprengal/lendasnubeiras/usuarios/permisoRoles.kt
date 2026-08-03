package com.aprengal.lendasnubeiras.usuarios

enum class Rol {

    LECTOR,
    COLABORADOR, //vaise permitir a colaboración de persoas alleas a Cruz Vermella?
    AUTOR,
    EDITOR,
    ADMIN;

}


//Isto só valida o rol. A acción debe mirarse a nivel de servidor
object Permisos {

    fun podeEditar( usuario: Usuario ): Boolean {
        return usuario.rol != Rol.LECTOR
    }

    fun podePublicar( usuario: Usuario ): Boolean {
        return usuario.rol == Rol.AUTOR || usuario.rol == Rol.EDITOR || usuario.rol == Rol.ADMIN
    }

    fun podeEditarOutras( usuario: Usuario ): Boolean {
        return usuario.rol == Rol.EDITOR || usuario.rol == Rol.ADMIN
    }

    fun podeAdministrar( usuario: Usuario ): Boolean {
        return usuario.rol == Rol.ADMIN
    }

}