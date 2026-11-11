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
 * Xestor centralizado do sistema de internacionalización e localización da aplicación.
 *
 * Encárgase de determinar o idioma activo, cargar os recursos de tradución
 * desde os arquivos JSON incluídos nos assets e proporcionar os textos
 * correspondentes aos distintos elementos localizables.
 *
 * Organiza as traducións por dominios e mantén unha caché independente para
 * textos singulares, plurais e variantes numéricas, evitando cargar
 * repetidamente os mesmos recursos.
 *
 * Tamén xestiona as regras gramaticais de pluralización mediante ICU,
 * adaptándoas ás particularidades dos idiomas dispoñibles na aplicación.
 * O idioma activo exponse mediante un fluxo reactivo que permite observar
 * os cambios de configuración.
 */
object Localizacion {

    /** Contexto global da aplicación, empregado para acceder aos recursos incluídos nos assets. */
    private lateinit var appContext: Context

    /** Caché das traducións singulares, organizadas por dominio e clave de localización. */
    private val traducions = mutableMapOf<Dominio, Map<L10nSingular, String>>()

    /** Caché das traducións plurais, organizadas por dominio, clave e categoría gramatical. */
    private val traducionsPlurais = mutableMapOf<Dominio, Map<L10nPlural, Map<String, String>>>()

    /** Caché das traducións de variantes, organizadas por dominio, clave e valor numérico. */
    private val traducionsVariantes = mutableMapOf<Dominio, Map<L10nVariante, Map<String, String>>>()

    /** Fluxo de estado interno que almacena e comunica o idioma activo da aplicación. */
    private val idiomaActual = MutableStateFlow( Idioma.Nada )

    /** Regras ICU de pluralización cardinal do idioma activo, cargadas baixo demanda. */
    private var pluraisCardinais: PluralRules? = null

    /** Regras ICU de pluralización ordinal do idioma activo, cargadas baixo demanda. */
    private var pluraisOrdinarios: PluralRules? = null

    /**
     * Obtén a clave identificativa dun elemento de localización singular,
     * delegando na implementación do dominio ao que pertence.
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
     * Obtén o dominio de localización ao que pertence un elemento singular,
     * delegando na implementación correspondente.
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
     * Inicializa o sistema de localización e determina o idioma activo da aplicación.
     *
     * Utiliza, por orde de preferencia, o idioma configurado especificamente
     * para a aplicación e o idioma establecido no sistema operativo.
     * Se ningún deles está dispoñible, selecciona o idioma predeterminado.
     *
     * Se o sistema xa foi inicializado e dispón dun idioma activo, devolve
     * o fluxo existente sen repetir o proceso.
     *
     * O contexto recibido substitúese polo contexto global da aplicación
     * para evitar manter referencias innecesarias a contextos de menor duración.
     *
     * @param contexto Contexto empregado para acceder aos recursos da aplicación.
     * @return Fluxo de só lectura que comunica o idioma activo e os seus cambios.
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
     * Establece un novo idioma activo para a aplicación.
     *
     * Actualiza o fluxo do idioma actual, reinicia a aplicación se existe
     * un lector activo e invalida todas as cachés de traducións e regras
     * gramaticais para que os recursos posteriores se carguen no novo idioma.
     *
     * Se o idioma solicitado xa está activo, non realiza ningunha operación.
     *
     * @param novoIdioma Idioma que se quere establecer como activo.
     * @return true se a operación se procesa, incluído o caso no que o idioma
     * xa estaba seleccionado.
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
     * Obtén todas as claves de localización singular pertencentes a un dominio.
     *
     * Selecciona as entradas da enumeración correspondente en función
     * do dominio indicado.
     *
     * @param dominio Dominio do que se queren obter as claves.
     * @return Lista de claves singulares asociadas ao dominio.
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
     * Le o contido do arquivo JSON de traducións correspondente a un dominio
     * e ao idioma actualmente seleccionado.
     *
     * Busca primeiro un arquivo específico do código lingüístico e,
     * se non existe, utiliza o arquivo correspondente ao código lingüístico
     * combinado co código de rexión.
     *
     * @param dominio Dominio cuxos recursos de tradución se queren cargar.
     * @return Contido íntegro do arquivo JSON seleccionado.
     * @throws FileNotFoundException Se non se atopa o arquivo correspondente.
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
     * Carga e procesa os recursos JSON dun dominio se aínda non están almacenados
     * na caché.
     *
     * Identifica as entradas do arquivo segundo o tipo de elemento ao que
     * pertencen: textos singulares, plurais ou variantes numéricas.
     *
     * As claves JSON que non teñen correspondencia cun elemento definido
     * no código rexístranse como advertencias e ignóranse.
     *
     * Unha vez procesado o arquivo, almacena os resultados nas cachés
     * correspondentes para evitar lecturas e conversións posteriores.
     *
     * @param dominio Dominio cuxos recursos de localización se queren cargar.
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
     * Obtén o texto que se debe utilizar para representar un elemento.
     *
     * Se o elemento é unha clave de localización singular, recupera a súa
     * tradución no idioma activo. Se é un idioma, devolve o seu nome.
     * Para calquera outro tipo de obxecto, utiliza a súa representación textual
     * proporcionada mediante toString().
     *
     * @param elemento Elemento cuxo texto se quere obter.
     * @return Texto correspondente ao elemento.
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
     * Obtén os recursos textuais dunha actividade a partir do seu identificador
     * de localización no arquivo JSON correspondente ao idioma activo.
     *
     * Busca primeiro o arquivo específico do código lingüístico e,
     * se non existe, utiliza o correspondente ao código lingüístico e rexión.
     *
     * @param clave Clave identificativa da actividade dentro do arquivo JSON.
     * @return Obxecto JSON cos recursos da actividade ou un obxecto baleiro
     * se a clave non está presente.
     */
    internal fun collerTextoActividade( clave: String ): JSONObject {

        val arquivoBase = "actividades-${ idiomaActual.value.codigo }.json"
        val arquivoRexion = "actividades-${ idiomaActual.value.codigoRexion }.json"

        val arquivos = appContext.assets.list( "actividades" )!!

        val direccionArquivo = if ( arquivoBase in arquivos ) arquivoBase else arquivoRexion
        val ruta = "actividades/$direccionArquivo"

        val textoJson = appContext.assets.open( ruta ).bufferedReader().use { arquivo -> arquivo.readText() }
        val json = JSONObject( textoJson )

        return json.optJSONObject( clave ) ?: JSONObject()

    }

