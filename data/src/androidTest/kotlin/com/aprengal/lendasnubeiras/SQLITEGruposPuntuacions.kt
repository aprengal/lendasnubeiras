package com.aprengal.lendasnubeiras

import androidx.test.platform.app.InstrumentationRegistry
import com.aprengal.lendasnubeiras.data.actividades.Grupo
import com.aprengal.lendasnubeiras.data.actividades.dixitais.Dificultade
import com.aprengal.lendasnubeiras.data.configuracion.db.DB
import com.aprengal.lendasnubeiras.data.configuracion.db.DB.collerActividade
import com.aprengal.lendasnubeiras.data.configuracion.db.DB.collerClasificacion
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import java.time.Instant

class SQLITEGruposPuntuacions {

    companion object {

        private val db: DB = DB

        @JvmStatic
        @BeforeClass
        fun preparar() {

            try {
                val contexto = InstrumentationRegistry.getInstrumentation().targetContext
                db.arrancar(contexto )
            } catch ( _: IllegalArgumentException ) {

            }

        }

    }

    @Before
    fun limparAntes() {
        db.eliminar( "grupos", mapOf( "id" to mapOf( "operador" to "!=", "valor" to 0 ) ) )
        val ondeLimpeza = mapOf( "estado" to mapOf( "operador" to "IN", "valores" to setOf( -3, -2, -1, 3 ) ) )
        db.eliminar( "actividades", ondeLimpeza )
    }

    @After
    fun limparDespois() {
        db.eliminar( "grupos", mapOf( "id" to mapOf( "operador" to "!=", "valor" to 0 ) ) )
        val ondeLimpeza = mapOf( "estado" to mapOf( "operador" to "IN", "valores" to setOf( -3, -2, -1, 3 ) ) )
        db.eliminar( "actividades", ondeLimpeza )
    }

    @Test
    fun grupoIdDuplicadaRexeitado() {

        val id = db.insertar( "grupos", mapOf( "id" to 9500L, "nome" to "Grupo Clave Primeira" ) )
        assertNotEquals( -1L, id )

        assertEquals( -1L, db.insertar( "grupos", mapOf( "id" to 9500L, "nome" to "Grupo Clave Segunda" ) ) )

        db.eliminar( "grupos", mapOf( "id" to mapOf( "operador" to "=", "valor" to 9500L ) ) )

    }

    @Test
    fun grupoIdNegativoRexeitado() {
        assertEquals( -1L, db.insertar( "grupos", mapOf( "id" to -5L, "nome" to "Grupo Id Negativo" ) ) )
    }

    @Test
    fun grupoNomeDemasiadoLongoRexeitado() {
        assertEquals( -1L, db.insertarGrupo( "a".repeat( 101 ) ) )
    }

    @Test
    fun grupoNomeLimiteMinimoAceptado() {
        val id = db.insertarGrupo( "a" )
        assertNotEquals( -1L, id )
        db.eliminar( "grupos", mapOf( "id" to mapOf( "operador" to "=", "valor" to id ) ) )
    }

    @Test
    fun grupoNomeLimiteMaximoAceptado() {
        val id = db.insertarGrupo( "a".repeat( 100 ) )
        assertNotEquals( -1L, id )
        db.eliminar( "grupos", mapOf( "id" to mapOf( "operador" to "=", "valor" to id ) ) )
    }

    @Test
    fun inserirGrupoNomeDuplicado() {
        val id = db.insertarGrupo( "Equipo A" )
        assertNotEquals( -1, id )
        assertEquals( -1, db.insertarGrupo( "equipo a" ) )
    }

    @Test
    fun actualizarGrupoInexistente() {

        val idGrupo1 = db.insertarGrupo( "Equipo I" )
        val idGrupo2 = db.insertarGrupo( "Equipo J" )
        val grupo2 = db.collerGrupo( idGrupo2 )!!

        assertEquals( -1, db.actualizarGrupo( grupo2, mapOf( "nome" to "equipo i" ) ) )

        db.eliminar( "grupos", mapOf( "id" to mapOf( "operador" to "IN", "valores" to setOf( idGrupo1, idGrupo2 ) ) ) )

    }

