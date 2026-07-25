package com.example.lendasnubeiras.localizacion

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import org.json.JSONObject
import java.io.FileNotFoundException

object Localizacion {

    private lateinit var appContext: Context

    private val IDIOMAS_SOPORTADOS = setOf( "es_ES", "gl_ES", "en_GB" )

    private val traducions = mutableMapOf<String, MutableMap<String, String>>()

    private val traducionsPlurais = mutableMapOf<String, MutableMap<String, Map<String, String>>>()

    private const val PENDENTE = "PENDENTE!!"

    private lateinit var idiomaActual: String

    fun arrancar( contexto: Context ) {

        if ( ::appContext.isInitialized || ::idiomaActual.isInitialized ) { return }

        appContext = contexto
        val prefs = appContext.getSharedPreferences( "prefs_idioma", Context.MODE_PRIVATE )
        val idiomaGardado = prefs.getString( "IDIOMA", "es_ES" ) ?: "es_ES"

        idiomaActual = if ( idiomaGardado in IDIOMAS_SOPORTADOS ) idiomaGardado else "es_ES"

    }

    fun collerIdioma(): String {
        return idiomaActual
    }

    fun gardarIdioma( novoIdioma: String ) {

        if ( idiomaActual == novoIdioma || novoIdioma !in IDIOMAS_SOPORTADOS ) return

        idiomaActual = novoIdioma

        val prefs = appContext.getSharedPreferences( "prefs_idioma", Context.MODE_PRIVATE )
        prefs.edit { putString( "IDIOMA", novoIdioma ) }

        traducions.clear()
        traducionsPlurais.clear()

    }

    private fun collerArquivoIdioma( dominio: String ): String {

        val carpeta = "cadeas/$dominio"
        val arquivoBase = "$dominio-${ idiomaActual.substringBefore( '_' ) }.json"
        val arquivoRexion = "$dominio-$idiomaActual.json"

        val arquivos = appContext.assets.list( carpeta )!!

        val direccionArquivo = if (arquivoBase in arquivos) arquivoBase else arquivoRexion
        val ruta = "$carpeta/$direccionArquivo"

        return appContext.assets.open( ruta ).bufferedReader().use { it.readText() }

    }

    private fun cargarDominio( dominio: String ) {

        if ( traducions.containsKey( dominio ) ) {
            return
        }

        try {

            val jsonString = collerArquivoIdioma( dominio )
            val jsonObject = JSONObject( jsonString )

            val dominioMap = traducions.getOrPut( dominio ) { mutableMapOf() }
            val claves = jsonObject.keys()

            while ( claves.hasNext() ) {

                val clave = claves.next()
                val valor = jsonObject.get(clave)

                if ( valor is JSONObject ) {

                    val mapaPlural = mutableMapOf<String, String>()
                    val clavesPlural = valor.keys()

                    while ( clavesPlural.hasNext() ) {
                        val clavePlural = clavesPlural.next()
                        mapaPlural[clavePlural] = valor.getString( clavePlural )
                    }

                    val pluralDominioMap = traducionsPlurais.getOrPut(dominio ) { mutableMapOf() }
                    pluralDominioMap[ clave ] = mapaPlural

                } else {
                    dominioMap[ clave ] = valor.toString()
                }

            }

        } catch ( _: FileNotFoundException ) {
            Log.d( "IDIOMA", "O dominio $dominio non existe para o idioma $idiomaActual" )
        }

    }

    fun quitarDominio( dominio: String ) {
        traducions.remove( dominio )
        traducionsPlurais.remove( dominio )
    }

    fun l10n( indice: String, dominio: String ): String {
        cargarDominio( dominio )
        return traducions[ dominio ]?.get( indice ) ?: PENDENTE
    }

    fun l10nPlural( indice: String, num: Int, dominio: String ): String {

        cargarDominio( dominio )
        val listaPlurais = traducionsPlurais[ dominio ]?.get( indice )

        if ( listaPlurais != null ) {

            val clavePlural = when {
                listaPlurais.containsKey( num.toString() ) -> num.toString()
                num == 1 -> "s"
                else -> "pl"
            }

            return String.format( listaPlurais[ clavePlural ] ?: PENDENTE, num )

        }

        return PENDENTE

    }

}