package com.example.lendasnubeiras

import com.aprengal.lendasnubeiras.localizacion.Idioma
import com.aprengal.lendasnubeiras.pantallas.Pantalla
import org.junit.Test
import kotlin.test.DefaultAsserter.assertEquals
import kotlin.test.DefaultAsserter.assertTrue

class TestUnitarios {

    @Test
    fun todasPantallasUsadas() {

        val subclasesReales = Pantalla::class.sealedSubclasses
            .mapNotNull { it.objectInstance }
            .toSet()

        val pantallasRegistradas = (Pantalla.todas + Pantalla.autenticacion).map { it::class }.toSet()
        val subclasesRealesClasses = subclasesReales.map { it::class }.toSet()
        val faltanEnLista = subclasesRealesClasses - pantallasRegistradas
        val sobranEnLista = pantallasRegistradas - subclasesRealesClasses

        assertTrue(
            "Faltan en Pantalla.todas: ${faltanEnLista.map { it::class.simpleName }}",
            faltanEnLista.isEmpty()
        )

        assertTrue(
            "Sobran en Pantalla.todas (no son subclases de Pantalla): ${sobranEnLista.map { it::class.simpleName }}",
            sobranEnLista.isEmpty()
        )

        assertEquals(
            "El tamaño no coincide, revisa duplicados en Pantalla.todas",
            subclasesReales.size,
            Pantalla.todas.size + Pantalla.autenticacion.size
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
            assertEquals( "Código incorrecto para $idioma",codigo, idioma.codigoRexion )
        }

    }

}