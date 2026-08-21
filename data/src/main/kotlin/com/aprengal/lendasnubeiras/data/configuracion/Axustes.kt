package com.aprengal.lendasnubeiras.data.configuracion

import android.content.Context
import android.util.Log
import androidx.datastore.core.CorruptionException
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import okio.IOException

sealed class Opcion<T : Any>( val nome: String, val predeterminado: T ) {

    data object SesionUsuario: Opcion<String>( "sesion-usuario", "" )
    data object SesionAnonima: Opcion<Boolean>( "sesion-anonima",false )
    data object Tema: Opcion<String>( "tema", "predeterminado" )
    data object Idioma: Opcion<String>( "idioma", "" )
    data object IdDispositivo: Opcion<String> ( "id-dispositivo", "" )

}

object Axustes {

    private lateinit var appContext: Context
    private val cache = mutableMapOf<String, Any>()

    private val Context.opcions by preferencesDataStore( name = "opcions" )

    suspend fun arrancar( contexto: Context ) {

        if ( ::appContext.isInitialized ) return

        appContext = contexto.applicationContext

        try {
            appContext.opcions.data.first().asMap().forEach { ( clave,  valor ) -> cache[ clave.name ] = valor }
        } catch ( e: CorruptionException ) {
            Log.w( "OPCIONS", "As opcións están corruptas: ${ e.message }" )
        } catch ( e: IOException ) {
            Log.w( "OPCIONS", "Fallou a carga de opcións: ${ e.message }" )
        }

    }

    fun <T : Any> collerOpcion( opcion: Opcion<T> ): T {
        @Suppress( "UNCHECKED_CAST" )
        return ( cache[ opcion.nome ] as? T ) ?: opcion.predeterminado
    }

    suspend fun <T : Any> gardarOpcion( opcion: Opcion<T>, valor: T ): Boolean {

        val nome = opcion.nome

        try {

            appContext.opcions.edit { opcions ->

                when ( valor ) {
                    is String -> opcions[ stringPreferencesKey( nome ) ] = valor
                    is Int -> opcions[ intPreferencesKey( nome ) ] = valor
                    is Boolean -> opcions[ booleanPreferencesKey( nome ) ] = valor
                    //is Float -> opcions[ floatPreferencesKey( nome ) ] = valor
                    is Long -> opcions[ longPreferencesKey( nome ) ] = valor
                    else -> throw IllegalArgumentException( "Tipo non soportado: ${valor::class.simpleName}" )
                }

            }

            cache[ nome ] = valor
            return true

        } catch ( e: IOException ) {
            Log.w( "OPCIONS", "Non se puido gardar a opción $nome: ${ e.message }" )
        }

        return false

    }

    suspend fun <T : Any> borrarOpcion( opcion: Opcion<T> ): Boolean {

        val nome = opcion.nome

        try {

            val predeterminado = opcion.predeterminado

            appContext.opcions.edit { opcions ->
                when ( predeterminado ) {
                    is String -> opcions.remove( stringPreferencesKey( nome ) )
                    is Int -> opcions.remove( intPreferencesKey( nome ) )
                    is Boolean -> opcions.remove( booleanPreferencesKey( nome ) )
                    //is Float -> opcions.remove( floatPreferencesKey( nome ) )
                    is Long -> opcions.remove( longPreferencesKey( nome ) )
                    else -> throw IllegalArgumentException( "Tipo non soportado: ${predeterminado::class.simpleName}" )
                }
            }

            cache.remove( nome )
            return true

        } catch ( e: IOException ) {
            Log.w( "OPCIONS", "Non se puido borrar a opción $nome: ${ e.message }" )
        }

        return false

    }

    fun collerSesionLocal( sufixo: String ): String {
        return cache[ Opcion.SesionUsuario.nome + "_" + sufixo ] as? String ?: ""
    }

    suspend fun gardarSesionLocal( sufixo: String, datos: String ): Boolean {

        try {
            val nome = Opcion.SesionUsuario.nome + "_" + sufixo
            appContext.opcions.edit { opcions -> opcions[ stringPreferencesKey( nome ) ] = datos }
            return true
        } catch ( e: IOException ) {
            Log.i( "OPCIONS", "Non se puido gardar unha sesión local: ${ e.message }" )
        }

        return false

    }

    suspend fun borrarSesionsLocais(): Boolean {

        val nome = Opcion.SesionUsuario.nome

        try {

            appContext.opcions.edit { opcions ->
                opcions.asMap().keys.filter { clave -> clave.name.startsWith( nome ) }
                    .forEach { clave -> opcions.remove( stringPreferencesKey( clave.name ) ) }
            }

            return true

        } catch ( e: IOException ) {
            Log.i( "OPCIONS", "Non se puideron borrar as sesións locais: ${ e.message }" )
        }

        return false

    }

}