package org.aprengal.lendasnubeiras.ui.navegacion

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import org.aprengal.lendasnubeiras.data.bd.operacions.Actividades.collerActividade
import org.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeAcceder
import org.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeAdministrar
import org.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeCrear
import org.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeEditar
import org.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeIniciarSesion
import org.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeLer
import org.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeRexistrarse
import org.aprengal.lendasnubeiras.ui.navegacion.Locais.LocalNavegacion
import kotlin.reflect.KClass

/**
 * Controla o acceso ás pantallas segundo os permisos do usuario.
 *
 * Cada ruta ten asociado un permiso. Antes de mostrar unha pantalla
 * compróbase que o usuario o teña e que a ruta sexa válida.
 */
internal object ControlAcceso {

    //Hai que mirar se isto ao final acaba duplicando comportamento en ListaNavegacion
    //Non se pode navegar á ruta que nunca se rexistrou
    /**
     * Comproba se o usuario actual ten permiso para acceder a unha ruta.
     *
     * Cada clase de ruta asócíase a un permiso: calquera usuario ([PodeAcceder]),
     * autenticación ([PodeIniciarSesion], [PodeRexistrarse]), lectura
     * ([PodeLer]), creación ([PodeCrear]), edición ([PodeEditar]) ou
     * administración ([PodeAdministrar]).
     *
     * @param ruta Clase da ruta á que se quere acceder.
     * @return `true` se o usuario ten o permiso asociado a esa ruta.
     * @throws IllegalStateException Se a ruta non ten ningún permiso asignado.
     */
    private fun verificarAcceso( ruta: KClass<out Ruta> ): Boolean {

        val permiso = when ( ruta ) {

            //Calquera
            Ruta.Axustes::class -> PodeAcceder

            // Autenticación
            Ruta.Benvida::class -> PodeIniciarSesion
            Ruta.Acceso::class -> PodeIniciarSesion
            Ruta.Rexistro::class -> PodeRexistrarse

            // Lectura
            Ruta.Inicio::class -> PodeLer
            Ruta.Catalogo::class -> PodeLer
            Ruta.ActividadeDetalle::class -> PodeLer
            Ruta.Buscar::class -> PodeLer
            //Ruta.Idioma::class -> PodeLer

            // Creación
            Ruta.ListarActividades::class -> PodeCrear
            Ruta.CrearActividade::class -> PodeCrear
            Ruta.ModificarActividade::class -> PodeEditar

            // Administración
            Ruta.Administrar::class -> PodeAdministrar

            else -> error( "A ruta ${ ruta.simpleName } non ten permiso asignado" )

        }

        return permiso()

    }

    /**
     * Comproba que o usuario poida acceder a unha pantalla.
     *
     * Se non ten permiso, rexístrase o intento no log e a navegación
     * reiníciase (ver [Navegacion.reiniciar]), de modo que só queda a primeira
     * pantalla da pila.
     *
     * @param ruta Clase da ruta á que se quere acceder.
     * @return `true` se o acceso está permitido; `false` se non.
     */
    @Composable
    internal fun comprobarAcceso( ruta: KClass<out Ruta> ): Boolean {

        if ( verificarAcceso( ruta ) ) return true

        val navegacion = LocalNavegacion.current

        Log.wtf( "PERMISO", "Tratouse de realizar un acceso indebido" )
        LaunchedEffect( Unit ) { navegacion.reiniciar() }

        return false

    }

    /**
     * Comproba que unha ruta concreta sexa válida.
     *
     * Por agora só se valida [Ruta.ActividadeDetalle]: se a actividade
     * indicada non existe, redirixe ao [Ruta.Catalogo].
     *
     * @param ruta Ruta que se quere mostrar.
     * @return `true` se a ruta é válida; `false` se se redirixiu a outra pantalla.
     */
    @Composable
    internal fun verificarRuta( ruta: Ruta ): Boolean {

        val navegacion = LocalNavegacion.current

        val redirixir = when ( ruta ) {
            is Ruta.ActividadeDetalle -> if ( collerActividade( ruta.clave ) == null ) Ruta.Catalogo else null
            else -> null
        }

        if ( redirixir != null ) {
            LaunchedEffect( redirixir ) { navegacion.redirixir( redirixir ) }
            return false
        }

        return true

    }

}