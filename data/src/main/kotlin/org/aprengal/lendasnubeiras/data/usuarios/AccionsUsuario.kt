package org.aprengal.lendasnubeiras.data.usuarios

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import org.aprengal.lendasnubeiras.data.api.ConexionApi.MetodoApi
import org.aprengal.lendasnubeiras.data.api.ConexionApi.procesarPeticion
import org.aprengal.lendasnubeiras.data.api.RutasApi
import org.aprengal.lendasnubeiras.data.api.resposta.RespostaXenerica
import org.aprengal.lendasnubeiras.data.api.resposta.SesionUsuario
import org.aprengal.lendasnubeiras.data.axustes.Axustes.borrarOpcion
import org.aprengal.lendasnubeiras.data.axustes.Axustes.borrarSesionsLocais
import org.aprengal.lendasnubeiras.data.axustes.Axustes.collerIdDispositivo
import org.aprengal.lendasnubeiras.data.axustes.Axustes.collerOpcion
import org.aprengal.lendasnubeiras.data.axustes.Axustes.gardarOpcion
import org.aprengal.lendasnubeiras.data.axustes.Axustes.gardarSesionLocal
import org.aprengal.lendasnubeiras.data.axustes.Opcion
import org.aprengal.lendasnubeiras.data.localizacion.clases.Idioma
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nAutenticacion
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nBase
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nSingular
import org.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeBorrarConta
import org.aprengal.lendasnubeiras.data.usuarios.Rol.Companion.buscarRol
import org.aprengal.lendasnubeiras.data.usuarios.SesionActual.cambiarSesion
import org.aprengal.lendasnubeiras.data.usuarios.SesionActual.usuarioActual
import org.aprengal.lendasnubeiras.data.utilidades.Corrutinas.corrutina
import org.aprengal.lendasnubeiras.data.utilidades.Corrutinas.corrutinaResposta
import kotlin.time.Duration.Companion.milliseconds

/**
 * Agrupa as operacións de autenticación e xestión da conta de usuario.
 *
 * Centraliza os procesos de rexistro, inicio de sesión e borrado de conta,
 * comunicándose co servidor mediante a API e xestionando as sesións locais
 * cando corresponde.
 *
 * As operacións de rexistro e inicio de sesión executanse en corrutinas
 * independentes, permitindo informar do progreso e dos posibles erros
 * mediante funcións de actualización de estado.
 */
object AccionsUsuario {

