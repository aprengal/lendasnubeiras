package org.aprengal.lendasnubeiras.data.usuarios

import org.aprengal.lendasnubeiras.data.axustes.Axustes.collerOpcion
import org.aprengal.lendasnubeiras.data.axustes.Opcion.SesionAnonima
import org.aprengal.lendasnubeiras.data.usuarios.SesionActual.usuarioActual

//Isto só valida o rol. A acción debe revisarse a nivel de servidor
/**
 * Clase sellada que define o sistema de control de permisos baseado en roles de usuario.
 * Cada obxecto contén a súa propia lóxica de validación mediante a sobrecarga do operador `invoke`.
 */
sealed class Permiso {

    /**
     * Comproba se o usuario actual cumpre coas condicións para este permiso.
     *
     * @param usuario O [Usuario] a avaliar (por defecto emprega o usuario actual da sesión).
     * @return `true` se ten permisos, `false` en caso contrario.
     */
    abstract operator fun invoke( usuario: Usuario = usuarioActual() ): Boolean

    /** Permiso universal aberto a calquera usuario ou estado. */
    object PodeAcceder : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return true
        }
    }

    /** Permiso para iniciar sesión (require non estar autenticado previamente, é dicir, rol NADA). */
    object PodeIniciarSesion : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.rol == Rol.NADA
        }
    }

    /** Permiso para rexistrarse (require non ter sesión iniciada). */
    object PodeRexistrarse : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.rol == Rol.NADA
        }
    }

    /** Permiso para pechar sesión (require que exista usuario ou que a opción anónima estea activa). */
    object PodePecharSesion : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.existe || collerOpcion( SesionAnonima )
        }
    }

    /** Permiso básico de lectura (require calquera rol válido distinto de NADA). */
    object PodeLer : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.rol != Rol.NADA
        }
    }

    /** Permiso para borrar a propia conta (require que o usuario exista e non sexa ADMIN). */
    object PodeBorrarConta : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.existe && usuario.rol != Rol.ADMIN
        }
    }

    /** Permiso para xestionar usuarios e grupos (require calquera rol activo). */
    object PodeXestionarUsuariosGrupos : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.rol != Rol.NADA
            //return usuario.rol in listOf( Rol.MONITOR, Rol.COLABORADOR, Rol.AUTOR, Rol.EDITOR, Rol.ADMIN )
        }
    }

    /** Permiso para crear contido (para colaboradores, autores, editores e administradores). */
    object PodeCrear : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.rol in listOf( Rol.COLABORADOR, Rol.AUTOR, Rol.EDITOR, Rol.ADMIN )
        }
    }

    /** Permiso para descartar contido. */
    object PodeDescartar : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.rol in listOf( Rol.COLABORADOR, Rol.AUTOR, Rol.EDITOR, Rol.ADMIN )
        }
    }

    /** Permiso para editar contido propio. */
    object PodeEditar : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.rol in listOf( Rol.COLABORADOR, Rol.AUTOR, Rol.EDITOR, Rol.ADMIN )
        }
    }

    /** Permiso para publicar contido (restringido a autores, editores e administradores). */
    object PodePublicar : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.rol in listOf( Rol.AUTOR, Rol.EDITOR, Rol.ADMIN )
        }
    }

    /** Permiso para borrar contido propio. */
    object PodeBorrar : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.rol in listOf( Rol.AUTOR, Rol.EDITOR, Rol.ADMIN )
        }
    }

    /** Permiso para restaurar contido propio. */
    object PodeRestaurar : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.rol in listOf( Rol.AUTOR, Rol.EDITOR, Rol.ADMIN )
        }
    }

    /** Permiso para editar contido doutros usuarios (exclusivo de editores e administradores). */
    object PodeEditarOutras : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.rol in listOf( Rol.EDITOR, Rol.ADMIN )
        }
    }

    /** Permiso para borrar contido doutros usuarios. */
    object PodeBorrarOutras : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.rol in listOf( Rol.EDITOR, Rol.ADMIN )
        }
    }

    /** Permiso para restaurar contido doutros usuarios. */
    object PodeRestaurarOutras : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.rol in listOf( Rol.EDITOR, Rol.ADMIN )
        }
    }

    /** Permiso total de administración (exclusivo para o rol ADMIN). */
    object PodeAdministrar : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.rol == Rol.ADMIN
        }
    }

}