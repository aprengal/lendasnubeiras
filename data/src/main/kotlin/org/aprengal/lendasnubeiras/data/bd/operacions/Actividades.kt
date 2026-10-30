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

object Actividades {

    fun collerActividadesBuscables( /*termo: String,*/ colOrdenable: String, dirOrdenable: Orde = Orde.DESC, filtros: Map<String, Any> = emptyMap() ): List<ActividadeBuscable> {

        //Taboa buscador quitaríase

        //Agora habería que buscar en todas as actividades o termo que se busca en x idioma? En claveTitulo e en descrición?
        //Ou é mellor prescindir de termo completamente?

        val onde = mutableMapOf<String, Condicion>()
        onde[ "estado" ] = Condicion.En( setOf( 2, -3 ) )

        for ( ( campo, valor ) in filtros ) {
            require( valor is ElementoL10n || valor is Idioma ) { "Tipo non soportado: ${ valor::class }" }
            onde[ campo ] = Condicion.Simple( valor )
        }

        val columnas = setOf( "id", "clave_titulo", "id_categoria", "id_destinatario", "estado" )
        //val datosCombinacion = listOf( CombinacionSQL( TipoCombinacion.INNER, "id", "f", "docid", TaboaLectura.BUSCADOR_ACTIVIDADES ) )
        val ordenar = mapOf( colOrdenable to dirOrdenable )
        val datos = SeleccionSQL( columnas, alias = "a", onde = onde, ordenar = ordenar )

        val resultados = bd.seleccionar( Taboa.ACTIVIDADES, datos )

        return resultados.map { actividade -> crearActividadeBuscable( actividade ) }

    }

    fun collerActividade( id: Long ): Actividade? {
        return buscarActividade( mapOf( "id" to Condicion.Simple( id ) ) )
    }

    fun collerActividade( titulo: String ): Actividade? {
        val onde = mapOf( "clave_titulo" to Condicion.Simple( titulo ) )
        return buscarActividade( onde )
    }

    fun collerActividades( onde: Map<String, Condicion> = mapOf() ): List<Actividade> {
        return buscarElementos( Taboa.ACTIVIDADES, onde ) { actividade -> crearActividade( actividade ) }
    }

    private fun buscarActividade( onde: Map<String, Condicion> ): Actividade? {
        return buscarElemento( Taboa.ACTIVIDADES, onde ) { actividade -> crearActividade( actividade ) }
    }

    fun listarActividadesEditables(): List<Actividade> {

        val usuarioActual = usuarioActual()

        require( PodeCrear( usuarioActual ) ) { "Non se poden listar as actividades se non pode crealas" }

        val onde: MutableMap<String, Condicion> = mutableMapOf()

        if ( !PodeEditarOutras( usuarioActual ) ) {
            onde[ "id_autoria" ] = Condicion.Simple( usuarioActual.id )
        }

        return buscarElementos( Taboa.ACTIVIDADES, onde ) { actividade -> crearActividade( actividade ) }

    }

    private fun crearActividade( datos: Map<String, Any> ): Actividade {

        val actividade = Actividade(
            datos[ "id" ] as Long,
            datos[ "clave_titulo" ] as String,
            datos[ "id_autoria" ] as Long,
            escollerCategoria( datos[ "id_categoria" ] as String ),
            escollerDestinatario( datos[ "id_destinatario" ] as String ),
            escollerEstado( ( datos[ "estado" ] as Long ).toInt() ),
             ( datos[ "duracion" ] as Long ).toInt(),
            //datos[ "descricion" ] as String,
            //datos[ "obxectivo" ] as String,
            //datos[ "materiais" ] as String,
            datos[ "data_modificado" ] as Long
        )

        return actividade

    }

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