package com.aprengal.lendasnubeiras

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.aprengal.lendasnubeiras.localizacion.Localizacion
import com.aprengal.lendasnubeiras.navegacion.NavegacionPrincipal
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
                //A seguinte liña está marcada como obsoleta, pero é necesaria de Android 14 para atrás.
                // Tamén é a única alternativa en Android 15 Xiaomi?
                @Suppress( "DEPRECATION" )
                window.navigationBarColor = MaterialTheme.colorScheme.surfaceContainer.toArgb()
                NavegacionPrincipal()
            }

        }

    }

}

fun arrancarConfiguracion( contexto: Context ) {

    Axustes.arrancar( contexto )

    runBlocking {
        Localizacion.arrancar( contexto )
        Tema.arrancar()
    }

}

fun reiniciarAplicacion( contexto: Context ) {

    val intento = contexto.packageManager.getLaunchIntentForPackage( contexto.packageName )

    intento?.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK )
    contexto.startActivity( intento )
    Runtime.getRuntime().exit( 0 )

}