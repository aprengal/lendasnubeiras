package com.aprengal.lendasnubeiras.usuarios

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.aprengal.lendasnubeiras.Axustes.borrarOpcion
import com.aprengal.lendasnubeiras.Axustes.borrarSesionsLocais
import com.aprengal.lendasnubeiras.Axustes.collerOpcion
import com.aprengal.lendasnubeiras.Axustes.collerSesionLocal
import com.aprengal.lendasnubeiras.Axustes.gardarOpcion
import com.aprengal.lendasnubeiras.Axustes.gardarSesionLocal
import com.aprengal.lendasnubeiras.Opcion
import com.aprengal.lendasnubeiras.configuracion.api.ApiException
import com.aprengal.lendasnubeiras.configuracion.api.AutenticacionException
import com.aprengal.lendasnubeiras.configuracion.api.Conexion.procesarPeticion
import com.aprengal.lendasnubeiras.configuracion.api.LimiteTaxaException
import com.aprengal.lendasnubeiras.configuracion.api.MetodoPeticion
import com.aprengal.lendasnubeiras.configuracion.api.OutroErroApiException
import com.aprengal.lendasnubeiras.configuracion.api.PermisoException
import com.aprengal.lendasnubeiras.configuracion.api.PeticionInvalidaException
import com.aprengal.lendasnubeiras.configuracion.api.RespostaXenerica
import com.aprengal.lendasnubeiras.configuracion.api.RutaApi
import com.aprengal.lendasnubeiras.configuracion.api.SenConexionException
import com.aprengal.lendasnubeiras.configuracion.api.ServidorCaidoException
import com.aprengal.lendasnubeiras.configuracion.api.SesionUsuario
import com.aprengal.lendasnubeiras.configuracion.api.TempoEsgotadoException
import com.aprengal.lendasnubeiras.configuracion.corrutina
import com.aprengal.lendasnubeiras.usuarios.Permisos.podePecharSesion
import com.aprengal.lendasnubeiras.usuarios.Rol.Companion.buscarRol

data class Usuario(
    val id: Long,
    val correo: String,
    val rol: Rol
) {

    val existe: Boolean
        get() = rol != Rol.NADA && correo.isNotBlank()

}

object UsuarioActual {

    private var usuario: Usuario by mutableStateOf( Usuario( id = 0L, correo = "", rol = Rol.NADA ) )

    fun collerUsuarioActual(): Usuario = usuario

    fun sesionAnonima() {

        require( collerOpcion( Opcion.SesionUsuario ).isBlank() ) { "Unha sesión anónima só se pode outorgar se non hai ningunha sesión válida" }

        corrutina {
            gardarOpcion( Opcion.SesionAnonima, true )
            borrarOpcion( Opcion.SesionUsuario )
            usuario = Usuario( id = 1L, correo = "", rol = Rol.LECTOR )
        }

    }

    fun pecharSesion() {

        require( podePecharSesion( usuario ) ) { "Só se pode pechar sesión se o usuario actual existe" }

        corrutina {

            try {

                procesarPeticion( MetodoPeticion.DELETE, RutaApi.VALIDARSESION ) as RespostaXenerica
                borrarSesionsLocais()
                usuario = Usuario( id = 1L, correo = "", rol = Rol.LECTOR )

            } catch ( e: ApiException ) {
                Log.d( "SESION", "Non se puido pechar a sesión porque ${ e.message }" )
            }

        }

    }

    fun validarSesion() {

        if ( collerOpcion( Opcion.SesionAnonima ) ) { //Sesión anónima
            usuario = Usuario( id = 1L, correo = "", rol = Rol.LECTOR )
            return
        }

        val sesionActual = collerOpcion( Opcion.SesionUsuario )
        if ( sesionActual.isBlank() ) return //Baleiro = descartado

        corrutina {

            var id = 1L
            var correo = ""
            var rol = Rol.LECTOR

            try {

                val idDispositivo = collerOpcion( Opcion.IdDispositivo )
                val datos: SesionUsuario = procesarPeticion( MetodoPeticion.GET, RutaApi.VALIDARSESION, mapOf( "id" to idDispositivo ) )

                if ( datos.exito ) { //Usuario atopado

                    gardarOpcion( Opcion.SesionUsuario, datos.sesion )
                    borrarOpcion( Opcion.SesionAnonima )
                    gardarSesionLocal( datos.sesion, datos.info )

                    id = datos.idUsuario
                    correo = datos.correo
                    rol = datos.rol

                } else { //Non se atopou ningún usuario. Malformación ou sesión caducada
                    borrarOpcion( Opcion.SesionUsuario )
                }

            } catch ( e: ApiException ) {

                when ( e ) {

                    is AutenticacionException, is PermisoException -> { borrarOpcion( Opcion.SesionUsuario ) }
                    is LimiteTaxaException, is OutroErroApiException, is PeticionInvalidaException,
                    is SenConexionException, is ServidorCaidoException, is TempoEsgotadoException -> {

                        Log.v( "SESIONINVALIDA", e.message ?: "Erro sen mensaxe" )
                        val datosAlmacenados = collerSesionLocal( sesionActual )

                        if ( datosAlmacenados.isNotBlank() ) {

                            val infoSesion = datosAlmacenados.split( "|" )

                            if ( infoSesion.size == 2 ) {

                                try {
                                    id = infoSesion[ 0 ].toLong()
                                    rol = buscarRol( infoSesion[ 1 ] )
                                } catch ( _: NumberFormatException ) { //Datos corruptos
                                    borrarSesionsLocais()
                                }

                            }

                        }

                    }

                }

            }

            usuario = Usuario( id, correo, rol )

        }

    }

}