    @Test
    fun actualizarGrupAceptado() {

        val idGrupo = db.insertarGrupo( "Equipo K" )
        val grupo = db.collerGrupo( idGrupo )!!

        assertEquals( 1, db.actualizarGrupo( grupo, mapOf( "nome" to "Equipo K Renomeado" ) ) )
        assertEquals( "Equipo K Renomeado", db.collerGrupo( idGrupo )!!.nome )

        db.eliminar( "grupos", mapOf( "id" to mapOf( "operador" to "=", "valor" to idGrupo ) ) )

    }

    @Test
    fun eliminarGrupo() { //Efecto cascada con xogador e puntuacións

        val idActividade = 900L
        db.insertar( "actividades", datosCompletos( mapOf( "id" to idActividade.toString(), "titulo" to "Cascade" ) ) )
        val actividade = collerActividade( idActividade )!!

        val idGrupo = db.insertarGrupo( "Equipo B" )
        val grupo = db.collerGrupo( idGrupo )!!
        val idXogador = db.insertarXogador( "Ana", grupo )
        val xogador = db.collerXogador( idXogador )!!

        db.insertarPuntuacion( actividade, Dificultade.FACIL, xogador, 100L )
        db.eliminarGrupo( grupo )
        assertNull( db.collerXogador( idXogador ) )

        val onde = mapOf( "columnas" to setOf( "*" ), "onde" to mapOf( "xogador_id" to mapOf( "operador" to "=", "valor" to idXogador ) ) )
        assertTrue( db.seleccionar( "puntuacions", onde ).isEmpty() )

        db.actualizar( "actividades", mapOf( "estado" to 3 ), mapOf( "id" to mapOf( "operador" to "=", "valor" to idActividade ) ) )

    }

    @Test
    fun xogadorIdDuplicadaRexeitado() {

        val idGrupo = db.insertarGrupo( "Equipo Clave Xogador" )
        val grupo = db.collerGrupo( idGrupo )!!

        val id = db.insertar( "xogadores", mapOf( "id" to 9600L, "nome" to "Primeiro", "grupo_id" to grupo.id ) )
        assertNotEquals( -1L, id )

        assertEquals( -1L, db.insertar( "xogadores", mapOf( "id" to 9600L, "nome" to "Segundo", "grupo_id" to grupo.id ) ) )

        db.eliminar( "grupos", mapOf( "id" to mapOf( "operador" to "=", "valor" to idGrupo ) ) )

    }

    @Test
    fun xogadorIdNegativoRexeitado() {

        val idGrupo = db.insertarGrupo( "Equipo Check X" )
        val grupo = db.collerGrupo( idGrupo )!!

        assertEquals( -1L, db.insertar( "xogadores", mapOf( "id" to -7L, "nome" to "Proba", "grupo_id" to grupo.id ) ) )

        db.eliminar( "grupos", mapOf( "id" to mapOf( "operador" to "=", "valor" to idGrupo ) ) )

    }

    @Test
    fun xogadorGrupoIdNegativoRexeitado() {
        assertEquals( -1L, db.insertar( "xogadores", mapOf( "nome" to "Proba", "grupo_id" to -8L ) ) )
    }

    @Test
    fun xogadorNomeBaleiroRexeitado() {

        val idGrupo = db.insertarGrupo( "Equipo Check Y" )
        val grupo = db.collerGrupo( idGrupo )!!

        assertEquals( -1L, db.insertarXogador( "", grupo ) )

        db.eliminar( "grupos", mapOf( "id" to mapOf( "operador" to "=", "valor" to idGrupo ) ) )

    }

    @Test
    fun xogadorNomeDemasiadoLongoRexeitado() {

        val idGrupo = db.insertarGrupo( "Equipo Check Z" )
        val grupo = db.collerGrupo( idGrupo )!!

        assertEquals( -1L, db.insertarXogador( "a".repeat( 101 ), grupo ) )

        db.eliminar( "grupos", mapOf( "id" to mapOf( "operador" to "=", "valor" to idGrupo ) ) )

    }

    @Test
    fun xogadorNomeLimitesAceptados() {

        val idGrupo = db.insertarGrupo( "Equipo Check W" )
        val grupo = db.collerGrupo( idGrupo )!!

        assertNotEquals( -1L, db.insertarXogador( "a", grupo ) )
        assertNotEquals( -1L, db.insertarXogador( "a".repeat( 100 ), grupo ) )

        db.eliminar( "grupos", mapOf( "id" to mapOf( "operador" to "=", "valor" to idGrupo ) ) )

    }

