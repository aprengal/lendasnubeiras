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
import org.aprengal.lendasnubeiras.data.api.resposta.ActividadeCreada
import org.aprengal.lendasnubeiras.data.api.resposta.DatosActividades
import org.aprengal.lendasnubeiras.data.api.resposta.RespostaApi
import org.aprengal.lendasnubeiras.data.api.resposta.RespostaXenerica
import org.aprengal.lendasnubeiras.data.api.resposta.SesionUsuario
import java.net.HttpURLConnection
import java.net.URL

/**
 * Obxecto singleton encargado de xestionar todas as peticións HTTP/HTTPS coa API do backend.
 * Encargase da serialización en JSON, manexo de cabeceiras, redireccións e tratamento centralizado de erros.
 */
object ConexionApi {

    /**
     * Define os métodos HTTP soportados polas rutas da API.
     */
    enum class MetodoApi( val clave: String ) {
        GET( "GET" ),
        POST( "POST" ),
        DELETE( "DELETE" )
    }

    /**
     * Excepción interna para manexar erros específicos da API que conteñen un código de estado HTTP.
     */
    private class ApiException( mensaxe: String, val codigo: Int ) : Exception( mensaxe )

    /** URL base da API obtida desde a configuración do proxecto. */
    private const val URL_BASE = BuildConfig.API_URL

    /** Contexto global da aplicación necesario para operacións internas se se require. */
    private lateinit var appContext: Context

    /**
     * Inicializa o contexto global da aplicación de forma segura.
     *
     * @contexto O contexto actual da aplicación.
     */
    fun arrancar( contexto: Context ) {
        if ( ::appContext.isInitialized ) return
        appContext = contexto.applicationContext
    }

    /**
     * Realiza unha petición á API e converte o resultado ao tipo de resposta correspondente.
     *
     * @param T O tipo de obxecto que herda de RespostaApi que se vai devolver.
     * @param metodoApi O método HTTP empregado (GET, POST, DELETE).
     * @param rutaApi A ruta específica da API a consultar.
     * @param campos O mapa de parámetros que se enviarán no corpo da petición.
     * @return Unha instancia tipada coa resposta da API.
     */
    suspend fun <T : RespostaApi> procesarPeticion( metodoApi: MetodoApi, rutaApi: RutasApi, campos: Map<String, Any> = emptyMap() ): T {

        val datos = peticion( metodoApi, rutaApi, campos )

        val saida = when( rutaApi ) {
            RutasApi.ACCESO -> SesionUsuario( datos )
            RutasApi.ACTUALIZAR -> DatosActividades( datos )
            RutasApi.CREARACTIVIDADE -> ActividadeCreada( datos )
            RutasApi.REXISTRO, RutasApi.VALIDACION, RutasApi.MODIFICARACTIVIDADE,
            RutasApi.REPORTES, RutasApi.USUARIOS, RutasApi.BORRARCONTA, RutasApi.LIMPAR -> RespostaXenerica( datos )
        }

        @Suppress( "UNCHECKED_CAST" )
        return saida as T

    }

    /**
     * Capa intermediaria que intercepta excepcións da API, rexistra o erro e devolve un JSON de emerxencia co código.
     */
    private suspend fun peticion( metodoApi: MetodoApi, rutaApi: RutasApi, campos: Map<String, Any> ): JSONObject {

        try {
            return realizarPeticion( metodoApi, rutaApi, campos )
        } catch ( e: ApiException ) {
            e.message?.let { mensaxe -> Log.i( "API", mensaxe ) }
            return JSONObject().apply { put( "codigo", e.codigo ) }
        }

    }

    /**
     * Converte un mapa de clave-valor nunha cadea de texto en formato JSON puro.
     *
     * @param campos O mapa cos datos a serializar.
     * @return Unha cadea JSON válida.
     */
    private fun crearCorpo( campos: Map<String, Any> ): String {
        val jsonObject = JSONObject()
        for ( ( clave, valor ) in campos ) {
            jsonObject.put( clave, valor )
        }
        return jsonObject.toString()
    }

    /**
     * Procesa a resposta de texto bruto recibida do servidor e inxecta o código HTTP no JSON.
     */
    private fun procesarResposta( texto: String, codigo: Int ): JSONObject {

        val resposta = if ( texto.isNotBlank() ) {
            JSONObject( texto ).apply { put( "codigo", codigo ) }
        } else {
            JSONObject().apply { put( "codigo", codigo ) }
        }

        return resposta

    }

    /**
     * Núcleo de comunicación HTTP que manexa conexións, timeouts, redireccións e cabeceras.
     */
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

                    if ( sesion.isNotBlank() ) conexion.setRequestProperty( "acceso_rastro", "Bearer $sesion" )

                    val haiCorpo = metodoApi != MetodoApi.GET && corpo.isNotBlank()

                    if ( haiCorpo ) {

                        conexion.doOutput = true

                        conexion.setRequestProperty( "Content-Type", "application/json; charset=UTF-8" )
                        conexion.setRequestProperty( "Accept", "application/json" )

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