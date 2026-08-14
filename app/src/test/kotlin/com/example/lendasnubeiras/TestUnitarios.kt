package com.example.lendasnubeiras

import com.aprengal.lendasnubeiras.localizacion.Idioma
import com.aprengal.lendasnubeiras.navegacion.Navegacion
import com.aprengal.lendasnubeiras.ui.pantallas.Pantalla

import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals

class TestUnitarios {

    @Test
    fun todasPantallasUsadas() {

        val subclasesReais = Pantalla::class.sealedSubclasses.toSet()

        val pantallasNavegacion = ( Navegacion.pantallasAdmin + Navegacion.pantallasAutenticacion ).map { it::class }.toSet()
        val faltanEnLista = subclasesReais - pantallasNavegacion
        val sobranEnLista = pantallasNavegacion - subclasesReais

        assertTrue(
            "Faltan as seguintes pantallas por asignar ao Menú: ${ faltanEnLista.map { e -> e.simpleName } }",
            faltanEnLista.isEmpty()
        )

        assertTrue(
            "Sobran os seguintes elementos: ${sobranEnLista.map { e -> e.simpleName }}",
            sobranEnLista.isEmpty()
        )

        assertEquals(
            "O tamano non coincide",
            subclasesReais.size,
            Navegacion.pantallasAdmin.size + Navegacion.pantallasAutenticacion.size
        )

    }

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
            assertEquals( "Código incorrecto para $idioma", codigo, idioma.codigoRexion, )
        }

    }

}