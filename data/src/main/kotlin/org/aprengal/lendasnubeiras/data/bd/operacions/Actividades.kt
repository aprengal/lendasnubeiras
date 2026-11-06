package org.aprengal.lendasnubeiras.data.bd.operacions

import org.aprengal.lendasnubeiras.data.actividades.datos.Actividade
import org.aprengal.lendasnubeiras.data.actividades.datos.ActividadeBuscable
import org.aprengal.lendasnubeiras.data.actividades.datos.Categoria.Companion.escollerCategoria
import org.aprengal.lendasnubeiras.data.actividades.datos.Destinatario.Companion.escollerDestinatario
import org.aprengal.lendasnubeiras.data.actividades.datos.Estado.Companion.escollerEstado
import org.aprengal.lendasnubeiras.data.bd.clases.BD.Orde
import org.aprengal.lendasnubeiras.data.bd.clases.BD.buscarElemento
import org.aprengal.lendasnubeiras.data.bd.clases.BD.buscarElementos
import org.aprengal.lendasnubeiras.data.bd.clases.BD.bd
import org.aprengal.lendasnubeiras.data.bd.clasesAxuda.Condicion
import org.aprengal.lendasnubeiras.data.bd.clasesAxuda.SeleccionSQL
import org.aprengal.lendasnubeiras.data.bd.clases.Taboa
import org.aprengal.lendasnubeiras.data.localizacion.clases.ElementoL10n
import org.aprengal.lendasnubeiras.data.localizacion.clases.Idioma
import org.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeCrear
import org.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeEditarOutras
import org.aprengal.lendasnubeiras.data.usuarios.SesionActual.usuarioActual

/**
 * Obxecto encargado de xestionar as operacións de consulta e acceso
 * ás actividades almacenadas na base de datos.
 *
 * Permite obter actividades mediante diferentes criterios de busca,
 * consultar as actividades dispoñibles no buscador, listar aquelas
 * que o usuario actual ten permiso para editar e construír os obxectos
 * correspondentes a partir dos rexistros da base de datos.
 */
object Actividades {

    /**
     * Obtén as actividades dispoñibles para o buscador, permitindo aplicar
     * filtros sobre os seus campos e establecer a ordenación dos resultados.
     *
     * Só se inclúen actividades publicadas no servidor (estado 2) ou borradas
     * localmente que xa foran publicadas (estado -3).
     *
     * Os filtros están limitados a campos con valores pechados, admitindo
     * exclusivamente elementos de tipo ElementoL10n ou Idioma. Isto permite
     * filtrar por categorías, destinatarios e outros campos equivalentes,
     * evitando a introdución de valores arbitrarios.
     *
     * @param colOrdenable Nome da columna pola que se ordenarán os resultados.
     * @param dirOrdenable Sentido da ordenación. Por defecto, descendente.
     * @param filtros Mapa cos campos e valores polos que se filtrarán as actividades.
     * @return Lista de actividades adaptadas para o buscador.
     * @throws IllegalArgumentException Se algún dos valores dos filtros non é de tipo ElementoL10n ou Idioma.
     */
    fun collerActividadesBuscables( colOrdenable: String, dirOrdenable: Orde = Orde.DESC, filtros: Map<String, Any> = emptyMap() ): List<ActividadeBuscable> {

        val onde = mutableMapOf<String, Condicion>()
        onde[ "estado" ] = Condicion.En( setOf( 2, -3 ) )

        for ( ( campo, valor ) in filtros ) {
            require( valor is ElementoL10n || valor is Idioma ) { "Tipo non soportado: ${ valor::class }" }
            onde[ campo ] = Condicion.Simple( valor )
        }

        val columnas = setOf( "id", "clave_titulo", "id_categoria", "id_destinatario", "estado" )
        val ordenar = mapOf( colOrdenable to dirOrdenable )
        val datos = SeleccionSQL( columnas, alias = "a", onde = onde, ordenar = ordenar )
        val resultados = bd.seleccionar( Taboa.ACTIVIDADES, datos )

        return resultados.map { actividade -> crearActividadeBuscable( actividade ) }

    }

    /**
     * Obtén unha actividade a partir do seu identificador.
     *
     * @param id Identificador da actividade que se quere obter.
     * @return Obxecto Actividade correspondente ou null se non existe.
     */
    fun collerActividade( id: Long ): Actividade? {
        return buscarActividade( mapOf( "id" to Condicion.Simple( id ) ) )
    }