    @Test
    fun inserirXogadorGrupoInexistente() {
        val grupoFalso = Grupo( id = 9999L, nome = "Non existe" )
        assertEquals( -1L, db.insertarXogador( "Pedro", grupoFalso ) )
    }

    @Test
    fun inserirXogadorNomeDuplicadoRexeitado() {

        val idGrupo = db.insertarGrupo( "Equipo O" )
        val grupo = db.collerGrupo( idGrupo )!!

        db.insertarXogador( "Marta", grupo )

        assertEquals( -1L, db.insertarXogador( "marta", grupo ) )

        db.eliminar( "grupos", mapOf( "id" to mapOf( "operador" to "=", "valor" to idGrupo ) ) )

    }

    @Test
    fun inserirXogadorNomeDuplicadoGruposDistintosAceptado() {

        val idGrupo1 = db.insertarGrupo( "Equipo P" )
        val idGrupo2 = db.insertarGrupo( "Equipo Q" )
        val grupo1 = db.collerGrupo( idGrupo1 )!!
        val grupo2 = db.collerGrupo( idGrupo2 )!!

        val idXogador1 = db.insertarXogador( "Marta", grupo1 )
        val idXogador2 = db.insertarXogador( "Marta", grupo2 )

        assertNotEquals( -1L, idXogador1 )
        assertNotEquals( -1L, idXogador2 )

        db.eliminar( "grupos", mapOf( "id" to mapOf( "operador" to "IN", "valores" to setOf( idGrupo1, idGrupo2 ) ) ) )

    }

    @Test
    fun actualizarXogadorGrupoRexeitado() {

        val idGrupo = db.insertarGrupo( "Equipo L" )
        val grupo = db.collerGrupo( idGrupo )!!
        val idXogador = db.insertarXogador( "Breixo", grupo )
        val xogador = db.collerXogador( idXogador )!!

        assertEquals( -1, db.actualizarXogador( xogador, mapOf( "grupo_id" to 9999L ) ) )

        db.eliminar( "grupos", mapOf( "id" to mapOf( "operador" to "=", "valor" to idGrupo ) ) )

    }

    @Test
    fun actualizarXogadorNomeAceptado() {

        val idGrupo = db.insertarGrupo( "Equipo M" )
        val grupo = db.collerGrupo( idGrupo )!!
        val idXogador = db.insertarXogador( "Xela", grupo )
        val xogador = db.collerXogador( idXogador )!!

        assertEquals( 1, db.actualizarXogador( xogador, mapOf( "nome" to "Xela Renomeada" ) ) )
        assertEquals( "Xela Renomeada", db.collerXogador( idXogador )!!.nome )

        db.eliminar( "grupos", mapOf( "id" to mapOf( "operador" to "=", "valor" to idGrupo ) ) )

    }

    @Test
    fun actualizarXogadorNomeDuplicadoRexeitado() {

        val idGrupo = db.insertarGrupo( "Equipo N" )
        val grupo = db.collerGrupo( idGrupo )!!

        db.insertarXogador( "Noa", grupo )
        val idXogador2 = db.insertarXogador( "Uxía", grupo )
        val xogador2 = db.collerXogador( idXogador2 )!!

        assertEquals( -1, db.actualizarXogador( xogador2, mapOf( "nome" to "noa" ) ) )

        db.eliminar( "grupos", mapOf( "id" to mapOf( "operador" to "=", "valor" to idGrupo ) ) )

    }

    @Test
    fun eliminarXogador() {

        val idActividade = 901L
        db.insertar( "actividades", datosCompletos( mapOf( "id" to idActividade.toString(), "titulo" to "Cascade Xogador" ) ) )
        val actividade = collerActividade( idActividade )!!

        val idGrupo = db.insertarGrupo( "Equipo C" )
        val grupo = db.collerGrupo( idGrupo )!!
        val idXogador = db.insertarXogador( "Marta", grupo )
        val xogador = db.collerXogador( idXogador )!!

        db.insertarPuntuacion( actividade, Dificultade.FACIL, xogador, 50L )
        db.eliminarXogador( xogador )

        val onde = mapOf( "columnas" to setOf( "*" ), "onde" to mapOf( "xogador_id" to mapOf( "operador" to "=", "valor" to idXogador ) ) )
        assertTrue( db.seleccionar( "puntuacions", onde ).isEmpty() )

        db.eliminarGrupo( grupo )
        db.actualizar( "actividades", mapOf( "estado" to 3 ), mapOf( "id" to mapOf( "operador" to "=", "valor" to idActividade ) ) )

    }

