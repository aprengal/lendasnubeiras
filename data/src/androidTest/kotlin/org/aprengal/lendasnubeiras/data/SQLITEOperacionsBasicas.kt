package org.aprengal.lendasnubeiras.data

import androidx.test.platform.app.InstrumentationRegistry
import org.aprengal.lendasnubeiras.data.actividades.datos.Categoria
import org.aprengal.lendasnubeiras.data.bd.clases.BBDD
import org.aprengal.lendasnubeiras.data.bd.clases.BD
import org.aprengal.lendasnubeiras.data.bd.clasesAxuda.Condicion
import org.aprengal.lendasnubeiras.data.bd.clasesAxuda.SeleccionSQL
import org.aprengal.lendasnubeiras.data.bd.operacions.Actividades.collerActividade
import org.aprengal.lendasnubeiras.data.bd.operacions.Actividades.collerActividadesBuscables
import org.aprengal.lendasnubeiras.data.bd.clases.Taboa
//import org.aprengal.lendasnubeiras.data.bd.taboas.TaboaLectura
import org.aprengal.lendasnubeiras.data.localizacion.clases.Localizacion
import org.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeCrear
import org.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeEditarOutras
import org.aprengal.lendasnubeiras.data.usuarios.Rol
import org.aprengal.lendasnubeiras.data.usuarios.Usuario

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

        private lateinit var bd: BBDD
        private val db: BD = BD

        @JvmStatic
        @BeforeClass
        fun preparar() {
            val contexto = InstrumentationRegistry.getInstrumentation().targetContext
            db.arrancar( contexto )
            Localizacion.arrancar( contexto )
            bd = db.bd
        }

    }

    @Before
    fun limparAntes() {
        val ondeLimpeza = mapOf( "estado" to Condicion.En( setOf( -3, -2, -1, 3 ) ) )
        bd.eliminar( Taboa.ACTIVIDADES, ondeLimpeza )
    }

    @After
    fun limparDespois() {
        val ondeLimpeza = mapOf( "estado" to Condicion.En( setOf( -3, -2, -1, 3 ) ) )
        bd.eliminar( Taboa.ACTIVIDADES, ondeLimpeza )
    }

    @Test
    fun inserirActividade() {

        val datos = datosCompletos(mapOf("id" to "101", "clave_titulo" to "proba-1"))
        val id = bd.insertar( Taboa.ACTIVIDADES, datos )

        assertTrue( id > 0 )

        val resultado = collerActividade( id )!!

        assertEquals( "proba-1", resultado.claveTitulo )
        //assertEquals( "Descrición de proba abondo longa", resultado.descricion )

    }

    @Test
    fun inserirVariasActividades() {

        val datos = listOf(
            datosCompletosAny(mapOf("id" to 1L, "clave_titulo" to "proba-1")),
            datosCompletosAny(mapOf("id" to 2L, "clave_titulo" to "proba-2")),
            datosCompletosAny(mapOf("id" to 3L, "clave_titulo" to "proba-3"))
        )

        val ids = bd.insertar( Taboa.ACTIVIDADES, datos )

        assertEquals( 3, ids.size )
        assertTrue( ids.all { it > 0 } )

        val consulta = SeleccionSQL( columnas = setOf( "*" ) )
        val resultados = bd.seleccionar( Taboa.ACTIVIDADES, consulta )

        assertEquals( 3, resultados.size )

    }

    @Test
    fun actualizarActividade() {

        val datos = datosCompletos( mapOf( "id" to "102", "clave_titulo" to "proba-1" ) )
        val id = bd.insertar( Taboa.ACTIVIDADES, datos )

        val cambios = mapOf( "clave_titulo" to "proba-2" )
        val onde = mapOf( "id" to Condicion.Simple( id ) )
        val actualizadas = bd.actualizar( Taboa.ACTIVIDADES, cambios, onde )

        assertEquals( 1, actualizadas )
        assertEquals( "proba-2", collerActividade( id )!!.claveTitulo )
        assertEquals( "proba-2", collerActividade( "proba-2" )!!.claveTitulo )

    }

    @Test
    fun eliminarActividade() {

        val datos = datosCompletos( mapOf( "id" to "103", "clave_titulo" to "proba-1", "estado" to "-1" ) )
        val id = bd.insertar( Taboa.ACTIVIDADES, datos )

        val onde = mapOf( "id" to Condicion.Simple( id ) )
        val eliminadas = bd.eliminar( Taboa.ACTIVIDADES, onde )

        assertEquals( 1, eliminadas )
        assertNull( collerActividade( id ) )

    }

    @Test
    fun eliminarActividadeEnviadaRexeitada() {

        val estadosProhibidos = listOf( "0", "1", "2" )

        for ( estado in estadosProhibidos ) {

            val id = 800L + estado.toInt()
            val datos = datosCompletos(mapOf("id" to id.toString(), "estado" to estado))
            bd.insertar( Taboa.ACTIVIDADES, datos )

            val onde = mapOf( "id" to Condicion.Simple( id ) )
            assertEquals( -1, bd.eliminar( Taboa.ACTIVIDADES, onde ) )

            //Para que poida eliminarse
            bd.actualizar( Taboa.ACTIVIDADES, mapOf( "estado" to 3 ), onde )
            bd.eliminar( Taboa.ACTIVIDADES, onde )

        }

    }

    @Test
    fun idNegativaAceptada() {
        assertNotEquals( -1, bd.insertar( Taboa.ACTIVIDADES, datosCompletos(mapOf("id" to "-200"))) )
    }

    //Non se pode facer o test de comprobar que non se admiten valores nulos porque xa non se admiten estes valores
    //nos métodos que realizan operacións coa base de datos directamente

    @Test
    fun idActividadeDuplicadaRexeitada() {

        val datos1 = datosCompletos(mapOf("id" to "500", "clave_titulo" to "proba-1"))
        bd.insertar( Taboa.ACTIVIDADES, datos1 )

        val datos2 = datosCompletos(mapOf("id" to "500", "clave_titulo" to "proba-2"))
        assertEquals( -1, bd.insertar( Taboa.ACTIVIDADES, datos2 ) )

    }

    @Test
    fun estadoActividadeRexeitado() {
        assertEquals( -1, bd.insertar( Taboa.ACTIVIDADES, datosCompletos(mapOf("id" to "503", "estado" to "4"))) )
        assertEquals( -1, bd.insertar( Taboa.ACTIVIDADES, datosCompletos(mapOf("id" to "504", "estado" to "-4"))) )
    }

    @Test
    fun duracionActividadeRexeitada() {
        assertEquals( -1, bd.insertar( Taboa.ACTIVIDADES, datosCompletos(mapOf("id" to "505", "duracion" to "0"))) )
        assertEquals( -1, bd.insertar( Taboa.ACTIVIDADES, datosCompletos(mapOf("id" to "506", "duracion" to "181"))) )
    }

    /*@Test
    fun descricionActividadeRexeitada() {
        assertEquals( -1, bd.insertar( TaboaBase.ACTIVIDADES, datosCompletos( mapOf( "id" to "507", "descricion" to "curta" ) ) ) )
        assertEquals( -1, bd.insertar( TaboaBase.ACTIVIDADES, datosCompletos( mapOf( "id" to "508", "descricion" to "a".repeat( 1001 ) ) ) ) )
    }*/

    /*@Test
    fun obxectivoActividadeRexeitado() {
        assertEquals( -1, bd.insertar( TaboaBase.ACTIVIDADES, datosCompletos( mapOf( "id" to "509", "obxectivo" to "curto" ) ) ) )
        assertEquals( -1, bd.insertar( TaboaBase.ACTIVIDADES, datosCompletos( mapOf( "id" to "510", "obxectivo" to "a".repeat( 201 ) ) ) ) )
    }*/

    /*@Test
    fun materiaisActividadeRexeitados() {
        assertEquals( -1, bd.insertar( Taboa.ACTIVIDADES, datosCompletos( mapOf( "id" to "511", "materiais" to "a" ) ) ) )
        assertEquals( -1, bd.insertar( Taboa.ACTIVIDADES, datosCompletos( mapOf( "id" to "512", "materiais" to "a".repeat( 201 ) ) ) ) )
    }*/

    @Test
    fun actualizarIdExistente() {

        val datos1 = datosCompletos( mapOf( "id" to "600", "clave_titulo" to "proba-1", "estado" to "-1" ) )
        bd.insertar( Taboa.ACTIVIDADES, datos1 )

        val datos2 = datosCompletos( mapOf( "id" to "601", "clave_titulo" to "proba-2", "estado" to "-1" ) )
        bd.insertar( Taboa.ACTIVIDADES, datos2 )

        val cambios = mapOf( "id" to 600L )
        val onde = mapOf( "id" to Condicion.Simple( 601L ) )

        assertEquals( -1, bd.actualizar( Taboa.ACTIVIDADES, cambios, onde ) )

    }

    @Test
    fun actualizarIdActividadeEnviada() {

        val datos = datosCompletos(mapOf("id" to "602", "clave_titulo" to "proba-1", "estado" to "0"))
        bd.insertar( Taboa.ACTIVIDADES, datos )

        val cambios = mapOf( "id" to 999L )
        val onde = mapOf( "id" to Condicion.Simple( 602L ) )

        assertEquals( -1, bd.actualizar( Taboa.ACTIVIDADES, cambios, onde ) )

        //Actualización de estado para que sexa borrado ou iso crea conflitos noutros tests
        bd.actualizar( Taboa.ACTIVIDADES, mapOf( "estado" to 3 ), onde )

    }

    @Test
    fun actualizarIdActividadeNonEnviada() {

        val datos = datosCompletos( mapOf("id" to "603", "clave_titulo" to "proba-1", "estado" to "-1" ) )
        bd.insertar( Taboa.ACTIVIDADES, datos )

        val cambios = mapOf( "id" to 604L )
        val onde = mapOf( "id" to Condicion.Simple( 603L ) )

        val actualizadas = bd.actualizar( Taboa.ACTIVIDADES, cambios, onde )

        assertEquals( 1, actualizadas )

    }

    /*@Test
    fun inserirActividadeNoBuscador() {

        val id = 700L
        val datos = datosCompletos(mapOf("id" to id.toString(), "clave_titulo" to "Busca Proba", "estado" to "2"))
        bd.insertar( TaboaBase.ACTIVIDADES, datos )

        val onde = mapOf( "docid" to Condicion.Simple( id ) )
        val consulta = SeleccionSQL( columnas = setOf( "*" ), onde = onde )
        val resultado = bd.seleccionar( TaboaLectura.BUSCADOR_ACTIVIDADES, consulta )

        assertEquals( 1, resultado.size )
        assertEquals( "Busca Proba", resultado[ 0 ][ "clave_titulo" ] )

    }

    @Test
    fun actualizarActividadeBuscador() {

        val id = 701L
        val datos = datosCompletos(mapOf("id" to id.toString(), "clave_titulo" to "Título orixinal", "estado" to "2"))
        bd.insertar( TaboaBase.ACTIVIDADES, datos )

        val cambios = mapOf( "clave_titulo" to "Título actualizado" )
        val onde = mapOf( "id" to Condicion.Simple( id ) )
        bd.actualizar( TaboaBase.ACTIVIDADES, cambios, onde )

        val consulta = SeleccionSQL( columnas = setOf( "clave_titulo" ), onde = mapOf( "docid" to Condicion.Simple( id ) ) )
        val resultado = bd.seleccionar( TaboaLectura.BUSCADOR_ACTIVIDADES, consulta )

        assertEquals( "Título actualizado", resultado[ 0 ][ "clave_titulo" ] )

    }

    @Test
    fun eliminarActividadeBuscador() {

        val id = 702L
        val datos = datosCompletos(mapOf("id" to id.toString(), "estado" to "-1"))
        bd.insertar( TaboaBase.ACTIVIDADES, datos )

        val onde = mapOf( "id" to Condicion.Simple( id ) )
        bd.eliminar( TaboaBase.ACTIVIDADES, onde )

        val consulta = SeleccionSQL( columnas = setOf( "*" ), onde = mapOf( "docid" to Condicion.Simple( id ) ) )
        val resultado = bd.seleccionar( TaboaLectura.BUSCADOR_ACTIVIDADES, consulta )

        assertTrue( resultado.isEmpty() )

    }

    @Test
    fun insertarActividadeNonBuscable() {

        val id = 703L
        val datos = datosCompletos(mapOf("id" to id.toString(), "estado" to "0"))
        bd.insertar( TaboaBase.ACTIVIDADES, datos )

        val onde = mapOf( "docid" to Condicion.Simple( id ) )
        val consulta = SeleccionSQL( columnas = setOf( "*" ), onde = onde )
        val resultado = bd.seleccionar( TaboaLectura.BUSCADOR_ACTIVIDADES, consulta )

        assertTrue( resultado.isEmpty() )

        // Axuste a estado 3 para permitir o borrado en limpar()
        val ondeActividade = mapOf( "id" to Condicion.Simple( id ) )
        bd.actualizar( TaboaBase.ACTIVIDADES, mapOf( "estado" to 3 ), ondeActividade )

    }

    @Test
    fun actualizarActividadeBuscable() {

        val id = 704L
        val datos = datosCompletos(mapOf("id" to id.toString(), "estado" to "0", "clave_titulo" to "Pendente"))
        bd.insertar( TaboaBase.ACTIVIDADES, datos )

        val onde = mapOf( "id" to Condicion.Simple( id ) )
        bd.actualizar( TaboaBase.ACTIVIDADES, mapOf( "estado" to 2 ), onde )

        val ondeBuscador = mapOf( "docid" to Condicion.Simple( id ) )
        val consulta = SeleccionSQL( columnas = setOf( "*" ), onde = ondeBuscador )
        val resultado = bd.seleccionar( TaboaLectura.BUSCADOR_ACTIVIDADES, consulta )

        assertEquals( 1, resultado.size )

        // Axuste a estado 3 para permitir o borrado en limpar()
        bd.actualizar( TaboaBase.ACTIVIDADES, mapOf( "estado" to 3 ), onde )

    }

    @Test
    fun actualizarActividadeNonBuscable() {

        val id = 705L
        val datos = datosCompletos(mapOf("id" to id.toString(), "estado" to "2", "clave_titulo" to "Para retirar"))
        bd.insertar( TaboaBase.ACTIVIDADES, datos )

        val onde = mapOf( "id" to Condicion.Simple( id ) )
        bd.actualizar( TaboaBase.ACTIVIDADES, mapOf( "estado" to 3 ), onde )

        val ondeBuscador = mapOf( "docid" to Condicion.Simple( id ) )
        val consulta = SeleccionSQL( columnas = setOf( "*" ), onde = ondeBuscador )
        val resultado = bd.seleccionar( TaboaLectura.BUSCADOR_ACTIVIDADES, consulta )

        assertTrue( resultado.isEmpty() )

    }*/

    @Test
    fun buscarActividades() {

        val datos = listOf(
            datosCompletosAny(
                mapOf(
                    "id" to 801L,
                    "clave_titulo" to "proba-1",
                    //"descricion" to "Sesión práctica de percusión para principiantes",
                    "id_categoria" to "musica",
                    "estado" to 2
                )
            ),
            datosCompletosAny(
                mapOf(
                    "id" to 802L,
                    "clave_titulo" to "proba-2",
                    //"descricion" to "Ritmos tradicionais con percusión",
                    "id_categoria" to "musica",
                    "estado" to 2
                )
            ),
            datosCompletosAny(
                mapOf(
                    "id" to 803L,
                    "clave_titulo" to "proba-3",
                    //"descricion" to "Técnicas básicas de acuarela",
                    "id_categoria" to "arte",
                    "estado" to 1
                )
            )
        )

        bd.insertar( Taboa.ACTIVIDADES, datos )

        val resultados = collerActividadesBuscables( /*"percusión",*/ "data_modificado" )

        assertEquals( 2, resultados.size )
        assertTrue( resultados.any { it.claveTitulo == "proba-1" } )
        assertTrue( resultados.any { it.claveTitulo == "proba-2" } )
        assertTrue( resultados.none { it.claveTitulo == "proba-3" } )

        bd.actualizar( Taboa.ACTIVIDADES, mapOf( "estado" to 3 ), mapOf( "id" to Condicion.En( setOf( 801L, 802L, 803L ) ) ) )

    }

    @Test
    fun buscarActividadesConFiltro() {

        val datos = listOf(
            datosCompletosAny(
                mapOf(
                    "id" to 804L,
                    "clave_titulo" to "proba-1",
                    //"descricion" to "Ritmo sen instrumentos",
                    "id_categoria" to Categoria.Interior,
                    "estado" to 2
                )
            ),
            datosCompletosAny(
                mapOf(
                    "id" to 805L,
                    "clave_titulo" to "proba-2",
                    //"descricion" to "Percusión aplicada ao movemento",
                    "id_categoria" to Categoria.Outros,
                    "estado" to 1
                )
            )
        )

        bd.insertar( Taboa.ACTIVIDADES, datos )

        val resultados = collerActividadesBuscables( "data_modificado", filtros = mapOf( "id_categoria" to Categoria.Interior ) )

        assertEquals( 1, resultados.size )
        assertEquals( "proba-1", resultados[ 0 ].claveTitulo )

        bd.actualizar( Taboa.ACTIVIDADES, mapOf( "estado" to 3 ), mapOf( "id" to Condicion.En( setOf( 804L, 805L ) ) ) )

    }

    //Esta función permite que se poida cambiar a ID e o Rol a nivel interno
    fun collerUsuarioActualTest( id: Long = 0L, rol: Rol ): Usuario {
        return Usuario( id = id, correo = "test@test.com", rol )
    }

    fun listarActividadesTest( id: Long, rol: Rol ): List<Map<String, Any>> {

        val usuarioActual = collerUsuarioActualTest( id, rol )

        require( PodeCrear( usuarioActual ) ) { "Non se poden listar as actividades se non pode crealas" }

        val onde = if ( !PodeEditarOutras( usuarioActual ) ) {
            mapOf( "id_autoria" to Condicion.Simple( usuarioActual.id ) )
        } else {
            emptyMap()
        }

        val datos = SeleccionSQL( columnas = setOf( "*" ), onde = onde )

        val resultados = bd.seleccionar( Taboa.ACTIVIDADES, datos )
        val saida = mutableListOf<Map<String, Any>>()

        resultados.forEach { actividade -> saida.add( actividade ) }

        return saida

    }

    @Test
    fun listarActividadesEditables() {

        revisarHashElemento(
            "listarActividadesEditables",
            "f85f3317b65e63ede4820e573027f576a80d4c370ab08fddea61aecb21839af8"
        )

        val datos = listOf(
            datosCompletosAny( mapOf( "id" to 1L, "id_autoria" to 30L, "clave_titulo" to "proba-1" ) ),
            datosCompletosAny( mapOf( "id" to 2L, "id_autoria" to 20L, "clave_titulo" to "proba-2" ) ),
            datosCompletosAny( mapOf( "id" to 3L, "id_autoria" to 30L, "clave_titulo" to "proba-3" ) ),
            datosCompletosAny( mapOf( "id" to 4L, "id_autoria" to 30L, "clave_titulo" to "proba-4" ) ),
            datosCompletosAny( mapOf( "id" to 5L, "id_autoria" to 50L, "clave_titulo" to "proba-5" ) ),
        )

        bd.insertar( Taboa.ACTIVIDADES, datos )

        // O ideal é comparar con permisos, non con roles directamente
        // Pero neste caso o que se quere verificar é que cada rol ten os resultados esperados
        for ( rol in Rol.entries ) {

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