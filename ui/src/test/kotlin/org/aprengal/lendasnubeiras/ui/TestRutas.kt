package org.aprengal.lendasnubeiras.ui

import org.junit.Test
class TestRutas {

    @Test
    fun okRutas() {

        //fail()

    }

    /*@Test
    fun todasRutasUsadas() {

        val declaradas = Ruta::class.sealedSubclasses.toSet()
        val configuradas = datosRutas.keys
        val faltan = declaradas - configuradas

        val mensaxe = if ( faltan.size == 1 ) {
            "Falta a seguinta ruta por configurar: ${ faltan.first().simpleName }"
        } else {
            "Faltan as seguintes rutas por configurar: ${ faltan.map { it.simpleName } }"
        }

        assertTrue( mensaxe, faltan.isEmpty() )

    }*/

}