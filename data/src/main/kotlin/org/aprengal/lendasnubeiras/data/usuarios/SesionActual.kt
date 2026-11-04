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
import org.aprengal.lendasnubeiras.data.api.resposta.SesionUsuario
import org.aprengal.lendasnubeiras.data.utilidades.Corrutinas.corrutina
import org.aprengal.lendasnubeiras.data.utilidades.Corrutinas.corrutinaResposta
import org.aprengal.lendasnubeiras.data.usuarios.Rol.Companion.buscarRol
import org.aprengal.lendasnubeiras.data.usuarios.Permiso.PodePecharSesion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object SesionActual {

    private var usuario = Usuario( 0L, "", Rol.NADA )

    private val _idSesion = MutableStateFlow( "" )

    val sesionAnonima: Boolean
        get() = collerOpcion( Opcion.SesionAnonima )

    suspend fun arrancar() {

        if ( _idSesion.value.isNotBlank() ) return

        validarSesion()

    }

    fun usuarioActual(): Usuario {
        return usuario
    }

    fun collerSesionActual() : StateFlow<String> {
        return _idSesion.asStateFlow()
    }

    internal fun cambiarSesion( id: Long = 0L, correo: String = "", rol: Rol = Rol.NADA ) {
        usuario = Usuario( id, correo, rol )
        _idSesion.value = UUID.randomUUID().toString()
    }

    fun crearSesionAnonima() {

        require( collerOpcion( Opcion.SesionUsuario ).isBlank() ) { "Unha sesión anónima só se pode outorgar se non hai ningunha sesión válida" }

        corrutina {
            borrarSesionsLocais()
            gardarOpcion( Opcion.SesionAnonima, true )
            cambiarSesion( 1L, "", Rol.MONITOR )
        }

    }

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
            val datos: SesionUsuario = procesarPeticion( MetodoApi.POST, RutasApi.VALIDACION, mapOf( "uuid" to idDispositivo ) )

            if ( datos.exito ) {

                gardarOpcion( Opcion.SesionUsuario, datos.sesion )
                gardarSesionLocal( datos.sesion, datos.info )

                id = datos.idUsuario
                correo = datos.correo
                rol = buscarRol( datos.rol )

            } else if ( datos.codigo in setOf( 401, 403 ) ) { //Usuario non atopado ou denegouse a entrada

                borrarOpcion( Opcion.SesionUsuario )

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