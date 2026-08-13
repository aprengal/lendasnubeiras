package com.aprengal.lendasnubeiras.configuracion.api

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.aprengal.lendasnubeiras.NomeOpcion.SESIONUSUARIO
import com.aprengal.lendasnubeiras.Axustes.collerOpcion
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

enum class MetodoPeticion {
    GET, POST, DELETE;
}

object Conexion {

    private const val URL_BASE = BuildConfig.API_URL

    private lateinit var appContext: Context

    fun arrancar( contexto: Context ) {
        require( !::appContext.isInitialized ) { "A aplicación xa estaba inicializada!" }
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

    suspend fun <T : RespostaApi> procesarPeticion( metodoPeticion: MetodoPeticion, rutaApi: RutaApi, campos: Map<String, Any> = emptyMap() ): T {

        val datos = peticion( metodoPeticion, rutaApi, campos )

        val saida = when( rutaApi ) {
            RutaApi.VALIDARSESION -> SesionUsuario( datos )
            RutaApi.REXISTRO, RutaApi.INICIOSESION -> RespostaXenerica( datos )
        }

        @Suppress( "UNCHECKED_CAST" )
        return saida as T

    }

    private suspend fun peticion(metodoPeticion: MetodoPeticion, rutaApi: RutaApi, campos: Map<String, Any> ): JSONObject {

        val ruta = rutaApi.ruta

        if ( !haiConexion() ) throw SenConexionException( "O dispositivo non ten conexión a internet" )

        val ( codigo, contido ) = realizarPeticion( metodoPeticion, ruta, campos )
        return interpretarResposta( codigo, contido )

    }

    private suspend fun realizarPeticion(metodoPeticion: MetodoPeticion, ruta: String, campos: Map<String, Any> ): Pair<Int, String> {

        val clave = collerOpcion( SESIONUSUARIO, "" )

        val resultado = withContext( Dispatchers.IO ) {

            val corpo = if ( campos.isNotEmpty() ) {
                FormBody.Builder().apply {
                    campos.forEach { ( nome, valor ) -> add( nome, valor.toString() ) }
                }.build()
            } else null

            val builder = Request.Builder().url( URL_BASE + ruta ).addHeader( "permiso", "Bearer $clave" )

            when ( metodoPeticion.name ) {
                "GET" -> builder.get()
                "POST" -> builder.post( corpo ?: FormBody.Builder().build() )
                "DELETE" -> if ( corpo != null ) builder.delete( corpo ) else builder.delete()
                else -> throw IllegalArgumentException( "Método non soportado: $metodoPeticion" )
            }

            val peticion = builder.build()

            val respostaBruta = try {

                cliente.newCall( peticion ).execute().use { resposta ->
                    Pair( resposta.code, resposta.body.string() )
                }

            } catch ( _: SocketTimeoutException ) {

                throw TempoEsgotadoException( "A petición tardou demasiado en responder" )

            } catch ( _: IOException ) {

                throw SenConexionException( "Non hai conexión ou o servidor non responde" )

            }

            respostaBruta

        }

        return resultado

    }

    private fun interpretarResposta( codigo: Int, contido: String ): JSONObject {

        val resposta = try {
            JSONObject( contido )
        } catch ( _: JSONException ) {
            throw OutroErroApiException(
                "Resposta non válida da API con código HTTP $codigo",
                codigo = ""
            )
        }

        if ( codigo !in 200..< 300 ) {
            xestionarRespostaErro( codigo, resposta )
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