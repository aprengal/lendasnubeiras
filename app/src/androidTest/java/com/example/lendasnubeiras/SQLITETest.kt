package com.example.lendasnubeiras

import androidx.test.core.app.ApplicationProvider
import com.aprengal.lendasnubeiras.conexions.DB
import org.junit.After

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SQLITETest {

    private val db: DB = DB

    @Before
    fun preparar() {
        db.arrancar( ApplicationProvider.getApplicationContext() )

        val ondeLimpeza = mapOf( "estado" to mapOf( "operador" to "IN", "valores" to listOf( -3, -2, -1, 3 ) ) )
        db.eliminar( "actividade", ondeLimpeza )
    }

    @After
    fun limpar() {
        val ondeLimpeza = mapOf( "estado" to mapOf( "operador" to "IN", "valores" to listOf( -3, -2, -1, 3 ) ) )
        db.eliminar( "actividade", ondeLimpeza )
    }

    private fun datosCompletos( overrides: Map<String, String> = emptyMap() ): Map<String, String> {
        val base = mapOf(
            "id" to System.currentTimeMillis().toString(),
            "titulo" to "Actividade de proba",
            "id_autoria" to "1",
            "id_categoria" to "infantil",
            "id_destinatario" to "individual",
            "id_idioma" to "gl",
            "duracion" to "30",
            "descricion" to "Descrición de proba abondo longa",
            "obxectivo" to "Obxectivo de proba abondo longo para pasar o check",
            "materiais" to "Materiais de proba abondo longos para pasar o check",
            "data_modificado" to System.currentTimeMillis().toString()
        )
        return base + overrides
    }

    private fun datosCompletosAny( overrides: Map<String, Any> = emptyMap() ): Map<String, Any> {
        val base = mapOf(
            "id" to System.currentTimeMillis(),
            "titulo" to "Actividade de proba",
            "id_autoria" to 1L,
            "id_categoria" to "infantil",
            "id_destinatario" to "individual",
            "id_idioma" to "gl",
            "duracion" to 30,
            "descricion" to "Descrición de proba abondo longa",
            "obxectivo" to "Obxectivo de proba abondo longo para pasar o check",
            "materiais" to "Materiais de proba abondo longos para pasar o check",
            "data_modificado" to System.currentTimeMillis()
        )
        return base + overrides
    }

    @Test
    fun inserirActividade() {

        val datos = datosCompletos( mapOf( "id" to "101", "titulo" to "Actividade de proba" ) )
        val id = db.insertar( "actividade", datos )

        assertTrue( id > 0 )

        val onde = mapOf( "id" to mapOf( "valor" to id ) )
        val consulta = mapOf( "columnas" to listOf( "*" ), "onde" to onde )
        val resultado = db.seleccionar( "actividade", consulta )[ 0 ]

        assertEquals( "Actividade de proba", resultado[ "titulo" ].toString() )
        assertEquals( "Descrición de proba abondo longa", resultado[ "descricion" ].toString() )

    }

    @Test
    fun inserirVariasActividades() {

        val datos = listOf(
            datosCompletosAny( mapOf( "id" to 1L, "titulo" to "Actividade 1" ) ),
            datosCompletosAny( mapOf( "id" to 2L, "titulo" to "Actividade 2" ) ),
            datosCompletosAny( mapOf( "id" to 3L, "titulo" to "Actividade 3" ) )
        )
        val ids = db.insertar( "actividade", datos )

        assertEquals( 3, ids.size )
        assertTrue( ids.all { it > 0 } )

        val consulta = mapOf( "columnas" to listOf( "*" ) )
        val resultados = db.seleccionar( "actividade", consulta )

        assertEquals( 3, resultados.size )

    }

    @Test
    fun actualizarActividade() {

        val datos = datosCompletos( mapOf( "id" to "102", "titulo" to "Título inicial" ) )
        val id = db.insertar( "actividade", datos )

        val cambios = mapOf( "titulo" to "Título cambiado" )
        val onde = mapOf( "id" to mapOf( "valor" to id ) )
        val actualizadas = db.actualizar( "actividade", cambios, onde )

        assertEquals( 1, actualizadas )

        val consulta = mapOf( "columnas" to listOf( "*" ), "onde" to onde )
        val resultado = db.seleccionar( "actividade", consulta )

        assertEquals( "Título cambiado", resultado[ 0 ][ "titulo" ] )

    }

    @Test
    fun eliminarActividade() {

        val datos = datosCompletos( mapOf( "id" to "103", "titulo" to "Temporal", "estado" to "-1" ) )
        val id = db.insertar( "actividade", datos )

        val onde = mapOf( "id" to mapOf( "valor" to id ) )
        val eliminadas = db.eliminar( "actividade", onde )

        assertEquals( 1, eliminadas )

        val consulta = mapOf( "columnas" to listOf( "*" ), "onde" to onde )
        val resultado = db.seleccionar( "actividade", consulta )

        assertTrue( resultado.isEmpty() )

    }

}