package com.aprengal.lendasnubeiras.data.usuarios

import android.util.Log
import com.aprengal.lendasnubeiras.data.configuracion.Axustes.borrarOpcion
import com.aprengal.lendasnubeiras.data.configuracion.Axustes.borrarSesionsLocais
import com.aprengal.lendasnubeiras.data.configuracion.Axustes.collerOpcion
import com.aprengal.lendasnubeiras.data.configuracion.Axustes.collerSesionLocal
import com.aprengal.lendasnubeiras.data.configuracion.Axustes.gardarOpcion
import com.aprengal.lendasnubeiras.data.configuracion.Axustes.gardarSesionLocal
import com.aprengal.lendasnubeiras.data.configuracion.Opcion
import com.aprengal.lendasnubeiras.data.configuracion.api.Conexion.procesarPeticion
import com.aprengal.lendasnubeiras.data.configuracion.api.MetodoApi
import com.aprengal.lendasnubeiras.data.configuracion.api.RespostaXenerica
import com.aprengal.lendasnubeiras.data.configuracion.api.RutaApi
import com.aprengal.lendasnubeiras.data.configuracion.api.SesionUsuario
import com.aprengal.lendasnubeiras.data.configuracion.corrutina
import com.aprengal.lendasnubeiras.data.configuracion.corrutinaResposta
import com.aprengal.lendasnubeiras.data.usuarios.Rol.Companion.buscarRol
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object SesionActual {

    private var usuario = Usuario( 0L, "", Rol.NADA )

    private val _idSesion = MutableStateFlow( "" )

    val sesionAnonima: Boolean
        get() = collerOpcion( Opcion.SesionAnonima )

    fun usuarioActual(): Usuario {
        return usuario
    }

    fun collerSesionActual() : StateFlow<String> {
        if ( _idSesion.value.isBlank() ) validarSesion()
        return _idSesion.asStateFlow()
    }

    private fun cambiarSesion( id: Long = 0L, correo: String = "", rol: Rol = Rol.NADA ) {
        usuario = Usuario( id, correo, rol )
        _idSesion.value = UUID.randomUUID().toString()
    }

    fun crearSesionAnonima() {

        require( collerOpcion( Opcion.SesionUsuario ).isBlank() ) { "Unha sesión anónima só se pode outorgar se non hai ningunha sesión válida" }

        corrutina {
            borrarSesionsLocais()
            gardarOpcion( Opcion.SesionAnonima, true )
            cambiarSesion( 1L, "", Rol.AUTOR )//Rol.LECTOR ) //TODO cambiar a lector
        }

    }

    private fun validarSesion() {

        if ( sesionAnonima ) {  //TODO cambiar a lector
            cambiarSesion( 1L, "", Rol.AUTOR )//Rol.LECTOR )
            return
        }

        val sesionActual = collerOpcion( Opcion.SesionUsuario )
        if ( sesionActual.isBlank() ) return //Baleiro = descartado

        corrutina {

            var id = 1L
            var correo = ""
            var rol = Rol.MONITOR

            val idDispositivo = collerOpcion( Opcion.IdDispositivo )
            val datos: SesionUsuario = procesarPeticion( MetodoApi.GET, RutaApi.VALIDACION, mapOf( "id" to idDispositivo ) )

            if ( datos.exito ) { //Usuario atopado

                gardarOpcion( Opcion.SesionUsuario, datos.sesion )
                gardarSesionLocal( datos.sesion, datos.info )

                id = datos.idUsuario
                correo = datos.correo
                rol = buscarRol( datos.rol )

            } else if ( datos.codigo in setOf( 401, 403 ) ) { //Usuario non atopado ou denegouse a entrada

                borrarOpcion( Opcion.SesionUsuario )

            } else { //Outros: servidor de vacacións, non hai internet ou a saber, pero non se puido chegar ao servidor

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

            cambiarSesion( id, correo, rol )

        }

    }

    suspend fun pecharSesion(): Boolean {

        require( PodePecharSesion( usuario ) ) { "Só se pode pechar sesión se o usuario actual existe" }

        val resposta = corrutinaResposta {

            if ( sesionAnonima ) {

                if ( borrarOpcion( Opcion.SesionAnonima ) ) {
                    cambiarSesion()
                    return@corrutinaResposta true
                }

                Log.i( "Sesion", "Fallou o peche de sesión anónimo" )
                return@corrutinaResposta false

            }

            val resposta: RespostaXenerica = procesarPeticion( MetodoApi.DELETE, RutaApi.VALIDACION )

            if ( resposta.exito ) {

                if ( borrarSesionsLocais() ) {
                    cambiarSesion()
                    return@corrutinaResposta true
                }

                Log.i( "Sesion", "Fallou o peche de sesión a nivel local, pero si se fixo no servidor" )
                return@corrutinaResposta false

            }

            Log.w( "Sesion", "Non se puido pechar a sesión no servidor" )
            return@corrutinaResposta false

        }.await()

        return resposta

    }

}