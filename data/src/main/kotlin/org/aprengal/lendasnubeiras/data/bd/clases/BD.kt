package org.aprengal.lendasnubeiras.data.bd.clases

import android.content.Context
import android.util.Log
import org.aprengal.lendasnubeiras.data.actividades.datos.base.Actividade
import org.aprengal.lendasnubeiras.data.api.ConexionApi.MetodoApi
import org.aprengal.lendasnubeiras.data.api.ConexionApi.procesarPeticion
import org.aprengal.lendasnubeiras.data.api.resposta.DatosActividades
import org.aprengal.lendasnubeiras.data.api.resposta.RespostaXenerica
import org.aprengal.lendasnubeiras.data.api.RutasApi
import org.aprengal.lendasnubeiras.data.axustes.Axustes.collerIdDispositivo
import org.aprengal.lendasnubeiras.data.bd.clasesAxuda.Condicion
import org.aprengal.lendasnubeiras.data.bd.clasesAxuda.SeleccionSQL
import org.aprengal.lendasnubeiras.data.bd.operacions.Actividades
import org.aprengal.lendasnubeiras.data.utilidades.Corrutinas
import org.aprengal.lendasnubeiras.data.bd.clases.Taboa.ACTIVIDADES
import org.aprengal.lendasnubeiras.data.bd.clases.Taboa.PUNTUACIONS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.aprengal.lendasnubeiras.data.localizacion.clases.ElementoL10n
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nBase

/**
 * Xestor central da base de datos SQLite local e da sincronización do catálogo de actividades
 * co servidor.
 *
 * Centraliza o acceso á instancia da base de datos, proporciona operacións de consulta e
 * eliminación de elementos e xestiona a actualización dos datos locais a partir da información
 * recibida do servidor.
 */
object BD {

    /**
     * Instancia da base de datos SQLite utilizada pola aplicación.
     *
     * Inicialízase mediante [arrancar] e só permite o acceso interno ao módulo.
     */
    internal lateinit var bd: BBDD
        private set

    /**
     * Interface que deben implementar as clases cuxos obxectos se queiran procesar mediante
     * os métodos xenéricos de consulta definidos neste obxecto.
     *
     * Permite utilizar os métodos [buscarElemento] e [buscarElementos], que requiren que
     * o tipo de elemento devolto implemente esta interface.
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

    /** Orde dos resultados da busca. O id vai en orde de creación das actividades. */
    enum class OrdeBusca( override val clave: String, override val nome: L10nBase, val orde: Orde ): ElementoL10n {

        /** Primeiro as actividades máis novas. */
        Recentes( "recentes", L10nBase.OrdeRecentes, Orde.DESC ),

        /** Primeiro as actividades máis antigas. */
        Antigas( "antigas", L10nBase.OrdeAntigas, Orde.ASC )

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

        /** Busca mediante o operador MATCH, normalmente empregado en buscas de texto completo. */
        MATCH( "MATCH" ),

        /** Busca de patróns mediante o operador LIKE. */
        LIKE( "LIKE" ),

        /** Busca de patróns que non coinciden mediante NOT LIKE. */
        NOT_LIKE( "NOT LIKE" )
    }

    /**
     * Inicializa a base de datos e, opcionalmente, inicia a actualización do catálogo
     * de actividades en segundo plano.
     *
     * Se a base de datos xa foi inicializada, a función remata sen realizar ningunha operación.
     *
     * @param contexto Contexto de Android utilizado para crear a base de datos. Emprégase
     * o contexto da aplicación para evitar manter referencias a contextos de curta duración.
     * @param actualizar Indica se se debe iniciar a sincronización do catálogo despois
     * de inicializar a base de datos. O valor predeterminado é `true`.
     */
    suspend fun arrancar( contexto: Context, actualizar: Boolean = true ) {

        if ( ::bd.isInitialized ) return
        bd = BBDD( contexto.applicationContext )

        val ambito = CoroutineScope( SupervisorJob() + Dispatchers.IO )
        if ( actualizar ) {
            actualizarCatalogo( ambito )
        }

    }

    /**
     * Busca o primeiro elemento dunha táboa que cumpra as condicións especificadas.
     *
     * Recupera todos os campos do primeiro rexistro coincidente e transforma o resultado
     * nun elemento do tipo solicitado mediante a función de conversión.
     *
     * @param taboa Táboa da base de datos na que se realiza a busca.
     * @param onde Condicións que deben cumprir os rexistros para seren seleccionados.
     * @param accion Función que converte os datos do rexistro nun elemento do tipo `T`.
     * @return O primeiro elemento coincidente convertido ao tipo `T`, ou `null` se non
     * existen resultados.
     */
    internal fun <T: ElementoBD> buscarElemento( taboa: Taboa, onde: Map<String, Condicion>, accion: (Map<String, Any> ) -> T ): T? {
        val resultados = bd.seleccionar( taboa, SeleccionSQL( setOf( "*" ), onde = onde ) )
        return resultados.firstOrNull()?.let( accion )
    }

