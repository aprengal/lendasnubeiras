package com.aprengal.lendasnubeiras

import com.aprengal.lendasnubeiras.data.localizacion.Idioma
import com.aprengal.lendasnubeiras.ui.navegacion.Navegacion
import com.aprengal.lendasnubeiras.ui.navegacion.Pantalla
import org.junit.Assert
import org.junit.Test

class TestUnitarios {

    @Test
    fun todasPantallasUsadas() {

        val subclasesReais = Pantalla::class.sealedSubclasses.toSet()

        val pantallasNavegacion = ( Navegacion.pantallasAdmin + Navegacion.pantallasAutenticacion ).map { it::class }.toSet()
        val faltanEnLista = subclasesReais - pantallasNavegacion
        val sobranEnLista = pantallasNavegacion - subclasesReais

        Assert.assertTrue(
            "Faltan as seguintes pantallas por asignar ao Menú: ${faltanEnLista.map { e -> e.simpleName }}",
            faltanEnLista.isEmpty()
        )

        Assert.assertTrue(
            "Sobran os seguintes elementos: ${sobranEnLista.map { e -> e.simpleName }}",
            sobranEnLista.isEmpty()
        )

        Assert.assertEquals(
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
            Assert.assertEquals("Código incorrecto para $idioma", codigo, idioma.codigoRexion)
        }

    }

}