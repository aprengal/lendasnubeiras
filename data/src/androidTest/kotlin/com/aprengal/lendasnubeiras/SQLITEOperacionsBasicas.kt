package com.aprengal.lendasnubeiras

import androidx.test.platform.app.InstrumentationRegistry
import com.aprengal.lendasnubeiras.data.actividades.datos.atributos.Categoria
import com.aprengal.lendasnubeiras.data.bd.BD
import com.aprengal.lendasnubeiras.data.bd.BD.collerActividade
import com.aprengal.lendasnubeiras.data.localizacion.Idioma
import com.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeCrear
import com.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeEditarOutras
import com.aprengal.lendasnubeiras.data.usuarios.Rol
import com.aprengal.lendasnubeiras.data.usuarios.Usuario

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

class SQLITEOperacionsBasicas {

    companion object {

        private val db: BD = BD

        @JvmStatic
        @BeforeClass
        fun preparar() {

            try {
                val contexto = InstrumentationRegistry.getInstrumentation().targetContext
                db.arrancar( contexto )
            } catch ( _: IllegalArgumentException ) {

            }

        }

    }

    @Before
    fun limparAntes() {
        val ondeLimpeza = mapOf( "estado" to mapOf( "operador" to "IN", "valores" to setOf( -3, -2, -1, 3 ) ) )
        db.eliminar( "actividades", ondeLimpeza )
    }

    @After
    fun limparDespois() {
        val ondeLimpeza = mapOf( "estado" to mapOf( "operador" to "IN", "valores" to setOf( -3, -2, -1, 3 ) ) )
        db.eliminar( "actividades", ondeLimpeza )
    }

    @Test
    fun inserirActividade() {

        val datos = datosCompletos( mapOf( "id" to "101", "titulo" to "Actividade de proba" ) )
        val id = db.insertar( "actividades", datos )

        assertTrue( id > 0 )

        val resultado = collerActividade( id )!!

        assertEquals( "Actividade de proba", resultado.titulo )
        assertEquals( "Descrición de proba abondo longa", resultado.descricion )

    }

    @Test
    fun inserirVariasActividades() {

        val datos = listOf(
            datosCompletosAny( mapOf( "id" to 1L, "titulo" to "Actividade 1" ) ),
            datosCompletosAny( mapOf( "id" to 2L, "titulo" to "Actividade 2" ) ),
            datosCompletosAny( mapOf( "id" to 3L, "titulo" to "Actividade 3" ) )
        )

        val ids = db.insertar( "actividades", datos )

        assertEquals( 3, ids.size )
        assertTrue( ids.all { it > 0 } )

        val consulta = mapOf( "columnas" to setOf( "*" ) )
        val resultados = db.seleccionar( "actividades", consulta )

        assertEquals( 3, resultados.size )

    }

    @Test
    fun actualizarActividade() {

        val datos = datosCompletos( mapOf( "id" to "102", "titulo" to "Título inicial" ) )
        val id = db.insertar( "actividades", datos )

        val cambios = mapOf( "titulo" to "Título cambiado" )
        val onde = mapOf( "id" to mapOf( "operador" to "=", "valor" to id ) )
        val actualizadas = db.actualizar( "actividades", cambios, onde )

        assertEquals( 1, actualizadas )
        assertEquals( "Título cambiado", collerActividade( id )!!.titulo )
        assertEquals( "Título cambiado", collerActividade( "título cambiado", Idioma.GALEGO )!!.titulo )

    }

    @Test
    fun eliminarActividade() {

        val datos = datosCompletos( mapOf( "id" to "103", "titulo" to "Temporal", "estado" to "-1" ) )
        val id = db.insertar( "actividades", datos )

        val onde = mapOf( "id" to mapOf( "operador" to "=", "valor" to id ) )
        val eliminadas = db.eliminar( "actividades", onde )

        assertEquals( 1, eliminadas )
        assertNull( collerActividade( id ) )

    }

