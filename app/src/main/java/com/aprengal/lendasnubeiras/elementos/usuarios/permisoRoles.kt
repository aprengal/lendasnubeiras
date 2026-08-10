package com.aprengal.lendasnubeiras.elementos.usuarios

enum class Rol {

    LECTOR,
    COLABORADOR, //vaise permitir a colaboración de persoas alleas a Cruz Vermella?
    AUTOR,
    EDITOR,
    ADMIN;

}


//Isto só valida o rol. A acción debe revisarse a nivel de servidor
object Permisos {

    fun podeCrear( usuario: Usuario ): Boolean {
        return usuario.rol != Rol.LECTOR
    }

    fun podeDescartar( usuario: Usuario ): Boolean {
        return usuario.rol != Rol.LECTOR
    }

    fun podeEditar( usuario: Usuario ): Boolean {
        return usuario.rol != Rol.LECTOR
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