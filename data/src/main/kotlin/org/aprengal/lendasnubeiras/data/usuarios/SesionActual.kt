package org.aprengal.lendasnubeiras.data.usuarios

import org.aprengal.lendasnubeiras.data.axustes.Axustes.borrarOpcion
import org.aprengal.lendasnubeiras.data.axustes.Axustes.borrarSesionsLocais
import org.aprengal.lendasnubeiras.data.axustes.Axustes.collerOpcion
import org.aprengal.lendasnubeiras.data.axustes.Axustes.collerSesionLocal
import org.aprengal.lendasnubeiras.data.axustes.Axustes.gardarOpcion
import org.aprengal.lendasnubeiras.data.axustes.Axustes.gardarSesionLocal
import org.aprengal.lendasnubeiras.data.axustes.Opcion
import org.aprengal.lendasnubeiras.data.api.ConexionApi.procesarPeticion
import org.aprengal.lendasnubeiras.data.api.ConexionApi.MetodoApi
import org.aprengal.lendasnubeiras.data.api.resposta.RespostaXenerica
import org.aprengal.lendasnubeiras.data.api.RutasApi
import org.aprengal.lendasnubeiras.data.api.resposta.RespostaValidacionSesion
import org.aprengal.lendasnubeiras.data.utilidades.Corrutinas.corrutina
import org.aprengal.lendasnubeiras.data.utilidades.Corrutinas.corrutinaResposta
import org.aprengal.lendasnubeiras.data.usuarios.Rol.Companion.buscarRol
import org.aprengal.lendasnubeiras.data.usuarios.Permiso.PodePecharSesion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID


/**
 * Xestor da sesión activa da aplicación.
 *
 * Mantén os datos do usuario autenticado, permite consultar o estado da sesión,
 * crear sesións anónimas e xestionar a súa validación e peche.
 *
 * Ao iniciar, comproba se existe unha sesión anónima ou unha credencial de usuario
 * almacenada. Neste último caso, intenta validala co servidor e, se non é posible
 * establecer a conexión, pode recuperar parte da información almacenada localmente.
 */
object SesionActual {

    /** Datos do usuario asociado á sesión actual. Inicialmente non hai ningún usuario autenticado. */
    private var usuario = Usuario( 0L, "", Rol.NADA )

    /** Identificador reactivo da sesión actual, que cambia cada vez que se establece unha nova sesión. */
    private val _idSesion = MutableStateFlow( "" )

    /**
     * Indica se a aplicación está funcionando cunha sesión anónima.
     *
     * O valor obtense directamente das opcións almacenadas no dispositivo.
     */
    val sesionAnonima: Boolean
        get() = collerOpcion( Opcion.SesionAnonima )

    /**
     * Inicializa a sesión actual se aínda non foi establecida.
     *
     * Comproba o identificador interno da sesión para evitar repetir o proceso
     * de inicialización. Se non existe unha sesión activa, delega a súa
     * comprobación e recuperación en [validarSesion].
     */
    suspend fun arrancar() {

        if ( _idSesion.value.isNotBlank() ) return

        validarSesion()

    }

    /**
     * Obtén os datos do usuario asociado á sesión actual.
     *
     * @return Obxecto [Usuario] coa identificación, correo electrónico e rol actuais.
     */
    fun usuarioActual(): Usuario {
        return usuario
    }

    /**
     * Obtén un fluxo observable do identificador da sesión actual.
     *
     * Permite detectar os cambios de sesión sen proporcionar acceso directo
     * á modificación do fluxo interno.
     *
     * @return Un [StateFlow] co identificador actual da sesión.
     */
    fun collerSesionActual() : StateFlow<String> {
        return _idSesion.asStateFlow()
    }

    /**
     * Establece os datos do usuario da sesión actual e xera un novo identificador.
     *
     * Os parámetros teñen valores predeterminados que permiten restablecer
     * a sesión a un estado sen usuario autenticado.
     *
     * @param id Identificador do usuario.
     * @param correo Correo electrónico asociado ao usuario.
     * @param rol Rol asignado ao usuario.
     */
    internal fun cambiarSesion( id: Long = 0L, correo: String = "", rol: Rol = Rol.NADA ) {
        usuario = Usuario( id, correo, rol )
        _idSesion.value = UUID.randomUUID().toString()
    }

    /**
     * Crea unha sesión anónima con permisos de monitor.
     *
     * Require que non exista unha credencial de usuario almacenada. Antes de
     * establecer a sesión, elimina as sesións locais anteriores, activa a opción
     * de sesión anónima e asigna un usuario ficticio co rol [Rol.MONITOR].
     *
     * A operación realízase mediante unha corrutina.
     */
    fun crearSesionAnonima() {

        require( collerOpcion( Opcion.SesionUsuario ).isBlank() ) { "Unha sesión anónima só se pode outorgar se non hai ningunha sesión válida" }

        corrutina {
            borrarSesionsLocais()
            gardarOpcion( Opcion.SesionAnonima, true )
            cambiarSesion( 1L, "", Rol.MONITOR )
        }

    }

