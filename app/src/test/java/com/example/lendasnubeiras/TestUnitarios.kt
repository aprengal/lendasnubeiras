package com.example.lendasnubeiras

import com.aprengal.lendasnubeiras.navegacion.Pantalla
import kotlin.test.DefaultAsserter.assertEquals
import kotlin.test.DefaultAsserter.assertTrue

class TestUnitarios {

    fun navegacionCompleta() {

        val subclasesReales = Pantalla::class.sealedSubclasses
            .mapNotNull { it.objectInstance }
            .toSet()

        val subclasesEnLista = Pantalla.todas.toSet()

        val faltanEnLista = subclasesReales - subclasesEnLista
        val sobranEnLista = subclasesEnLista - subclasesReales

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
            Pantalla.todas.size
        )
    }

}