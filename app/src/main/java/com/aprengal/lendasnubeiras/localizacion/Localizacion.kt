package com.aprengal.lendasnubeiras.localizacion

import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.os.LocaleListCompat
import com.aprengal.lendasnubeiras.Axustes.collerOpcion
import com.aprengal.lendasnubeiras.Axustes.gardarOpcion
import com.aprengal.lendasnubeiras.NomeOpcion
import com.aprengal.lendasnubeiras.configuracion.haiLector
import com.aprengal.lendasnubeiras.reiniciarAplicacion
import org.json.JSONObject
import java.io.FileNotFoundException

enum class Idioma( val nome: String, val codigo: String, val rexion: String, val pendente: String ) {

    GALEGO( "Galego", "gl", "ES", pendente = "PENDENTE!!" ),
    CASTELAN( "Español", "es", "ES", pendente = "¡PENDIENTE!" ),
    INGLES( "English", "en", "GB", pendente = "PENDING!!" ),

    NADA( "", "", "", "" );

    val codigoRexion: String get() = "${codigo}_$rexion"

    companion object {

        fun escollerIdiomaAplicacion( vararg codigos: String ): Idioma {
            codigos.forEach { codigoRexion -> entries.find { idioma -> idioma.codigoRexion == codigoRexion }?.let { resultado -> return resultado } }
            return CASTELAN
        }

        fun escollerIdioma( etiqueta: String ): Idioma {
            return entries.find { idioma -> idioma.codigoRexion == etiqueta } ?: NADA
        }

    }

}

//TODO: Test unitario para verificar que todas as cadeas están definidas.
//Neste test non se miraría o valor real e para iso habería facer unha revisión manual
object Localizacion {

    private lateinit var appContext: Context

    private val traducions = mutableMapOf<String, MutableMap<String, String>>()
    private val traducionsPlurais = mutableMapOf<String, MutableMap<String, Map<String, String>>>()

    var idiomaActual: MutableState<Idioma> = mutableStateOf( Idioma.NADA )
        private set

    private var dominiosRecordados: MutableSet<String> = mutableSetOf()

    var recordarDominios: Boolean = false

        set( valor ) {

            if ( field == valor ) return
            field = valor

            if ( !field && dominiosRecordados.isNotEmpty() ) {
                dominiosRecordados.forEach { dominio -> descargarDominio( dominio ) }
            }

            dominiosRecordados.clear()

        }

    fun arrancar( contexto: Context ) {

        require( !::appContext.isInitialized && idiomaActual.value == Idioma.NADA ) { "A aplicación xa estaba inicializada!" }

        appContext = contexto.applicationContext
        idiomaActual.value = Idioma.escollerIdiomaAplicacion(
            collerOpcion( NomeOpcion.IDIOMA, "" ),
            Resources.getSystem().configuration.locales[ 0 ].toString()
        )

        if ( haiLector( contexto ) ) {
            val idiomaOpcions = LocaleListCompat.forLanguageTags( idiomaActual.value.codigoRexion.replace( "_", "-" ) )
            AppCompatDelegate.setApplicationLocales( idiomaOpcions )
        }

    }

    suspend fun gardarIdioma( novoIdioma: Idioma, reiniciar: Boolean = false ) {

        if ( idiomaActual.value == novoIdioma || !gardarOpcion( NomeOpcion.IDIOMA, novoIdioma.codigoRexion ) ) return

        idiomaActual.value = novoIdioma

        if ( reiniciar ) reiniciarAplicacion( appContext )

        traducions.clear()
        traducionsPlurais.clear()

    }

    private fun collerArquivoIdioma( dominio: String ): String {

        val carpeta = "cadeas/$dominio"
        val arquivoBase = "$dominio-${ idiomaActual.value.codigo }.json"
        val arquivoRexion = "$dominio-${ idiomaActual.value.codigoRexion }.json"

        val arquivos = appContext.assets.list( carpeta )!!

        val direccionArquivo = if ( arquivoBase in arquivos ) arquivoBase else arquivoRexion
        val ruta = "$carpeta/$direccionArquivo"

        return appContext.assets.open( ruta ).bufferedReader().use { arquivo -> arquivo.readText() }

    }

    private fun cargarDominio( dominio: String ) {

        if ( traducions.containsKey( dominio ) ) return

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

            if ( recordarDominios && dominio.startsWith( "actividade" ) ) {
                dominiosRecordados.add( dominio )
            }

        } catch ( e: FileNotFoundException ) {
            Log.w( "IDIOMA", "O dominio $dominio non existe para o idioma $idiomaActual", e )
        }

    }

    private fun descargarDominio( dominio: String ) {
        traducions.remove( dominio )
        traducionsPlurais.remove( dominio )
    }

    fun l10n( indice: String, dominio: String ): String {

        cargarDominio( dominio )

        //Log.w(  "IDIOMA", "Falta a clave '$indice' no dominio '$dominio' para o idioma $idiomaActual" )
        return traducions[ dominio ]?.get( indice ) ?: idiomaActual.value.pendente

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

            //Log.w( "IDIOMA", "Falta a clave plural '$clavePlural' para '$indice' no dominio '$dominio' para o idioma ${ idiomaActual.value }" )
            val patron = listaPlurais[ clavePlural ] ?: idiomaActual.value.pendente

            return String.format( patron, num )

        }

        //Log.w( "IDIOMA", "Falta a entrada plural '$indice' no dominio '$dominio' para o idioma ${ idiomaActual.value }" )
        return idiomaActual.value.pendente

    }

}