    @Test
    fun inserirPuntuacionClaveDuplicada() {

        val idActividade = 902L
        db.insertar( "actividades", datosCompletos( mapOf( "id" to idActividade.toString(), "titulo" to "Clave Duplicada" ) ) )
        val actividade = collerActividade( idActividade )!!

        val idGrupo = db.insertarGrupo( "Equipo D" )
        val grupo = db.collerGrupo( idGrupo )!!
        val idXogador = db.insertarXogador( "Luis", grupo )
        val xogador = db.collerXogador( idXogador )!!

        db.insertarPuntuacion( actividade, Dificultade.FACIL, xogador, 10L )

        assertEquals( -1L, db.insertarPuntuacion( actividade, Dificultade.FACIL, xogador, 20L ) )

        db.eliminarGrupo( grupo )
        db.actualizar( "actividades", mapOf( "estado" to 3 ), mapOf( "id" to mapOf( "operador" to "=", "valor" to idActividade ) ) )

    }

    @Test
    fun puntuacionActividadeIdNegativoRexeitado() {
        assertEquals(
            -1L,
            db.insertar(
                "puntuacions",
                mapOf( "actividade_id" to -9L, "dificultade" to Dificultade.FACIL.nome, "xogador_id" to 1L, "puntos" to 10L, "unix_rexistro" to 1000L )
            )
        )
    }

    @Test
    fun puntuacionXogadorIdNegativoRexeitado() {

        val idActividade = 918L
        db.insertar( "actividades", datosCompletos( mapOf( "id" to idActividade.toString(), "titulo" to "Check Puntuacion Xogador" ) ) )

        assertEquals(
            -1L,
            db.insertar(
                "puntuacions",
                mapOf( "actividade_id" to idActividade, "dificultade" to Dificultade.FACIL.nome, "xogador_id" to -6L, "puntos" to 10L, "unix_rexistro" to 1000L )
            )
        )

        db.actualizar( "actividades", mapOf( "estado" to 3 ), mapOf( "id" to mapOf( "operador" to "=", "valor" to idActividade ) ) )

    }

    @Test
    fun puntuacionPuntosNegativoRexeitado() {

        val idActividade = 919L
        db.insertar( "actividades", datosCompletos( mapOf( "id" to idActividade.toString(), "titulo" to "Check Puntos" ) ) )

        val idGrupo = db.insertarGrupo( "Equipo Check Puntos" )
        val grupo = db.collerGrupo( idGrupo )!!
        val idXogador = db.insertarXogador( "Breixo Check", grupo )

        assertEquals(
            -1L,
            db.insertar(
                "puntuacions",
                mapOf( "actividade_id" to idActividade, "dificultade" to Dificultade.FACIL.nome, "xogador_id" to idXogador, "puntos" to -12L, "unix_rexistro" to 1000L )
            )
        )

        db.eliminar( "grupos", mapOf( "id" to mapOf( "operador" to "=", "valor" to idGrupo ) ) )
        db.actualizar( "actividades", mapOf( "estado" to 3 ), mapOf( "id" to mapOf( "operador" to "=", "valor" to idActividade ) ) )

    }

    @Test
    fun puntuacionUnixRexistroNegativoRexeitado() {

        val idActividade = 920L
        db.insertar( "actividades", datosCompletos( mapOf( "id" to idActividade.toString(), "titulo" to "Check Unix" ) ) )

        val idGrupo = db.insertarGrupo( "Equipo Check Unix" )
        val grupo = db.collerGrupo( idGrupo )!!
        val idXogador = db.insertarXogador( "Sabela Check", grupo )

        assertEquals(
            -1L,
            db.insertar(
                "puntuacions",
                mapOf( "actividade_id" to idActividade, "dificultade" to Dificultade.FACIL.nome, "xogador_id" to idXogador, "puntos" to 10L, "unix_rexistro" to -15L )
            )
        )

        db.eliminar( "grupos", mapOf( "id" to mapOf( "operador" to "=", "valor" to idGrupo ) ) )
        db.actualizar( "actividades", mapOf( "estado" to 3 ), mapOf( "id" to mapOf( "operador" to "=", "valor" to idActividade ) ) )

    }

