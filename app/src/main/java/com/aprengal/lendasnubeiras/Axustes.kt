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

}