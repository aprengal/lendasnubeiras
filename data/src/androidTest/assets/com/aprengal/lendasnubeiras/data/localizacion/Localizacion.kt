package com.aprengal.lendasnubeiras.data.localizacion

import android.content.Context
import android.content.res.Resources
import android.icu.text.PluralRules
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.aprengal.lendasnubeiras.data.configuracion.haiLector
import com.aprengal.lendasnubeiras.data.configuracion.reiniciarAplicacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONException
import org.json.JSONObject
import java.io.FileNotFoundException
import java.util.Locale

//TODO: Test unitario para verificar que todas as cadeas están definidas.
//Neste test non se miraría o valor real e para iso habería facer unha revisión manual
object Localizacion {

    private lateinit var appContext: Context

    private val traducions: MutableMap<Dominio, Map<L10nSingular, String>> = mutableMapOf()

    private val traducionsPlurais: MutableMap<Dominio, Map<L10nPlural, Map<String, String>>> = mutableMapOf()

    private val traducionsVariantes: MutableMap<Dominio, Map<L10nVariante, Map<String, String>>> = mutableMapOf()

    private val _idiomaActual = MutableStateFlow( Idioma.NADA )

    private var regrasPlurais: PluralRules? = null

    private var dominiosRecordados: MutableSet<Dominio> = mutableSetOf()

    var recordarDominios: Boolean = false

        set( valor ) {

            if ( field == valor ) return
            field = valor

            if ( !field && dominiosRecordados.isNotEmpty() ) {
                dominiosRecordados.forEach { dominio -> descargarDominio( dominio ) }
            }

            dominiosRecordados.clear()

        }

    fun arrancar( contexto: Context ): StateFlow<Idioma> {

        if ( ::appContext.isInitialized && _idiomaActual.value != Idioma.NADA ) return _idiomaActual.asStateFlow()

        appContext = contexto.applicationContext
        _idiomaActual.value = Idioma.escollerIdiomaAplicacion(
            AppCompatDelegate.getApplicationLocales().get( 0 )?.toString() ?: "",
            Resources.getSystem().configuration.locales[ 0 ].toString()
        )

        return _idiomaActual.asStateFlow()

    }

    fun cambiarIdioma( novoIdioma: Idioma ): Boolean {

        if ( _idiomaActual.value == novoIdioma ) return true

        _idiomaActual.value = novoIdioma
        if ( appContext.haiLector() ) appContext.reiniciarAplicacion()

        traducions.clear()
        traducionsPlurais.clear()
        regrasPlurais = null

        return true

    }

    private fun collerArquivoIdioma( dominio: Dominio ): String {

        val dominioTexto = dominio.nome
        val carpeta = "cadeas/$dominioTexto"
        val arquivoBase = "$dominioTexto-${ _idiomaActual.value.codigo }.json"
        val arquivoRexion = "$dominioTexto-${ _idiomaActual.value.codigoRexion }.json"

        val arquivos = appContext.assets.list( carpeta )!!

        val direccionArquivo = if ( arquivoBase in arquivos ) arquivoBase else arquivoRexion
        val ruta = "$carpeta/$direccionArquivo"

        return appContext.assets.open( ruta ).bufferedReader().use { arquivo -> arquivo.readText() }

    }

