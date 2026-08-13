package com.aprengal.lendasnubeiras.usuarios

enum class Rol {

    NADA,
    LECTOR,
    COLABORADOR, //vaise permitir a colaboración de persoas alleas a Cruz Vermella?
    AUTOR,
    EDITOR,
    ADMIN;

    companion object {

        fun buscarRol( clave: String ): Rol {
            return entries.find { it.name == clave } ?: NADA
        }

    }

}

//Isto só valida o rol. A acción debe revisarse a nivel de servidor
object Permisos {

    fun podeRexistrarse( usuario: Usuario ): Boolean {
        return usuario.rol == Rol.NADA
    }

    fun podeIniciarSesion( usuario: Usuario ): Boolean {
        return usuario.rol == Rol.NADA
    }

    fun podePecharSesion( usuario: Usuario ): Boolean {
        return usuario.existe
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