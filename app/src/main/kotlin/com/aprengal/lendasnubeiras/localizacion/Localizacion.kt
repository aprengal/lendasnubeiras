package com.aprengal.lendasnubeiras.localizacion

import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.os.LocaleListCompat
import com.aprengal.lendasnubeiras.configuracion.Axustes.collerOpcion
import com.aprengal.lendasnubeiras.configuracion.Axustes.gardarOpcion
import com.aprengal.lendasnubeiras.configuracion.Opcion
import com.aprengal.lendasnubeiras.configuracion.haiLector
import com.aprengal.lendasnubeiras.configuracion.reiniciarAplicacion
import org.json.JSONObject
import java.io.FileNotFoundException

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

        if ( ::appContext.isInitialized && idiomaActual.value != Idioma.NADA ) return

        appContext = contexto.applicationContext
        idiomaActual.value = Idioma.escollerIdiomaAplicacion(
            collerOpcion( Opcion.Idioma ),
            Resources.getSystem().configuration.locales[ 0 ].toString()
        )

        if ( appContext.haiLector() ) {
            val idiomaOpcions = LocaleListCompat.forLanguageTags( idiomaActual.value.codigoRexion.replace( "_", "-" ) )
            AppCompatDelegate.setApplicationLocales( idiomaOpcions )
        }

    }

    suspend fun gardarIdioma( novoIdioma: Idioma ) {

        if ( idiomaActual.value == novoIdioma || !gardarOpcion( Opcion.Idioma, novoIdioma.codigoRexion ) ) return

        idiomaActual.value = novoIdioma

        if ( appContext.haiLector() ) appContext.reiniciarAplicacion()

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
        return traducions[ dominio ]?.get( indice ) ?: idiomaActual.value.pendente
    }

    fun l10nPlural( indice: String, num: Int, dominio: String ): String {

        cargarDominio( dominio )
        val listaPlurais = traducionsPlurais[ dominio ]?.get( indice ) ?: return idiomaActual.value.pendente

        val clavePlural = when {
            listaPlurais.containsKey( num.toString() ) -> num.toString()
            num == 1 -> "s"
            else -> "pl"
        }

        return listaPlurais[ clavePlural ]?.let { clave -> String.format( clave, num ) } ?: idiomaActual.value.pendente

    }

}