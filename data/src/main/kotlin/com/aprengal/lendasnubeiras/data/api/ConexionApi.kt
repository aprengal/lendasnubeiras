package com.aprengal.lendasnubeiras.data.api

import android.content.Context
import android.util.Log
import com.aprengal.lendasnubeiras.data.axustes.Axustes.collerOpcion
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
import com.aprengal.lendasnubeiras.data.axustes.Opcion
import com.aprengal.lendasnubeiras.data.BuildConfig
import com.aprengal.lendasnubeiras.data.api.RespostaApi.DatosActividades
import com.aprengal.lendasnubeiras.data.api.RespostaApi.RespostaXenerica
import com.aprengal.lendasnubeiras.data.api.RespostaApi.SesionUsuario

object ConexionApi {

    enum class MetodoApi { GET, POST, DELETE }

    private class ApiException( mensaxe: String, val codigo: Int ) : Exception( mensaxe )

    private const val URL_BASE = BuildConfig.API_URL

    private lateinit var appContext: Context

    fun arrancar( contexto: Context ) {
        if ( ::appContext.isInitialized ) return
        appContext = contexto.applicationContext
    }

    private val cliente = OkHttpClient.Builder()
        .connectTimeout( 5, TimeUnit.SECONDS )
        .readTimeout( 5, TimeUnit.SECONDS )
        .writeTimeout( 5, TimeUnit.SECONDS )
        .build()

    suspend fun <T : RespostaApi> procesarPeticion( metodoApi: MetodoApi, rutaApi: RutasApi, campos: Map<String, Any> = emptyMap() ): T {

        val datos = peticion( metodoApi, rutaApi, campos )

        val saida = when( rutaApi ) {
            RutasApi.VALIDACION -> SesionUsuario( datos )
            RutasApi.REXISTRO, RutasApi.ACCESO, RutasApi.REPORTES -> RespostaXenerica( datos )
            RutasApi.ACTUALIZAR -> DatosActividades(datos)
        }

        @Suppress( "UNCHECKED_CAST" )
        return saida as T

    }

    private suspend fun peticion(metodoApi: MetodoApi, rutaApi: RutasApi, campos: Map<String, Any> ): JSONObject {

        try {
            return realizarPeticion( metodoApi, rutaApi, campos )
        } catch ( e: ApiException ) {

            e.message?.let { mensaxe -> Log.i( "API", mensaxe ) }
            return JSONObject().apply { put( "codigo", e.codigo ) }

        }

    }

    private suspend fun realizarPeticion(metodoApi: MetodoApi, ruta: RutasApi, campos: Map<String, Any> ): JSONObject {

        val sesion = collerOpcion( Opcion.SesionUsuario )

        val resultado = withContext( Dispatchers.IO ) {

            val corpo = if ( campos.isNotEmpty() ) {
                FormBody.Builder().apply {
                    campos.forEach { ( nome, valor ) -> add( nome, valor.toString() ) }
                }.build()
            } else null

            val builder = Request.Builder().url( URL_BASE + ruta.ruta )
            if ( sesion.isNotBlank() ) builder.addHeader( "permiso", "Bearer $sesion" )

            when ( metodoApi ) {
                MetodoApi.GET -> builder.get()
                MetodoApi.POST -> builder.post( corpo ?: FormBody.Builder().build() )
                MetodoApi.DELETE -> if ( corpo != null ) builder.delete( corpo ) else builder.delete()
            }

            val peticion = builder.build()

            val respostaBruta = try {

                cliente.newCall( peticion ).execute().use { resposta ->

                    val corpo = resposta.body.string()

                    if ( corpo.isNotBlank() ) {
                        JSONObject( corpo ).apply { put( "codigo", resposta.code ) }
                    } else {
                        JSONObject().apply { put( "codigo", resposta.code ) }
                    }

                }

            } catch ( _: SocketTimeoutException ) {

                throw ApiException( "A petición tardou demasiado en responder", 408 )

            } catch ( _: IOException ) {

                throw ApiException( "Non hai conexión ou o servidor non responde", 0 )

            } catch ( _: JSONException ) {

                throw ApiException( "A API respondeu con formato descoñecido", 1 )

            }

            respostaBruta

        }

        return resultado

    }

}