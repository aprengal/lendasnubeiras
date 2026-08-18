package com.aprengal.lendasnubeiras

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.aprengal.lendasnubeiras.data.configuracion.db.DB
import com.aprengal.lendasnubeiras.data.configuracion.api.Conexion
import com.aprengal.lendasnubeiras.data.configuracion.Axustes
import com.aprengal.lendasnubeiras.data.localizacion.Idioma
import com.aprengal.lendasnubeiras.data.localizacion.Localizacion
import com.aprengal.lendasnubeiras.ui.pantallas.PantallaBase
import com.aprengal.lendasnubeiras.ui.tema.Tema
import com.aprengal.lendasnubeiras.ui.tema.TemaNubeiro
import com.aprengal.lendasnubeiras.data.usuarios.SesionActual.collerUsuarioActual
import com.aprengal.lendasnubeiras.data.usuarios.SesionActual.idSesion
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class Entrada : AppCompatActivity() {

    override fun attachBaseContext( contexto: Context ) {
        super.attachBaseContext( contexto )
        arrancarConfiguracion( contexto )
    }

    private lateinit var idioma: StateFlow<Idioma>

    private fun arrancarConfiguracion( contexto: Context ) {

        runBlocking {
            Axustes.arrancar( contexto )
            Conexion.arrancar( contexto )
            DB.arrancar( contexto )
            idioma = Localizacion.arrancar( contexto )
            Tema.arrancar()
        }

    }

    override fun onCreate( savedInstanceState: Bundle?) {

        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate( savedInstanceState )

        lifecycleScope.launch {
            collerUsuarioActual()
        }

        setContent {

            TemaNubeiro {

                @Suppress( "DEPRECATION" )
                window.navigationBarColor = MaterialTheme.colorScheme.surfaceContainer.toArgb()

                val idSesion by idSesion.collectAsState()
                val idiomaActual by idioma.collectAsState()

                key( idSesion ) {
                    PantallaBase( idiomaActual )
                }

            }

        }

    }

}