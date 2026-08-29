package com.aprengal.lendasnubeiras

import com.aprengal.lendasnubeiras.ui.navegacion.Ruta
import com.aprengal.lendasnubeiras.ui.navegacion.DatosNavegacion.datosRutas
import junit.framework.TestCase.assertTrue
import org.junit.Test

class TestRutas {

    @Test
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

    }

}