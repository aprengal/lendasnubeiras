package com.example.lendasnubeiras

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.aprengal.lendasnubeiras.configuracion.Axustes
import com.aprengal.lendasnubeiras.configuracion.api.Conexion
import com.aprengal.lendasnubeiras.configuracion.db.DB
import com.aprengal.lendasnubeiras.localizacion.Localizacion
import com.aprengal.lendasnubeiras.ui.tema.Tema
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertThrows
import org.junit.Test

class AplicacionTest {

    fun arrancarConfiguracionTest( contexto: Context ) {

        runBlocking {
            Axustes.arrancar( contexto )
            Conexion.arrancar( contexto )
            DB.arrancar( contexto )
            Localizacion.arrancar( contexto )
            Tema.arrancar()
        }

    }

    @Test
    fun dobreArranqueFallido() {

        val contexto = InstrumentationRegistry.getInstrumentation().targetContext

        arrancarConfiguracionTest( contexto )

        revisarHashElemento( "arrancarConfiguracion", "d6ca24bd1a9717010dd03f1ffd61722921a746bec8b909e9fc1405584b5f1d4e" )

        assertThrows(IllegalArgumentException::class.java ) {
            runBlocking {
                Axustes.arrancar( contexto )
            }
        }

        assertThrows( IllegalArgumentException::class.java ) {
            Conexion.arrancar( contexto )
        }

        assertThrows( IllegalArgumentException::class.java ) {
            DB.arrancar( contexto )
        }

        assertThrows( IllegalArgumentException::class.java ) {
            Localizacion.arrancar( contexto )
        }

        assertThrows( IllegalArgumentException::class.java ) {
            Tema.arrancar()
        }

    }

}