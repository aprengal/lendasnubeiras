package org.aprengal.lendasnubeiras.data.api

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
import org.aprengal.lendasnubeiras.data.api.resposta.RespostaAcceso
import org.aprengal.lendasnubeiras.data.api.resposta.RespostaApi
import org.aprengal.lendasnubeiras.data.api.resposta.RespostaXenerica
import org.aprengal.lendasnubeiras.data.api.resposta.RespostaValidacionSesion
import java.net.HttpURLConnection
import java.net.URL

/**
 * Obxecto singleton encargado de xestionar as peticións HTTP e HTTPS coa API do backend.
 *
 * Centraliza a comunicación co servidor, incluíndo a serialización dos datos enviados
 * en formato JSON, a configuración das cabeceiras, a xestión das redireccións HTTP,
 * o tratamento das respostas e a conversión dos erros de comunicación.
 *
 * As peticións execútanse nun fío de entrada/saída para non bloquear o fío principal.
 * As respostas procésanse e convértense ao tipo correspondente segundo a ruta da API.
 */
object ConexionApi {

    /**
     * Métodos HTTP dispoñibles para realizar peticións á API.
     *
     * Cada elemento contén a cadea de texto que se utiliza ao configurar
     * o método da conexión HTTP.
     */
    enum class MetodoApi( val clave: String ) {
        GET( "GET" ),
        POST( "POST" ),
        DELETE( "DELETE" )
    }

    /**
     * Excepción interna utilizada para representar erros de comunicación coa API.
     *
     * Permite asociar unha mensaxe descritiva a un código que posteriormente
     * se incorpora á resposta JSON devolta pola capa de peticións.
     *
     * @param mensaxe Descrición do erro producido.
     * @param codigo Código asociado ao erro.
     */
    private class ApiException( mensaxe: String, val codigo: Int ) : Exception( mensaxe )

    /** URL base do servidor, obtida da configuración de compilación do proxecto. */
    private const val URL_BASE = BuildConfig.API_URL

    /**
     * Realiza unha petición á API e transforma a resposta recibida nun obxecto
     * da clase correspondente á ruta solicitada.
     *
     * A conversión realízase segundo a seguinte correspondencia:
     * - [RutasApi.ACCESO]: [RespostaAcceso].
     * - [RutasApi.VALIDACION]: [RespostaValidacionSesion].
     * - [RutasApi.ACTUALIZAR]: [DatosActividades].
     * - [RutasApi.CREARACTIVIDADE]: [ActividadeCreada].
     * - Resto de rutas: [RespostaXenerica].
     *
     * Delega o envío da petición HTTP na función [peticion] e converte os datos
     * recibidos na instancia correspondente.
     *
     * @param T Tipo de resposta esperado, que debe herdar de [RespostaApi].
     * @param metodoApi Método HTTP que se empregará na petición.
     * @param rutaApi Ruta da API á que se realizará a petición.
     * @param campos Parámetros que se enviarán na petición. Por defecto, un mapa baleiro.
     * @return Obxecto que representa a resposta da API, convertido ao tipo [T].
     */
    suspend fun <T : RespostaApi> procesarPeticion( metodoApi: MetodoApi, rutaApi: RutasApi, campos: Map<String, Any> = emptyMap() ): T {

        val datos = peticion( metodoApi, rutaApi, campos )

        val saida = when( rutaApi ) {
            RutasApi.ACCESO -> RespostaAcceso( datos )
            RutasApi.VALIDACION -> RespostaValidacionSesion( datos )
            RutasApi.ACTUALIZAR -> DatosActividades( datos )
            RutasApi.CREARACTIVIDADE -> ActividadeCreada( datos )
            RutasApi.REXISTRO, RutasApi.PECHE, RutasApi.MODIFICARACTIVIDADE,
            RutasApi.REPORTES, RutasApi.USUARIOS, RutasApi.BORRARCONTA, RutasApi.LIMPAR -> RespostaXenerica( datos )
        }

        @Suppress( "UNCHECKED_CAST" )
        return saida as T

    }

    /**
     * Intercepta os erros de comunicación representados mediante [ApiException].
     *
     * Delega a petición en [realizarPeticion]. Se esta produce unha [ApiException],
     * rexistra a súa mensaxe no rexistro da aplicación e devolve un obxecto JSON
     * que contén o código asociado ao erro.
     *
     * As demais excepcións non se interceptan neste método.
     *
     * @param metodoApi Método HTTP que se empregará na petición.
     * @param rutaApi Ruta da API á que se realizará a petición.
     * @param campos Datos que se enviarán no corpo da petición, se corresponde.
     * @return Obxecto JSON coa resposta do servidor ou co código do erro producido.
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
     * Converte un mapa de datos nunha cadea de texto en formato JSON.
     *
     * Cada entrada do mapa engádese ao obxecto JSON utilizando a súa clave
     * e o valor correspondente.
     *
     * @param campos Mapa cos datos que se queren serializar.
     * @return Cadea de texto que representa o obxecto JSON resultante.
     */
    private fun crearCorpo( campos: Map<String, Any> ): String {
        val jsonObject = JSONObject()
        for ( ( clave, valor ) in campos ) {
            jsonObject.put( clave, valor )
        }
        return jsonObject.toString()
    }

    /**
     * Converte o texto recibido do servidor nun obxecto JSON e incorpora o código HTTP.
     *
     * Se o servidor devolve contido, interprétase como un obxecto JSON e engádeselle
     * o código de resposta HTTP. Se a resposta non contén texto, créase un obxecto
     * JSON que inclúe unicamente ese código.
     *
     * O código HTTP engadido substitúe calquera valor previo asociado á clave `codigo`.
     *
     * @param texto Contido textual recibido na resposta do servidor.
     * @param codigo Código de estado HTTP devolto polo servidor.
     * @return Obxecto JSON co contido da resposta e o código HTTP correspondente.
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
     * Executa a petición HTTP e procesa a resposta recibida do servidor.
     *
     * Obtén a sesión actual do usuario e configura a conexión coa URL correspondente
     * á ruta solicitada. A comunicación realízase en [Dispatchers.IO] e establece
     * tempos máximos de espera de 10 segundos tanto para a conexión como para a lectura.
     *
     * Se existe unha sesión, engade a súa credencial á cabeceira `sesion_rastro`.
     * Cando a petición contén datos e o método HTTP non é GET, serializa os campos
     * en JSON e envíaos no corpo da petición, configurando as cabeceiras correspondentes.
     *
     * As redireccións HTTP xestiónanse manualmente, seguindo un máximo de cinco saltos.
     * Finalmente, le o contido da resposta: utiliza o fluxo normal para respostas
     * satisfactorias e o fluxo de erro para o resto dos códigos HTTP.
     *
     * Os erros de tempo de espera, conexión e formato JSON convértense en [ApiException]
     * cos seguintes códigos:
     * - `408`: tempo de espera esgotado.
     * - `0`: erro de conexión ou ausencia de resposta do servidor.
     * - `1`: resposta recibida cun formato JSON non válido.
     *
     * @param metodoApi Método HTTP que se empregará na petición.
     * @param ruta Ruta da API á que se realizará a petición.
     * @param campos Datos que se enviarán no corpo da petición, se corresponde.
     * @return Obxecto JSON procesado a partir da resposta recibida do servidor.
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
                    conexion.connectTimeout = 10000
                    conexion.readTimeout = 10000
                    conexion.requestMethod = metodoApi.clave
                    conexion.instanceFollowRedirects = false

                    if ( sesion.isNotBlank() ) conexion.setRequestProperty( "cabeceira_sesion", "Bearer $sesion" )

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