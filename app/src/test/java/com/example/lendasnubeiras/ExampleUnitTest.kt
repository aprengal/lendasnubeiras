package com.aprengal.lendasnubeiras

import com.aprengal.lendasnubeiras.navegacion.Pantalla
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
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