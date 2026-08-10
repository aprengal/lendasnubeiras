package com.example.lendasnubeiras

import android.database.sqlite.SQLiteConstraintException
import androidx.test.core.app.ApplicationProvider
import com.aprengal.lendasnubeiras.configuracion.db.DB
import org.junit.After

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SQLITETest {

    private val db: DB = DB

    @Before
    fun preparar() {
        val ondeLimpeza = mapOf( "estado" to mapOf( "operador" to "IN", "valores" to listOf( -3, -2, -1, 3 ) ) )
        db.arrancar( ApplicationProvider.getApplicationContext() )
        db.eliminar( "actividades", ondeLimpeza )
    }

    @After
    fun limpar() {
        val ondeLimpeza = mapOf( "estado" to mapOf( "operador" to "IN", "valores" to listOf( -3, -2, -1, 3 ) ) )
        db.eliminar( "actividades", ondeLimpeza )
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
        val id = db.insertar( "actividades", datos )

        assertTrue( id > 0 )

        val onde = mapOf( "id" to mapOf( "valor" to id ) )
        val consulta = mapOf( "columnas" to listOf( "*" ), "onde" to onde )
        val resultado = db.seleccionar( "actividades", consulta )[ 0 ]

        assertEquals( "Actividade de proba", resultado[ "titulo" ].toString() )
        assertEquals( "Descrición de proba abondo longa", resultado[ "descricion" ].toString() )

    }

    @Test
    fun inserirVariasActividades() {

        //limpar()

        val datos = listOf(
            datosCompletosAny( mapOf( "id" to 1L, "titulo" to "Actividade 1" ) ),
            datosCompletosAny( mapOf( "id" to 2L, "titulo" to "Actividade 2" ) ),
            datosCompletosAny( mapOf( "id" to 3L, "titulo" to "Actividade 3" ) )
        )

        val ids = db.insertar( "actividades", datos )

        assertEquals( 3, ids.size )
        assertTrue( ids.all { it > 0 } )

        val consulta = mapOf( "columnas" to listOf( "*" ) )
        val resultados = db.seleccionar( "actividades", consulta )

        assertEquals( 3, resultados.size )

    }

    @Test
    fun actualizarActividade() {

        val datos = datosCompletos( mapOf( "id" to "102", "titulo" to "Título inicial" ) )
        val id = db.insertar( "actividades", datos )

        val cambios = mapOf( "titulo" to "Título cambiado" )
        val onde = mapOf( "id" to mapOf( "valor" to id ) )
        val actualizadas = db.actualizar( "actividades", cambios, onde )

        assertEquals( 1, actualizadas )

        val consulta = mapOf( "columnas" to listOf( "*" ), "onde" to onde )
        val resultado = db.seleccionar( "actividades", consulta )

        assertEquals( "Título cambiado", resultado[ 0 ][ "titulo" ] )

    }

    @Test
    fun eliminarActividade() {

        val datos = datosCompletos( mapOf( "id" to "103", "titulo" to "Temporal", "estado" to "-1" ) )
        val id = db.insertar( "actividades", datos )

        val onde = mapOf( "id" to mapOf( "valor" to id ) )
        val eliminadas = db.eliminar( "actividades", onde )

        assertEquals( 1, eliminadas )

        val consulta = mapOf( "columnas" to listOf( "*" ), "onde" to onde )
        val resultado = db.seleccionar( "actividades", consulta )

        assertTrue( resultado.isEmpty() )

    }

    @Test
    fun idNegativoRexeitado() {
        assertThrows( SQLiteConstraintException::class.java ) {
            db.insertar( "actividades", datosCompletos( mapOf( "id" to "-1" ) ) )
        }
    }

    //Non se pode facer o test de comprobar que non se admiten valores nulos porque xa non se admiten estes valores
    //nos métodos que realizan operacións coa base de datos directamente

    @Test
    fun idDuplicadoRexeitado() {
        val datos1 = datosCompletos( mapOf( "id" to "500", "titulo" to "Primeira" ) )
        db.insertar( "actividades", datos1 )

        val datos2 = datosCompletos( mapOf( "id" to "500", "titulo" to "Segunda" ) )
        assertThrows( SQLiteConstraintException::class.java ) {
            db.insertar( "actividades", datos2 )
        }
    }

    @Test
    fun tituloIdiomaDuplicadoRexeitado() {
        val datos1 = datosCompletos( mapOf( "id" to "501", "titulo" to "Repetido", "id_idioma" to "gl" ) )
        db.insertar( "actividades", datos1 )

        val datos2 = datosCompletos( mapOf( "id" to "502", "titulo" to "Repetido", "id_idioma" to "gl" ) )
        assertThrows( SQLiteConstraintException::class.java ) {
            db.insertar( "actividades", datos2 )
        }
    }

    @Test
    fun estadoForaDeRangoRexeitado() {

        assertThrows( SQLiteConstraintException::class.java ) {
            db.insertar( "actividades", datosCompletos( mapOf( "id" to "503", "estado" to "4" ) ) )
        }

        assertThrows( SQLiteConstraintException::class.java ) {
            db.insertar( "actividades", datosCompletos( mapOf( "id" to "504", "estado" to "-4" ) ) )
        }

    }

    @Test
    fun duracionForaDeRangoRexeitado() {

        assertThrows( SQLiteConstraintException::class.java ) {
            db.insertar( "actividades", datosCompletos( mapOf( "id" to "505", "duracion" to "0" ) ) )
        }

        assertThrows( SQLiteConstraintException::class.java ) {
            db.insertar( "actividades", datosCompletos( mapOf( "id" to "506", "duracion" to "181" ) ) )
        }

    }

    @Test
    fun descricionRexeitada() {

        assertThrows( SQLiteConstraintException::class.java ) {
            db.insertar( "actividades", datosCompletos( mapOf( "id" to "507", "descricion" to "curta" ) ) )
        }

        assertThrows( SQLiteConstraintException::class.java ) {
            db.insertar( "actividades", datosCompletos( mapOf( "id" to "508", "descricion" to "a".repeat( 1001 ) ) ) )
        }

    }

    @Test
    fun obxectivoRexeitado() {

        assertThrows( SQLiteConstraintException::class.java ) {
            db.insertar( "actividades", datosCompletos( mapOf( "id" to "509", "obxectivo" to "curto" ) ) )
        }

        assertThrows( SQLiteConstraintException::class.java ) {
            db.insertar( "actividades", datosCompletos( mapOf( "id" to "510", "obxectivo" to "a".repeat( 201 ) ) ) )
        }

    }

    @Test
    fun materiaisRexeitados() {

        assertThrows( SQLiteConstraintException::class.java ) {
            db.insertar( "actividades", datosCompletos( mapOf( "id" to "511", "materiais" to "curto" ) ) )
        }

        assertThrows( SQLiteConstraintException::class.java ) {
            db.insertar( "actividades", datosCompletos( mapOf( "id" to "512", "materiais" to "a".repeat( 201 ) ) ) )
        }

    }

    @Test
    fun actualizarIdExistente() {

        val datos1 = datosCompletos( mapOf( "id" to "600", "titulo" to "Primeira", "estado" to "-1" ) )
        db.insertar( "actividades", datos1 )

        val datos2 = datosCompletos( mapOf( "id" to "601", "titulo" to "Segunda", "estado" to "-1" ) )
        db.insertar( "actividades", datos2 )

        val cambios = mapOf( "id" to 600L )
        val onde = mapOf( "id" to mapOf( "valor" to 601L ) )

        assertThrows( SQLiteConstraintException::class.java ) {
            db.actualizar( "actividades", cambios, onde )
        }

    }

    @Test
    fun actualizarIdActividadeEnviada() {

        val datos = datosCompletos( mapOf( "id" to "602", "titulo" to "Xa enviada", "estado" to "0" ) )
        db.insertar( "actividades", datos )

        val cambios = mapOf( "id" to 999L )
        val onde = mapOf( "id" to mapOf( "valor" to 602L ) )

        assertThrows( SQLiteConstraintException::class.java ) {
            db.actualizar( "actividades", cambios, onde )
        }

        //Actualización de estado para que sexa borrado ou iso crea conflitos noutros tests
        db.actualizar( "actividades", mapOf( "estado" to 3 ), onde )

    }

    @Test
    fun actualizarIdActividadeNonEnviada() {

        val datos = datosCompletos( mapOf( "id" to "603", "titulo" to "Non enviada", "estado" to "-1" ) )
        db.insertar( "actividades", datos )

        val cambios = mapOf( "id" to 604L )
        val onde = mapOf( "id" to mapOf( "valor" to 603L ) )

        val actualizadas = db.actualizar( "actividades", cambios, onde )

        assertEquals( 1, actualizadas )

    }

}