    /**
     * Solicita o rexistro dunha nova conta de usuario.
     *
     * Envía ao servidor o enderezo de correo electrónico, o identificador
     * do dispositivo e o idioma seleccionado.
     *
     * Actualiza o estado da operación segundo a resposta recibida, permitindo
     * informar do progreso, dos erros de validación ou dos problemas de conexión.
     *
     * Cando o rexistro se completa correctamente, informa do resultado e,
     * tras unha pequena espera, executa a función de confirmación.
     *
     * @param correo Enderezo de correo electrónico asociado á conta que se
     * quere rexistrar.
     * @param idioma Idioma seleccionado para a comunicación co servidor.
     * @param actualizarEstado Función utilizada para comunicar o estado da
     * operación e indicar se representa un erro.
     * @param rexistroCorrecto Función executada cando o rexistro se completa
     * correctamente.
     */
    fun rexistrarse( correo: String, idioma: Idioma, actualizarEstado: (L10nSingular?, Boolean ) -> Unit, rexistroCorrecto: () -> Unit ) {

        corrutina( CoroutineScope( SupervisorJob() + Dispatchers.IO ) ) {

            actualizarEstado( L10nAutenticacion.ComprobandoCorreo, false )

            try {

                val idDispositivo = collerIdDispositivo()

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
                    in 500 .. 505 -> actualizarEstado( L10nBase.ErroServidor, true )
                    else -> actualizarEstado( L10nAutenticacion.ErroRexistro, true )

                }

            } catch ( _: Exception ) {
                actualizarEstado( L10nBase.ErroConexion, true )
            }
        }

    }

    /**
     * Solicita o inicio de sesión dunha conta de usuario.
     *
     * Envía ao servidor o enderezo de correo electrónico, o identificador
     * do dispositivo e o idioma seleccionado.
     *
     * Se a solicitude inicial é correcta, garda os datos da sesión e inicia
     * un proceso de comprobación periódica para verificar a súa validación
     * no servidor.
     *
     * A comprobación realízase cun máximo de 225 intentos, separados por
     * intervalos de catro segundos, o que permite agardar ata quince minutos
     * pola validación da sesión.
     *
     * Unha vez validada, actualiza a sesión local cos datos do usuario,
     * incluíndo o seu identificador, correo electrónico e rol.
     *
     * Durante o proceso informa do progreso e dos posibles erros mediante
     * a función de actualización de estado.
     *
     * @param correo Enderezo de correo electrónico da conta.
     * @param idioma Idioma seleccionado para a comunicación co servidor.
     * @param actualizarEstado Función utilizada para comunicar o estado da
     * operación e indicar se representa un erro.
     */
    fun iniciarSesion( correo: String, idioma: Idioma, actualizarEstado: ( L10nSingular?, Boolean ) -> Unit ) {

        corrutina( CoroutineScope( SupervisorJob() + Dispatchers.IO ) ) {

            actualizarEstado( L10nAutenticacion.ComprobandoCorreo, false )

            try {

                val idDispositivo = collerIdDispositivo()

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
                        var sesionValidada = false

                        //límite establecido en 225 reintentos
                        // 225 * 4 = 900 segundos = 15 minutos
                        while ( reintentos < 225 ) {

                            delay( 4000.milliseconds )

                            val idDispositivo = collerOpcion( Opcion.IdDispositivo )
                            val datos: SesionUsuario = procesarPeticion( MetodoApi.POST, RutasApi.VALIDACION, mapOf( "uuid" to idDispositivo ) )

                            when ( datos.codigo ) {

                                200 -> {

                                    actualizarEstado( L10nAutenticacion.SesionValidadaCorrectamente, false )
                                    delay( 1000.milliseconds )

                                    val id = datos.idUsuario
                                    val correo = datos.correo
                                    val rol = buscarRol( datos.rol )

                                    cambiarSesion( id, correo, rol )
                                    sesionValidada = true

                                    break

                                }

                                401 -> reintentos++

                                400, 404  -> {
                                    actualizarEstado( L10nAutenticacion.ErroAcceso, true )
                                    break
                                }

                                in 500 .. 599 -> {
                                    actualizarEstado( L10nBase.ErroServidor, true )
                                    break
                                }

                                else -> reintentos++

                            }

                        }

                        if ( !sesionValidada && reintentos >= 225 ) {
                            actualizarEstado( L10nAutenticacion.CorreoElectronico, true )
                        }

                    }

                    400 -> actualizarEstado( L10nBase.ErroDatosInvalidos, true )
                    403 -> actualizarEstado( L10nAutenticacion.ContaNonActiva, true )
                    404 -> actualizarEstado( L10nAutenticacion.ContaNonAtopada, true )
                    429 -> actualizarEstado( L10nBase.ErroLimiteIntentos, true )
                    in 500 .. 505 -> actualizarEstado( L10nBase.ErroServidor, true )
                    else -> actualizarEstado( L10nAutenticacion.ErroAcceso, true )

                }

            } catch ( _: Exception ) {
                actualizarEstado( L10nBase.ErroConexion, true )
            }

        }

    }

    /**
     * Solicita o borrado da conta do usuario actual.
     *
     * Comproba previamente que existe un usuario actual e que se permite
     * solicitar o borrado da súa conta.
     *
     * Envía ao servidor o identificador do dispositivo e o idioma seleccionado.
     * Se a solicitude se completa correctamente, elimina as sesións locais,
     * borra os datos de sesión almacenados e restablece a sesión actual.
     *
     * @param idioma Idioma seleccionado para a comunicación co servidor.
     * @return `true` se o servidor acepta a solicitude e se completa a limpeza
     * da sesión local; `false` se a solicitude non ten éxito.
     * @throws IllegalArgumentException Se non existe un usuario actual ou non
     * se permite solicitar o borrado da conta.
     */
    suspend fun borrarConta( idioma: Idioma ): Boolean {

        require( PodeBorrarConta( usuarioActual() ) ) { "Só se pode solicitar o borrado se o usuario actual existe" }

        val resposta = corrutinaResposta {

            val idDispositivo = collerOpcion( Opcion.IdDispositivo )
            val corpoPeticion = mapOf( "uuid" to idDispositivo, "idioma" to idioma.codigo )
            val resposta: RespostaXenerica = procesarPeticion( MetodoApi.POST, RutasApi.BORRARCONTA, corpoPeticion )

            if ( resposta.exito ) {

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