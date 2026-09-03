package org.aprengal.lendasnubeiras.data

import androidx.test.platform.app.InstrumentationRegistry
import org.aprengal.lendasnubeiras.data.actividades.dixitais.datos.Grupo
import org.aprengal.lendasnubeiras.data.actividades.dixitais.datos.Dificultade
import org.aprengal.lendasnubeiras.data.bd.clases.BBDD
import org.aprengal.lendasnubeiras.data.bd.clases.BD
import org.aprengal.lendasnubeiras.data.bd.clases.BD.eliminarPuntuacionActividade
import org.aprengal.lendasnubeiras.data.bd.clasesAxuda.Condicion
import org.aprengal.lendasnubeiras.data.bd.clases.BD.OperadorSimple
import org.aprengal.lendasnubeiras.data.bd.clasesAxuda.SeleccionSQL
import org.aprengal.lendasnubeiras.data.bd.operacions.Actividades.collerActividade
import org.aprengal.lendasnubeiras.data.bd.operacions.Actividades.collerActividades
import org.aprengal.lendasnubeiras.data.bd.operacions.Grupos.actualizarGrupo

import org.aprengal.lendasnubeiras.data.bd.operacions.Grupos.collerGrupo
import org.aprengal.lendasnubeiras.data.bd.operacions.Grupos.collerGrupos
import org.aprengal.lendasnubeiras.data.bd.operacions.Grupos.eliminarGrupo
import org.aprengal.lendasnubeiras.data.bd.operacions.Grupos.insertarGrupo
import org.aprengal.lendasnubeiras.data.bd.operacions.Puntuacions.actualizarPuntuacion
import org.aprengal.lendasnubeiras.data.bd.operacions.Puntuacions.collerClasificacion
import org.aprengal.lendasnubeiras.data.bd.operacions.Puntuacions.eliminarPuntuacion
import org.aprengal.lendasnubeiras.data.bd.operacions.Puntuacions.insertarPuntuacion
import org.aprengal.lendasnubeiras.data.bd.operacions.Xogadores.actualizarXogador
import org.aprengal.lendasnubeiras.data.bd.operacions.Xogadores.collerXogador
import org.aprengal.lendasnubeiras.data.bd.operacions.Xogadores.collerXogadores
import org.aprengal.lendasnubeiras.data.bd.operacions.Xogadores.eliminarXogador
import org.aprengal.lendasnubeiras.data.bd.operacions.Xogadores.insertarXogador
import org.aprengal.lendasnubeiras.data.bd.taboas.TaboaBase
import org.aprengal.lendasnubeiras.datosCompletos

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

class SQLITEMonitor {

    companion object {

        private lateinit var bd: BBDD
        private val db: BD = BD

        @JvmStatic
        @BeforeClass
        fun preparar() {
            val contexto = InstrumentationRegistry.getInstrumentation().targetContext
            db.arrancar( contexto )
            bd = db.bd
        }

    }

    @Before
    fun limparAntes() {
        bd.eliminar( TaboaBase.GRUPOS, mapOf( "id" to Condicion.Simple( 0, OperadorSimple.DISTINTO ) ) )
        val ondeLimpeza = mapOf( "estado" to Condicion.En( setOf( -3, -2, -1, 3 ) ) )
        bd.eliminar( TaboaBase.ACTIVIDADES, ondeLimpeza )
    }

    @After
    fun limparDespois() {
        bd.eliminar( TaboaBase.GRUPOS, mapOf( "id" to Condicion.Simple( 0, OperadorSimple.DISTINTO ) ) )
        val ondeLimpeza = mapOf( "estado" to Condicion.En( setOf( -3, -2, -1, 3 ) ) )
        bd.eliminar( TaboaBase.ACTIVIDADES, ondeLimpeza )
    }

    @Test
    fun grupoIdDuplicadaRexeitado() {

        val id = bd.insertar( TaboaBase.GRUPOS, mapOf( "id" to 9500L, "nome" to "Grupo Clave Primeira" ) )
        assertNotEquals( -1L, id )

        assertEquals( -1L, bd.insertar( TaboaBase.GRUPOS, mapOf( "id" to 9500L, "nome" to "Grupo Clave Segunda" ) ) )

        bd.eliminar( TaboaBase.GRUPOS, mapOf( "id" to Condicion.Simple( 9500L ) ) )

    }

    @Test
    fun grupoIdNegativoRexeitado() {
        assertEquals( -1L, bd.insertar( TaboaBase.GRUPOS, mapOf( "id" to -5L, "clave" to "Grupo Id Negativo" ) ) )
    }

