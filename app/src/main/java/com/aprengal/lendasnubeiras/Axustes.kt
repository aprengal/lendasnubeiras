package com.aprengal.lendasnubeiras

import android.content.Context

import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import okio.IOException

object Axustes {

    private lateinit var appContext: Context

    private val Context.opcions by preferencesDataStore( name = "opcions" )

    fun arrancar( contexto: Context ) {
        if ( ::appContext.isInitialized ) return
        appContext = contexto.applicationContext
    }

    suspend fun <T : Any> collerOpcion( nome: String, predeterminado: T ): T {

        val opcions = appContext.opcions.data.first()

        val valor = when ( predeterminado ) {
            is String  -> opcions[ stringPreferencesKey( nome ) ] ?: predeterminado
            is Int     -> opcions[ intPreferencesKey( nome ) ] ?: predeterminado
            is Boolean -> opcions[ booleanPreferencesKey( nome ) ] ?: predeterminado
            is Float   -> opcions[ floatPreferencesKey( nome ) ] ?: predeterminado
            is Long    -> opcions[ longPreferencesKey( nome ) ] ?: predeterminado
            else -> throw IllegalArgumentException( "Tipo non soportado: ${predeterminado::class.simpleName}" )
        }

        @Suppress( "UNCHECKED_CAST" )
        return valor as T

    }

    suspend fun gardarOpcion( nome: String, valor: Any ): Boolean {

        var resultado: Boolean

        try {

            appContext.opcions.edit { opcions ->

                when (valor) {
                    is String -> opcions[stringPreferencesKey(nome)] = valor
                    is Int -> opcions[intPreferencesKey(nome)] = valor
                    is Boolean -> opcions[booleanPreferencesKey(nome)] = valor
                    is Float -> opcions[floatPreferencesKey(nome)] = valor
                    is Long -> opcions[longPreferencesKey(nome)] = valor
                    else -> throw IllegalArgumentException("Tipo non soportado: ${valor::class.simpleName}")
                }

            }

            resultado = true

        } catch ( _: IOException ) {
            resultado = false
        }

        return  resultado

    }

}