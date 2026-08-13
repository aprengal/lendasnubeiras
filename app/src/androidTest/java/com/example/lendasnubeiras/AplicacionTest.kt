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

        revisarHashElemento( "arrancarConfiguracion", "1ff641ef77b597c865cf9ca2411ed960ac075b6a09b48f5c12605d22804697eb" )

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