package com.aprengal.lendasnubeiras.data.bd.operacions

import com.aprengal.lendasnubeiras.data.actividades.dixitais.elementos.Grupo
import com.aprengal.lendasnubeiras.data.actividades.dixitais.elementos.Xogador
import com.aprengal.lendasnubeiras.data.bd.BD
import com.aprengal.lendasnubeiras.data.bd.BD.db
import com.aprengal.lendasnubeiras.data.bd.taboas.TaboaBase

object Xogadores {

    fun insertarXogador( nome: String, grupo: Grupo ): Long {
        return db.insertar( TaboaBase.XOGADORES, mapOf( "nome" to nome, "grupo_id" to grupo.id ) )
    }

    fun actualizarXogador( xogador: Xogador, campos: Map<String, Any> ): Int {
        return db.actualizar( TaboaBase.XOGADORES, campos, mapOf( "id" to mapOf( "operador" to "=", "valor" to xogador.id ) ) )
    }

    fun eliminarXogador( xogador: Xogador ): Int {
        return db.eliminar( TaboaBase.XOGADORES, mapOf( "id" to mapOf( "operador" to "=", "valor" to xogador.id ) ) )
    }

    fun collerXogador( id: Long ): Xogador? {
        val onde = mapOf( "id" to mapOf( "operador" to "=", "valor" to id ) )
        return BD.buscarElemento( TaboaBase.XOGADORES, onde ) { fila -> crearXogador( fila ) }
    }

    fun collerXogadores( grupo: Grupo ): List<Xogador> {
        val onde = mapOf( "grupo_id" to mapOf( "operador" to "=", "valor" to grupo.id ) )
        return BD.buscarElementos( TaboaBase.XOGADORES, onde ) { xogador -> crearXogador( xogador ) }
    }

    private fun crearXogador( datos: Map<String, Any> ): Xogador {
        return Xogador( id = datos[ "id" ] as Long, nome = datos[ "nome" ] as String )
    }

}