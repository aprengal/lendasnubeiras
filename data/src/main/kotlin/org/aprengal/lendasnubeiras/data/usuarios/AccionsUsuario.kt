package org.aprengal.lendasnubeiras.data.usuarios

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import org.aprengal.lendasnubeiras.data.api.ConexionApi.MetodoApi
import org.aprengal.lendasnubeiras.data.api.ConexionApi.procesarPeticion
import org.aprengal.lendasnubeiras.data.api.RutasApi
import org.aprengal.lendasnubeiras.data.api.resposta.RespostaXenerica
import org.aprengal.lendasnubeiras.data.api.resposta.SesionUsuario
import org.aprengal.lendasnubeiras.data.axustes.Axustes.collerIdDispositivo
import org.aprengal.lendasnubeiras.data.axustes.Axustes.collerOpcion
import org.aprengal.lendasnubeiras.data.axustes.Axustes.gardarOpcion
import org.aprengal.lendasnubeiras.data.axustes.Axustes.gardarSesionLocal
import org.aprengal.lendasnubeiras.data.axustes.Opcion
import org.aprengal.lendasnubeiras.data.localizacion.clases.Idioma
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nAutenticacion
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nBase
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nSingular
import org.aprengal.lendasnubeiras.data.usuarios.Rol.Companion.buscarRol
import org.aprengal.lendasnubeiras.data.usuarios.SesionActual.cambiarSesion
import org.aprengal.lendasnubeiras.data.utilidades.Corrutinas.corrutina
import kotlin.time.Duration.Companion.milliseconds

object AccionsUsuario {

    fun rexistrarse( correo: String, idioma: Idioma, actualizarEstado: (L10nSingular?, Boolean ) -> Unit, rexistroCorrecto: () -> Unit ) {

        corrutina( CoroutineScope( SupervisorJob() + Dispatchers.IO ) ) {

            actualizarEstado( L10nAutenticacion.ComprobandoCorreo, false )

            try {

                val idDispositivo = collerIdDispositivo()

                Log.d( "ID", idDispositivo )

                val campos = mapOf<String, Any>(
                    "correo" to correo,
                    "uuid" to idDispositivo,
                    "idioma" to idioma.codigo
                )

                val resposta: RespostaXenerica = procesarPeticion( MetodoApi.POST, RutasApi.REXISTRO, campos )

                when ( resposta.codigo ) {

                    200 -> {

                        actualizarEstado( L10nAutenticacion.RexistroCorrecto, false )

                        delay( 5000.milliseconds )
                        rexistroCorrecto()

                    }

                    400 -> actualizarEstado( L10nBase.ErroDatosInvalidos, true )
                    409 -> actualizarEstado( L10nAutenticacion.RexistroContaXaExiste, true )
                    429 -> actualizarEstado( L10nBase.ErroLimiteIntentos, true )
                    else -> actualizarEstado( L10nAutenticacion.ErroRexistro, true )

                }

            } catch ( _: Exception ) {
                actualizarEstado( L10nBase.ErroConexion, true )
            }
        }

    }

    fun iniciarSesion( correo: String, idioma: Idioma, actualizarEstado: ( L10nSingular?, Boolean ) -> Unit ) {

        corrutina( CoroutineScope( SupervisorJob() + Dispatchers.IO ) ) {

            actualizarEstado( L10nAutenticacion.ComprobandoCorreo, false )

            try {

                val idDispositivo = collerIdDispositivo()

                Log.d( "ID", idDispositivo )

                val campos = mapOf<String, Any>(
                    "correo" to correo,
                    "uuid" to idDispositivo,
                    "idioma" to idioma.codigo
                )

                val resposta: SesionUsuario = procesarPeticion( MetodoApi.POST, RutasApi.ACCESO, campos )

                when ( resposta.codigo ) {

                    200 -> {

                        actualizarEstado( L10nAutenticacion.AgardandoValidacionSesion, false )
                        gardarOpcion( Opcion.SesionUsuario, resposta.sesion )
                        gardarSesionLocal( resposta.sesion, resposta.info )

                        var reintentos = 0

                        //límite establecido en 225 reintentos
                        // 225 * 4 = 900 segundos = 15 minutos
                        while ( reintentos < 225 ) {

                            delay( 4000.milliseconds )

                            val idDispositivo = collerOpcion( Opcion.IdDispositivo )
                            val datos: SesionUsuario = procesarPeticion( MetodoApi.POST, RutasApi.VALIDACION, mapOf( "uuid" to idDispositivo ) )

                            if ( datos.exito ) {

                                actualizarEstado( L10nAutenticacion.SesionValidadaCorrectamente, false )

                                delay( 1000.milliseconds )

                                val id = datos.idUsuario
                                val correo = datos.correo
                                val rol = buscarRol( datos.rol )

                                cambiarSesion( id, correo, rol )

                                break
                            }

                            reintentos++

                        }

                        if ( reintentos == 225 ) {
                            actualizarEstado( L10nAutenticacion.CorreoElectronico, true )
                        }

                    }

                    400 -> actualizarEstado( L10nBase.ErroDatosInvalidos, true )
                    403 -> actualizarEstado( L10nAutenticacion.ContaNonActiva, true )
                    404 -> actualizarEstado( L10nAutenticacion.ContaNonAtopada, true )
                    429 -> actualizarEstado( L10nBase.ErroLimiteIntentos, true )
                    else -> actualizarEstado( L10nAutenticacion.ErroAcceso, true )

                }

            } catch ( _: Exception ) {
                actualizarEstado( L10nBase.ErroConexion, true )
            }

        }

    }

}