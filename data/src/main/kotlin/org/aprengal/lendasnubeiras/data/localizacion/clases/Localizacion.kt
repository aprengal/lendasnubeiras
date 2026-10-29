package org.aprengal.lendasnubeiras.data.localizacion.clases

import android.content.Context
import android.content.res.Resources
import android.icu.text.PluralRules
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import org.aprengal.lendasnubeiras.data.utilidades.Contexto.haiLector
import org.aprengal.lendasnubeiras.data.utilidades.Contexto.reiniciarAplicacion
import org.aprengal.lendasnubeiras.data.localizacion.cantidades.L10nPlural
import org.aprengal.lendasnubeiras.data.localizacion.cantidades.L10nVariante
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nActividades
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nAutenticacion
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nBase
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nIconas
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nOpcions
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nSingular
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nTitulos
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nValidacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONException
import org.json.JSONObject
import java.io.FileNotFoundException
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

/**
 * Obxecto encargado da internacionalización (i18n) e localización da aplicación.
 * Xestiona a carga de recursos JSON de idioma desde os assets, o cambio dinámico de idioma,
 * as regras gramaticais de plurais (cardinais e ordinais) e variantes.
 */
object Localizacion {

    /** Contexto global da aplicación. */
    private lateinit var appContext: Context

    /** Almacén en caché das traducións singulares organizadas por dominio. */
    private val traducions = mutableMapOf<Dominio, Map<L10nSingular, String>>()

    /** Almacén en caché das traducións plurais organizadas por dominio. */
    private val traducionsPlurais = mutableMapOf<Dominio, Map<L10nPlural, Map<String, String>>>()

    /** Almacén en caché das traducións de variantes organizadas por dominio. */
    private val traducionsVariantes = mutableMapOf<Dominio, Map<L10nVariante, Map<String, String>>>()

    /** Fluxo de estado reactivo que emite o idioma actual da aplicación. */
    private val idiomaActual = MutableStateFlow( Idioma.Nada )

    /** Regras de plurais cardinais para a factoría ICU. */
    private var pluraisCardinais: PluralRules? = null

    /** Regras de plurais ordinais para a factoría ICU. */
    private var pluraisOrdinarios: PluralRules? = null

    /**
     * Obtén a clave de texto dun elemento singular, delegando segundo a súa implementación de dominio.
     */
    internal val L10nSingular.clave: String

        get() = when ( this ) {
            is L10nActividades -> clave
            is L10nAutenticacion -> clave
            is L10nBase -> clave
            is L10nTitulos -> clave
            is L10nIconas -> clave
            is L10nValidacion -> clave
            is L10nOpcions -> clave
        }

    /**
     * Obtén o dominio ao que pertence un elemento singular.
     */
    internal val L10nSingular.dominio: Dominio

        get() = when ( this ) {
            is L10nActividades -> dominio
            is L10nAutenticacion -> dominio
            is L10nBase -> dominio
            is L10nTitulos -> dominio
            is L10nIconas -> dominio
            is L10nValidacion -> dominio
            is L10nOpcions -> dominio
        }

    /**
     * Inicializa o sistema de localización se non estivese xa arrancado.
     *
     * @param contexto O contexto actual da aplicación.
     * @return Un [StateFlow] co fluxo do idioma actual.
     */
    fun arrancar( contexto: Context ): StateFlow<Idioma> {

        if ( ::appContext.isInitialized && idiomaActual.value != Idioma.Nada ) return idiomaActual.asStateFlow()

        appContext = contexto.applicationContext
        idiomaActual.value = Idioma.escollerIdiomaAplicacion(
            AppCompatDelegate.getApplicationLocales().get( 0 )?.toString() ?: "",
            Resources.getSystem().configuration.locales[ 0 ].toString()
        )

        return idiomaActual.asStateFlow()

    }

    /**
     * Cambia o idioma activo da aplicación, limpa a caché de traducións e reinicia a app se é preciso.
     *
     * @param novoIdioma O novo [Idioma] que se desexa aplicar.
     * @return `true` se se procesou correctamente.
     */
    fun cambiarIdioma( novoIdioma: Idioma ): Boolean {

        if ( idiomaActual.value == novoIdioma ) return true

        idiomaActual.value = novoIdioma
        if ( haiLector( appContext ) ) reiniciarAplicacion( appContext )

        traducions.clear()
        traducionsPlurais.clear()
        traducionsVariantes.clear()
        pluraisCardinais = null
        pluraisOrdinarios = null

        return true

    }

    /**
     * Devolve a lista de claves singulares asociadas a un dominio concreto.
     */
    private fun collerClavesSingular( dominio: Dominio): List<L10nSingular> {

        val lista = when( dominio ) {
            Dominio.DATOS_ACTIVIDADES -> L10nActividades.entries
            Dominio.AUTENTICACION -> L10nAutenticacion.entries
            Dominio.BASE -> L10nBase.entries
            Dominio.TITULOS -> L10nTitulos.entries
            Dominio.ICONAS -> L10nIconas.entries
            Dominio.VALIDACION -> L10nValidacion.entries
            Dominio.OPCIONS -> L10nOpcions.entries
        }

        return lista

    }

    /**
     * Lê o arquivo JSON de traducións correspondente ao dominio e ao idioma actual desde os assets.
     */
    private fun collerArquivoIdioma( dominio: Dominio): String {

        val dominioTexto = dominio.nome
        val carpeta = "cadeas/$dominioTexto"
        val arquivoBase = "$dominioTexto-${ idiomaActual.value.codigo }.json"
        val arquivoRexion = "$dominioTexto-${ idiomaActual.value.codigoRexion }.json"

        val arquivos = appContext.assets.list( carpeta )!!

        val direccionArquivo = if ( arquivoBase in arquivos ) arquivoBase else arquivoRexion
        val ruta = "$carpeta/$direccionArquivo"

        return appContext.assets.open( ruta ).bufferedReader().use { arquivo -> arquivo.readText() }

    }

