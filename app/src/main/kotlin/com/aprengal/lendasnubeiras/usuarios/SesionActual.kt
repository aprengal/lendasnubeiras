package com.aprengal.lendasnubeiras.usuarios

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.aprengal.lendasnubeiras.configuracion.Axustes.borrarOpcion
import com.aprengal.lendasnubeiras.configuracion.Axustes.borrarSesionsLocais
import com.aprengal.lendasnubeiras.configuracion.Axustes.collerOpcion
import com.aprengal.lendasnubeiras.configuracion.Axustes.collerSesionLocal
import com.aprengal.lendasnubeiras.configuracion.Axustes.gardarOpcion
import com.aprengal.lendasnubeiras.configuracion.Axustes.gardarSesionLocal
import com.aprengal.lendasnubeiras.configuracion.Opcion
import com.aprengal.lendasnubeiras.configuracion.api.Conexion.procesarPeticion
import com.aprengal.lendasnubeiras.configuracion.api.MetodoApi
import com.aprengal.lendasnubeiras.configuracion.api.RespostaXenerica
import com.aprengal.lendasnubeiras.configuracion.api.RutaApi
import com.aprengal.lendasnubeiras.configuracion.api.SesionUsuario
import com.aprengal.lendasnubeiras.configuracion.corrutina
import com.aprengal.lendasnubeiras.configuracion.corrutinaResposta
import com.aprengal.lendasnubeiras.localizacion.Localizacion.l10n
import com.aprengal.lendasnubeiras.usuarios.Permisos.podePecharSesion

object SesionActual {

    private var usuario: Usuario by mutableStateOf( Usuario( 0L, "", "nada" ) )

    val sesionAnonima: Boolean
        get() = collerOpcion( Opcion.SesionAnonima )

    fun collerUsuarioActual(): Usuario = usuario

    fun crearSesionAnonima() {

        require( collerOpcion( Opcion.SesionUsuario ).isBlank() ) { "Unha sesión anónima só se pode outorgar se non hai ningunha sesión válida" }

        corrutina {
            borrarSesionsLocais()
            gardarOpcion( Opcion.SesionAnonima, true )
            usuario = Usuario( 0L, "", "lector" )
        }

    }

    fun validarSesion() {

        if ( sesionAnonima ) {
            usuario = Usuario( 1L, "", "lector" )
            return
        }

        val sesionActual = collerOpcion( Opcion.SesionUsuario )
        if ( sesionActual.isBlank() ) return //Baleiro = descartado

        corrutina {

            var id = 1L
            var correo = ""
            var rol = "lector"

            val idDispositivo = collerOpcion( Opcion.IdDispositivo )
            val datos: SesionUsuario = procesarPeticion( MetodoApi.GET, RutaApi.VALIDARSESION, mapOf( "id" to idDispositivo ) )

            if ( datos.exito ) { //Usuario atopado

                gardarOpcion( Opcion.SesionUsuario, datos.sesion )
                gardarSesionLocal( datos.sesion, datos.info )

                id = datos.idUsuario
                correo = datos.correo
                rol = datos.rol

            } else if ( datos.codigo in setOf( 401, 403 ) ) { //Usuario non atopado ou denegouse a entrada

                borrarOpcion( Opcion.SesionUsuario )

            } else { //Outros: servidor de vacacións, non hai internet ou

                val datosAlmacenados = collerSesionLocal( sesionActual )

                if ( datosAlmacenados.isNotBlank() ) {

                    val infoSesion = datosAlmacenados.split( "|" )

                    if ( infoSesion.size == 2 ) {

                        try {
                            id = infoSesion[ 0 ].toLong()
                            rol = infoSesion[ 1 ]
                        } catch ( _: NumberFormatException ) { //Datos corruptos
                            borrarSesionsLocais()
                        }

                    }

                }

            }

            usuario = Usuario( id, correo, rol )

        }

    }

    suspend fun pecharSesion(): String {

        require( podePecharSesion( usuario ) ) { "Só se pode pechar sesión se o usuario actual existe" }

        val resposta: String = corrutinaResposta {

            if ( sesionAnonima ) {

                if ( borrarOpcion( Opcion.SesionAnonima ) ) {
                    usuario = Usuario( 1L, "", "nada" )
                    return@corrutinaResposta "exito"
                }

                return@corrutinaResposta "peche_anonimo_fallido"

            }

            val resposta: RespostaXenerica = procesarPeticion( MetodoApi.DELETE, RutaApi.VALIDARSESION )

            if ( resposta.exito ) {

                if ( borrarSesionsLocais() ) {
                    usuario = Usuario( 1L, "", "nada" )
                    return@corrutinaResposta "exito"
                }

                return@corrutinaResposta "peche_local_fallido"

            }

            return@corrutinaResposta "peche_servidor_fallidoº"

        }.await()

        return if ( resposta != "exito" ) l10n( resposta, "autenticacion" ) else ""

    }

}