    @Test
    fun insertarPuntuacionConTimestampActual() {

        val idActividade = 917L
        db.insertar( "actividades", datosCompletos( mapOf( "id" to idActividade.toString(), "titulo" to "Insertar Puntuacion Real" ) ) )
        val actividade = collerActividade( idActividade )!!

        val idGrupo = db.insertarGrupo( "Equipo Y" )
        val grupo = db.collerGrupo( idGrupo )!!
        val idXogador = db.insertarXogador( "Roi", grupo )
        val xogador = db.collerXogador( idXogador )!!

        val antes = Instant.now().epochSecond
        val idPuntuacion = db.insertarPuntuacion( actividade, Dificultade.FACIL, xogador, 200L )
        val despois = Instant.now().epochSecond

        assertNotEquals( -1L, idPuntuacion )

        val onde = mapOf(
            "columnas" to setOf( "*" ),
            "onde" to mapOf(
                "actividade_id" to mapOf( "operador" to "=", "valor" to idActividade ),
                "dificultade" to mapOf( "operador" to "=", "valor" to Dificultade.FACIL.nome ),
                "xogador_id" to mapOf( "operador" to "=", "valor" to idXogador )
            )
        )

        val resultado = db.seleccionar( "puntuacions", onde )

        assertEquals( 1, resultado.size )
        assertEquals( 200L, resultado[ 0 ][ "puntos" ] )

        val unixRexistro = resultado[ 0 ][ "unix_rexistro" ] as Long
        assertTrue( unixRexistro in antes..despois )

        db.eliminar( "grupos", mapOf( "id" to mapOf( "operador" to "=", "valor" to idGrupo ) ) )
        db.actualizar( "actividades", mapOf( "estado" to 3 ), mapOf( "id" to mapOf( "operador" to "=", "valor" to idActividade ) ) )

    }

    @Test
    fun actualizarPuntuacionInferior() {

        val idActividade = 903L
        db.insertar( "actividades", datosCompletos( mapOf( "id" to idActividade.toString(), "titulo" to "Puntuacion Inferior" ) ) )
        val actividade = collerActividade( idActividade )!!

        val idGrupo = db.insertarGrupo( "Equipo E" )
        val grupo = db.collerGrupo( idGrupo )!!
        val idXogador = db.insertarXogador( "Sara", grupo )
        val xogador = db.collerXogador( idXogador )!!

        db.insertarPuntuacion( actividade, Dificultade.FACIL, xogador, 100L )

        assertEquals( 0, db.actualizarPuntuacion( actividade, Dificultade.FACIL, xogador, 50L ) )

        db.eliminarGrupo( grupo )
        db.actualizar( "actividades", mapOf( "estado" to 3 ), mapOf( "id" to mapOf( "operador" to "=", "valor" to idActividade ) ) )

    }

    @Test
    fun actualizarPuntuacionIgual() {

        val idActividade = 904L
        db.insertar( "actividades", datosCompletos( mapOf( "id" to idActividade.toString(), "titulo" to "Puntuacion Igual" ) ) )
        val actividade = collerActividade( idActividade )!!

        val idGrupo = db.insertarGrupo( "Equipo F" )
        val grupo = db.collerGrupo( idGrupo )!!
        val idXogador = db.insertarXogador( "Iria", grupo )
        val xogador = db.collerXogador( idXogador )!!

        db.insertarPuntuacion( actividade, Dificultade.FACIL, xogador, 100L )

        assertEquals( 0, db.actualizarPuntuacion( actividade, Dificultade.FACIL, xogador, 100L ) )

        db.eliminarGrupo( grupo )
        db.actualizar( "actividades", mapOf( "estado" to 3 ), mapOf( "id" to mapOf( "operador" to "=", "valor" to idActividade ) ) )

    }

