package com.aprengal.lendasnubeiras.data.usuarios

import com.aprengal.lendasnubeiras.data.axustes.Axustes.collerOpcion
import com.aprengal.lendasnubeiras.data.axustes.Opcion.SesionAnonima
import com.aprengal.lendasnubeiras.data.usuarios.SesionActual.usuarioActual

//Isto só valida o rol. A acción debe revisarse a nivel de servidor
sealed class Permiso {
    abstract operator fun invoke( usuario: Usuario = usuarioActual() ): Boolean

    object PodeAcceder : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return true
        }
    }

    object PodeIniciarSesion : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.rol == Rol.NADA
        }
    }

    object PodeRexistrarse : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.rol == Rol.NADA
        }
    }

    object PodePecharSesion : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.existe || collerOpcion( SesionAnonima )
        }
    }

    object PodeLer : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.rol != Rol.NADA
        }
    }

    object PodeBorrarConta : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.existe && usuario.rol != Rol.ADMIN
        }
    }

    object PodeCrear : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.rol in listOf( Rol.COLABORADOR, Rol.AUTOR, Rol.EDITOR, Rol.ADMIN )
        }
    }

    object PodeDescartar : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.rol in listOf( Rol.COLABORADOR, Rol.AUTOR, Rol.EDITOR, Rol.ADMIN )
        }
    }

    object PodeEditar : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.rol in listOf( Rol.COLABORADOR, Rol.AUTOR, Rol.EDITOR, Rol.ADMIN )
        }
    }

    object PodePublicar : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.rol in listOf( Rol.AUTOR, Rol.EDITOR, Rol.ADMIN )
        }
    }

    object PodeBorrar : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.rol in listOf( Rol.AUTOR, Rol.EDITOR, Rol.ADMIN )
        }
    }

    object PodeRestaurar : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.rol in listOf( Rol.AUTOR, Rol.EDITOR, Rol.ADMIN )
        }
    }

    object PodeEditarOutras : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.rol in listOf( Rol.EDITOR, Rol.ADMIN )
        }
    }

    object PodeBorrarOutras : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.rol in listOf( Rol.EDITOR, Rol.ADMIN )
        }
    }

    object PodeRestaurarOutras : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.rol in listOf( Rol.EDITOR, Rol.ADMIN )
        }
    }

    object PodeAdministrar : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.rol == Rol.ADMIN
        }
    }

}