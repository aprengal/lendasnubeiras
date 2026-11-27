package org.aprengal.lendasnubeiras.data

import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.aprengal.lendasnubeiras.data.actividades.datos.base.Categoria
import org.aprengal.lendasnubeiras.data.actividades.datos.buscador.CriteriosBusca
import org.aprengal.lendasnubeiras.data.bd.clases.OperacionsBD
import org.aprengal.lendasnubeiras.data.bd.clases.BD
import org.aprengal.lendasnubeiras.data.bd.clases.BD.Taboa
import org.aprengal.lendasnubeiras.data.bd.clases.BD.OperadorSimple
import org.aprengal.lendasnubeiras.data.bd.clasesAxuda.Condicion
import org.aprengal.lendasnubeiras.data.bd.clasesAxuda.SeleccionSQL
import org.aprengal.lendasnubeiras.data.bd.operacions.Actividades.collerActividade
import org.aprengal.lendasnubeiras.data.bd.operacions.Actividades.collerActividadesBuscables
import org.aprengal.lendasnubeiras.data.bd.operacions.Actividades.limparCache
import org.aprengal.lendasnubeiras.data.localizacion.clases.Localizacion

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test

class SQLITEOperacionsBasicas {

    companion object {

        private lateinit var bd: OperacionsBD
        private val db: BD = BD

        @JvmStatic
        @BeforeClass
        fun preparar() {
            val contexto = InstrumentationRegistry.getInstrumentation().targetContext
            runBlocking { db.arrancar( contexto, false ) }
            Localizacion.arrancar( contexto )
            bd = db.bd

        }

    }

    @After
    fun limparActividades() {
        val ondeLimpeza = mapOf( "id" to Condicion.Simple( 0, OperadorSimple.MAIOR_IGUAL ) )
        bd.eliminar( Taboa.ACTIVIDADES, ondeLimpeza )
    }

    @Test
    fun inserirActividade() {

        val tituloClave = "dados-historiosos"
        val datos = datosCompletos( mapOf( "id" to "101", "clave_titulo" to tituloClave ) )
        val id = bd.insertar( Taboa.ACTIVIDADES, datos )

        assertTrue( id > 0 )

        val resultado = collerActividade( tituloClave )!!
        assertEquals( "dados-historiosos", resultado.claveTitulo )

    }

    @Test
    fun inserirVariasActividades() {

        val datos = listOf(
            datosCompletosAny( mapOf( "id" to 1L, "clave_titulo" to "dados-historiosos" ) ),
            datosCompletosAny( mapOf( "id" to 2L, "clave_titulo" to "quen-o-ten" ) ),
            datosCompletosAny( mapOf( "id" to 3L, "clave_titulo" to "palabras-encadenadas" ) )
        )

        val ids = bd.insertar( Taboa.ACTIVIDADES, datos )

        assertEquals( 3, ids.size )
        assertTrue( ids.all { it > 0 } )

        val consulta = SeleccionSQL( columnas = setOf( "id", "clave_titulo", "clave_categoria", "clave_destinatario", "duracion", "data_modificado" ) )
        val resultados = bd.seleccionar( Taboa.ACTIVIDADES, consulta )

        assertEquals( 3, resultados.size )

    }

    @Test
    fun actualizarActividade() {

        val tituloClave = "dados-historiosos"
        val novoTituloClave = "quen-o-ten"

        val datos = datosCompletos( mapOf( "id" to "102", "clave_titulo" to tituloClave ) )
        val id = bd.insertar( Taboa.ACTIVIDADES, datos )

        val cambios = mapOf( "clave_titulo" to novoTituloClave )
        val onde = mapOf( "id" to Condicion.Simple( id ) )
        val actualizadas = bd.actualizar( Taboa.ACTIVIDADES, cambios, onde )
        limparCache()

        assertEquals( 1, actualizadas )
        assertNull( collerActividade( tituloClave ) )
        assertEquals( "quen-o-ten", collerActividade( novoTituloClave )!!.claveTitulo )

    }

    @Test
    fun eliminarActividade() {

        val tituloClave = "dados-historiosos"
        val datos = datosCompletos( mapOf( "id" to "103", "clave_titulo" to tituloClave ) )
        val id = bd.insertar( Taboa.ACTIVIDADES, datos )

        val onde = mapOf( "id" to Condicion.Simple( id ) )
        val eliminadas = bd.eliminar( Taboa.ACTIVIDADES, onde )
        limparCache()

        assertEquals( 1, eliminadas )
        assertNull( collerActividade( tituloClave ) )

    }