    @Test
    fun grupoNomeDemasiadoLongoRexeitado() {
        assertEquals( -1L, insertarGrupo( "a".repeat( 101 ) ) )
    }

    @Test
    fun grupoNomeLimiteMinimoAceptado() {
        val id = insertarGrupo( "a" )
        assertNotEquals( -1L, id )
        bd.eliminar( TaboaBase.GRUPOS, mapOf( "id" to Condicion.Simple( id ) ) )
    }

    @Test
    fun grupoNomeLimiteMaximoAceptado() {
        val id = insertarGrupo( "a".repeat( 100 ) )
        assertNotEquals( -1L, id )
        bd.eliminar( TaboaBase.GRUPOS, mapOf( "id" to Condicion.Simple( id ) ) )
    }

    @Test
    fun inserirGrupoNomeDuplicado() {
        val id = insertarGrupo( "Equipo A" )
        assertNotEquals( -1, id )
        assertEquals( -1, insertarGrupo( "equipo a" ) )
    }

    @Test
    fun actualizarGrupoInexistente() {

        val idGrupo1 = insertarGrupo( "Equipo I" )
        val idGrupo2 = insertarGrupo( "Equipo J" )
        val grupo2 = collerGrupo( idGrupo2 )!!

        assertEquals( -1, actualizarGrupo( grupo2, mapOf( "clave" to "equipo i" ) ) )

        bd.eliminar( TaboaBase.GRUPOS, mapOf( "id" to Condicion.En( setOf( idGrupo1, idGrupo2 ) ) ) )

    }

    @Test
    fun actualizarGrupAceptado() {

        val idGrupo = insertarGrupo( "Equipo K" )
        val grupo = collerGrupo( idGrupo )!!

        assertEquals( 1, actualizarGrupo( grupo, mapOf( "nome" to "Equipo K Renomeado" ) ) )
        assertEquals( "Equipo K Renomeado", collerGrupo( idGrupo )!!.nome )

        bd.eliminar( TaboaBase.GRUPOS, mapOf( "id" to Condicion.Simple( idGrupo ) ) )

    }

    @Test
    fun quitarGrupo() { //Efecto cascada con xogador e puntuacións

        val idActividade = 900L
        bd.insertar( TaboaBase.ACTIVIDADES,
            datosCompletos(mapOf("id" to idActividade.toString(), "titulo" to "Cascade"))
        )
        val actividade = collerActividade( idActividade )!!

        val idGrupo = insertarGrupo( "Equipo B" )
        val grupo = collerGrupo( idGrupo )!!
        val idXogador = insertarXogador( "Ana", grupo )
        val xogador = collerXogador( idXogador )!!

        insertarPuntuacion( actividade, Dificultade.Facil, xogador, 100L )
        eliminarGrupo( grupo )
        assertNull( collerXogador( idXogador ) )

        val onde = SeleccionSQL( columnas = setOf( "*" ), onde = mapOf( "xogador_id" to Condicion.Simple( idXogador ) ) )
        assertTrue( bd.seleccionar( TaboaBase.PUNTUACIONS, onde ).isEmpty() )

        bd.actualizar( TaboaBase.ACTIVIDADES, mapOf( "estado" to 3 ), mapOf( "id" to Condicion.Simple( idActividade ) ) )

    }

    @Test
    fun xogadorIdDuplicadaRexeitado() {

        val idGrupo = insertarGrupo( "Equipo Clave Xogador" )
        val grupo = collerGrupo( idGrupo )!!

        val id = bd.insertar( TaboaBase.XOGADORES, mapOf( "id" to 9600L, "nome" to "Primeiro", "grupo_id" to grupo.id ) )
        assertNotEquals( -1L, id )

        assertEquals( -1L, bd.insertar( TaboaBase.XOGADORES, mapOf( "id" to 9600L, "nome" to "Segundo", "grupo_id" to grupo.id ) ) )

        bd.eliminar( TaboaBase.GRUPOS, mapOf( "id" to Condicion.Simple( idGrupo ) ) )

    }

    @Test
    fun xogadorIdNegativoRexeitado() {

        val idGrupo = insertarGrupo( "Equipo Check X" )
        val grupo = collerGrupo( idGrupo )!!

        assertEquals( -1L, bd.insertar( TaboaBase.XOGADORES, mapOf( "id" to -7L, "clave" to "Proba", "grupo_id" to grupo.id ) ) )

        bd.eliminar( TaboaBase.GRUPOS, mapOf( "id" to Condicion.Simple( idGrupo ) ) )

    }

