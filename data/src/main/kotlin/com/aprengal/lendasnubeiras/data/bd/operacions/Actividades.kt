package com.aprengal.lendasnubeiras.data.bd.operacions

import com.aprengal.lendasnubeiras.data.actividades.datos.Actividade
import com.aprengal.lendasnubeiras.data.actividades.datos.ActividadeBuscable
import com.aprengal.lendasnubeiras.data.actividades.datos.atributos.Categoria.Companion.escollerCategoria
import com.aprengal.lendasnubeiras.data.actividades.datos.atributos.Destinatario.Companion.escollerDestinatario
import com.aprengal.lendasnubeiras.data.actividades.datos.atributos.Estado.Companion.escollerEstado
import com.aprengal.lendasnubeiras.data.bd.clases.BD.TipoCombinacion
import com.aprengal.lendasnubeiras.data.bd.clases.BD.OperadorSimple
import com.aprengal.lendasnubeiras.data.bd.clases.BD.Orde
import com.aprengal.lendasnubeiras.data.bd.clases.BD.buscarElemento
import com.aprengal.lendasnubeiras.data.bd.clases.BD.buscarElementos
import com.aprengal.lendasnubeiras.data.bd.clases.BD.bd
import com.aprengal.lendasnubeiras.data.bd.clasesAxuda.CombinacionSQL
import com.aprengal.lendasnubeiras.data.bd.clasesAxuda.Condicion
import com.aprengal.lendasnubeiras.data.bd.clasesAxuda.SeleccionSQL
import com.aprengal.lendasnubeiras.data.bd.taboas.TaboaBase
import com.aprengal.lendasnubeiras.data.bd.taboas.TaboaLectura
import com.aprengal.lendasnubeiras.data.localizacion.ElementoL10n
import com.aprengal.lendasnubeiras.data.localizacion.Idioma
import com.aprengal.lendasnubeiras.data.localizacion.Idioma.Companion.escollerIdioma
import com.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeCrear
import com.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeEditarOutras
import com.aprengal.lendasnubeiras.data.usuarios.SesionActual.usuarioActual

object Actividades {

    fun collerActividadesBuscables( termo: String, colOrdenable: String, dirOrdenable: Orde = Orde.DESC, filtros: Map<String, Any> = emptyMap() ): List<ActividadeBuscable> {

        val onde = mutableMapOf<String, Condicion>()
        onde[ "buscador_actividades" ] = Condicion.Simple( termo, OperadorSimple.MATCH )

        for ( ( campo, valor ) in filtros ) {
            require( valor is ElementoL10n || valor is Idioma ) { "Tipo non soportado: ${ valor::class }" }
            onde[ "a.$campo" ] = Condicion.Simple( valor )
        }

        val columnas = setOf( "a.id", "a.titulo", "a.descricion", "a.id_categoria", "a.id_destinatario", "a.id_idioma", "a.estado" )
        val datosCombinacion = listOf( CombinacionSQL( TipoCombinacion.INNER, "id", "f", "docid", TaboaLectura.BUSCADOR_ACTIVIDADES ) )
        val ordenar = mapOf( colOrdenable to dirOrdenable )
        val datos = SeleccionSQL( columnas, alias = "a", combinacions = datosCombinacion, onde = onde, ordenar = ordenar )

        val resultados = bd.seleccionar( TaboaBase.ACTIVIDADES, datos )

        return resultados.map { actividade -> crearActividadeBuscable( actividade ) }

    }

    fun collerActividade( id: Long ): Actividade? {
        return buscarActividade( mapOf( "id" to Condicion.Simple( id ) ) )
    }

    fun collerActividade( titulo: String, idioma: Idioma ): Actividade? {
        val onde = mapOf( "titulo" to Condicion.Simple(titulo ), "id_idioma" to Condicion.Simple( idioma ) )
        return buscarActividade( onde )
    }

    fun collerActividades( onde: Map<String, Condicion> = mapOf() ): List<Actividade> {
        return buscarElementos( TaboaBase.ACTIVIDADES, onde ) { actividade -> crearActividade( actividade ) }
    }

    private fun buscarActividade( onde: Map<String, Condicion> ): Actividade? {
        return buscarElemento( TaboaBase.ACTIVIDADES, onde ) { actividade -> crearActividade( actividade ) }
    }

    fun listarActividadesEditables(): List<Actividade> {

        val usuarioActual = usuarioActual()

        require( PodeCrear( usuarioActual ) ) { "Non se poden listar as actividades se non pode crealas" }

        val onde: MutableMap<String, Condicion> = mutableMapOf()

        if ( !PodeEditarOutras( usuarioActual ) ) {
            onde[ "id_autoria" ] = Condicion.Simple( usuarioActual.id )
        }

        return buscarElementos( TaboaBase.ACTIVIDADES, onde ) { actividade -> crearActividade( actividade ) }

    }

    private fun crearActividade( datos: Map<String, Any> ): Actividade {

        val actividade = Actividade(
            datos[ "id" ] as Long,
            datos[ "titulo" ] as String,
            datos[ "id_autoria" ] as Long,
            escollerCategoria( datos[ "id_categoria" ] as String ),
            escollerDestinatario( datos[ "id_destinatario" ] as String ),
            escollerIdioma( datos[ "id_idioma" ] as String ),
            escollerEstado( ( datos[ "estado" ] as Long ).toInt() ),
             ( datos[ "duracion" ] as Long ).toInt(),
            datos[ "descricion" ] as String,
            datos[ "obxectivo" ] as String,
            datos[ "materiais" ] as String,
            datos[ "data_modificado" ] as Long
        )

        return actividade

    }

    private fun crearActividadeBuscable( datos: Map<String, Any> ): ActividadeBuscable {

        val actividade = ActividadeBuscable(
            datos[ "id" ] as Long,
            datos[ "titulo" ] as String,
            datos[ "descricion" ] as String,
            escollerCategoria( datos[ "id_categoria" ] as String ),
            escollerDestinatario( datos[ "id_destinatario" ] as String ),
            escollerIdioma( datos[ "id_idioma" ] as String ),
            escollerEstado( ( datos[ "estado" ] as Long ).toInt() )
        )

        return actividade

    }

}