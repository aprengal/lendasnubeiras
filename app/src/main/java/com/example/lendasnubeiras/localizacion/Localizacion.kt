package com.example.lendasnubeiras.localizacion

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import org.json.JSONObject
import androidx.core.content.edit

object Localizacion {

    private lateinit var appContext: Context

    private val IDIOMAS_SOPORTADOS = setOf( "es-ES", "gl-ES", "en-EN" )

    private val traducions = mutableMapOf<String, MutableMap<String, MutableMap<String, String>>>()

    private val traducionsPlurais = mutableMapOf<String, MutableMap<String, MutableMap<String, Map<String, String>>>>()

    private const val PENDENTE = "PENDENTE!!"

    private lateinit var idiomaActual: String

    fun arrancar( contexto: Context ) {

        if ( ::appContext.isInitialized || ::idiomaActual.isInitialized ) { return }

        appContext = contexto

        val prefs = appContext.getSharedPreferences( "prefs_idioma", Context.MODE_PRIVATE )
        val idiomaGuardado = prefs.getString( "IDIOMA", "es-ES" ) ?: "es-ES"

        idiomaActual = if ( idiomaGuardado in IDIOMAS_SOPORTADOS ) {
            idiomaGuardado
        } else {
            "es-ES"
        }

    }

    private fun cargarDominio( codigoIdioma: String, dominio: String ): Boolean {

        var resultado = false

        if ( idiomaActual == codigoIdioma && traducions[ codigoIdioma ]?.containsKey( dominio ) == true ) {
            return true
        }

        try {

            val path = "cadeas/$dominio/${dominio}_$codigoIdioma.json"
            val jsonString = appContext.assets.open(path ).bufferedReader().use { it.readText() }
            val jsonObject = JSONObject( jsonString )

            var dominioMap = traducions[ codigoIdioma ]

            if ( dominioMap == null ) {
                dominioMap = mutableMapOf()
                traducions[ codigoIdioma ] = dominioMap
            }

            val claveValorMap = mutableMapOf<String, String>()
            val claves = jsonObject.keys()

            while ( claves.hasNext() ) {

                val clave = claves.next()
                val valor = jsonObject.get(clave)

                if ( valor is JSONObject ) {

                    val mapaPlural = mutableMapOf<String, String>()

                    val clavesPlural = valor.keys()

                    while (clavesPlural.hasNext()) {
                        val clavePlural = clavesPlural.next()
                        mapaPlural[clavePlural] = valor.getString(clavePlural)
                    }

                    var pluralIdiomaMap = traducionsPlurais[codigoIdioma]

                    if (pluralIdiomaMap == null) {
                        pluralIdiomaMap = mutableMapOf()
                        traducionsPlurais[codigoIdioma] = pluralIdiomaMap
                    }

                    var pluralDominioMap = pluralIdiomaMap[dominio]

                    if (pluralDominioMap == null) {
                        pluralDominioMap = mutableMapOf()
                        pluralIdiomaMap[dominio] = pluralDominioMap
                    }

                    pluralDominioMap[clave] = mapaPlural

                } else {

                    claveValorMap[clave] = valor.toString()

                }

            }

            dominioMap[ dominio ] = claveValorMap

            idiomaActual = codigoIdioma

            dominioMap[ dominio ] = claveValorMap

            resultado = true

        } catch ( e: Exception ) {
            e.printStackTrace()
        }

        return resultado

    }

    fun l10n( indice: String, dominio: String = "base" ): String {

        cargarDominio( idiomaActual, dominio )

        return traducions[ idiomaActual ]?.get( dominio )?.get( indice ) ?: PENDENTE

    }

    fun collerIdioma(): String {
        return idiomaActual
    }

    fun gardar( langCode: String ) {

        Log.d( "MERDAZA", langCode )
        if ( idiomaActual == langCode || langCode !in IDIOMAS_SOPORTADOS ) { Log.d( "MEH", "non" )
            return }

        idiomaActual = langCode

        val prefs = appContext.getSharedPreferences( "prefs_idioma", Context.MODE_PRIVATE )
        prefs.edit { putString( "IDIOMA", langCode ) }

        traducions.clear()
        traducionsPlurais.clear()

    }

    fun l10nPlural( indice: String, num: Int, dominio: String = "base"): String {

        cargarDominio( idiomaActual, dominio )

        val idiomaMap = traducionsPlurais[ idiomaActual ]

        if ( idiomaMap != null ) {

            val dominioMap = idiomaMap[ dominio ]

            if ( dominioMap != null ) {

                val listaPlurais: Map<String, String>? = dominioMap[ indice ]

                if ( listaPlurais != null ) {

                    val clavePlural: String = when {
                        listaPlurais.containsKey(num.toString()) -> num.toString()
                        num == 1 -> "s"
                        else -> "pl"
                    }

                    return String.format( listaPlurais[ clavePlural ] ?: PENDENTE, num )

                }

            }

        }

        return PENDENTE

    }

    fun quitarDominio( dominio: String ) {

        traducions[ idiomaActual ]?.remove( dominio )
        traducionsPlurais[ idiomaActual ]?.remove( dominio )

    }

}