    @Test
    fun xogadorGrupoIdNegativoRexeitado() {
        assertEquals( -1L, bd.insertar( TaboaBase.XOGADORES, mapOf( "clave" to "Proba", "grupo_id" to -8L ) ) )
    }

    @Test
    fun xogadorNomeBaleiroRexeitado() {

        val idGrupo = insertarGrupo( "Equipo Check Y" )
        val grupo = collerGrupo( idGrupo )!!

        assertEquals( -1L, insertarXogador( "", grupo ) )

        bd.eliminar( TaboaBase.GRUPOS, mapOf( "id" to Condicion.Simple( idGrupo ) ) )

    }

    @Test
    fun xogadorNomeDemasiadoLongoRexeitado() {

        val idGrupo = insertarGrupo( "Equipo Check Z" )
        val grupo = collerGrupo( idGrupo )!!

        assertEquals( -1L, insertarXogador( "a".repeat( 101 ), grupo ) )

        bd.eliminar( TaboaBase.GRUPOS, mapOf( "id" to Condicion.Simple( idGrupo ) ) )

    }

    @Test
    fun xogadorNomeLimitesAceptados() {

        val idGrupo = insertarGrupo( "Equipo Check W" )
        val grupo = collerGrupo( idGrupo )!!

        assertNotEquals( -1L, insertarXogador( "a", grupo ) )
        assertNotEquals( -1L, insertarXogador( "a".repeat( 100 ), grupo ) )

        bd.eliminar( TaboaBase.GRUPOS, mapOf( "id" to Condicion.Simple( idGrupo ) ) )

    }

    @Test
    fun inserirXogadorGrupoInexistente() {
        val grupoFalso = Grupo( id = 9999L, nome = "Non existe" )
        assertEquals( -1L, insertarXogador( "Pedro", grupoFalso ) )
    }

    @Test
    fun inserirXogadorNomeDuplicadoRexeitado() {

        val idGrupo = insertarGrupo( "Equipo O" )
        val grupo = collerGrupo( idGrupo )!!

        insertarXogador( "Marta", grupo )

        assertEquals( -1L, insertarXogador( "marta", grupo ) )

        bd.eliminar( TaboaBase.GRUPOS, mapOf( "id" to Condicion.Simple( idGrupo ) ) )

    }

    @Test
    fun inserirXogadorNomeDuplicadoGruposDistintosAceptado() {

        val idGrupo1 = insertarGrupo( "Equipo P" )
        val idGrupo2 = insertarGrupo( "Equipo Q" )
        val grupo1 = collerGrupo( idGrupo1 )!!
        val grupo2 = collerGrupo( idGrupo2 )!!

        val idXogador1 = insertarXogador( "Marta", grupo1 )
        val idXogador2 = insertarXogador( "Marta", grupo2 )

        assertNotEquals( -1L, idXogador1 )
        assertNotEquals( -1L, idXogador2 )

        bd.eliminar( TaboaBase.GRUPOS, mapOf( "id" to Condicion.En( setOf( idGrupo1, idGrupo2 ) ) ) )

    }

    @Test
    fun actualizarXogadorGrupoRexeitado() {

        val idGrupo = insertarGrupo( "Equipo L" )
        val grupo = collerGrupo( idGrupo )!!
        val idXogador = insertarXogador( "Breixo", grupo )
        val xogador = collerXogador( idXogador )!!

        assertEquals( -1, actualizarXogador( xogador, mapOf( "grupo_id" to 9999L ) ) )

        bd.eliminar( TaboaBase.GRUPOS, mapOf( "id" to Condicion.Simple( idGrupo ) ) )

    }

    @Test
    fun actualizarXogadorNomeAceptado() {

        val idGrupo = insertarGrupo( "Equipo M" )
        val grupo = collerGrupo( idGrupo )!!
        val idXogador = insertarXogador( "Xela", grupo )
        val xogador = collerXogador( idXogador )!!

        assertEquals( 1, actualizarXogador( xogador, mapOf( "nome" to "Xela Renomeada" ) ) )
        assertEquals( "Xela Renomeada", collerXogador( idXogador )!!.nome )

        bd.eliminar( TaboaBase.GRUPOS, mapOf( "id" to Condicion.Simple( idGrupo ) ) )

    }

    @Test
    fun actualizarXogadorNomeDuplicadoRexeitado() {

        val idGrupo = insertarGrupo( "Equipo N" )
        val grupo = collerGrupo( idGrupo )!!

        insertarXogador( "Noa", grupo )
        val idXogador2 = insertarXogador( "Uxía", grupo )
        val xogador2 = collerXogador( idXogador2 )!!

        assertEquals( -1, actualizarXogador( xogador2, mapOf( "clave" to "noa" ) ) )

        bd.eliminar( TaboaBase.GRUPOS, mapOf( "id" to Condicion.Simple( idGrupo ) ) )

    }

