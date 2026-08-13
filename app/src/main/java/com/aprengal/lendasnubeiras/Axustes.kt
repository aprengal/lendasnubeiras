package com.aprengal.lendasnubeiras

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import okio.IOException

enum class NomeOpcion( val nome: String ) {

    SESIONUSUARIO( "sesion_usuario" ),
    SESIONANONIMA( "sesion_anonima" ),
    TEMA( "tema" ),
    IDIOMA( "idioma" );

}

object Axustes {

    private lateinit var appContext: Context
    private val cache = mutableMapOf<String, Any>()

    private val Context.opcions by preferencesDataStore( name = "opcions" )

    suspend fun arrancar( contexto: Context ) {

        require( !::appContext.isInitialized ) { "A aplicación xa estaba inicializada!" }

        appContext = contexto.applicationContext
        appContext.opcions.data.first().asMap().forEach { ( clave,  valor ) -> cache[ clave.name ] = valor }

    }

    fun <T : Any> collerOpcion( nomeOpcion: NomeOpcion, predeterminado: T ): T {
        @Suppress( "UNCHECKED_CAST" )
        return ( cache[ nomeOpcion.nome ] as? T ) ?: predeterminado
    }

    suspend fun gardarOpcion( nomeOpcion: NomeOpcion, valor: Any ): Boolean {

        try {

            val nome = nomeOpcion.nome
            appContext.opcions.edit { opcions ->

                when ( valor ) {
                    is String -> opcions[ stringPreferencesKey( nome ) ] = valor
                    is Int -> opcions[ intPreferencesKey( nome ) ] = valor
                    is Boolean -> opcions[ booleanPreferencesKey( nome ) ] = valor
                    is Float -> opcions[ floatPreferencesKey( nome ) ] = valor
                    is Long -> opcions[ longPreferencesKey( nome ) ] = valor
                    else -> throw IllegalArgumentException( "Tipo non soportado: ${valor::class.simpleName}" )
                }

            }

            cache[ nome ] = valor
            return true

        } catch ( _: IOException ) {}

        return false

    }

    suspend fun borrarOpcion( nomeOpcion: NomeOpcion ): Boolean {

        try {

            val nome = nomeOpcion.nome

            appContext.opcions.edit { opcions ->
                opcions.asMap().keys.firstOrNull { clave -> clave.name == nome } ?.let { clave -> opcions.remove( clave ) }
            }

            cache.remove( nome )
            return true

        } catch ( _: IOException ) {}

        return false

    }

    fun collerSesionLocal(sufixo: String ): String {
        return cache[ NomeOpcion.SESIONUSUARIO.nome + "_" + sufixo ] as? String ?: ""
    }

    suspend fun gardarSesionLocal(sufixo: String, datos: String ) {

        try {
            val nome = NomeOpcion.SESIONUSUARIO.nome + "_" + sufixo
            appContext.opcions.edit { opcions -> opcions[ stringPreferencesKey(nome ) ] = datos }
        } catch ( _: IOException ) {}

    }

    suspend fun borrarSesionsLocais(): Boolean {

        val nome = NomeOpcion.SESIONUSUARIO.nome

        try {

            appContext.opcions.edit { opcions ->
                opcions.asMap().keys.filter { clave -> clave.name.startsWith( nome ) }
                    .forEach { clave -> opcions.remove(stringPreferencesKey( clave.name ) ) }
            }

            return true

        } catch ( _: IOException ) {}

        return false

    }

}