    /**
     * Comproba e establece a sesión almacenada no dispositivo.
     *
     * O proceso contempla tres situacións:
     *
     * - Se existe unha sesión anónima, establécese directamente un usuario
     *   co rol de monitor, sen realizar peticións ao servidor.
     * - Se existe unha credencial de usuario, envíase unha petición de validación
     *   ao servidor mediante o identificador do dispositivo.
     * - Se non hai credencial almacenada, non se establece ningunha sesión.
     *
     * Cando o servidor valida correctamente a sesión, actualízanse as credenciais
     * e os datos locais do usuario. Se responde cun código 401 ou 403, elimínase
     * a credencial almacenada e mantéñense os valores predeterminados de monitor.
     *
     * Se a petición non pode completarse por outros motivos, inténtase recuperar
     * a identificación e o rol a partir dos datos da sesión gardados localmente.
     * Durante esta recuperación, o rol queda limitado como máximo ao de autor.
     */
    private suspend fun validarSesion() {

        if ( sesionAnonima ) {
            cambiarSesion( 1L, "", Rol.MONITOR )
            return
        }

        val sesionActual = collerOpcion( Opcion.SesionUsuario )
        if ( sesionActual.isBlank() ) return

        val ( id, correo, rol ) = corrutinaResposta {

            var id = 1L
            var correo = ""
            var rol = Rol.MONITOR

            val idDispositivo = collerOpcion( Opcion.IdDispositivo )
            val datos: RespostaValidacionSesion = procesarPeticion( MetodoApi.POST, RutasApi.VALIDACION, mapOf( "uuid" to idDispositivo ) )

            if ( datos.exito ) {

                gardarSesionLocal( sesionActual, datos.info )

                id = datos.idUsuario
                correo = datos.correo
                rol = buscarRol( datos.rol )

            } else if ( datos.codigo in setOf( 401, 403 ) ) { //Usuario non atopado ou denegouse a entrada

                borrarOpcion( Opcion.SesionUsuario )
                id = 0L
                correo = ""
                rol = Rol.NADA

            } else { //Outros: servidor fóra de servizo, non hai internet ou a saber, pero non se puido chegar ao servidor

                val datosAlmacenados = collerSesionLocal( sesionActual )

                if ( datosAlmacenados.isNotBlank() ) {

                    val infoSesion = datosAlmacenados.split( "|" )

                    if ( infoSesion.size == 2 ) {

                        try {
                            id = infoSesion[ 0 ].toLong()
                            val rolGardado = buscarRol( infoSesion[ 1 ] )
                            rol = if ( rolGardado > Rol.AUTOR ) Rol.AUTOR else rolGardado
                        } catch ( _: NumberFormatException ) { //Datos corruptos
                            borrarSesionsLocais()
                        }

                    }

                }

            }

            return@corrutinaResposta Triple( id, correo, rol )

        }.await()

        cambiarSesion( id, correo, rol )

    }

    /**
     * Pecha a sesión actual e elimina as credenciais correspondentes.
     *
     * Require que o usuario actual teña permisos para pechar a súa sesión.
     *
     * Se a sesión é anónima, elimina a opción correspondente e restablece
     * o usuario actual aos seus valores predeterminados.
     *
     * Se é unha sesión de usuario, solicita o peche ao servidor mediante
     * o identificador do dispositivo. Se a operación ten éxito ou o servidor
     * responde cun código 401, elimínanse as credenciais e os datos locais
     * da sesión e restablécese o usuario actual.
     *
     * Se a petición falla por outro motivo, mantense a sesión actual.
     *
     * @return `true` se a sesión se pechou correctamente; `false` se non foi
     * posible completar a operación.
     */
    suspend fun pecharSesion(): Boolean {

        require( PodePecharSesion( usuario ) ) { "Só se pode pechar sesión se o usuario actual existe" }

        val resposta = corrutinaResposta {

            if ( sesionAnonima ) {

                if ( borrarOpcion( Opcion.SesionAnonima ) ) {
                    cambiarSesion()
                    return@corrutinaResposta true
                }

                return@corrutinaResposta false

            }

            val idDispositivo = collerOpcion( Opcion.IdDispositivo )
            val resposta: RespostaXenerica = procesarPeticion( MetodoApi.POST, RutasApi.PECHE, mapOf( "uuid" to idDispositivo ) )

            if ( resposta.exito || resposta.codigo == 401 ) {

                borrarSesionsLocais()
                borrarOpcion( Opcion.SesionUsuario )
                cambiarSesion()

                return@corrutinaResposta true

            }

            return@corrutinaResposta false

        }.await()

        return resposta

    }

}