    @Test
    fun quitarXogador() {

        val idActividade = 901L
        bd.insertar( TaboaBase.ACTIVIDADES,
            datosCompletos(mapOf("id" to idActividade.toString(), "titulo" to "Cascade Xogador"))
        )
        val actividade = collerActividade( idActividade )!!

        val idGrupo = insertarGrupo( "Equipo C" )
        val grupo = collerGrupo( idGrupo )!!
        val idXogador = insertarXogador( "Marta", grupo )
        val xogador = collerXogador( idXogador )!!

        insertarPuntuacion( actividade, Dificultade.Facil, xogador, 50L )
        eliminarXogador( xogador )

        val onde = SeleccionSQL( columnas = setOf( "*" ), onde = mapOf( "xogador_id" to Condicion.Simple( idXogador ) ) )
        assertTrue( bd.seleccionar( TaboaBase.PUNTUACIONS, onde ).isEmpty() )

        eliminarGrupo( grupo )
        bd.actualizar( TaboaBase.ACTIVIDADES, mapOf( "estado" to 3 ), mapOf( "id" to Condicion.Simple( idActividade ) ) )

    }

    @Test
    fun inserirPuntuacionClaveDuplicada() {

        val idActividade = 902L
        bd.insertar( TaboaBase.ACTIVIDADES,
            datosCompletos(mapOf("id" to idActividade.toString(), "titulo" to "Clave Duplicada"))
        )
        val actividade = collerActividade( idActividade )!!

        val idGrupo = insertarGrupo( "Equipo D" )
        val grupo = collerGrupo( idGrupo )!!
        val idXogador = insertarXogador( "Luis", grupo )
        val xogador = collerXogador( idXogador )!!

        insertarPuntuacion( actividade, Dificultade.Facil, xogador, 10L )

        assertEquals( -1L, insertarPuntuacion( actividade, Dificultade.Facil, xogador, 20L ) )

        eliminarGrupo( grupo )
        bd.actualizar( TaboaBase.ACTIVIDADES, mapOf( "estado" to 3 ), mapOf( "id" to Condicion.Simple( idActividade ) ) )

    }

    @Test
    fun puntuacionActividadeIdNegativoRexeitado() {
        assertEquals(
            -1L,
            bd.insertar(
                TaboaBase.PUNTUACIONS,
                mapOf( "actividade_id" to -9L, "dificultade" to Dificultade.Facil.clave, "xogador_id" to 1L, "puntos" to 10L, "unix_rexistro" to 1000L )
            )
        )
    }

    @Test
    fun puntuacionXogadorIdNegativoRexeitado() {

        val idActividade = 918L
        bd.insertar( TaboaBase.ACTIVIDADES,
            datosCompletos(mapOf("id" to idActividade.toString(), "titulo" to "Check Puntuacion Xogador"))
        )

        assertEquals(
            -1L,
            bd.insertar(
                TaboaBase.PUNTUACIONS,
                mapOf( "actividade_id" to idActividade, "dificultade" to Dificultade.Facil.clave, "xogador_id" to -6L, "puntos" to 10L, "unix_rexistro" to 1000L )
            )
        )

        bd.actualizar( TaboaBase.ACTIVIDADES, mapOf( "estado" to 3 ), mapOf( "id" to Condicion.Simple( idActividade ) ) )

    }

    @Test
    fun puntuacionPuntosNegativoRexeitado() {

        val idActividade = 919L
        bd.insertar( TaboaBase.ACTIVIDADES,
            datosCompletos(mapOf("id" to idActividade.toString(), "titulo" to "Check Puntos"))
        )

        val idGrupo = insertarGrupo( "Equipo Check Puntos" )
        val grupo = collerGrupo( idGrupo )!!
        val idXogador = insertarXogador( "Breixo Check", grupo )

        assertEquals(
            -1L,
            bd.insertar(
                TaboaBase.PUNTUACIONS,
                mapOf( "actividade_id" to idActividade, "dificultade" to Dificultade.Facil.clave, "xogador_id" to idXogador, "puntos" to -12L, "unix_rexistro" to 1000L )
            )
        )

        bd.eliminar( TaboaBase.GRUPOS, mapOf( "id" to Condicion.Simple( idGrupo ) ) )
        bd.actualizar( TaboaBase.ACTIVIDADES, mapOf( "estado" to 3 ), mapOf( "id" to Condicion.Simple( idActividade ) ) )

    }

