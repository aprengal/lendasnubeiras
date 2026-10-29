package org.aprengal.lendasnubeiras.data.usuarios

import android.util.Log
import kotlinx.coroutines.delay
import org.aprengal.lendasnubeiras.data.api.ConexionApi.MetodoApi
import org.aprengal.lendasnubeiras.data.api.ConexionApi.procesarPeticion
import org.aprengal.lendasnubeiras.data.api.RutasApi
import org.aprengal.lendasnubeiras.data.api.resposta.RespostaXenerica
import org.aprengal.lendasnubeiras.data.axustes.Axustes.collerIdDispositivo
import org.aprengal.lendasnubeiras.data.localizacion.clases.Idioma
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nAutenticacion
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nBase
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nSingular
import org.aprengal.lendasnubeiras.data.utilidades.Corrutinas.corrutina
import kotlin.time.Duration.Companion.milliseconds

object AccionsUsuario {

    fun rexistrarse( correo: String, idioma: Idioma, actualizarEstado: (L10nSingular?, Boolean ) -> Unit, rexistroCorrecto: () -> Unit ) {

        corrutina {

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

    fun iniciarSesion() {

        TODO()

    }

}