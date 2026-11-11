package org.aprengal.lendasnubeiras.data.bd.operacions

import org.aprengal.lendasnubeiras.data.actividades.datos.dixitais.Grupo
import org.aprengal.lendasnubeiras.data.actividades.datos.dixitais.Xogador
import org.aprengal.lendasnubeiras.data.bd.clases.BD
import org.aprengal.lendasnubeiras.data.bd.clases.BD.bd
import org.aprengal.lendasnubeiras.data.bd.clasesAxuda.Condicion
import org.aprengal.lendasnubeiras.data.bd.clases.Taboa

/**
 * Obxecto encargado de xestionar as operacións de acceso e modificación
 * dos xogadores almacenados na base de datos.
 *
 * Centraliza as operacións de inserción, consulta, actualización e eliminación
 * de xogadores, así como a conversión dos rexistros obtidos en obxectos Xogador.
 */
object Xogadores {

    /**
     * Insire un novo xogador na base de datos e asígnao ao grupo indicado.
     *
     * @param nome Nome do xogador que se quere crear.
     * @param grupo Grupo ao que pertencerá o novo xogador.
     * @return Identificador do xogador inserido.
     */
    fun insertarXogador( nome: String, grupo: Grupo ): Long {
        return bd.insertar( Taboa.XOGADORES, mapOf( "nome" to nome, "grupo_id" to grupo.id ) )
    }

    /**
     * Actualiza os campos dun xogador existente na base de datos.
     *
     * @param xogador Xogador cuxos datos se queren actualizar.
     * @param campos Mapa cos nomes das columnas e os novos valores que se aplicarán.
     * @return Número de rexistros modificados.
     */
    fun actualizarXogador( xogador: Xogador, campos: Map<String, Any> ): Int {
        return bd.actualizar( Taboa.XOGADORES, campos, mapOf( "id" to Condicion.Simple( xogador.id ) ) )
    }

    /**
     * Elimina un xogador da base de datos a partir do seu identificador.
     *
     * @param xogador Xogador que se quere eliminar.
     * @return Número de rexistros eliminados.
     */
    fun eliminarXogador( xogador: Xogador ): Int {
        return bd.eliminar( Taboa.XOGADORES, mapOf( "id" to Condicion.Simple( xogador.id ) ) )
    }

    /**
     * Obtén un xogador da base de datos a partir do seu identificador.
     *
     * @param id Identificador do xogador que se quere obter.
     * @return Obxecto Xogador correspondente ou null se non existe ningún rexistro co identificador indicado.
     */
    fun collerXogador( id: Long ): Xogador? {
        val onde = mapOf( "id" to Condicion.Simple( id ) )
        return BD.buscarElemento( Taboa.XOGADORES, onde ) { fila -> crearXogador( fila ) }
    }

    /**
     * Obtén todos os xogadores pertencentes ao grupo indicado.
     *
     * @param grupo Grupo cuxos xogadores se queren obter.
     * @return Lista cos xogadores pertencentes ao grupo.
     */
    fun collerXogadores( grupo: Grupo ): List<Xogador> {
        val onde = mapOf( "grupo_id" to Condicion.Simple( grupo.id ) )
        return BD.buscarElementos( Taboa.XOGADORES, onde ) { xogador -> crearXogador( xogador ) }
    }

    /**
     * Constrúe un obxecto Xogador a partir dos datos dun rexistro da base de datos.
     *
     * @param datos Mapa que contén os campos e valores correspondentes ao xogador.
     * @return Obxecto Xogador construído cos datos recibidos.
     */
    private fun crearXogador( datos: Map<String, Any> ): Xogador {
        return Xogador( id = datos[ "id" ] as Long, nome = datos[ "nome" ] as String )
    }

}