    @Test
    fun puntuacionUnixRexistroNegativoRexeitado() {

        val idActividade = 920L
        bd.insertar( TaboaBase.ACTIVIDADES,
            datosCompletos(mapOf("id" to idActividade.toString(), "titulo" to "Check Unix"))
        )

        val idGrupo = insertarGrupo( "Equipo Check Unix" )
        val grupo = collerGrupo( idGrupo )!!
        val idXogador = insertarXogador( "Sabela Check", grupo )

        assertEquals(
            -1L,
            bd.insertar(
                TaboaBase.PUNTUACIONS,
                mapOf( "actividade_id" to idActividade, "dificultade" to Dificultade.Facil.clave, "xogador_id" to idXogador, "puntos" to 10L, "unix_rexistro" to -15L )
            )
        )

        bd.eliminar( TaboaBase.GRUPOS, mapOf( "id" to Condicion.Simple( idGrupo ) ) )
        bd.actualizar( TaboaBase.ACTIVIDADES, mapOf( "estado" to 3 ), mapOf( "id" to Condicion.Simple( idActividade ) ) )

    }

    @Test
    fun insertarPuntuacionConTimestampActual() {

        val idActividade = 917L
        bd.insertar( TaboaBase.ACTIVIDADES,
            datosCompletos(mapOf("id" to idActividade.toString(), "titulo" to "Insertar Puntuacion Real"))
        )
        val actividade = collerActividade( idActividade )!!

        val idGrupo = insertarGrupo( "Equipo Y" )
        val grupo = collerGrupo( idGrupo )!!
        val idXogador = insertarXogador( "Roi", grupo )
        val xogador = collerXogador( idXogador )!!

        val antes = Instant.now().epochSecond
        val idPuntuacion = insertarPuntuacion( actividade, Dificultade.Facil, xogador, 200L )
        val despois = Instant.now().epochSecond

        assertNotEquals( -1L, idPuntuacion )

        val onde = SeleccionSQL(
            columnas = setOf( "*" ),
            onde = mapOf(
                "actividade_id" to Condicion.Simple( idActividade ),
                "dificultade" to Condicion.Simple( Dificultade.Facil.clave ),
                "xogador_id" to Condicion.Simple( idXogador )
            )
        )

        val resultado = bd.seleccionar( TaboaBase.PUNTUACIONS, onde )

        assertEquals( 1, resultado.size )
        assertEquals( 200L, resultado[ 0 ][ "puntos" ] )

        val unixRexistro = resultado[ 0 ][ "unix_rexistro" ] as Long
        assertTrue( unixRexistro in antes..despois )

        bd.eliminar( TaboaBase.GRUPOS, mapOf( "id" to Condicion.Simple( idGrupo ) ) )
        bd.actualizar( TaboaBase.ACTIVIDADES, mapOf( "estado" to 3 ), mapOf( "id" to Condicion.Simple( idActividade ) ) )

    }

    @Test
    fun actualizarPuntuacionInferior() {

        val idActividade = 903L
        bd.insertar( TaboaBase.ACTIVIDADES,
            datosCompletos(mapOf("id" to idActividade.toString(), "titulo" to "Puntuacion Inferior"))
        )
        val actividade = collerActividade( idActividade )!!

        val idGrupo = insertarGrupo( "Equipo E" )
        val grupo = collerGrupo( idGrupo )!!
        val idXogador = insertarXogador( "Sara", grupo )
        val xogador = collerXogador( idXogador )!!

        insertarPuntuacion( actividade, Dificultade.Facil, xogador, 100L )

        assertEquals( 0, actualizarPuntuacion( actividade, Dificultade.Facil, xogador, 50L ) )

        eliminarGrupo( grupo )
        bd.actualizar( TaboaBase.ACTIVIDADES, mapOf( "estado" to 3 ), mapOf( "id" to Condicion.Simple( idActividade ) ) )

    }

    @Test
    fun actualizarPuntuacionIgual() {

        val idActividade = 904L
        bd.insertar( TaboaBase.ACTIVIDADES,
            datosCompletos(mapOf("id" to idActividade.toString(), "titulo" to "Puntuacion Igual"))
        )
        val actividade = collerActividade( idActividade )!!

        val idGrupo = insertarGrupo( "Equipo F" )
        val grupo = collerGrupo( idGrupo )!!
        val idXogador = insertarXogador( "Iria", grupo )
        val xogador = collerXogador( idXogador )!!

        insertarPuntuacion( actividade, Dificultade.Facil, xogador, 100L )

        assertEquals( 0, actualizarPuntuacion( actividade, Dificultade.Facil, xogador, 100L ) )

        eliminarGrupo( grupo )
        bd.actualizar( TaboaBase.ACTIVIDADES, mapOf( "estado" to 3 ), mapOf( "id" to Condicion.Simple( idActividade ) ) )

    }

