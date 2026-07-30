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
import com.aprengal.lendasnubeiras.accesibilidade.haiLector
import com.aprengal.lendasnubeiras.reiniciarAplicacion
import org.json.JSONObject
import java.io.FileNotFoundException


//Valorar converter Idioma nun obxecto ou nunha clase selada

enum class Idioma( val nome: String, val codigo: String, val rexion: String, val pendente:String ) {

    GALEGO( "Galego", "gl", "ES", pendente = "PENDENTE!!" ),
    CASTELAN( "Español", "es", "ES", pendente = "¡PENDIENTE!" ),
    INGLES( "English", "en", "GB", pendente = "PENDING!!" ),

    NADA( "", "", "", "" );

    val codigoRexion: String get() = "{$codigo}_$rexion"

    companion object {
        fun buscar( etiqueta: String ): Idioma? = entries.find { it.codigoRexion == etiqueta }
    }

}

object Localizacion {

    private lateinit var appContext: Context

    private val traducions = mutableMapOf<String, MutableMap<String, String>>()
    private val traducionsPlurais = mutableMapOf<String, MutableMap<String, Map<String, String>>>()

    var idiomaActual: MutableState<Idioma> = mutableStateOf( Idioma.NADA )
    private set

    suspend fun arrancar(contexto: Context ) {

        if ( ::appContext.isInitialized || idiomaActual.value != Idioma.NADA ) return

        appContext = contexto
        idiomaActual.value = determinarIdioma()

        if ( haiLector( contexto ) ) {
            val idiomaOpcions = LocaleListCompat.forLanguageTags( idiomaActual.value.codigoRexion.replace( "_", "-" ) )
            AppCompatDelegate.setApplicationLocales( idiomaOpcions )
        }

    }

    suspend fun determinarIdioma(): Idioma {

        return collerOpcion( "idioma", "" ).let { Idioma.buscar( it ) }
            ?: Resources.getSystem().configuration.locales[ 0 ].toString().let { Idioma.buscar( it ) }
            ?: Idioma.CASTELAN

    }

    suspend fun gardarIdioma( novoIdioma: Idioma, reiniciar: Boolean = false ) {

        if ( idiomaActual.value == novoIdioma || !gardarOpcion( "idioma", novoIdioma.codigoRexion ) ) return

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

        return appContext.assets.open( ruta ).bufferedReader().use { it.readText() }

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

        } catch ( e: FileNotFoundException ) {
            Log.w( "IDIOMA", "O dominio $dominio non existe para o idioma $idiomaActual", e )
        }

    }

    fun quitarDominio( dominio: String ) {
        traducions.remove( dominio )
        traducionsPlurais.remove( dominio )
    }

    fun l10n( indice: String, dominio: String ): String {

        cargarDominio( dominio )

        return traducions[ dominio ]?.get( indice ) ?: run {
            Log.w(  "IDIOMA", "Falta a clave '$indice' no dominio '$dominio' para o idioma $idiomaActual" )
            idiomaActual.value.pendente
        }

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

            val patron = listaPlurais[ clavePlural ] ?: run {
                Log.w( "IDIOMA", "Falta a clave plural '$clavePlural' para '$indice' no dominio '$dominio' para o idioma ${ idiomaActual.value }" )
                idiomaActual.value.pendente
            }

            return String.format( patron, num )

        }

        Log.w( "IDIOMA", "Falta a entrada plural '$indice' no dominio '$dominio' para o idioma ${ idiomaActual.value }" )
        return idiomaActual.value.pendente

    }

}