package com.aprengal.lendasnubeiras

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import okio.IOException

object Axustes {

    private lateinit var appContext: Context
    private val cache = mutableMapOf<String, Any>()

    private val Context.opcions by preferencesDataStore( name = "opcions" )

    suspend fun arrancar( contexto: Context ) {

        require( !::appContext.isInitialized ) { "A aplicación xa estaba inicializada!" }

        appContext = contexto.applicationContext
        appContext.opcions.data.first().asMap().forEach { ( clave,  valor ) -> cache[ clave.name ] = valor }

    }

    fun <T : Any> collerOpcion( nome: String, predeterminado: T ): T {
        @Suppress( "UNCHECKED_CAST" )
        return ( cache[ nome ] as? T ) ?: predeterminado
    }

    suspend fun gardarOpcion( nome: String, valor: Any ): Boolean {

        try {

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