package com.aprengal.lendasnubeiras

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.key
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.aprengal.lendasnubeiras.configuracion.Axustes
import com.aprengal.lendasnubeiras.configuracion.api.Conexion
import com.aprengal.lendasnubeiras.configuracion.db.DB
import com.aprengal.lendasnubeiras.localizacion.Localizacion
import com.aprengal.lendasnubeiras.ui.pantallas.PantallaBase
import com.aprengal.lendasnubeiras.ui.tema.Tema
import com.aprengal.lendasnubeiras.ui.tema.TemaNubeiro
import com.aprengal.lendasnubeiras.usuarios.SesionActual.collerUsuarioActual
import com.aprengal.lendasnubeiras.usuarios.SesionActual.idSesion
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class Entrada : AppCompatActivity() {

    override fun attachBaseContext( contexto: Context ) {
        super.attachBaseContext( contexto )
        arrancarConfiguracion( contexto )
    }

    private fun arrancarConfiguracion( contexto: Context ) {

        runBlocking {
            Axustes.arrancar( contexto )
            Conexion.arrancar( contexto )
            DB.arrancar( contexto )
            Localizacion.arrancar( contexto )
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

                Log.d( "SESION", "EHHHHHHHHHHHHHHHHHH" )

                key( idSesion ) {
                    PantallaBase()
                }

            }

        }

    }

}