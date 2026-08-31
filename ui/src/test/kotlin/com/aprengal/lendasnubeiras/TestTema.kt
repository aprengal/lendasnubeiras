package com.aprengal.lendasnubeiras

import com.aprengal.lendasnubeiras.ui.tema.Tema.Variante
import junit.framework.TestCase.assertEquals
import org.junit.Test

class TestTema {

    fun comprobarNomeVariante( variante: Variante ) {

        val esperado = when ( variante ) {
            Variante.CLARO -> "claro"
            Variante.ESCURO -> "escuro"
            Variante.PREDETERMINADO -> "predeterminado"
        }

        assertEquals( esperado, variante.nome )

    }

    @Test
    fun nomeVariante() {
        Variante::class.sealedSubclasses.forEach { opcion -> comprobarNomeVariante( opcion.objectInstance!! ) }
    }

    @Test
    fun buscarVariante() {
        assertEquals( Variante.CLARO, Variante.buscar( "claro" ) )
        assertEquals( Variante.ESCURO, Variante.buscar( "escuro" ) )
        assertEquals( Variante.PREDETERMINADO, Variante.buscar( "predeterminado" ) )
    }

    @Test
    fun buscarVarianteInexistente() {
        assertEquals( Variante.PREDETERMINADO, Variante.buscar( "outra" ) )
    }


}