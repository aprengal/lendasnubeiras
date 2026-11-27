package org.aprengal.lendasnubeiras.data.usuarios

import org.aprengal.lendasnubeiras.data.axustes.Axustes.collerOpcion
import org.aprengal.lendasnubeiras.data.axustes.Opcion.SesionAnonima
import org.aprengal.lendasnubeiras.data.usuarios.SesionActual.usuarioActual

/**
 * Clase selada que define o sistema de permisos da aplicación.
 *
 * Cada obxecto derivado representa un permiso concreto e implementa a súa
 * propia lóxica de validación en función do [Usuario] e, cando corresponda,
 * doutras condicións da aplicación.
 */
sealed class Permiso {

    /**
     * Comproba se o [usuario] indicado cumpre as condicións necesarias para
     * dispoñer deste permiso.
     *
     * Debe ser implementado por cada clase derivada mediante a definición
     * da súa propia lóxica de validación.
     *
     * @param usuario Usuario que se quere avaliar. Por defecto, utiliza o usuario actual da sesión.
     * @return `true` se se cumpren as condicións do permiso ou `false` en caso contrario.
     */
    abstract operator fun invoke( usuario: Usuario = usuarioActual() ): Boolean

    /** Permiso universal aberto a calquera usuario ou estado. */
    object PodeAcceder : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return true
        }
    }

    /** Permiso para iniciar sesión (require o rol [Rol.NADA]). */
    object PodeIniciarSesion : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.rol == Rol.NADA
        }
    }

    /** Permiso para rexistrarse (require o rol [Rol.NADA]). */
    object PodeRexistrarse : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.rol == Rol.NADA
        }
    }

    /** Permiso para pechar sesión (require que exista usuario ou que [SesionAnonima] estea activa). */
    object PodePecharSesion : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.existe || collerOpcion( SesionAnonima )
        }
    }

    /** Permiso para borrar a propia conta (require que o usuario exista). */
    object PodeBorrarConta : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.existe
        }
    }

    /** Permiso básico de lectura (require un rol distinto de [Rol.NADA]). */
    object PodeLer : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.rol != Rol.NADA
        }
    }

    /** Permiso para xestionar xogadores (require o rol [Rol.MONITOR]). */
    object PodeXestionarXogadores : Permiso() {
        override fun invoke( usuario: Usuario ): Boolean {
            return usuario.rol == Rol.MONITOR
        }
    }

}