    /**
     * Obtén a tradución correspondente a un elemento singular no idioma activo.
     *
     * Carga previamente os recursos do dominio ao que pertence o elemento,
     * se aínda non están dispoñibles na caché.
     *
     * Se non existe unha tradución para a clave solicitada, devolve o texto
     * identificativo de tradución pendente correspondente ao idioma activo.
     *
     * @param elemento Elemento singular cuxa tradución se quere obter.
     * @return Texto traducido ou indicador de tradución pendente.
     */
    internal fun l10n( elemento: L10nSingular ): String {
        cargarDominio( elemento.dominio )
        return traducions[ elemento.dominio ]?.get( elemento ) ?: idiomaActual.value.pendente
    }

    /**
     * Obtén unha tradución plural aplicando as regras gramaticais do idioma activo.
     *
     * Determina a categoría plural correspondente ao número recibido,
     * diferenciando entre pluralización cardinal e ordinal.
     *
     * O número tamén se formatea segundo a configuración rexional do idioma
     * e incorpórase ao texto traducido mediante un marcador de formato.
     *
     * Se non existe a tradución correspondente á categoría gramatical obtida,
     * devolve o indicador de tradución pendente do idioma activo.
     *
     * @param elemento Elemento de localización plural que se quere traducir.
     * @param num Número utilizado para determinar a categoría plural e completar o texto.
     * @param cardinal Indica se se deben aplicar regras cardinais (true) ou ordinais (false).
     * Por defecto, utilízanse as regras cardinais.
     * @return Texto traducido coa representación formateada do número ou indicador de tradución pendente.
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
     * Obtén unha tradución correspondente a unha variante numérica concreta.
     *
     * Selecciona o texto asociado ao valor recibido e incorpora a súa
     * representación numérica, formateada segundo a configuración rexional
     * do idioma activo.
     *
     * Se non existe unha tradución para o valor indicado, devolve o indicador
     * de tradución pendente correspondente ao idioma activo.
     *
     * @param elemento Elemento de localización que contén as distintas variantes.
     * @param num Valor numérico cuxa variante se quere obter.
     * @return Texto correspondente á variante ou indicador de tradución pendente.
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
     * Determina a categoría gramatical correspondente a un número,
     * utilizando as regras de pluralización cardinais ou ordinais de ICU.
     *
     * As regras ordinais almacénanse en caché para evitar a súa creación
     * repetida durante as consultas posteriores.
     *
     * @param numAbs Valor numérico absoluto empregado para determinar a categoría.
     * @param local Configuración rexional correspondente ao idioma activo.
     * @param cardinal Indica se se deben aplicar regras cardinais ou ordinais.
     * @return Identificador textual da categoría plural correspondente.
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
     * Determina a categoría gramatical dos plurais cardinais.
     *
     * Para o galego e o castelán aplica regras específicas: utiliza a categoría
     * one para o valor absoluto igual a un e many para os múltiplos non nulos
     * dun millón.
     *
     * Para os demais casos utiliza as regras cardinais proporcionadas por ICU,
     * cuxo resultado se almacena en caché para evitar a súa creación repetida.
     *
     * @param numAbs Valor numérico absoluto empregado para determinar a categoría.
     * @param local Configuración rexional correspondente ao idioma activo.
     * @return Identificador textual da categoría plural correspondente.
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