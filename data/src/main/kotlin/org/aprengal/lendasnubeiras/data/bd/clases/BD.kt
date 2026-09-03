package org.aprengal.lendasnubeiras.data.bd.clases

import android.content.Context
import android.util.Log
import org.aprengal.lendasnubeiras.data.actividades.datos.Actividade
import org.aprengal.lendasnubeiras.data.api.ConexionApi
import org.aprengal.lendasnubeiras.data.api.RutasApi
import org.aprengal.lendasnubeiras.data.bd.clasesAxuda.Condicion
import org.aprengal.lendasnubeiras.data.bd.clasesAxuda.SeleccionSQL
import org.aprengal.lendasnubeiras.data.bd.operacions.Actividades
import org.aprengal.lendasnubeiras.data.bd.taboas.Taboa
import org.aprengal.lendasnubeiras.data.bd.taboas.TaboaBase
import org.aprengal.lendasnubeiras.data.usuarios.SesionActual
import org.aprengal.lendasnubeiras.data.utilidades.Corrutinas
import kotlinx.coroutines.CoroutineScope
import org.aprengal.lendasnubeiras.data.api.resposta.DatosActividades
import org.aprengal.lendasnubeiras.data.api.resposta.RespostaXenerica
import java.util.UUID

object BD {

    internal lateinit var bd: BBDD
    private set

    internal interface ElementoBD

    enum class Orde( val clave: String ) { ASC( "ASC" ), DESC( "DESC" ) }

    //As combinacións con Right dan problemas en versións anteriores de Android de 2022
    enum class TipoCombinacion( val clave: String ) { INNER( "INNER" ), LEFT( "LEFT" ) }

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

    fun arrancar( contexto: Context) {
        if ( ::bd.isInitialized ) return
        bd = BBDD( contexto.applicationContext )
    }

    internal fun <T: ElementoBD> buscarElemento( taboa: Taboa, onde: Map<String, Condicion>, accion: (Map<String, Any> ) -> T ): T? {
        val resultados = bd.seleccionar( taboa, SeleccionSQL( setOf( "*" ), onde = onde ) )
        return resultados.firstOrNull()?.let( accion )
    }

    internal fun <T: ElementoBD> buscarElementos( taboa: Taboa, onde: Map<String, Condicion> = emptyMap(), accion: ( Map<String, Any> ) -> T ): List<T> {
        val resultados = bd.seleccionar( taboa, SeleccionSQL( setOf( "*" ), onde = onde ) )
        return resultados.map( accion )
    }

    //Chamado ao eliminar unha actividade que se quita tras realizar unha actualización da táboa de actividades (as FK estarían desactivadas)
    //Pero os triggers deixarían borrar en paz?
    fun eliminarPuntuacionActividade( actividade: Actividade): Int {
        bd.eliminar( TaboaBase.PUNTUACIONS, mapOf( "actividade_id" to Condicion.Simple( actividade.id ) ) )
        return bd.eliminar( TaboaBase.ACTIVIDADES, mapOf( "id" to Condicion.Simple( actividade.id ) ) )
    }

    suspend fun actualizarCatalogo( ambito: CoroutineScope): Boolean {

        val resposta = Corrutinas.corrutinaResposta(ambito) {

            val problemas = mutableListOf<String>()
            val idPeticion = UUID.randomUUID().toString()
            val campos = mutableMapOf( "sesion" to SesionActual.collerSesionActual().value, "id_peticion" to idPeticion )

            val actividadesServidor: DatosActividades = ConexionApi.procesarPeticion(
                ConexionApi.MetodoApi.GET,
                RutasApi.ACTUALIZAR,
                campos
            )

            if ( !actividadesServidor.exito ) return@corrutinaResposta false

            val ondeLocal = mapOf("id" to Condicion.Simple(0, OperadorSimple.MAIOR_IGUAL))

            val actividadesLocais = Actividades.collerActividades(ondeLocal).associateBy { elemento -> elemento.id }
            val actividadesObsoletas = actividadesLocais.toMutableMap()

            actividadesServidor.lista.forEach { actServidor ->

                try {

                    val id = (actServidor["id"] as Number).toLong()
                    val dataModificadoServidor = (actServidor["dataModificado"] as Number).toLong()

                    val actLocal = actividadesLocais[id]

                    when {

                        actLocal == null -> bd.insertar(TaboaBase.ACTIVIDADES, actServidor)

                        dataModificadoServidor > actLocal.dataModificado -> {
                            actividadesObsoletas.remove(id)
                            bd.actualizar(TaboaBase.ACTIVIDADES, actServidor, mapOf("id" to Condicion.Simple(id)))
                        }

                        else -> actividadesObsoletas.remove(id)

                    }

                } catch (e: ClassCastException) {
                    val mensaxe = "Actividade con formato inesperado do servidor: $actServidor"
                    Log.wtf("ActualizarCatalogo", mensaxe, e)
                    problemas.add(mensaxe)
                }

            }

            if ( problemas.isNotEmpty() ) {
                campos[ "erro" ] = problemas.toString()
                ConexionApi.procesarPeticion(
                    ConexionApi.MetodoApi.POST,
                    RutasApi.REPORTES,
                    campos
                ) as RespostaXenerica
            }

            actividadesObsoletas.values.forEach { actividade -> eliminarPuntuacionActividade(actividade) }

            return@corrutinaResposta true

        }.await()

        return resposta

    }

}