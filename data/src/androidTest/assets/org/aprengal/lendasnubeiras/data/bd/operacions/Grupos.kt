package org.aprengal.lendasnubeiras.data.bd.operacions

import org.aprengal.lendasnubeiras.data.actividades.datos.dixitais.Grupo
import org.aprengal.lendasnubeiras.data.bd.clases.BD
import org.aprengal.lendasnubeiras.data.bd.clasesAxuda.Condicion
import org.aprengal.lendasnubeiras.data.bd.clases.Taboa

/**
 * Obxecto encargado de xestionar as operacións de acceso e modificación
 * dos grupos almacenados na base de datos.
 *
 * Centraliza as operacións de inserción, consulta, actualización e eliminación
 * de grupos, así como a conversión dos rexistros obtidos en obxectos Grupo.
 */
object Grupos {

    /**
     * Insire un novo grupo na base de datos.
     *
     * @param nome Nome do grupo que se quere crear.
     * @return Identificador do grupo inserido.
     */
    fun insertarGrupo( nome: String ): Long {
        return BD.bd.insertar( Taboa.GRUPOS, mapOf( "nome" to nome ) )
    }

    /**
     * Obtén un grupo da base de datos a partir do seu identificador.
     *
     * @param id Identificador do grupo que se quere obter.
     * @return Obxecto Grupo correspondente ou null se non existe ningún rexistro co identificador indicado.
     */
    fun collerGrupo( id: Long ): Grupo?{
        val onde = mapOf( "id" to Condicion.Simple( id ) )
        return BD.buscarElemento( Taboa.GRUPOS, onde ) { fila -> crearGrupo( fila ) }
    }

    /**
     * Obtén todos os grupos almacenados na base de datos.
     *
     * @return Lista cos grupos rexistrados.
     */
    fun collerGrupos(): List<Grupo> {
        return BD.buscarElementos( Taboa.GRUPOS ) { fila -> crearGrupo( fila ) }
    }

    /**
     * Constrúe un obxecto Grupo a partir dos datos dun rexistro da base de datos.
     *
     * @param datos Mapa que contén os campos e valores correspondentes ao grupo.
     * @return Obxecto Grupo construído cos datos recibidos.
     */
    private fun crearGrupo( datos: Map<String, Any> ): Grupo {
        return Grupo( id = datos[ "id" ] as Long, nome = datos[ "nome" ] as String )
    }

    /**
     * Actualiza os campos dun grupo existente na base de datos.
     *
     * @param grupo Grupo cuxos datos se queren actualizar.
     * @param campos Mapa cos nomes das columnas e os novos valores que se aplicarán.
     * @return Número de rexistros modificados.
     */
    fun actualizarGrupo( grupo: Grupo, campos: Map<String, Any> ): Int {
        return BD.bd.actualizar( Taboa.GRUPOS, campos, mapOf( "id" to Condicion.Simple( grupo.id ) ) )
    }

    /**
     * Elimina un grupo da base de datos a partir do seu identificador.
     *
     * @param grupo Grupo que se quere eliminar.
     * @return Número de rexistros eliminados.
     */
    fun eliminarGrupo( grupo: Grupo ): Int {
        return BD.bd.eliminar( Taboa.GRUPOS, mapOf( "id" to Condicion.Simple( grupo.id ) ) )
    }

}
