package com.aprengal.lendasnubeiras.data.bd.operacions

import com.aprengal.lendasnubeiras.data.actividades.dixitais.elementos.Grupo
import com.aprengal.lendasnubeiras.data.bd.BD
import com.aprengal.lendasnubeiras.data.bd.taboas.TaboaBase

internal object Grupos {

    fun insertarGrupo( nome: String ): Long {
        return BD.db.insertar( TaboaBase.GRUPOS, mapOf( "nome" to nome ) )
    }

    fun collerGrupo( id: Long ): Grupo?{
        val onde = mapOf( "id" to mapOf( "operador" to "=", "valor" to id ) )
        return BD.buscarElemento( TaboaBase.GRUPOS, onde ) { fila -> crearGrupo( fila ) }
    }

    fun collerGrupos(): List<Grupo> {
        return BD.buscarElementos( TaboaBase.GRUPOS ) { fila -> crearGrupo( fila ) }
    }

    private fun crearGrupo( datos: Map<String, Any> ): Grupo {
        return Grupo( id = datos[ "id" ] as Long, nome = datos[ "nome" ] as String )
    }

    fun actualizarGrupo( grupo: Grupo, campos: Map<String, Any> ): Int {
        return BD.db.actualizar( TaboaBase.GRUPOS, campos, mapOf( "id" to mapOf( "operador" to "=", "valor" to grupo.id ) ) )
    }

    fun eliminarGrupo( grupo: Grupo ): Int {
        return BD.db.eliminar( TaboaBase.GRUPOS, mapOf( "id" to mapOf( "operador" to "=", "valor" to grupo.id ) ) )
    }

}