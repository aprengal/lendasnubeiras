package com.aprengal.lendasnubeiras

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.aprengal.lendasnubeiras.ui.tema.Tema.Variante
import kotlinx.coroutines.flow.first
import okio.IOException

sealed class Opcion<T : Any>( val predeterminado: T ) {

    val nome: String = this::class.simpleName!!.lowercase()

    data object SesionUsuario: Opcion<String>( "" )
    data object SesionAnonima: Opcion<Boolean>( false )
    data object Tema: Opcion<String>( Variante.PREDETERMINADO.clave )
    data object Idioma: Opcion<String>( "" )
    data object IdDispositivo: Opcion<String> ( "" )

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

    fun <T : Any> collerOpcion( opcion: Opcion<T> ): T {
        @Suppress( "UNCHECKED_CAST" )
        return ( cache[ opcion.nome ] as? T ) ?: opcion.predeterminado
    }

    suspend fun <T : Any> gardarOpcion( opcion: Opcion<T>, valor: T ): Boolean {

        try {

            val nome = opcion.nome
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

        } catch ( _: IOException ) {}

        return false

    }

    suspend fun <T : Any> borrarOpcion( opcion: Opcion<T> ): Boolean {

        try {

            val nome = opcion.nome
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

        } catch ( _: IOException ) {}

        return false

    }

    fun collerSesionLocal( sufixo: String ): String {
        return cache[ Opcion.SesionUsuario.nome + "_" + sufixo ] as? String ?: ""
    }

    suspend fun gardarSesionLocal( sufixo: String, datos: String ) {

        try {
            val nome = Opcion.SesionUsuario.nome + "_" + sufixo
            appContext.opcions.edit { opcions -> opcions[ stringPreferencesKey( nome ) ] = datos }
        } catch ( _: IOException ) {}

    }

    suspend fun borrarSesionsLocais(): Boolean {

        val nome = Opcion.SesionUsuario.nome

        try {

            appContext.opcions.edit { opcions ->
                opcions.asMap().keys.filter { clave -> clave.name.startsWith( nome ) }
                    .forEach { clave -> opcions.remove( stringPreferencesKey( clave.name ) ) }
            }

            return true

        } catch ( _: IOException ) {}

        return false

    }

}