    @Test
    fun actualizarPuntuacionSuperior() {

        val idActividade = 905L
        bd.insertar( TaboaBase.ACTIVIDADES,
            datosCompletos(mapOf("id" to idActividade.toString(), "titulo" to "Puntuacion Superior"))
        )
        val actividade = collerActividade( idActividade )!!

        val idGrupo = insertarGrupo( "Equipo G" )
        val grupo = collerGrupo( idGrupo )!!
        val idXogador = insertarXogador( "Noa", grupo )
        val xogador = collerXogador( idXogador )!!

        insertarPuntuacion( actividade, Dificultade.Facil, xogador, 100L )

        assertEquals( 1, actualizarPuntuacion( actividade, Dificultade.Facil, xogador, 150L ) )

        eliminarGrupo( grupo )
        bd.actualizar( TaboaBase.ACTIVIDADES, mapOf( "estado" to 3 ), mapOf( "id" to Condicion.Simple( idActividade ) ) )

    }

    @Test
    fun proteccionActualizarPuntuacion() {

        val idActividade = 906L
        bd.insertar( TaboaBase.ACTIVIDADES,
            datosCompletos(mapOf("id" to idActividade.toString(), "titulo" to "Trigger Proteccion"))
        )
        val actividade = collerActividade( idActividade )!!

        val idGrupo = insertarGrupo( "Equipo H" )
        val grupo = collerGrupo( idGrupo )!!
        val idXogador = insertarXogador( "Uxía", grupo )
        val xogador = collerXogador( idXogador )!!

        insertarPuntuacion( actividade, Dificultade.Facil, xogador, 100L )

        val onde = mapOf(
            "actividade_id" to Condicion.Simple( actividade.id ),
            "dificultade" to Condicion.Simple( Dificultade.Facil.clave ),
            "xogador_id" to Condicion.Simple( xogador.id )
        )

        assertEquals( -1, bd.actualizar( TaboaBase.PUNTUACIONS, mapOf( "puntos" to 100L ), onde ) )

        eliminarGrupo( grupo )
        bd.actualizar( TaboaBase.ACTIVIDADES, mapOf( "estado" to 3 ), mapOf( "id" to Condicion.Simple( idActividade ) ) )

    }

    @Test
    fun eliminarPuntuacionExistente() {

        val idActividade = 907L
        bd.insertar( TaboaBase.ACTIVIDADES,
            datosCompletos(mapOf("id" to idActividade.toString(), "titulo" to "Eliminar Puntuacion"))
        )
        val actividade = collerActividade( idActividade )!!

        val idGrupo = insertarGrupo( "Equipo N" )
        val grupo = collerGrupo( idGrupo )!!
        val idXogador = insertarXogador( "Iago", grupo )
        val xogador = collerXogador( idXogador )!!

        insertarPuntuacion( actividade, Dificultade.Facil, xogador, 100L )

        assertEquals( 1, eliminarPuntuacion( actividade, Dificultade.Facil, xogador ) )

        val onde = SeleccionSQL( columnas = setOf( "*" ), onde = mapOf( "xogador_id" to Condicion.Simple( idXogador ) ) )
        assertTrue( bd.seleccionar( TaboaBase.PUNTUACIONS, onde ).isEmpty() )

        bd.eliminar( TaboaBase.GRUPOS, mapOf( "id" to Condicion.Simple( idGrupo ) ) )
        bd.actualizar( TaboaBase.ACTIVIDADES, mapOf( "estado" to 3 ), mapOf( "id" to Condicion.Simple( idActividade ) ) )

    }

