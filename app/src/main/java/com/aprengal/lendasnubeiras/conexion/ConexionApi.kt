package com.aprengal.lendasnubeiras.conexion

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import com.aprengal.lendasnubeiras.BuildConfig

object ConexionApi {

    //TODO: Cambiar ruta
    private const val URL_BASE = BuildConfig.API_URL

    private lateinit var appContext: Context

    fun arrancar( contexto: Context ) {
        if ( ::appContext.isInitialized ) return
        appContext = contexto.applicationContext
    }

    private fun haiConexion(): Boolean {

        val xestor = appContext.getSystemService( Context.CONNECTIVITY_SERVICE ) as ConnectivityManager
        val rede = xestor.activeNetwork ?: return false
        val capacidades = xestor.getNetworkCapabilities( rede ) ?: return false

        return capacidades.hasCapability( NetworkCapabilities.NET_CAPABILITY_VALIDATED )

    }

    private val cliente = OkHttpClient.Builder()
        .connectTimeout( 5, TimeUnit.SECONDS )
        .readTimeout( 5, TimeUnit.SECONDS )
        .writeTimeout( 5, TimeUnit.SECONDS )
        .build()

    suspend fun peticion( metodo: String, ruta: String, campos: Map<String, Any> = emptyMap() ): JSONObject {

        try {

            if ( !haiConexion() ) {
                throw SenConexionException( "O dispositivo non ten conexión a internet" )
            }

            val datos = realizarPeticion( metodo, ruta, campos )
            return interpretarResposta( datos.first, datos.second )

        } catch ( e: ApiException ) {

            Log.e( "ConexionApi", "Erro en '$ruta': ${e.message} (codigo ${e.codigo})", e )

            return JSONObject().apply {
                put( "exito", false )
                put( "erro", e::class.simpleName ?: "ErroDescoñecido" )
                put( "codigo", e.codigo )
            }

        }

    }

    suspend fun get( ruta: String, campos: Map<String, Any> = emptyMap() ) = peticion( "GET", ruta, campos )
    suspend fun post( ruta: String, campos: Map<String, Any> = emptyMap() ) = peticion( "POST", ruta, campos )
    suspend fun delete( ruta: String, campos: Map<String, Any> = emptyMap() ) = peticion( "DELETE", ruta, campos )


    private suspend fun realizarPeticion( metodo: String, ruta: String, campos: Map<String, Any> ): Pair<String, Int> {

        val resultado = withContext( Dispatchers.IO ) {

            val corpo = if ( campos.isNotEmpty() ) {
                FormBody.Builder().apply {
                    campos.forEach { ( nome, valor ) -> add( nome, valor.toString() ) }
                }.build()
            } else null

            val builder = Request.Builder().url( URL_BASE + ruta )

            when ( metodo ) {
                "GET" -> builder.get()
                "POST" -> builder.post( corpo ?: FormBody.Builder().build() )
                "DELETE" -> if ( corpo != null ) builder.delete( corpo ) else builder.delete()
                else -> throw IllegalArgumentException( "Método non soportado: $metodo" )
            }

            val peticion = builder.build()

            val respostaBruta = try {

                cliente.newCall( peticion ).execute().use { resposta ->
                    Pair( resposta.body.string(), resposta.code )
                }

            } catch ( _: SocketTimeoutException ) {

                //Quizais haxa que revisar estas mensaxes e tratar de traducilas a nivel de usuario?

                throw TempoEsgotadoException( "A petición tardou demasiado en responder" )

            } catch ( _: IOException ) {

                throw SenConexionException( "Non hai conexión ou o servidor non responde" )

            }

            return@withContext respostaBruta

        }

        return resultado

    }

    private fun interpretarResposta( rbody: String, rcode: Int ): JSONObject {

        val resposta = try {
            JSONObject( rbody )
        } catch ( _: JSONException ) {
            throw OutroErroApiException(
                "Resposta non válida da API con código HTTP $rcode",
                codigo = ""
            )
        }

        if ( rcode !in 200..< 300 ) {
            xestionarRespostaErro( rcode, resposta )
        }

        return resposta

    }

    private fun xestionarRespostaErro( rcode: Int, resposta: JSONObject ) {

        if ( !resposta.has( "erro" ) ) {
            throw OutroErroApiException(
                "Obxecto de resposta inválido da API con código HTTP $rcode",
                codigo = ""
            )
        }

        val datosErro = resposta.optJSONObject( "erro" )

        val mensaxe = datosErro?.optString( "mensaxe" ) ?: "Erro descoñecido"
        val codigo = datosErro?.optString( "codigo" ) ?: ""

        val erro: ApiException = when ( rcode ) {
            400, 404 -> PeticionInvalidaException( mensaxe, codigo )
            401 -> AutenticacionException( mensaxe, codigo )
            403 -> PermisoException( mensaxe, codigo )
            429 -> LimiteTaxaException( mensaxe, codigo )
            503 -> ServidorCaidoException( mensaxe, codigo )
            else -> OutroErroApiException( mensaxe, codigo )
        }

        throw erro

    }

}