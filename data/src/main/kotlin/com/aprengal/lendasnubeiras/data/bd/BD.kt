package com.aprengal.lendasnubeiras.data.bd

import android.content.Context
import android.util.Log
import com.aprengal.lendasnubeiras.data.actividades.datos.Actividade
import com.aprengal.lendasnubeiras.data.api.ConexionApi.procesarPeticion
import com.aprengal.lendasnubeiras.data.api.RespostaApi.DatosActividades
import com.aprengal.lendasnubeiras.data.api.ConexionApi.MetodoApi
import com.aprengal.lendasnubeiras.data.api.RespostaApi.RespostaXenerica
import com.aprengal.lendasnubeiras.data.api.RutasApi
import com.aprengal.lendasnubeiras.data.bd.operacions.Actividades.collerActividades
import com.aprengal.lendasnubeiras.data.bd.taboas.Taboa
import com.aprengal.lendasnubeiras.data.bd.taboas.TaboaBase
import com.aprengal.lendasnubeiras.data.utilidades.Corrutinas.corrutinaResposta
import com.aprengal.lendasnubeiras.data.usuarios.SesionActual.collerSesionActual
import kotlinx.coroutines.CoroutineScope
import java.util.UUID

internal object BD {

    lateinit var db: BBDD
    private set

    internal interface ElementoBD

    fun arrancar( contexto: Context ) {
        if ( ::db.isInitialized ) return
        db = BBDD( contexto.applicationContext )
    }

    fun <T: ElementoBD> buscarElemento( taboa: Taboa, onde: Map<String, Map<String, Any>>, accion: ( Map<String, Any> ) -> T ): T? {

        val resultados = db.seleccionar(
            taboa,
            mapOf(
                "columnas" to setOf( "*" ),
                "onde" to onde
            )
        )

        return resultados.firstOrNull()?.let( accion )

    }

    fun <T: ElementoBD> buscarElementos( taboa: Taboa, onde: Map<String, Map<String, Any>> = mapOf(), accion: ( Map<String, Any> ) -> T ): List<T> {

        val resultados = db.seleccionar(
            taboa,
            mapOf(
                "columnas" to setOf( "*" ),
                "onde" to onde
            )
        )

        return resultados.map( accion )

    }

    //Chamado ao eliminar unha actividade que se quita tras realizar unha actualización da táboa de actividades (as FK estarían desactivadas)
    //Pero os triggers deixarían borrar en paz?
    fun eliminarActividade( actividade: Actividade ): Int {

        db.eliminar(
            TaboaBase.PUNTUACIONS,
            mapOf( "actividade_id" to mapOf( "operador" to "=", "valor" to actividade.id ) )
        )

        return db.eliminar(
            TaboaBase.ACTIVIDADES,
            mapOf( "id" to mapOf( "operador" to "=", "valor" to actividade.id ) )
        )

    }

    suspend fun actualizarCatalogo( ambito: CoroutineScope ): Boolean {

        val resposta = corrutinaResposta( ambito ) {

            val problemas = mutableListOf<String>()
            val idPeticion = UUID.randomUUID().toString()
            val campos = mutableMapOf( "sesion" to collerSesionActual().value, "id_peticion" to idPeticion )

            val actividadesServidor: DatosActividades = procesarPeticion( MetodoApi.GET, RutasApi.ACTUALIZAR, campos )

            if ( !actividadesServidor.exito ) return@corrutinaResposta false

            val ondeLocal = mapOf( "id" to mapOf( "operador" to ">=", "valor" to 0 ) )

            val actividadesLocais = collerActividades( ondeLocal ).associateBy { elemento -> elemento.id }
            val actividadesObsoletas = actividadesLocais.toMutableMap()

            actividadesServidor.lista.forEach { actServidor ->

                try {

                    val id = ( actServidor[ "id" ] as Number ).toLong()
                    val actLocal = actividadesLocais[ id ]

                    when {

                        actLocal == null -> db.insertar( TaboaBase.ACTIVIDADES, actServidor )

                        ( actServidor[ "dataModificado" ] as Number ).toLong() > actLocal.dataModificado -> {

                            actividadesObsoletas.remove( id )
                            db.actualizar( TaboaBase.ACTIVIDADES, actServidor, mapOf( "id" to mapOf( "operador" to "=", "valor" to id ) ) )

                        }

                        else -> actividadesObsoletas.remove( id )

                    }

                } catch ( e: ClassCastException ) {
                    val mensaxe = "Actividade con formato inesperado do servidor: $actServidor"
                    Log.wtf( "ActualizarCatalogo", mensaxe, e )
                    problemas.add( mensaxe )
                }

            }

            if ( problemas.isNotEmpty() ) {
                campos[ "erro" ] = problemas.toString()
                procesarPeticion( MetodoApi.POST, RutasApi.REPORTES, campos ) as RespostaXenerica
            }

            actividadesObsoletas.values.forEach { actividade -> eliminarActividade( actividade ) }

            return@corrutinaResposta true

        }.await()

        return resposta

    }

}