    @Test
    fun collerClasificacionOrdenada() {

        val idActividade = 908L
        bd.insertar( TaboaBase.ACTIVIDADES,
            datosCompletos(mapOf("id" to idActividade.toString(), "titulo" to "Clasificacion Proba"))
        )
        val actividade = collerActividade( idActividade )!!

        val idGrupo = insertarGrupo( "Equipo S" )
        val grupo = collerGrupo( idGrupo )!!

        val idXogador1 = insertarXogador( "Breogan", grupo )
        val idXogador2 = insertarXogador( "Sabela", grupo )
        val idXogador3 = insertarXogador( "Antia", grupo )

        val idXogador4 = insertarXogador( "Antia2", grupo )

        val xogador1 = collerXogador( idXogador1 )!!
        val xogador2 = collerXogador( idXogador2 )!!
        val xogador3 = collerXogador( idXogador3 )!!
        val xogador4 = collerXogador( idXogador4 )!!

        val puntuacion = mutableMapOf(
            "actividade_id" to actividade.id,
            "dificultade" to Dificultade.Facil.clave,
            "xogador_id" to xogador1.id,
            "puntos" to 100L,
            "unix_rexistro" to 1000L
        )

        bd.insertar( TaboaBase.PUNTUACIONS, puntuacion )

        puntuacion[ "xogador_id" ] = xogador2.id
        puntuacion[ "puntos" ] = 150L
        puntuacion[ "unix_rexistro" ] = 2000L
        bd.insertar( TaboaBase.PUNTUACIONS, puntuacion )

        puntuacion[ "dificultade" ] = Dificultade.Normal.clave
        puntuacion[ "xogador_id" ] = xogador4.id
        bd.insertar( TaboaBase.PUNTUACIONS, puntuacion )

        puntuacion[ "dificultade" ] = Dificultade.Facil.clave
        puntuacion[ "xogador_id" ] = xogador3.id
        puntuacion[ "puntos" ] = 100L
        puntuacion[ "unix_rexistro" ] = 500L
        bd.insertar( TaboaBase.PUNTUACIONS, puntuacion )

        val clasificacion = collerClasificacion( actividade, Dificultade.Facil )

        assertEquals( "Clasificacion Proba", clasificacion.tituloActividade )
        assertEquals( Dificultade.Facil, clasificacion.dificultade )
        assertEquals( 3, clasificacion.listaPuntuacions.size )

        val ordeEsperada = listOf( "Sabela", "Antia", "Breogan" )
        assertEquals( ordeEsperada, clasificacion.listaPuntuacions.keys.toList() )
        assertFalse( clasificacion.listaPuntuacions.containsKey( "Antia2" ) )

        assertEquals( 150L, clasificacion.listaPuntuacions[ "Sabela" ]!![ "puntos" ] )
        assertEquals( 100L, clasificacion.listaPuntuacions[ "Antia" ]!![ "puntos" ] )
        assertEquals( 500L, clasificacion.listaPuntuacions[ "Antia" ]!![ "unix_rexistro" ] )
        assertEquals( 100L, clasificacion.listaPuntuacions[ "Breogan" ]!![ "puntos" ] )
        assertEquals( 1000L, clasificacion.listaPuntuacions[ "Breogan" ]!![ "unix_rexistro" ] )

        bd.eliminar( TaboaBase.GRUPOS, mapOf( "id" to Condicion.Simple( idGrupo ) ) )
        bd.actualizar( TaboaBase.ACTIVIDADES, mapOf( "estado" to 3 ), mapOf( "id" to Condicion.Simple( idActividade ) ) )

    }

    @Test
    fun collerClasificacionBaleira() {

        val idActividade = 909L
        bd.insertar( TaboaBase.ACTIVIDADES,
            datosCompletos(mapOf("id" to idActividade.toString(), "titulo" to "Clasificacion Baleira"))
        )
        val actividade = collerActividade( idActividade )!!

        val clasificacion = collerClasificacion( actividade, Dificultade.Facil )

        assertEquals( "Clasificacion Baleira", clasificacion.tituloActividade )
        assertTrue( clasificacion.listaPuntuacions.isEmpty() )

        bd.actualizar( TaboaBase.ACTIVIDADES, mapOf( "estado" to 3 ), mapOf( "id" to Condicion.Simple( idActividade ) ) )

    }

    @Test
    fun eliminarActividadePuntuacions() {

        val idActividade = 910L
        bd.insertar( TaboaBase.ACTIVIDADES,
            datosCompletos(mapOf("id" to idActividade.toString(), "titulo" to "Eliminar Actividade"))
        )
        val actividade = collerActividade( idActividade )!!

        val idGrupo = insertarGrupo( "Equipo T" )
        val grupo = collerGrupo( idGrupo )!!
        val idXogador = insertarXogador( "Tomas", grupo )
        val xogador = collerXogador( idXogador )!!

        insertarPuntuacion( actividade, Dificultade.Facil, xogador, 100L )

        val filasEliminadas = eliminarPuntuacionActividade( actividade )

        assertEquals( 1, filasEliminadas )
        assertNull( collerActividade( idActividade ) )

        val onde = SeleccionSQL( columnas = setOf( "*" ), onde = mapOf( "actividade_id" to Condicion.Simple( idActividade ) ) )
        assertTrue( bd.seleccionar( TaboaBase.PUNTUACIONS, onde ).isEmpty() )

        bd.eliminar( TaboaBase.GRUPOS, mapOf( "id" to Condicion.Simple( idGrupo ) ) )

    }