    @Test
    fun eliminarActividadeEnviadaRexeitada() {

        val estadosProhibidos = listOf( "0", "1", "2" )

        for ( estado in estadosProhibidos ) {

            val id = 800L + estado.toInt()
            val datos = datosCompletos( mapOf( "id" to id.toString(), "estado" to estado ) )
            db.insertar( "actividades", datos )

            val onde = mapOf( "id" to mapOf( "operador" to "=", "valor" to id ) )
            assertEquals( -1, db.eliminar( "actividades", onde ) )

            //Para que poida eliminarse
            db.actualizar( "actividades", mapOf( "estado" to 3 ), onde )
            db.eliminar( "actividades", onde )

        }

    }

    @Test
    fun idNegativaAceptada() {
        assertNotEquals( -1, db.insertar( "actividades", datosCompletos( mapOf( "id" to "-200" ) ) ) )
    }

    //Non se pode facer o test de comprobar que non se admiten valores nulos porque xa non se admiten estes valores
    //nos métodos que realizan operacións coa base de datos directamente

    @Test
    fun idDuplicadaRexeitada() {

        val datos1 = datosCompletos( mapOf( "id" to "500", "titulo" to "Primeira" ) )
        db.insertar( "actividades", datos1 )

        val datos2 = datosCompletos( mapOf( "id" to "500", "titulo" to "Segunda" ) )
        assertEquals( -1, db.insertar( "actividades", datos2 ) )

    }

    @Test
    fun tituloIdiomaDuplicadoRexeitado() {

        val datos1 = datosCompletos( mapOf( "id" to "501", "titulo" to "Repetido", "id_idioma" to Idioma.GALEGO.codigoRexion ) )
        assertEquals( 501, db.insertar( "actividades", datos1 ) )

        val datos2 = datosCompletos( mapOf( "id" to "502", "titulo" to "Repetido", "id_idioma" to Idioma.GALEGO.codigoRexion ) )
        assertEquals( -1, db.insertar( "actividades", datos2 ) )

    }

    @Test
    fun idiomaMinusculasRexeitado() {
        val datos1 = datosCompletos( mapOf( "id" to "502", "titulo" to "Repetido", "id_idioma" to Idioma.GALEGO.codigoRexion.lowercase() ) )
        assertEquals( -1, db.insertar( "actividades", datos1 ) )
    }

    @Test
    fun estadoForaDeRangoRexeitado() {
        assertEquals( -1, db.insertar( "actividades", datosCompletos( mapOf( "id" to "503", "estado" to "4" ) ) ) )
        assertEquals( -1, db.insertar( "actividades", datosCompletos( mapOf( "id" to "504", "estado" to "-4" ) ) ) )
    }

    @Test
    fun duracionForaDeRangoRexeitado() {
        assertEquals( -1, db.insertar( "actividades", datosCompletos( mapOf( "id" to "505", "duracion" to "0" ) ) ) )
        assertEquals( -1, db.insertar( "actividades", datosCompletos( mapOf( "id" to "506", "duracion" to "181" ) ) ) )
    }

    @Test
    fun descricionRexeitada() {
        assertEquals( -1, db.insertar( "actividades", datosCompletos( mapOf( "id" to "507", "descricion" to "curta" ) ) ) )
        assertEquals( -1, db.insertar( "actividades", datosCompletos( mapOf( "id" to "508", "descricion" to "a".repeat( 1001 ) ) ) ) )
    }

    @Test
    fun obxectivoRexeitado() {
        assertEquals( -1, db.insertar( "actividades", datosCompletos( mapOf( "id" to "509", "obxectivo" to "curto" ) ) ) )
        assertEquals( -1, db.insertar( "actividades", datosCompletos( mapOf( "id" to "510", "obxectivo" to "a".repeat( 201 ) ) ) ) )
    }

