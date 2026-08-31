package com.aprengal.lendasnubeiras

import com.aprengal.lendasnubeiras.data.axustes.Opcion
import com.aprengal.lendasnubeiras.data.localizacion.Idioma
import com.aprengal.lendasnubeiras.data.usuarios.Rol
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.collections.component1
import kotlin.collections.component2

class TesteoDatos {

    @Test
    fun idiomasCorrectos() {

        val esperados = Idioma.entries.associateWith { idioma ->
            when ( idioma ) {
                Idioma.GALEGO -> "gl_ES"
                Idioma.CASTELAN -> "es_ES"
                Idioma.INGLES -> "en_GB"
                Idioma.NADA -> "_"
            }
        }

        esperados.forEach { ( idioma, codigo ) ->
            assertEquals( "Código incorrecto para $idioma", codigo, idioma.codigoRexion )
        }

    }

    fun comprobarOpcion( opcion: Opcion<*> ) {

        val ( nome, preterminado ) = when ( opcion ) {
            Opcion.SesionUsuario -> "sesion-usuario" to ""
            Opcion.SesionAnonima -> "sesion-anonima" to false
            Opcion.Tema -> "tema" to "predeterminado"
            Opcion.IdDispositivo -> "id-dispositivo" to ""
        }

        assertEquals( nome, opcion.nome )
        assertEquals( preterminado, opcion.predeterminado )

    }

    @Test
    fun opcionBenDefinida() {
        Opcion::class.sealedSubclasses.forEach { opcion -> comprobarOpcion( opcion.objectInstance!! ) }
    }

    fun comprobarNomeRol( rol: Rol ) {

        val esperado = when ( rol ) {
            Rol.NADA -> "nada"
            Rol.MONITOR -> "monitor"
            Rol.COLABORADOR -> "colaborador"
            Rol.AUTOR -> "autor"
            Rol.EDITOR -> "editor"
            Rol.ADMIN -> "admin"
        }

        assertEquals( esperado, rol.nome )

    }

    @Test
    fun nomesRolCorrectos() {
        Rol.entries.forEach { rol -> comprobarNomeRol( rol ) }
    }

    @Test
    fun buscarRoles() {
        Rol.entries.forEach { rol -> assertEquals( rol, Rol.buscarRol( rol.nome ) ) }
        assertEquals( Rol.NADA, Rol.buscarRol( "invalido" ) )
        assertEquals( Rol.NADA, Rol.buscarRol( "" ) )
    }

    //TODO: Faltan por revisar as rutas api (endpoint)

    //Só test. Tamén falta atributo: categoria, destinatario e estado. E dificultades


}