    /**
     * Obtén unha actividade a partir da súa clave de título.
     *
     * @param claveTitulo Clave única que identifica o título da actividade.
     * @return Obxecto Actividade correspondente ou null se non existe.
     */
    fun collerActividade( claveTitulo: String ): Actividade? {
        val onde = mapOf( "clave_titulo" to Condicion.Simple( claveTitulo ) )
        return buscarActividade( onde )
    }

    /**
     * Obtén as actividades que cumpren as condicións especificadas.
     *
     * Se non se indican condicións, devolve todas as actividades almacenadas.
     *
     * @param onde Mapa coas condicións que deben cumprir as actividades.
     * Por defecto, non se aplica ningún filtro.
     * @return Lista de actividades que cumpren as condicións indicadas.
     */
    fun collerActividades( onde: Map<String, Condicion> = mapOf() ): List<Actividade> {
        return buscarElementos( Taboa.ACTIVIDADES, onde ) { actividade -> crearActividade( actividade ) }
    }

    /**
     * Busca unha única actividade na base de datos aplicando as condicións indicadas.
     *
     * @param onde Mapa coas condicións que debe cumprir a actividade.
     * @return Obxecto Actividade correspondente ou null se non se atopa ningún rexistro.
     */
    private fun buscarActividade( onde: Map<String, Condicion> ): Actividade? {
        return buscarElemento( Taboa.ACTIVIDADES, onde ) { actividade -> crearActividade( actividade ) }
    }

    /**
     * Lista as actividades que o usuario actual ten permiso para editar.
     *
     * Antes de realizar a consulta, comproba que o usuario ten permisos
     * para crear actividades. Se non pode editar actividades doutros usuarios,
     * limita os resultados ás actividades das que é autor.
     *
     * @return Lista de actividades que o usuario actual pode editar.
     * @throws IllegalArgumentException Se o usuario actual non ten permisos para crear actividades.
     */
    fun listarActividadesEditables(): List<Actividade> {

        val usuarioActual = usuarioActual()

        require( PodeCrear( usuarioActual ) ) { "Non se poden listar as actividades se non pode crealas" }

        val onde: MutableMap<String, Condicion> = mutableMapOf()

        if ( !PodeEditarOutras( usuarioActual ) ) {
            onde[ "id_autoria" ] = Condicion.Simple( usuarioActual.id )
        }

        return buscarElementos( Taboa.ACTIVIDADES, onde ) { actividade -> crearActividade( actividade ) }

    }

    /**
     * Constrúe unha instancia de Actividade a partir dos datos dun rexistro
     * da base de datos.
     *
     * Converte os identificadores de categoría, destinatario e estado nos
     * seus correspondentes obxectos e adapta os valores numéricos aos tipos
     * esperados pola clase Actividade.
     *
     * @param datos Mapa cos campos e valores correspondentes á actividade.
     * @return Instancia de Actividade construída cos datos recibidos.
     */
    private fun crearActividade( datos: Map<String, Any> ): Actividade {

        val actividade = Actividade(
            datos[ "id" ] as Long,
            datos[ "clave_titulo" ] as String,
            datos[ "id_autoria" ] as Long,
            escollerCategoria( datos[ "id_categoria" ] as String ),
            escollerDestinatario( datos[ "id_destinatario" ] as String ),
            escollerEstado( ( datos[ "estado" ] as Long ).toInt() ),
            ( datos[ "duracion" ] as Long ).toInt(),
            datos[ "data_modificado" ] as Long
        )

        return actividade

    }

    /**
     * Constrúe unha instancia de ActividadeBuscable a partir dos datos
     * dun rexistro da base de datos.
     *
     * A diferenza de Actividade, esta representación contén unicamente
     * os datos necesarios para identificar e mostrar unha actividade
     * nos resultados do buscador, evitando recuperar información adicional
     * que non é necesaria nese contexto.
     *
     * @param datos Mapa cos campos e valores correspondentes á actividade.
     * @return Instancia de ActividadeBuscable construída cos datos recibidos.
     */
    private fun crearActividadeBuscable( datos: Map<String, Any> ): ActividadeBuscable {

        val actividade = ActividadeBuscable(
            datos[ "id" ] as Long,
            datos[ "clave_titulo" ] as String,
            escollerCategoria( datos[ "id_categoria" ] as String ),
            escollerDestinatario( datos[ "id_destinatario" ] as String ),
            escollerEstado( ( datos[ "estado" ] as Long ).toInt() )
        )

        return actividade

    }

}