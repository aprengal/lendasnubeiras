package org.aprengal.lendasnubeiras.data.bd.operacions

import org.aprengal.lendasnubeiras.data.actividades.dixitais.datos.Grupo
import org.aprengal.lendasnubeiras.data.bd.clases.BD
import org.aprengal.lendasnubeiras.data.bd.clasesAxuda.Condicion
import org.aprengal.lendasnubeiras.data.bd.clases.Taboa

object Grupos {

    fun insertarGrupo( nome: String ): Long {
        return BD.bd.insertar( Taboa.GRUPOS, mapOf( "nome" to nome ) )
    }

    fun collerGrupo( id: Long ): Grupo?{
        val onde = mapOf( "id" to Condicion.Simple( id ) )
        return BD.buscarElemento( Taboa.GRUPOS, onde ) { fila -> crearGrupo( fila ) }
    }

    fun collerGrupos(): List<Grupo> {
        return BD.buscarElementos( Taboa.GRUPOS ) { fila -> crearGrupo( fila ) }
    }

    private fun crearGrupo( datos: Map<String, Any> ): Grupo {
        return Grupo( id = datos[ "id" ] as Long, nome = datos[ "nome" ] as String )
    }

    fun actualizarGrupo( grupo: Grupo, campos: Map<String, Any> ): Int {
        return BD.bd.actualizar( Taboa.GRUPOS, campos, mapOf( "id" to Condicion.Simple( grupo.id ) ) )
    }

    fun eliminarGrupo( grupo: Grupo ): Int {
        return BD.bd.eliminar( Taboa.GRUPOS, mapOf( "id" to Condicion.Simple( grupo.id ) ) )
    }

}