    @Test
    fun idNegativaAceptada() {
        assertNotEquals( -1, bd.insertar( Taboa.ACTIVIDADES, datosCompletos(mapOf("id" to "-200"))) )
    }

    //Non se pode facer o test de comprobar que non se admiten valores nulos porque xa non se admiten estes valores
    //nos métodos que realizan operacións coa base de datos directamente

    @Test
    fun idActividadeDuplicadaRexeitada() {

        val datos1 = datosCompletos(mapOf("id" to "500", "clave_titulo" to "dados-historiosos"))
        bd.insertar( Taboa.ACTIVIDADES, datos1 )

        val datos2 = datosCompletos(mapOf("id" to "500", "clave_titulo" to "quen-o-ten"))
        assertEquals( -1, bd.insertar( Taboa.ACTIVIDADES, datos2 ) )

    }

    @Test
    fun duracionActividadeRexeitada() {
        assertEquals( -1, bd.insertar( Taboa.ACTIVIDADES, datosCompletos(mapOf("id" to "505", "duracion" to "0"))) )
        assertEquals( -1, bd.insertar( Taboa.ACTIVIDADES, datosCompletos(mapOf("id" to "506", "duracion" to "181"))) )
    }

    @Test
    fun actualizarIdExistente() {

        val datos1 = datosCompletos( mapOf( "id" to "600", "clave_titulo" to "dados-historiosos" ) )
        bd.insertar( Taboa.ACTIVIDADES, datos1 )

        val datos2 = datosCompletos( mapOf( "id" to "601", "clave_titulo" to "quen-o-ten" ) )
        bd.insertar( Taboa.ACTIVIDADES, datos2 )

        val cambios = mapOf( "id" to 600L )
        val onde = mapOf( "id" to Condicion.Simple( 601L ) )

        assertEquals( -1, bd.actualizar( Taboa.ACTIVIDADES, cambios, onde ) )

    }

    @Test
    fun buscarActividades() {

        val datos1 = datosCompletosAny(
            mapOf(
                "id" to 801L,
                "clave_titulo" to "dados-historiosos",
                "clave_categoria" to "musica"
            )
        )

        val datos2 = datosCompletosAny(
            mapOf(
                "id" to 802L,
                "clave_titulo" to "quen-o-ten",
                "clave_categoria" to "musica"
            )
        )

        val datos3 = datosCompletosAny(
            mapOf(
                "id" to 803L,
                "clave_titulo" to "palabras-encadenadas",
                "estado" to 255
            )
        )

        assertTrue( bd.insertar( Taboa.ACTIVIDADES, datos1 ) != -1L )
        assertTrue( bd.insertar( Taboa.ACTIVIDADES, datos2 ) != -1L )
        assertEquals( -1L, bd.insertar( Taboa.ACTIVIDADES, datos3 ) )

        val resultados = collerActividadesBuscables( CriteriosBusca( colOrdenable = "data_modificado" ) )

        assertEquals( 2, resultados.size )
        assertTrue( resultados.any { it.claveTitulo == "dados-historiosos" } )
        assertTrue( resultados.any { it.claveTitulo == "quen-o-ten" } )
        assertTrue( resultados.none { it.claveTitulo == "palabras-encadenadas" } )

    }

    @Test
    fun buscarActividadesConFiltro() {

        val datos = listOf(
            datosCompletosAny(
                mapOf(
                    "id" to 804L,
                    "clave_titulo" to "dados-historiosos",
                    "clave_categoria" to "interior"
                )
            ),
            datosCompletosAny(
                mapOf(
                    "id" to 805L,
                    "clave_titulo" to "quen-o-ten",
                    "clave_categoria" to "outros"
                )
            )
        )

        bd.insertar( Taboa.ACTIVIDADES, datos )

        val resultados = collerActividadesBuscables( CriteriosBusca( "data_modificado", filtros = mapOf( "clave_categoria" to Categoria.Interior ) ) )

        assertEquals( 1, resultados.size )
        assertEquals( "dados-historiosos", resultados[ 0 ].claveTitulo )

    }

}