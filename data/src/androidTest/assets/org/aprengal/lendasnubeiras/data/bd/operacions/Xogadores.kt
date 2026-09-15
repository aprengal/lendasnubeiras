package org.aprengal.lendasnubeiras.data.bd.operacions

import org.aprengal.lendasnubeiras.data.actividades.dixitais.datos.Grupo
import org.aprengal.lendasnubeiras.data.actividades.dixitais.datos.Xogador
import org.aprengal.lendasnubeiras.data.bd.clases.BD
import org.aprengal.lendasnubeiras.data.bd.clases.BD.bd
import org.aprengal.lendasnubeiras.data.bd.clasesAxuda.Condicion
import org.aprengal.lendasnubeiras.data.bd.taboas.TaboaBase

object Xogadores {

    fun insertarXogador( nome: String, grupo: Grupo ): Long {
        return bd.insertar( TaboaBase.XOGADORES, mapOf( "nome" to nome, "grupo_id" to grupo.id ) )
    }

    fun actualizarXogador( xogador: Xogador, campos: Map<String, Any> ): Int {
        return bd.actualizar( TaboaBase.XOGADORES, campos, mapOf( "id" to Condicion.Simple( xogador.id ) ) )
    }

    fun eliminarXogador( xogador: Xogador ): Int {
        return bd.eliminar( TaboaBase.XOGADORES, mapOf( "id" to Condicion.Simple( xogador.id ) ) )
    }

    fun collerXogador( id: Long ): Xogador? {
        val onde = mapOf( "id" to Condicion.Simple( id ) )
        return BD.buscarElemento( TaboaBase.XOGADORES, onde ) { fila -> crearXogador( fila ) }
    }

    fun collerXogadores( grupo: Grupo ): List<Xogador> {
        val onde = mapOf( "grupo_id" to Condicion.Simple( grupo.id ) )
        return BD.buscarElementos( TaboaBase.XOGADORES, onde ) { xogador -> crearXogador( xogador ) }
    }

    private fun crearXogador( datos: Map<String, Any> ): Xogador {
        return Xogador( id = datos[ "id" ] as Long, nome = datos[ "nome" ] as String )
    }

}