    /**
     * Busca todos os elementos dunha táboa que cumpran as condicións especificadas.
     *
     * Se non se indican condicións, recupera todos os rexistros da táboa.
     * Cada rexistro convértese nun elemento do tipo `T` mediante a función proporcionada.
     *
     * @param taboa Táboa da base de datos na que se realiza a busca.
     * @param onde Condicións que deben cumprir os rexistros. Por defecto, non se aplica
     * ningún filtro.
     * @param accion Función que converte os datos de cada rexistro nun elemento do tipo `T`.
     * @return Lista de elementos coincidentes. Se non hai resultados, devolve unha lista baleira.
     */
    internal fun <T: ElementoBD> buscarElementos( taboa: Taboa, onde: Map<String, Condicion> = emptyMap(), accion: ( Map<String, Any> ) -> T ): List<T> {
        val resultados = bd.seleccionar( taboa, SeleccionSQL( setOf( "*" ), onde = onde ) )
        return resultados.map( accion )
    }

    /**
     * Elimina unha actividade e todas as súas puntuacións asociadas da base de datos.
     *
     * Primeiro elimina os rexistros da táboa [PUNTUACIONS] vinculados á actividade mediante
     * o seu identificador e, a continuación, elimina o propio rexistro da táboa [ACTIVIDADES].
     *
     * @param actividade Actividade que se quere eliminar.
     * @return Número de rexistros eliminados da táboa de actividades.
     */
    fun eliminarPuntuacionActividade( actividade: Actividade ): Int {
        bd.eliminar( PUNTUACIONS, mapOf( "actividade_id" to Condicion.Simple( actividade.id ) ) )
        return bd.eliminar( ACTIVIDADES, mapOf( "id" to Condicion.Simple( actividade.id ) ) )
    }

    /**
     * Sincroniza o catálogo local de actividades co catálogo dispoñible no servidor.
     *
     * O proceso realízase mediante unha corrutina no ámbito recibido e comprende as seguintes
     * operacións:
     *
     * 1. Envía ao servidor unha petición de actualización identificando o dispositivo.
     * 2. Obtén a lista de actividades dispoñibles no servidor.
     * 3. Compara as actividades recibidas coas almacenadas localmente.
     * 4. Insire as actividades que aínda non existen na base de datos local.
     * 5. Actualiza as actividades locais cando a versión do servidor é máis recente,
     * comparando os campos `dataModificado`.
     * 6. Identifica as actividades locais que xa non aparecen no catálogo do servidor.
     * 7. Elimina as actividades obsoletas e as súas puntuacións asociadas mediante
     * [eliminarPuntuacionActividade].
     * 8. Comunica ao servidor os problemas detectados durante o procesamento dos datos.
     *
     * A execución da operación xestiónase mediante [Corrutinas.corrutinaResposta].
     *
     * Os rexistros que presentan un formato inesperado ou valores nulos nos campos esperados
     * rexístranse como incidencias e non interrompen necesariamente o procesamento do resto
     * das actividades.
     *
     * @param ambito Ámbito de corrutinas no que se executa a operación de sincronización.
     * @return `true` se a petición inicial ao servidor ten éxito e se completa o proceso
     * de sincronización; `false` se a petición inicial non ten éxito.
     */
    suspend fun actualizarCatalogo( ambito: CoroutineScope ): Boolean {

        val resposta = Corrutinas.corrutinaResposta( ambito ) {

            val problemas = mutableListOf<String>()
            val campos = mutableMapOf( "uuid" to collerIdDispositivo() )
            val actividadesServidor: DatosActividades = procesarPeticion( MetodoApi.POST, RutasApi.ACTUALIZAR, campos )

            if ( !actividadesServidor.exito ) return@corrutinaResposta false

            val ondeLocal = mapOf( "id" to Condicion.Simple( 0, OperadorSimple.MAIOR_IGUAL ) )
            val actividadesLocais = Actividades.collerActividades( ondeLocal ).associateBy { elemento -> elemento.id }
            val actividadesObsoletas = actividadesLocais.toMutableMap()

            actividadesServidor.lista.forEach { actServidor ->

                try {

                    val id = ( actServidor[ "id" ] as Number ).toLong()
                    val dataModificadoServidor = ( actServidor[ "dataModificado" ] as? Number )?.toLong() ?: 0L
                    val actLocal = actividadesLocais[ id ]

                    when {

                        actLocal == null -> bd.insertar( ACTIVIDADES, actServidor )

                        dataModificadoServidor > actLocal.dataModificado -> {
                            actividadesObsoletas.remove( id )
                            bd.actualizar( ACTIVIDADES, actServidor, mapOf( "id" to Condicion.Simple( id ) ) )
                        }

                        else -> actividadesObsoletas.remove( id )

                    }

                } catch ( e: ClassCastException ) {
                    val mensaxe = "Actividade con formato inesperado do servidor: $actServidor"
                    Log.wtf( "ActualizarCatalogo", mensaxe, e )
                    problemas.add( mensaxe )
                } catch ( _: NullPointerException ) {
                    val mensaxe = "Actividade con valor nulo inesperado: $actServidor"
                    problemas.add( mensaxe )
                }

            }

            if ( problemas.isNotEmpty() ) {
                campos[ "erro" ] = problemas.toString()
                procesarPeticion( MetodoApi.POST, RutasApi.REPORTES, campos ) as RespostaXenerica
            }

            actividadesObsoletas.values.forEach { actividade ->
                eliminarPuntuacionActividade( actividade )
            }

            return@corrutinaResposta true

        }.await()

        return resposta

    }

}