    private fun cargarDominio( dominio: Dominio ) {

        if ( traducions.containsKey( dominio ) ) return

        try {

            val cadeasSingular = mutableMapOf<L10nSingular, String>()
            val cadeasPlurais = mutableMapOf<L10nPlural, Map<String, String>>()
            val cadeasVariantes = mutableMapOf<L10nVariante, Map<String, String>>()

            val clavesSingulares = L10nSingular.entries.filter { elemento -> elemento.dominio == dominio }
            val clavesPlurais = L10nPlural.entries.filter { elemento -> elemento.dominio == dominio }
            val clavesVariantes = L10nVariante.entries.filter { elemento -> elemento.dominio == dominio }

            val jsonString = collerArquivoIdioma( dominio )
            val jsonObject = JSONObject( jsonString )
            val claves = jsonObject.keys()

            while ( claves.hasNext() ) {

                val claveJSON = claves.next()
                val valor = jsonObject.get( claveJSON )

                if ( valor is JSONObject ) {

                    val clavesInternas = valor.keys().asSequence().toList()
                    val variante = clavesInternas.all { elemento -> elemento.toIntOrNull() != null }

                    if ( variante ) {

                        val clave = clavesVariantes.find { elemento -> elemento.clave == claveJSON }

                        if ( clave == null ) {
                            Log.w( "LOCALIZACION", "A cadea $clave sobra no dominio ${ dominio.nome }" )
                            continue
                        }

                        val mapaVariante = mutableMapOf<String, String>()
                        val clavesVariante = valor.keys()

                        while ( clavesVariante.hasNext() ) {
                            val claveVariante = clavesVariante.next()
                            mapaVariante[ claveVariante ] = valor.getString( claveVariante )
                        }

                        cadeasVariantes[ clave ] = mapaVariante

                    } else {

                        val clave = clavesPlurais.find { elemento -> elemento.clave == claveJSON }

                        if ( clave == null ) {
                            Log.w( "LOCALIZACION", "A cadea $clave sobra no dominio ${ dominio.nome }" )
                            continue
                        }

                        val mapaPlural = mutableMapOf<String, String>()
                        val clavesPlural = valor.keys()

                        while ( clavesPlural.hasNext() ) {
                            val clavePlural = clavesPlural.next()
                            mapaPlural[ clavePlural ] = valor.getString( clavePlural )
                        }

                        cadeasPlurais[ clave ] = mapaPlural

                    }

                } else {

                    val clave = clavesSingulares.find { elemento -> elemento.clave == claveJSON }

                    if ( clave == null ) {
                        Log.w( "LOCALIZACION", "A cadea $clave sobra no dominio ${ dominio.nome }" )
                        continue
                    }

                    cadeasSingular[ clave ] = valor.toString()

                }

            }

            traducions[ dominio ] = cadeasSingular.toMap()
            traducionsPlurais[ dominio ] = cadeasPlurais.toMap()
            traducionsVariantes[ dominio ] = cadeasVariantes.toMap()

            if ( recordarDominios && dominio.nome.startsWith( "actividade" ) ) {
                dominiosRecordados.add( dominio )
            }

        } catch ( e: FileNotFoundException ) {
            Log.wtf( "IDIOMA", "O dominio $dominio non existe para o idioma $_idiomaActual", e )
        } catch ( e: JSONException ) {
            Log.wtf( "IDIOMA", "O arquivo $dominio do idioma $_idiomaActual ten un formato incorrecto", e )
        }

    }

    private fun descargarDominio( dominio: Dominio ) {
        traducions.remove( dominio )
        traducionsPlurais.remove( dominio )
        traducionsVariantes.remove( dominio )
    }

    internal fun l10n( elemento: L10nSingular ): String {
        cargarDominio( elemento.dominio )
        return traducions[ elemento.dominio ]?.get( elemento ) ?: _idiomaActual.value.pendente
    }

    internal fun l10nPlural( elemento: L10nPlural, num: Int ): String {

        cargarDominio( elemento.dominio )
        val listaPlurais = traducionsPlurais[ elemento.dominio ]?.get( elemento ) ?: return _idiomaActual.value.pendente

        val clavePlural = categoriaPlurais( num )

        return listaPlurais[ clavePlural ]?.let { clave -> String.format( clave, num ) } ?: _idiomaActual.value.pendente

    }

    internal fun l10nVariante( elemento: L10nVariante, num: Int ): String {

        cargarDominio( elemento.dominio )
        val listaPlurais = traducionsVariantes[ elemento.dominio ]?.get( elemento ) ?: return _idiomaActual.value.pendente

        val claveVariante = num.toString() //Alternativas con números fixos

        return listaPlurais[ claveVariante ]?.let { clave -> String.format( clave, num ) } ?: _idiomaActual.value.pendente

    }

    private fun categoriaPlurais( num: Int ): String {

        regrasPlurais?.let { elemento -> return elemento.select( num.toDouble() ) }

        val local = Locale.forLanguageTag( _idiomaActual.value.codigoRexion.replace( '_', '-' ) )
        val regras = PluralRules.forLocale( local )
        regrasPlurais = regras

        return regras.select( num.toDouble() )

    }

}