    @Test
    fun actualizarPuntuacionSuperior() {

        val idActividade = 905L
        db.insertar( "actividades", datosCompletos( mapOf( "id" to idActividade.toString(), "titulo" to "Puntuacion Superior" ) ) )
        val actividade = collerActividade( idActividade )!!

        val idGrupo = db.insertarGrupo( "Equipo G" )
        val grupo = db.collerGrupo( idGrupo )!!
        val idXogador = db.insertarXogador( "Noa", grupo )
        val xogador = db.collerXogador( idXogador )!!

        db.insertarPuntuacion( actividade, Dificultade.FACIL, xogador, 100L )

        assertEquals( 1, db.actualizarPuntuacion( actividade, Dificultade.FACIL, xogador, 150L ) )

        db.eliminarGrupo( grupo )
        db.actualizar( "actividades", mapOf( "estado" to 3 ), mapOf( "id" to mapOf( "operador" to "=", "valor" to idActividade ) ) )

    }

    @Test
    fun proteccionActualizarPuntuacion() {

        val idActividade = 906L
        db.insertar( "actividades", datosCompletos( mapOf( "id" to idActividade.toString(), "titulo" to "Trigger Proteccion" ) ) )
        val actividade = collerActividade( idActividade )!!

        val idGrupo = db.insertarGrupo( "Equipo H" )
        val grupo = db.collerGrupo( idGrupo )!!
        val idXogador = db.insertarXogador( "Uxía", grupo )
        val xogador = db.collerXogador( idXogador )!!

        db.insertarPuntuacion( actividade, Dificultade.FACIL, xogador, 100L )

        val onde = mapOf(
            "actividade_id" to mapOf( "operador" to "=", "valor" to actividade.id ),
            "dificultade" to mapOf( "operador" to "=", "valor" to Dificultade.FACIL.name ),
            "xogador_id" to mapOf( "operador" to "=", "valor" to xogador.id )
        )

        assertEquals( -1, db.actualizar( "puntuacions", mapOf( "puntos" to 100L ), onde ) )

        db.eliminarGrupo( grupo )
        db.actualizar( "actividades", mapOf( "estado" to 3 ), mapOf( "id" to mapOf( "operador" to "=", "valor" to idActividade ) ) )

    }

    @Test
    fun eliminarPuntuacionExistente() {

        val idActividade = 907L
        db.insertar( "actividades", datosCompletos( mapOf( "id" to idActividade.toString(), "titulo" to "Eliminar Puntuacion" ) ) )
        val actividade = collerActividade( idActividade )!!

        val idGrupo = db.insertarGrupo( "Equipo N" )
        val grupo = db.collerGrupo( idGrupo )!!
        val idXogador = db.insertarXogador( "Iago", grupo )
        val xogador = db.collerXogador( idXogador )!!

        db.insertarPuntuacion( actividade, Dificultade.FACIL, xogador, 100L )

        assertEquals( 1, db.eliminarPuntuacion( actividade, Dificultade.FACIL, xogador ) )

        val onde = mapOf( "columnas" to setOf( "*" ), "onde" to mapOf( "xogador_id" to mapOf( "operador" to "=", "valor" to idXogador ) ) )
        assertTrue( db.seleccionar( "puntuacions", onde ).isEmpty() )

        db.eliminar( "grupos", mapOf( "id" to mapOf( "operador" to "=", "valor" to idGrupo ) ) )
        db.actualizar( "actividades", mapOf( "estado" to 3 ), mapOf( "id" to mapOf( "operador" to "=", "valor" to idActividade ) ) )

    }