    @Test
    fun materiaisRexeitados() {
        assertEquals( -1, db.insertar( "actividades", datosCompletos( mapOf( "id" to "511", "materiais" to "curto" ) ) ) )
        assertEquals( -1, db.insertar( "actividades", datosCompletos( mapOf( "id" to "512", "materiais" to "a".repeat( 201 ) ) ) ) )
    }

    @Test
    fun actualizarIdExistente() {

        val datos1 = datosCompletos( mapOf( "id" to "600", "titulo" to "Primeira", "estado" to "-1" ) )
        db.insertar( "actividades", datos1 )

        val datos2 = datosCompletos( mapOf( "id" to "601", "titulo" to "Segunda", "estado" to "-1" ) )
        db.insertar( "actividades", datos2 )

        val cambios = mapOf( "id" to 600L )
        val onde = mapOf( "id" to mapOf( "operador" to "=", "valor" to 601L ) )

        assertEquals( -1, db.actualizar( "actividades", cambios, onde ) )

    }

    @Test
    fun actualizarIdActividadeEnviada() {

        val datos = datosCompletos( mapOf( "id" to "602", "titulo" to "Xa enviada", "estado" to "0" ) )
        db.insertar( "actividades", datos )

        val cambios = mapOf( "id" to 999L )
        val onde = mapOf( "id" to mapOf( "operador" to "=", "valor" to 602L ) )

        assertEquals( -1, db.actualizar( "actividades", cambios, onde ) )

        //Actualización de estado para que sexa borrado ou iso crea conflitos noutros tests
        db.actualizar( "actividades", mapOf( "estado" to 3 ), onde )

    }

    @Test
    fun actualizarIdActividadeNonEnviada() {

        val datos = datosCompletos( mapOf( "id" to "603", "titulo" to "Non enviada", "estado" to "-1" ) )
        db.insertar( "actividades", datos )

        val cambios = mapOf( "id" to 604L )
        val onde = mapOf( "id" to mapOf( "operador" to "=", "valor" to 603L ) )

        val actualizadas = db.actualizar( "actividades", cambios, onde )

        assertEquals( 1, actualizadas )

    }

    @Test
    fun inserirActividadeNoBuscador() {

        val id = 700L
        val datos = datosCompletos( mapOf( "id" to id.toString(), "titulo" to "Busca Proba", "estado" to "2" ) )
        db.insertar( "actividades", datos )

        val onde = mapOf( "docid" to mapOf( "operador" to "=", "valor" to id ) )
        val consulta = mapOf( "columnas" to setOf( "*" ), "onde" to onde )
        val resultado = db.seleccionar( "buscador_actividades", consulta )

        assertEquals( 1, resultado.size )
        assertEquals( "Busca Proba", resultado[ 0 ][ "titulo" ] )

    }

    @Test
    fun actualizarActividadeBuscador() {

        val id = 701L
        val datos = datosCompletos( mapOf( "id" to id.toString(), "titulo" to "Título orixinal", "estado" to "2" ) )
        db.insertar( "actividades", datos )

        val cambios = mapOf( "titulo" to "Título actualizado" )
        val onde = mapOf( "id" to mapOf( "operador" to "=", "valor" to id ) )
        db.actualizar( "actividades", cambios, onde )

        val consulta = mapOf( "columnas" to setOf( "titulo" ), "onde" to mapOf( "docid" to mapOf( "operador" to "=", "valor" to id ) ) )
        val resultado = db.seleccionar( "buscador_actividades", consulta )

        assertEquals( "Título actualizado", resultado[ 0 ][ "titulo" ] )

    }

    @Test
    fun eliminarActividadeBuscador() {

        val id = 702L
        val datos = datosCompletos( mapOf( "id" to id.toString(), "estado" to "-1" ) )
        db.insertar( "actividades", datos )

        val onde = mapOf( "id" to mapOf( "operador" to "=", "valor" to id ) )
        db.eliminar( "actividades", onde )

        val consulta = mapOf( "columnas" to setOf( "*" ), "onde" to mapOf( "docid" to mapOf( "operador" to "=", "valor" to id ) ) )
        val resultado = db.seleccionar( "buscador_actividades", consulta )

        assertTrue( resultado.isEmpty() )

    }