    @Test
    fun eliminarActividadeSenPuntuacions() {

        val idActividade = 911L
        bd.insertar( TaboaBase.ACTIVIDADES,
            datosCompletos(mapOf("id" to idActividade.toString(), "titulo" to "Sen Puntuacions"))
        )
        val actividade = collerActividade( idActividade )!!

        val filasEliminadas = eliminarPuntuacionActividade( actividade )

        assertEquals( 1, filasEliminadas )
        assertNull( collerActividade( idActividade ) )

    }

    @Test
    fun collerTodosGrupos() {

        val idGrupo1 = insertarGrupo( "Equipo U" )
        val idGrupo2 = insertarGrupo( "Equipo V" )

        val grupos = collerGrupos()

        assertTrue( grupos.any { it.id == idGrupo1 && it.nome == "Equipo U" } )
        assertTrue( grupos.any { it.id == idGrupo2 && it.nome == "Equipo V" } )

        bd.eliminar( TaboaBase.GRUPOS, mapOf( "id" to Condicion.En( setOf( idGrupo1, idGrupo2 ) ) ) )

    }

    @Test
    fun collerXogadoresFiltrados() {

        val idGrupo1 = insertarGrupo( "Equipo W" )
        val idGrupo2 = insertarGrupo( "Equipo X" )
        val grupo1 = collerGrupo( idGrupo1 )!!
        val grupo2 = collerGrupo( idGrupo2 )!!

        insertarXogador( "Sabela", grupo1 )
        insertarXogador( "Iago", grupo1 )
        insertarXogador( "Noa", grupo2 )

        val xogadoresGrupo1 = collerXogadores( grupo1 )
        val xogadoresGrupo2 = collerXogadores( grupo2 )

        assertEquals( 2, xogadoresGrupo1.size )
        assertTrue( xogadoresGrupo1.any { it.nome == "Sabela" } )
        assertTrue( xogadoresGrupo1.any { it.nome == "Iago" } )

        assertEquals( 1, xogadoresGrupo2.size )
        assertEquals( "Noa", xogadoresGrupo2[ 0 ].nome )

        bd.eliminar( TaboaBase.GRUPOS, mapOf( "id" to Condicion.En( setOf( idGrupo1, idGrupo2 ) ) ) )

    }

    @Test
    fun collerActividadesFiltradas() {

        val id1 = 912L
        val id2 = 913L
        val id3 = 914L

        bd.insertar( TaboaBase.ACTIVIDADES,
            datosCompletos(mapOf("id" to id1.toString(), "titulo" to "Filtro A", "id_autoria" to "40"))
        )
        bd.insertar( TaboaBase.ACTIVIDADES,
            datosCompletos(mapOf("id" to id2.toString(), "titulo" to "Filtro B", "id_autoria" to "40"))
        )
        bd.insertar( TaboaBase.ACTIVIDADES,
            datosCompletos(mapOf("id" to id3.toString(), "titulo" to "Filtro C", "id_autoria" to "41"))
        )

        val onde = mapOf( "id_autoria" to Condicion.Simple( 40L ) )
        val resultado = collerActividades( onde )

        assertEquals( 2, resultado.size )
        assertTrue( resultado.any { it.titulo == "Filtro A" } )
        assertTrue( resultado.any { it.titulo == "Filtro B" } )
        assertTrue( resultado.none { it.titulo == "Filtro C" } )

        bd.actualizar( TaboaBase.ACTIVIDADES, mapOf( "estado" to 3 ), mapOf( "id" to Condicion.En( setOf( id1, id2, id3 ) ) ) )

    }

    @Test
    fun collerTodasActividades() {

        val id1 = 915L
        val id2 = 916L

        bd.insertar( TaboaBase.ACTIVIDADES, datosCompletos(mapOf("id" to id1.toString(), "titulo" to "Sen Filtro A")))
        bd.insertar( TaboaBase.ACTIVIDADES, datosCompletos(mapOf("id" to id2.toString(), "titulo" to "Sen Filtro B")))

        val resultado = collerActividades()

        assertTrue( resultado.any { it.titulo == "Sen Filtro A" } )
        assertTrue( resultado.any { it.titulo == "Sen Filtro B" } )

        bd.actualizar( TaboaBase.ACTIVIDADES, mapOf( "estado" to 3 ), mapOf( "id" to Condicion.En( setOf( id1, id2 ) ) ) )

    }

}