    @Test
    fun collerClasificacionOrdenada() {

        val idActividade = 908L
        db.insertar( "actividades", datosCompletos( mapOf( "id" to idActividade.toString(), "titulo" to "Clasificacion Proba" ) ) )
        val actividade = collerActividade( idActividade )!!

        val idGrupo = db.insertarGrupo( "Equipo S" )
        val grupo = db.collerGrupo( idGrupo )!!

        val idXogador1 = db.insertarXogador( "Breogan", grupo )
        val idXogador2 = db.insertarXogador( "Sabela", grupo )
        val idXogador3 = db.insertarXogador( "Antia", grupo )

        val idXogador4 = db.insertarXogador( "Antia2", grupo )

        val xogador1 = db.collerXogador( idXogador1 )!!
        val xogador2 = db.collerXogador( idXogador2 )!!
        val xogador3 = db.collerXogador( idXogador3 )!!
        val xogador4 = db.collerXogador( idXogador4 )!!

        val puntuacion = mutableMapOf(
            "actividade_id" to actividade.id,
            "dificultade" to Dificultade.FACIL.nome,
            "xogador_id" to xogador1.id,
            "puntos" to 100L,
            "unix_rexistro" to 1000L
        )
        db.insertar( "puntuacions", puntuacion )

        puntuacion[ "xogador_id" ] = xogador2.id
        puntuacion[ "puntos" ] = 150L
        puntuacion[ "unix_rexistro" ] = 2000L
        db.insertar( "puntuacions", puntuacion )

        puntuacion[ "dificultade" ] = Dificultade.MEDIA.nome
        puntuacion[ "xogador_id" ] = xogador4.id
        db.insertar( "puntuacions", puntuacion )

        puntuacion[ "dificultade" ] = Dificultade.FACIL.nome
        puntuacion[ "xogador_id" ] = xogador3.id
        puntuacion[ "puntos" ] = 100L
        puntuacion[ "unix_rexistro" ] = 500L
        db.insertar( "puntuacions", puntuacion )

        val clasificacion = collerClasificacion( actividade, Dificultade.FACIL )

        assertEquals( "Clasificacion Proba", clasificacion.tituloActividade )
        assertEquals( Dificultade.FACIL, clasificacion.dificultade )
        assertEquals( 3, clasificacion.listaPuntuacions.size )

        val ordeEsperada = listOf( "Sabela", "Antia", "Breogan" )
        assertEquals( ordeEsperada, clasificacion.listaPuntuacions.keys.toList() )
        assertFalse( clasificacion.listaPuntuacions.containsKey( "Antia2" ) )

        assertEquals( 150L, clasificacion.listaPuntuacions[ "Sabela" ]!![ "puntos" ] )
        assertEquals( 100L, clasificacion.listaPuntuacions[ "Antia" ]!![ "puntos" ] )
        assertEquals( 500L, clasificacion.listaPuntuacions[ "Antia" ]!![ "unix_rexistro" ] )
        assertEquals( 100L, clasificacion.listaPuntuacions[ "Breogan" ]!![ "puntos" ] )
        assertEquals( 1000L, clasificacion.listaPuntuacions[ "Breogan" ]!![ "unix_rexistro" ] )

        db.eliminar( "grupos", mapOf( "id" to mapOf( "operador" to "=", "valor" to idGrupo ) ) )
        db.actualizar( "actividades", mapOf( "estado" to 3 ), mapOf( "id" to mapOf( "operador" to "=", "valor" to idActividade ) ) )

    }

    @Test
    fun collerClasificacionBaleira() {

        val idActividade = 909L
        db.insertar( "actividades", datosCompletos( mapOf( "id" to idActividade.toString(), "titulo" to "Clasificacion Baleira" ) ) )
        val actividade = collerActividade( idActividade )!!

        val clasificacion = collerClasificacion( actividade, Dificultade.FACIL )

        assertEquals( "Clasificacion Baleira", clasificacion.tituloActividade )
        assertTrue( clasificacion.listaPuntuacions.isEmpty() )

        db.actualizar( "actividades", mapOf( "estado" to 3 ), mapOf( "id" to mapOf( "operador" to "=", "valor" to idActividade ) ) )

    }

    @Test
    fun eliminarActividadePuntuacions() {

        val idActividade = 910L
        db.insertar( "actividades", datosCompletos( mapOf( "id" to idActividade.toString(), "titulo" to "Eliminar Actividade" ) ) )
        val actividade = collerActividade( idActividade )!!

        val idGrupo = db.insertarGrupo( "Equipo T" )
        val grupo = db.collerGrupo( idGrupo )!!
        val idXogador = db.insertarXogador( "Tomas", grupo )
        val xogador = db.collerXogador( idXogador )!!

        db.insertarPuntuacion( actividade, Dificultade.FACIL, xogador, 100L )

        val filasEliminadas = db.eliminarActividade( actividade )

        assertEquals( 1, filasEliminadas )
        assertNull( collerActividade( idActividade ) )

        val onde = mapOf( "columnas" to setOf( "*" ), "onde" to mapOf( "actividade_id" to mapOf( "operador" to "=", "valor" to idActividade ) ) )
        assertTrue( db.seleccionar( "puntuacions", onde ).isEmpty() )

        db.eliminar( "grupos", mapOf( "id" to mapOf( "operador" to "=", "valor" to idGrupo ) ) )

    }

