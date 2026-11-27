package org.aprengal.lendasnubeiras.data.bd.clases

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.aprengal.lendasnubeiras.data.bd.operacions.Actividades.actualizarCatalogo
import org.aprengal.lendasnubeiras.data.localizacion.clases.ElementoL10n
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nBase

/**
 * Obxecto central que agrupa os elementos comúns relacionados coa base de datos SQLite local.
 *
 * Contén a instancia de [OperacionsBD] e define as interfaces, enumeracións e demais elementos
 * compartidos polos distintos compoñentes que traballan coa base de datos.
 *
 * Tamén permite inicializar a base de datos e, opcionalmente, iniciar a actualización
 * do catálogo de actividades.
 */
object BD {

    /**
     * Instancia da clase encargada de realizar as operacións sobre a base de datos SQLite.
     *
     * Inicialízase mediante [arrancar] e só permite o acceso interno ao módulo.
     */
    internal lateinit var bd: OperacionsBD
        private set

    /**
     * Interface que deben implementar os tipos de datos que se queiran utilizar nos métodos
     * xenéricos de consulta definidos en [OperacionsBD].
     *
     * Permite identificar os tipos de elementos compatibles cos métodos [OperacionsBD.buscarElemento]
     * e [OperacionsBD.buscarElementos].
     */
    internal interface ElementoBD

    /**
     * Orde de clasificación dos resultados nas consultas SQL.
     *
     * @property clave Palabra clave SQL correspondente á orde de clasificación.
     */
    enum class Orde( val clave: String ) {

        /** Orde ascendente, de menor a maior. */
        ASC( "ASC" ),

        /** Orde descendente, de maior a menor. */
        DESC( "DESC" )

    }

    /**
     * Define as opcións de ordenación dos resultados dunha busca de actividades.
     *
     * Permite establecer a orde na que se presentan os resultados segundo
     * o criterio de antigüidade.
     *
     * @property clave Identificador textual único da opción de ordenación, independente do idioma.
     * @property nome Denominación localizada da opción de ordenación, utilizada para obter
     * o texto que se presentará ao usuario.
     * @property orde Sentido da ordenación aplicado aos resultados.
     */
    enum class OrdeBusca( override val clave: String, override val nome: L10nBase, val orde: Orde ): ElementoL10n {

        /** Primeiro as actividades máis novas, segundo a orde de creación (id). */
        Recentes( "recentes", L10nBase.OrdeRecentes, Orde.DESC ),

        /** Primeiro as actividades máis antigas, segundo a orde de creación (id). */
        Antigas( "antigas", L10nBase.OrdeAntigas, Orde.ASC )

    }

    /**
     * Táboas da base de datos SQLite local da aplicación.
     *
     * Cada valor representa unha táboa e garda o seu nome en SQLite.
     *
     * @property nome Nome da táboa na base de datos.
     */
    enum class Taboa( val nome: String ) {

        /** Actividades do catálogo e xestión local das actividades. */
        ACTIVIDADES( "actividades" ),

        /** Grupos de participantes que organiza o monitor para realizar actividades. */
        GRUPOS( "grupos" ),

        /** Participantes (xogadores) que forman parte dos grupos. */
        XOGADORES( "xogadores" ),

        /** Puntuacións outorgadas nas actividades realizadas. */
        PUNTUACIONS( "puntuacions" )

    }

    /**
     * Tipos de combinación de táboas dispoñibles nas consultas SQL.
     *
     * Evítase o uso de RIGHT JOIN debido a problemas de compatibilidade con versións antigas
     * de Android.
     *
     * @property clave Palabra clave SQL correspondente ao tipo de combinación.
     */
    enum class TipoCombinacion( val clave: String ) {

        /** Combinación que devolve unicamente os rexistros coincidentes en ambas as táboas. */
        INNER( "INNER" ),

        /** Combinación que conserva todos os rexistros da táboa esquerda e engade as coincidencias da dereita. */
        LEFT( "LEFT" )

    }

    /**
     * Operadores dispoñibles para definir condicións simples nas consultas SQL.
     *
     * @property simbolo Símbolo ou palabra clave SQL que representa o operador.
     */
    enum class OperadorSimple( val simbolo: String ) {

        /** Igualdade entre dous valores. */
        IGUAL( "=" ),

        /** Desigualdade entre dous valores. */
        DISTINTO( "!=" ),

        /** Comparación de menor que. */
        MENOR( "<" ),

        /** Comparación de menor ou igual que. */
        MENOR_IGUAL( "<=" ),

        /** Comparación de maior que. */
        MAIOR( ">" ),

        /** Comparación de maior ou igual que. */
        MAIOR_IGUAL( ">=" ),

        /** Busca de patróns mediante o operador LIKE. */
        LIKE( "LIKE" ),

        /** Busca de patróns que non coinciden mediante NOT LIKE. */
        NOT_LIKE( "NOT LIKE" )

    }

    /**
     * Inicializa a instancia encargada de realizar as operacións sobre a base de datos e,
     * opcionalmente, inicia a actualización do catálogo de actividades en segundo plano.
     *
     * Se a base de datos xa foi inicializada, a función remata sen realizar ningunha operación.
     *
     * @param contexto Contexto de Android utilizado para crear a instancia da base de datos.
     * Emprégase o contexto da aplicación para evitar manter referencias a contextos
     * de curta duración.
     * @param actualizar Indica se se debe iniciar a actualización do catálogo despois
     * de inicializar a base de datos. O valor predeterminado é `true`.
     */
    suspend fun arrancar( contexto: Context, actualizar: Boolean = true ) {

        if ( ::bd.isInitialized ) return
        bd = OperacionsBD( contexto.applicationContext )

        val ambito = CoroutineScope( SupervisorJob() + Dispatchers.IO )
        if ( actualizar ) {
            actualizarCatalogo( ambito )
        }

    }

}