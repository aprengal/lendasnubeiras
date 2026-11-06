package org.aprengal.lendasnubeiras.data.bd.clases

import android.content.Context
import android.util.Log
import org.aprengal.lendasnubeiras.data.actividades.datos.Actividade
import org.aprengal.lendasnubeiras.data.api.ConexionApi.MetodoApi
import org.aprengal.lendasnubeiras.data.api.RutasApi
import org.aprengal.lendasnubeiras.data.bd.clasesAxuda.Condicion
import org.aprengal.lendasnubeiras.data.bd.clasesAxuda.SeleccionSQL
import org.aprengal.lendasnubeiras.data.bd.operacions.Actividades
import org.aprengal.lendasnubeiras.data.utilidades.Corrutinas
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.aprengal.lendasnubeiras.data.api.ConexionApi.procesarPeticion
import org.aprengal.lendasnubeiras.data.api.resposta.DatosActividades
import org.aprengal.lendasnubeiras.data.api.resposta.RespostaXenerica
import org.aprengal.lendasnubeiras.data.axustes.Axustes.collerIdDispositivo

/**
 * Xestor central da base de datos SQLite local e sincronización do catálogo.
 */
object BD {

    internal lateinit var bd: BBDD
        private set

    internal interface ElementoBD

    /** Orde de clasificación para as consultas SQL. */
    enum class Orde( val clave: String ) {
        ASC( "ASC" ),
        DESC( "DESC" )
    }

    /** Tipos de combinación de táboas (evítase RIGHT por incompatibilidades en versións antigas de Android). */
    enum class TipoCombinacion( val clave: String ) {
        INNER( "INNER" ),
        LEFT( "LEFT" )
    }

    /** Operadores dispoñibles para condicións simples en consultas SQL. */
    enum class OperadorSimple( val simbolo: String ) {
        IGUAL( "=" ),
        DISTINTO( "!=" ),
        MENOR( "<" ),
        MENOR_IGUAL( "<=" ),
        MAIOR( ">" ),
        MAIOR_IGUAL( ">=" ),
        MATCH( "MATCH" ),
        LIKE( "LIKE" ),
        NOT_LIKE( "NOT LIKE" )
    }

    /**
     * Inicializa a base de datos e lanza opcionalmente a actualización do catálogo en segundo plano.
     */
    suspend fun arrancar( contexto: Context, actualizar: Boolean = true ) {

        if ( ::bd.isInitialized ) return
        bd = BBDD( contexto.applicationContext )

        val ambito = CoroutineScope( SupervisorJob() + Dispatchers.IO )
        if ( actualizar ) {
            actualizarCatalogo( ambito )
        }

    }

    internal fun <T: ElementoBD> buscarElemento( taboa: Taboa, onde: Map<String, Condicion>, accion: (Map<String, Any> ) -> T ): T? {
        val resultados = bd.seleccionar( taboa, SeleccionSQL( setOf( "*" ), onde = onde ) )
        return resultados.firstOrNull()?.let( accion )
    }

    internal fun <T: ElementoBD> buscarElementos( taboa: Taboa, onde: Map<String, Condicion> = emptyMap(), accion: ( Map<String, Any> ) -> T ): List<T> {
        val resultados = bd.seleccionar( taboa, SeleccionSQL( setOf( "*" ), onde = onde ) )
        return resultados.map( accion )
    }

    /**
     * Elimina unha actividade e todas as súas puntuacións asociadas.
     */
    fun eliminarPuntuacionActividade( actividade: Actividade ): Int {
        bd.eliminar( Taboa.PUNTUACIONS, mapOf( "actividade_id" to Condicion.Simple( actividade.id ) ) )
        return bd.eliminar( Taboa.ACTIVIDADES, mapOf( "id" to Condicion.Simple( actividade.id ) ) )
    }

    /**
     * Sincroniza o catálogo local de actividades co servidor mediante peticions en segundo plano.
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

                        actLocal == null -> bd.insertar( Taboa.ACTIVIDADES, actServidor )

                        dataModificadoServidor > actLocal.dataModificado -> {
                            actividadesObsoletas.remove( id )
                            bd.actualizar( Taboa.ACTIVIDADES, actServidor, mapOf( "id" to Condicion.Simple( id ) ) )
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