    /**
     * Carga e parsea un dominio de traducións se non estivese previamente en caché.
     */
    private fun cargarDominio( dominio: Dominio ) {

        if ( traducions.containsKey( dominio ) ) return

        try {

            val cadeasSingular = mutableMapOf<L10nSingular, String>()
            val cadeasPlurais = mutableMapOf<L10nPlural, Map<String, String>>()
            val cadeasVariantes = mutableMapOf<L10nVariante, Map<String, String>>()

            val clavesSingulares = collerClavesSingular( dominio )
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
                            Log.w( "LOCALIZACION", "A cadea $claveJSON sobra no dominio ${ dominio.nome }" )
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
                            Log.w( "LOCALIZACION", "A cadea $claveJSON sobra no dominio ${ dominio.nome }" )
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

        } catch ( e: FileNotFoundException ) {
            Log.wtf( "IDIOMA", "O dominio $dominio non existe para o idioma $idiomaActual", e )
        } catch ( e: JSONException ) {
            Log.wtf( "IDIOMA", "O arquivo $dominio do idioma $idiomaActual ten un formato incorrecto", e )
        }

    }

    /**
     * Obtén o texto correspondente a calquera elemento localizable ou obxecto xenérico.
     */
    fun <T> obterTexto( elemento: T ): String {

        val texto = when ( elemento ) {
            is L10nSingular -> elemento.texto()
            is Idioma -> elemento.nome
            else -> elemento.toString()
        }

        return texto

    }

    /**
     * Recupera un obxecto JSON específico dunha actividade desde os assets do idioma actual.
     */
    internal fun collerTextoActividade( clave: String ): JSONObject {

        val arquivoBase = "actividades-${ idiomaActual.value.codigo }.json"
        val arquivoRexion = "actividades-${ idiomaActual.value.codigoRexion }.json"

        val arquivos = appContext.assets.list( "actividades" )!!

        val direccionArquivo = if ( arquivoBase in arquivos ) arquivoBase else arquivoRexion
        val ruta = "actividades/$direccionArquivo"

        val textoJson = appContext.assets.open( ruta ).bufferedReader().use { arquivo -> arquivo.readText() }
        val json = JSONObject( textoJson )

        return json.getJSONObject( clave )

    }

    /**
     * Traduce un elemento singular cargando previamente o seu dominio.
     */
    internal fun l10n( elemento: L10nSingular ): String {
        cargarDominio( elemento.dominio )
        return traducions[ elemento.dominio ]?.get( elemento ) ?: idiomaActual.value.pendente
    }

    /**
     * Xera unha cadea traducida manexando regras plurais (cardinais ou ordinais).
     */
    internal fun l10nPlural( elemento: L10nPlural, num: Number, cardinal: Boolean = true ): String {

        cargarDominio( elemento.dominio )
        val listaPlurais = traducionsPlurais[ elemento.dominio ]?.get( elemento ) ?: return idiomaActual.value.pendente

        val local = Locale.forLanguageTag( idiomaActual.value.codigoRexion.replace( '_', '-' ) )
        val clavePlural = categoriaPlurais( abs( num.toDouble() ), local, cardinal )
        val numero = NumberFormat.getNumberInstance( local ).format( num )

        return listaPlurais[ clavePlural ]?.let { clave -> String.format( clave, numero ) } ?: idiomaActual.value.pendente

    }

    /**
     * Traduce un elemento en base a unha variante numérica específica.
     */
    internal fun l10nVariante( elemento: L10nVariante, num: Int ): String {

        cargarDominio( elemento.dominio )
        val listaPlurais = traducionsVariantes[ elemento.dominio ]?.get( elemento ) ?: return idiomaActual.value.pendente

        val local = Locale.forLanguageTag( idiomaActual.value.codigoRexion.replace( '_', '-' ) )
        val claveVariante = num.toString()
        val numero = NumberFormat.getIntegerInstance( local ).format( num )

        return listaPlurais[ claveVariante ]?.let { clave -> String.format( clave, numero ) } ?: idiomaActual.value.pendente

    }

    /**
     * Determina a categoría de plural (ordinal ou cardinal) empregando a librería ICU.
     */
    private fun categoriaPlurais( numAbs: Double, local: Locale, cardinal: Boolean ): String {

        if ( cardinal ) {
            return categoriaCardinal( numAbs, local )
        }

        pluraisOrdinarios?.let { elemento -> return elemento.select( numAbs ) }
        val regras = PluralRules.forLocale( local, PluralRules.PluralType.ORDINAL )
        pluraisOrdinarios = regras

        return regras.select( numAbs )

    }

    /**
     * Aplica regras gramaticais específicas para plurais cardinais (con soporte para Galego e Castelán).
     */
    private fun categoriaCardinal( numAbs: Double, local: Locale ): String {

        if ( idiomaActual.value in listOf( Idioma.Galego, Idioma.Castelan ) ) {

            if ( numAbs == 1.0 ) return "one"

            if ( numAbs != 0.0 && numAbs % 1_000_000.0 == 0.0 ) {
                return "many"
            }

        }

        pluraisCardinais?.let { elemento -> return elemento.select( numAbs ) }
        val regras = PluralRules.forLocale( local, PluralRules.PluralType.CARDINAL )
        pluraisCardinais = regras

        return regras.select( numAbs )

    }

}