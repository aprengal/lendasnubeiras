package org.aprengal.lendasnubeiras.data.api

import android.content.Context
import android.util.Log
import org.aprengal.lendasnubeiras.data.axustes.Axustes.collerOpcion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException
import org.aprengal.lendasnubeiras.data.axustes.Opcion
import org.aprengal.lendasnubeiras.data.BuildConfig
import org.aprengal.lendasnubeiras.data.api.RespostaApi.DatosActividades
import org.aprengal.lendasnubeiras.data.api.RespostaApi.RespostaXenerica
import org.aprengal.lendasnubeiras.data.api.RespostaApi.SesionUsuario
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object ConexionApi {

    enum class MetodoApi( val clave: String ) { GET( "GET" ), POST( "POST" ), DELETE( "DELETE" ) }

    private class ApiException( mensaxe: String, val codigo: Int ) : Exception( mensaxe )

    private const val URL_BASE = BuildConfig.API_URL

    private lateinit var appContext: Context

    fun arrancar( contexto: Context ) {
        if ( ::appContext.isInitialized ) return
        appContext = contexto.applicationContext
    }

    suspend fun <T : RespostaApi> procesarPeticion( metodoApi: MetodoApi, rutaApi: RutasApi, campos: Map<String, Any> = emptyMap() ): T {

        val datos = peticion( metodoApi, rutaApi, campos )

        val saida = when( rutaApi ) {
            RutasApi.VALIDACION -> SesionUsuario( datos )
            RutasApi.REXISTRO, RutasApi.ACCESO, RutasApi.REPORTES -> RespostaXenerica( datos )
            RutasApi.ACTUALIZAR -> DatosActividades( datos )
        }

        @Suppress( "UNCHECKED_CAST" )
        return saida as T

    }

    private suspend fun peticion( metodoApi: MetodoApi, rutaApi: RutasApi, campos: Map<String, Any> ): JSONObject {

        try {
            return realizarPeticion( metodoApi, rutaApi, campos )
        } catch ( e: ApiException ) {

            e.message?.let { mensaxe -> Log.i( "API", mensaxe ) }
            return JSONObject().apply { put( "codigo", e.codigo ) }

        }

    }

    private fun crearCorpo( campos: Map<String, Any> ): String {

        val partes = mutableListOf<String>()

        for ( ( clave, valor ) in campos ) {
            val nomeCod = URLEncoder.encode( clave, "UTF-8" )
            val valorCod = URLEncoder.encode( valor.toString(), "UTF-8" )
            partes.add( "$nomeCod=$valorCod" )
        }

        return partes.joinToString( "&" )

    }

    private fun procesarResposta( texto: String, codigo: Int ): JSONObject {

        val resposta = if ( texto.isNotBlank() ) {
            JSONObject( texto ).apply { put( "codigo", codigo ) }
        } else {
            JSONObject().apply { put( "codigo", codigo ) }
        }

        return resposta

    }

    private suspend fun realizarPeticion( metodoApi: MetodoApi, ruta: RutasApi, campos: Map<String, Any> ): JSONObject {

        val sesion = collerOpcion( Opcion.SesionUsuario )

        val resultado = withContext( Dispatchers.IO ) {

            val corpo = if ( campos.isNotEmpty() ) { crearCorpo( campos ) } else ""

            val respostaBruta = try {

                var urlActual = URL_BASE + ruta.ruta
                var conexion: HttpURLConnection
                var codigo: Int
                var saltos = 0

                while ( true ) {

                    conexion = URL( urlActual ).openConnection() as HttpURLConnection
                    conexion.connectTimeout = 5000
                    conexion.readTimeout = 5000
                    conexion.requestMethod = metodoApi.clave
                    conexion.instanceFollowRedirects = false

                    if ( sesion.isNotBlank() ) conexion.setRequestProperty( "permiso", "Bearer $sesion" )

                    val haiCorpo = metodoApi != MetodoApi.GET && corpo.isNotBlank()

                    if ( haiCorpo ) {
                        conexion.doOutput = true
                        conexion.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded" )
                        val bytes = corpo.toByteArray( Charsets.UTF_8 )
                        conexion.setFixedLengthStreamingMode( bytes.size )
                        conexion.outputStream.use { it.write( bytes ) }
                    }

                    conexion.connect()
                    codigo = conexion.responseCode

                    if ( codigo !in intArrayOf( 301, 302, 303, 307, 308 ) || saltos >= 5 ) break

                    urlActual = conexion.getHeaderField( "Location" )
                    conexion.disconnect()

                    if ( urlActual.isBlank() ) break

                    saltos++

                }

                val stream = if ( codigo in 200..299 ) conexion.inputStream else conexion.errorStream
                val texto = stream?.bufferedReader()?.use { contido -> contido.readText() } ?: ""

                conexion.disconnect()
                procesarResposta( texto, codigo )

            } catch ( _: SocketTimeoutException ) {

                throw ApiException( "A petición tardou demasiado en responder", 408 )

            } catch ( _: IOException ) {

                throw ApiException( "Non hai conexión ou o servidor non responde", 0 )

            } catch ( _: JSONException ) {

                throw ApiException( "A API respondeu con formato descoñecido", 1 )

            }

            return@withContext respostaBruta

        }

        return resultado

    }

}