package com.aprengal.lendasnubeiras.data.localizacion

import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.aprengal.lendasnubeiras.data.configuracion.Axustes.collerOpcion
import com.aprengal.lendasnubeiras.data.configuracion.Axustes.gardarOpcion
import com.aprengal.lendasnubeiras.data.configuracion.Opcion
import com.aprengal.lendasnubeiras.data.configuracion.haiLector
import com.aprengal.lendasnubeiras.data.configuracion.reiniciarAplicacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONException
import org.json.JSONObject
import java.io.FileNotFoundException

//TODO: Test unitario para verificar que todas as cadeas están definidas.
//Neste test non se miraría o valor real e para iso habería facer unha revisión manual
object Localizacion {

    private lateinit var appContext: Context

    private val traducions: MutableMap<String, Map<String, String>> = mutableMapOf()

    private val traducionsPlurais: MutableMap<String, Map<String, Map<String, String>>> = mutableMapOf()

    private val _idiomaActual = MutableStateFlow(Idioma.NADA)
    //val idiomaActual = _idiomaActual.asStateFlow()

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

    fun arrancar( contexto: Context ): StateFlow<Idioma> {

        if ( ::appContext.isInitialized && _idiomaActual.value != Idioma.NADA ) return _idiomaActual.asStateFlow()

        appContext = contexto.applicationContext
        _idiomaActual.value = Idioma.escollerIdiomaAplicacion(
            collerOpcion( Opcion.Idioma ),
            Resources.getSystem().configuration.locales[ 0 ].toString()
        )

        if ( appContext.haiLector() ) {
            val idiomaOpcions = LocaleListCompat.forLanguageTags( _idiomaActual.value.codigoRexion.replace( "_", "-" ) )
            AppCompatDelegate.setApplicationLocales( idiomaOpcions )
        }

        return _idiomaActual.asStateFlow()

    }

    suspend fun gardarIdioma( novoIdioma: Idioma ): Boolean {

        if ( _idiomaActual.value == novoIdioma || !gardarOpcion( Opcion.Idioma, novoIdioma.codigoRexion ) ) return false

        _idiomaActual.value = novoIdioma

        if ( appContext.haiLector() ) appContext.reiniciarAplicacion()

        traducions.clear()
        traducionsPlurais.clear()

        return true

    }

    private fun collerArquivoIdioma( dominio: String ): String {

        val carpeta = "cadeas/$dominio"
        val arquivoBase = "$dominio-${ _idiomaActual.value.codigo }.json"
        val arquivoRexion = "$dominio-${ _idiomaActual.value.codigoRexion }.json"

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

            val cadeasSingular = mutableMapOf<String, String>()
            val cadeasPlurais = mutableMapOf<String, Map<String, String>>()
            val claves = jsonObject.keys()

            while ( claves.hasNext() ) {

                val clave = claves.next()
                val valor = jsonObject.get( clave )

                if ( valor is JSONObject ) {

                    val mapaPlural = mutableMapOf<String, String>()
                    val clavesPlural = valor.keys()

                    while ( clavesPlural.hasNext() ) {
                        val clavePlural = clavesPlural.next()
                        mapaPlural[ clavePlural ] = valor.getString( clavePlural )
                    }

                    cadeasPlurais[ clave ] = mapaPlural

                } else {
                    cadeasSingular[ clave ] = valor.toString()
                }

            }

            traducions[ dominio ] = cadeasSingular.toMap()
            traducionsPlurais[ dominio ] = cadeasPlurais.toMap()

            if ( recordarDominios && dominio.startsWith( "actividade" ) ) {
                dominiosRecordados.add( dominio )
            }

        } catch ( e: FileNotFoundException ) {
            Log.wtf( "IDIOMA", "O dominio $dominio non existe para o idioma $_idiomaActual", e )
        } catch ( e: JSONException ) {
            Log.wtf( "IDIOMA", "O arquivo $dominio do idioma $_idiomaActual ten un formato incorrecto", e )
        }

    }

    private fun descargarDominio( dominio: String ) {
        traducions.remove( dominio )
        traducionsPlurais.remove( dominio )
    }

    fun l10n( indice: String, dominio: String ): String {
        cargarDominio( dominio )
        return traducions[ dominio ]?.get( indice ) ?: _idiomaActual.value.pendente
    }

    fun l10nPlural( indice: String, dominio: String, num: Int ): String {

        cargarDominio( dominio )
        val listaPlurais = traducionsPlurais[ dominio ]?.get( indice ) ?: return _idiomaActual.value.pendente

        val clavePlural = when {
            listaPlurais.containsKey( num.toString() ) -> num.toString()
            num == 1 -> "s"
            else -> "pl"
        }

        return listaPlurais[ clavePlural ]?.let { clave -> String.format( clave, num ) } ?: _idiomaActual.value.pendente

    }

}