    @Test
    fun insertarActividadeNonBuscable() {

        val id = 703L
        val datos = datosCompletos( mapOf( "id" to id.toString(), "estado" to "0" ) )
        db.insertar( "actividades", datos )

        val onde = mapOf( "docid" to mapOf( "operador" to "=", "valor" to id ) )
        val consulta = mapOf( "columnas" to setOf( "*" ), "onde" to onde )
        val resultado = db.seleccionar( "buscador_actividades", consulta )

        assertTrue( resultado.isEmpty() )

        // Axuste a estado 3 para permitir o borrado en limpar()
        val ondeActividade = mapOf( "id" to mapOf( "operador" to "=", "valor" to id ) )
        db.actualizar( "actividades", mapOf( "estado" to 3 ), ondeActividade )

    }

    @Test
    fun actualizarActividadeBuscable() {

        val id = 704L
        val datos = datosCompletos( mapOf( "id" to id.toString(), "estado" to "0", "titulo" to "Pendente" ) )
        db.insertar( "actividades", datos )

        val onde = mapOf( "id" to mapOf( "operador" to "=", "valor" to id ) )
        db.actualizar( "actividades", mapOf( "estado" to 2 ), onde )

        val ondeBuscador = mapOf( "docid" to mapOf( "operador" to "=", "valor" to id ) )
        val consulta = mapOf( "columnas" to setOf( "*" ), "onde" to ondeBuscador )
        val resultado = db.seleccionar( "buscador_actividades", consulta )

        assertEquals( 1, resultado.size )

        // Axuste a estado 3 para permitir o borrado en limpar()
        db.actualizar( "actividades", mapOf( "estado" to 3 ), onde )

    }

    @Test
    fun actualizarActividadeNonBuscable() {

        val id = 705L
        val datos = datosCompletos( mapOf( "id" to id.toString(), "estado" to "2", "titulo" to "Para retirar" ) )
        db.insertar( "actividades", datos )

        val onde = mapOf( "id" to mapOf( "operador" to "=", "valor" to id ) )
        db.actualizar( "actividades", mapOf( "estado" to 3 ), onde )

        val ondeBuscador = mapOf( "docid" to mapOf( "operador" to "=", "valor" to id ) )
        val consulta = mapOf( "columnas" to setOf( "*" ), "onde" to ondeBuscador )
        val resultado = db.seleccionar( "buscador_actividades", consulta )

        assertTrue( resultado.isEmpty() )

    }

    @Test
    fun buscarActividades() {

        val datos = listOf(
            datosCompletosAny( mapOf( "id" to 801L, "titulo" to "Obradoiro de percusión", "descricion" to "Sesión práctica de percusión para principiantes", "id_categoria" to "musica", "estado" to 2 ) ),
            datosCompletosAny( mapOf( "id" to 802L, "titulo" to "Introdución á percusión africana", "descricion" to "Ritmos tradicionais con percusión", "id_categoria" to "musica", "estado" to 2 ) ),
            datosCompletosAny( mapOf( "id" to 803L, "titulo" to "Taller de pintura", "descricion" to "Técnicas básicas de acuarela", "id_categoria" to "arte", "estado" to 2 ) )
        )

        db.insertar( "actividades", datos )

        val resultados = db.collerActividadesBuscables( "percusión", "data_modificado" )

        assertEquals( 2, resultados.size )
        assertTrue( resultados.any { it.titulo == "Obradoiro de percusión" } )
        assertTrue( resultados.any { it.titulo == "Introdución á percusión africana" } )
        assertTrue( resultados.none { it.titulo == "Taller de pintura" } )

        db.actualizar( "actividades", mapOf( "estado" to 3 ), mapOf( "id" to mapOf( "operador" to "IN", "valores" to setOf( 801L, 802L, 803L ) ) ) )

    }

