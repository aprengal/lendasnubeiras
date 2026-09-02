package org.aprengal.lendasnubeiras

import org.aprengal.lendasnubeiras.data.axustes.Opcion
import org.aprengal.lendasnubeiras.data.localizacion.Idioma
import org.aprengal.lendasnubeiras.data.usuarios.Rol
import org.junit.Assert
import org.junit.Test

class TesteoDatos {

    @Test
    fun idiomasCorrectos() {

        val esperados = Idioma.entries.associateWith { idioma ->
            when ( idioma ) {
                Idioma.Galego -> "gl_ES"
                Idioma.Castelan -> "es_ES"
                Idioma.Ingles -> "en_GB"
                Idioma.Nada -> "_"
            }
        }

        esperados.forEach { ( idioma, codigo ) ->
            Assert.assertEquals("Código incorrecto para $idioma", codigo, idioma.codigoRexion)
        }

    }

    fun comprobarOpcion( opcion: Opcion<*> ) {

        val ( nome, preterminado ) = when ( opcion ) {
            Opcion.SesionUsuario -> "sesion-usuario" to ""
            Opcion.SesionAnonima -> "sesion-anonima" to false
            Opcion.Tema -> "tema" to "predeterminado"
            Opcion.IdDispositivo -> "id-dispositivo" to ""
        }

        Assert.assertEquals( nome, opcion.nome )
        Assert.assertEquals( preterminado, opcion.predeterminado )

    }

    @Test
    fun opcionBenDefinida() {
        Opcion::class.sealedSubclasses.forEach { opcion -> comprobarOpcion( opcion.objectInstance!! ) }
    }

    fun comprobarClaveRol( rol: Rol ) {

        val esperado = when ( rol ) {
            Rol.NADA -> "nada"
            Rol.MONITOR -> "monitor"
            Rol.COLABORADOR -> "colaborador"
            Rol.AUTOR -> "autor"
            Rol.EDITOR -> "editor"
            Rol.ADMIN -> "admin"
        }

        Assert.assertEquals( esperado, rol.clave )

    }

    @Test
    fun nomesRolCorrectos() {
        Rol.entries.forEach { rol -> comprobarClaveRol( rol ) }
    }

    @Test
    fun buscarRoles() {
        Rol.entries.forEach { rol -> Assert.assertEquals( rol, Rol.buscarRol( rol.clave ) ) }
        Assert.assertEquals( Rol.NADA, Rol.buscarRol( "invalido" ) )
        Assert.assertEquals( Rol.NADA, Rol.buscarRol( "" ) )
    }

    //TODO: Faltan por revisar as rutas api (endpoint)

    //Só test. Tamén falta atributo: categoria, destinatario e estado. E dificultades


}