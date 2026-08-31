package com.aprengal.lendasnubeiras.data.bd.operacions

import com.aprengal.lendasnubeiras.data.actividades.dixitais.elementos.Grupo
import com.aprengal.lendasnubeiras.data.bd.clases.BD
import com.aprengal.lendasnubeiras.data.bd.clasesAxuda.Condicion
import com.aprengal.lendasnubeiras.data.bd.taboas.TaboaBase

object Grupos {

    fun insertarGrupo( nome: String ): Long {
        return BD.bd.insertar( TaboaBase.GRUPOS, mapOf( "nome" to nome ) )
    }

    fun collerGrupo( id: Long ): Grupo?{
        val onde = mapOf( "id" to Condicion.Simple( id ) )
        return BD.buscarElemento( TaboaBase.GRUPOS, onde ) { fila -> crearGrupo( fila ) }
    }

    fun collerGrupos(): List<Grupo> {
        return BD.buscarElementos( TaboaBase.GRUPOS ) { fila -> crearGrupo( fila ) }
    }

    private fun crearGrupo( datos: Map<String, Any> ): Grupo {
        return Grupo( id = datos[ "id" ] as Long, nome = datos[ "nome" ] as String )
    }

    fun actualizarGrupo( grupo: Grupo, campos: Map<String, Any> ): Int {
        return BD.bd.actualizar( TaboaBase.GRUPOS, campos, mapOf( "id" to Condicion.Simple( grupo.id ) ) )
    }

    fun eliminarGrupo( grupo: Grupo ): Int {
        return BD.bd.eliminar( TaboaBase.GRUPOS, mapOf( "id" to Condicion.Simple( grupo.id ) ) )
    }

}