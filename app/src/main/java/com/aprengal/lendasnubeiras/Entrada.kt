package com.aprengal.lendasnubeiras

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.aprengal.lendasnubeiras.configuracion.api.Conexion
import com.aprengal.lendasnubeiras.configuracion.db.DB
import com.aprengal.lendasnubeiras.localizacion.Localizacion
import com.aprengal.lendasnubeiras.navegacion.IniciarAplicacion
import com.aprengal.lendasnubeiras.tema.Tema
import com.aprengal.lendasnubeiras.tema.TemaNubeiro
import kotlinx.coroutines.runBlocking

class Entrada : AppCompatActivity() {

    override fun attachBaseContext( contexto: Context ) {
        super.attachBaseContext( contexto )
        arrancarConfiguracion( contexto )
    }

    override fun onCreate( savedInstanceState: Bundle?) {

        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate( savedInstanceState )

        setContent {

            TemaNubeiro {
                @Suppress( "DEPRECATION" )
                window.navigationBarColor = MaterialTheme.colorScheme.surfaceContainer.toArgb()
                IniciarAplicacion()
            }

        }

    }

}

fun arrancarConfiguracion( contexto: Context ) {

    runBlocking {
        Axustes.arrancar( contexto )
    }

    Conexion.arrancar( contexto )
    DB.arrancar( contexto )
    Localizacion.arrancar( contexto )
    Tema.arrancar()

}

fun reiniciarAplicacion( contexto: Context ) {

    val intento = contexto.packageManager.getLaunchIntentForPackage( contexto.packageName )

    intento?.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK )
    contexto.startActivity( intento )
    Runtime.getRuntime().exit( 0 )

}