    @Test
    fun buscarActividadesConFiltro() {

        val datos = listOf(
            datosCompletosAny( mapOf( "id" to 804L, "titulo" to "Percusión corporal", "descricion" to "Ritmo sen instrumentos", "id_categoria" to Categoria.INTERIOR, "estado" to 2 ) ),
            datosCompletosAny( mapOf( "id" to 805L, "titulo" to "Percusión en obradoiro de baile", "descricion" to "Percusión aplicada ao movemento", "id_categoria" to Categoria.OUTROS, "estado" to 2 ) )
        )

        db.insertar( "actividades", datos )

        val resultados = db.collerActividadesBuscables( "percusión", "data_modificado", filtros = mapOf( "id_categoria" to Categoria.INTERIOR ) )

        assertEquals( 1, resultados.size )
        assertEquals( "Percusión corporal", resultados[ 0 ].titulo )

        db.actualizar( "actividades", mapOf( "estado" to 3 ), mapOf( "id" to mapOf( "operador" to "IN", "valores" to setOf( 804L, 805L ) ) ) )

    }

    //Esta función permite que se poida cambiar a ID e o Rol a nivel interno
    fun collerUsuarioActualTest( id: Long = 0L, rol: Rol ): Usuario {
        return Usuario( id = id, correo = "test@test.com", rol )
    }

    fun listarActividadesTest( id: Long, rol: Rol): List<Map<String, Any>> {

        val usuarioActual = collerUsuarioActualTest( id, rol )

        require( PodeCrear( usuarioActual ) ) { "Non se poden listar as actividades se non pode crealas" }

        val datos: MutableMap<String, Any> = mutableMapOf( "columnas" to setOf( "*" ) )

        if ( !PodeEditarOutras( usuarioActual ) ) {
            datos[ "onde" ] = mapOf( "id_autoria" to mapOf( "operador" to "=", "valor" to usuarioActual.id ) )
        }

        val resultados = db.seleccionar( "actividades", datos )
        val saida = mutableListOf<Map<String, Any>>()

        resultados.forEach { actividade -> saida.add( actividade ) }

        return saida

    }

    @Test
    fun listarActividadesEditables() {

        revisarHashElemento(
            "listarActividadesEditables",
            "ccb1d69fe408ca633cf84197db42d14e8ca9d7b64e13770ebbb5b64ed97dc89a"
        )

        val datos = listOf(
            datosCompletosAny( mapOf( "id" to 1L, "id_autoria" to 30L, "titulo" to "Actividade A" ) ),
            datosCompletosAny( mapOf( "id" to 2L, "id_autoria" to 20L, "titulo" to "Actividade B" ) ),
            datosCompletosAny( mapOf( "id" to 3L, "id_autoria" to 30L, "titulo" to "Actividade C" ) ),
            datosCompletosAny( mapOf( "id" to 4L, "id_autoria" to 30L, "titulo" to "Actividade D" ) ),
            datosCompletosAny( mapOf( "id" to 5L, "id_autoria" to 50L, "titulo" to "Actividade E" ) ),
        )

        db.insertar( "actividades", datos )

        // O ideal é comparar con permisos, non con roles directamente
        // Pero neste caso o que se quere verificar é que cada rol ten os resultados esperados
        for( rol in Rol.entries ) {

            if ( rol in setOf( Rol.NADA, Rol.MONITOR ) ) {

                assertThrows( IllegalArgumentException::class.java ) {
                    listarActividadesTest( 30L, rol )
                }

                continue

            }

            val resultado = listarActividadesTest( 30L, rol )

            if ( rol in setOf( Rol.EDITOR, Rol.ADMIN ) ) {
                assertEquals( "Resultado inesperado con $rol", 5, resultado.size )
            } else {
                assertEquals( "Resultado inesperado con $rol", 3, resultado.size )
            }

        }

    }

}