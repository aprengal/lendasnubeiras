package com.example.lendasnubeiras

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.aprengal.lendasnubeiras.Axustes
import com.aprengal.lendasnubeiras.arrancarConfiguracion
import com.aprengal.lendasnubeiras.configuracion.api.Conexion
import com.aprengal.lendasnubeiras.configuracion.db.DB
import com.aprengal.lendasnubeiras.localizacion.Localizacion
import com.aprengal.lendasnubeiras.tema.Tema
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertThrows
import org.junit.Test

class AplicacionTest {

    @Test
    fun dobreArranqueFallido() {

        val contexto = ApplicationProvider.getApplicationContext<Context>()

        arrancarConfiguracion( contexto )

        assertThrows( Exception::class.java ) {
            arrancarConfiguracion( contexto )
        }

        //Relanzamento específico de cada parte compoñente manual
        //Habería que ilos engadindo manualmente un a un para verificar funciona cada clase de maneira específica
        assertThrows( Exception::class.java ) {
            runBlocking {
                Axustes.arrancar( contexto )
            }
        }

        assertThrows( Exception::class.java ) {
            Conexion.arrancar( contexto )
        }

        assertThrows( Exception::class.java ) {
            DB.arrancar( contexto )
        }

        assertThrows( Exception::class.java ) {
            Localizacion.arrancar( contexto )
        }

        assertThrows( Exception::class.java ) {
            Tema.arrancar()
        }

    }

}