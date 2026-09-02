package com.aprengal.lendasnubeiras

import com.aprengal.lendasnubeiras.ui.tema.Tema.Variante
import junit.framework.TestCase.assertEquals
import org.junit.Test

class TestTema {

    fun comprobarNomeVariante( variante: Variante ) {

        val esperado = when ( variante ) {
            Variante.Claro -> "claro"
            Variante.Escuro -> "escuro"
            Variante.Predeterminado -> "predeterminado"
        }

        assertEquals( esperado, variante.clave )

    }

    @Test
    fun nomeVariante() {
        Variante::class.sealedSubclasses.forEach { opcion -> comprobarNomeVariante( opcion.objectInstance!! ) }
    }

    @Test
    fun buscarVariante() {
        assertEquals( Variante.Claro, Variante.buscar( "claro" ) )
        assertEquals( Variante.Escuro, Variante.buscar( "escuro" ) )
        assertEquals( Variante.Predeterminado, Variante.buscar( "predeterminado" ) )
    }

    @Test
    fun buscarVarianteInexistente() {
        assertEquals( Variante.Predeterminado, Variante.buscar( "outra" ) )
    }


}