    @Test
    fun eliminarActividadeSenPuntuacions() {

        val idActividade = 911L
        db.insertar( "actividades", datosCompletos( mapOf( "id" to idActividade.toString(), "titulo" to "Sen Puntuacions" ) ) )
        val actividade = collerActividade( idActividade )!!

        val filasEliminadas = db.eliminarActividade( actividade )

        assertEquals( 1, filasEliminadas )
        assertNull( collerActividade( idActividade ) )

    }

    @Test
    fun collerTodosGrupos() {

        val idGrupo1 = db.insertarGrupo( "Equipo U" )
        val idGrupo2 = db.insertarGrupo( "Equipo V" )

        val grupos = db.collerGrupos()

        assertTrue( grupos.any { it.id == idGrupo1 && it.nome == "Equipo U" } )
        assertTrue( grupos.any { it.id == idGrupo2 && it.nome == "Equipo V" } )

        db.eliminar( "grupos", mapOf( "id" to mapOf( "operador" to "IN", "valores" to setOf( idGrupo1, idGrupo2 ) ) ) )

    }

    @Test
    fun collerXogadoresFiltrados() {

        val idGrupo1 = db.insertarGrupo( "Equipo W" )
        val idGrupo2 = db.insertarGrupo( "Equipo X" )
        val grupo1 = db.collerGrupo( idGrupo1 )!!
        val grupo2 = db.collerGrupo( idGrupo2 )!!

        db.insertarXogador( "Sabela", grupo1 )
        db.insertarXogador( "Iago", grupo1 )
        db.insertarXogador( "Noa", grupo2 )

        val xogadoresGrupo1 = db.collerXogadores( grupo1 )
        val xogadoresGrupo2 = db.collerXogadores( grupo2 )

        assertEquals( 2, xogadoresGrupo1.size )
        assertTrue( xogadoresGrupo1.any { it.nome == "Sabela" } )
        assertTrue( xogadoresGrupo1.any { it.nome == "Iago" } )

        assertEquals( 1, xogadoresGrupo2.size )
        assertEquals( "Noa", xogadoresGrupo2[ 0 ].nome )

        db.eliminar( "grupos", mapOf( "id" to mapOf( "operador" to "IN", "valores" to setOf( idGrupo1, idGrupo2 ) ) ) )

    }

    @Test
    fun collerActividadesFiltradas() {

        val id1 = 912L
        val id2 = 913L
        val id3 = 914L

        db.insertar( "actividades", datosCompletos( mapOf( "id" to id1.toString(), "titulo" to "Filtro A", "id_autoria" to "40" ) ) )
        db.insertar( "actividades", datosCompletos( mapOf( "id" to id2.toString(), "titulo" to "Filtro B", "id_autoria" to "40" ) ) )
        db.insertar( "actividades", datosCompletos( mapOf( "id" to id3.toString(), "titulo" to "Filtro C", "id_autoria" to "41" ) ) )

        val onde = mapOf( "id_autoria" to mapOf( "operador" to "=", "valor" to 40L ) )
        val resultado = db.collerActividades( onde )

        assertEquals( 2, resultado.size )
        assertTrue( resultado.any { it.titulo == "Filtro A" } )
        assertTrue( resultado.any { it.titulo == "Filtro B" } )
        assertTrue( resultado.none { it.titulo == "Filtro C" } )

        db.actualizar( "actividades", mapOf( "estado" to 3 ), mapOf( "id" to mapOf( "operador" to "IN", "valores" to setOf( id1, id2, id3 ) ) ) )

    }

    @Test
    fun collerTodasActividades() {

        val id1 = 915L
        val id2 = 916L

        db.insertar( "actividades", datosCompletos( mapOf( "id" to id1.toString(), "titulo" to "Sen Filtro A" ) ) )
        db.insertar( "actividades", datosCompletos( mapOf( "id" to id2.toString(), "titulo" to "Sen Filtro B" ) ) )

        val resultado = db.collerActividades()

        assertTrue( resultado.any { it.titulo == "Sen Filtro A" } )
        assertTrue( resultado.any { it.titulo == "Sen Filtro B" } )

        db.actualizar( "actividades", mapOf( "estado" to 3 ), mapOf( "id" to mapOf( "operador" to "IN", "valores" to setOf( id1, id2 ) ) ) )

    }

}