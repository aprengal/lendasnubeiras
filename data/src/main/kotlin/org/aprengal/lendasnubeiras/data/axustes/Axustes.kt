package org.aprengal.lendasnubeiras.data.axustes

import android.content.Context
import android.util.Log
import androidx.datastore.core.CorruptionException
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import org.aprengal.lendasnubeiras.data.utilidades.Corrutinas.corrutina
import kotlinx.coroutines.flow.first
import okio.IOException
import java.util.UUID

/**
 * Xestor das opcións e preferencias locais da aplicación mediante DataStore.
 * Mantén unha caché en memoria para facilitar o acceso aos valores e xestiona
 * o almacenamento de información auxiliar relacionada coas sesións e co dispositivo.
 */
object Axustes {

    private lateinit var appContext: Context
    private val cache = mutableMapOf<String, Any>()

    private val Context.opcions by preferencesDataStore( name = "opcions" )

    /**
     * Inicializa o xestor de opcións e carga na caché os valores almacenados en DataStore.
     *
     * @param contexto Contexto da aplicación empregado para acceder ao almacén de preferencias.
     */
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

    /**
     * Recupera o valor dunha opción desde a caché ou devolve o seu valor predeterminado
     * se non existe ningún valor almacenado.
     *
     * @param opcion Definición da opción que se desexa consultar.
     * @return Valor almacenado ou valor predeterminado da opción.
     */
    internal fun <T : Any> collerOpcion( opcion: Opcion<T> ): T {
        @Suppress( "UNCHECKED_CAST" )
        return ( cache[ opcion.nome ] as? T ) ?: opcion.predeterminado
    }

    /**
     * Garda unha opción en DataStore e actualiza a caché en memoria tras completar
     * correctamente a operación.
     *
     * @param opcion Definición da opción que se desexa almacenar.
     * @param valor Novo valor da opción.
     * @return `true` se o almacenamento se completou correctamente.
     */
    internal suspend fun <T : Any> gardarOpcion( opcion: Opcion<T>, valor: T ): Boolean {

        val nome = opcion.nome

        try {

            appContext.opcions.edit { opcions ->

                when ( valor ) {
                    is String -> opcions[ stringPreferencesKey( nome ) ] = valor
                    is Int -> opcions[ intPreferencesKey( nome ) ] = valor
                    is Boolean -> opcions[ booleanPreferencesKey( nome ) ] = valor
                    //is Float -> opcions[ floatPreferencesKey( clave ) ] = valor
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

    /**
     * Elimina unha opción de DataStore e da caché, facendo que volva utilizarse
     * o seu valor predeterminado nas consultas posteriores.
     *
     * @param opcion Definición da opción que se desexa eliminar.
     * @return `true` se a eliminación se completou correctamente.
     */
    internal suspend fun <T : Any> borrarOpcion( opcion: Opcion<T> ): Boolean {

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

    // Xestión de información auxiliar das sesións almacenada localmente no dispositivo.
    // Permite recuperar datos dunha sesión previamente validada cando non se pode contactar co servidor.

    /**
     * Recupera da caché a información auxiliar dunha sesión identificada polo seu sufixo.
     *
     * @param sufixo Identificador do dato asociado á sesión.
     * @return Información almacenada ou unha cadea baleira se non existe.
     */
    internal fun collerSesionLocal( sufixo: String ): String {
        return cache[ Opcion.SesionUsuario.nome + "_" + sufixo ] as? String ?: ""
    }

    /**
     * Garda información auxiliar dunha sesión en DataStore e actualiza a caché.
     *
     * @param sufixo Identificador do dato asociado á sesión.
     * @param datos Información que se desexa almacenar.
     * @return `true` se o almacenamento se completou correctamente.
     */
    internal suspend fun gardarSesionLocal( sufixo: String, datos: String ): Boolean {

        try {
            val nome = Opcion.SesionUsuario.nome + "_" + sufixo
            appContext.opcions.edit { opcions -> opcions[ stringPreferencesKey( nome ) ] = datos }
            cache[ nome ] = datos
            return true
        } catch ( e: IOException ) {
            Log.i( "OPCIONS", "Non se puido gardar unha sesión local: ${ e.message }" )
        }

        return false

    }

    /**
     * Elimina de DataStore e da caché toda a información auxiliar almacenada
     * para as sesións locais.
     */
    suspend fun borrarSesionsLocais() {

        val nome = Opcion.SesionUsuario.nome

        try {

            appContext.opcions.edit { opcions ->
                opcions.asMap().keys.filter { clave -> clave.name.startsWith( nome + "_" ) }
                    .forEach { clave ->
                        opcions.remove( stringPreferencesKey( clave.name ) )
                        cache.remove( clave.name )
                    }
            }

        } catch ( e: IOException ) {
            Log.i( "OPCIONS", "Non se puideron borrar as sesións locais: ${ e.message }" )
        }

    }

    /**
     * Obtén o identificador único do dispositivo ou xera un novo se aínda non existe.
     * O identificador xerado gárdase nas opcións locais para reutilizalo posteriormente.
     *
     * @return Identificador único do dispositivo.
     */
    internal fun collerIdDispositivo(): String {

        var idDispositivo = collerOpcion( Opcion.IdDispositivo )
        if ( idDispositivo.isNotBlank() ) return idDispositivo

        idDispositivo = UUID.randomUUID().toString()
        corrutina { gardarOpcion( Opcion.IdDispositivo, idDispositivo ) }

        return idDispositivo

    }

}