package com.aprengal.lendasnubeiras

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.aprengal.lendasnubeiras.localizacion.Localizacion
import com.aprengal.lendasnubeiras.navegacion.PantallaPrincipal
import com.aprengal.lendasnubeiras.tema.TemaNubeiro

class Entrada : AppCompatActivity() {

    override fun attachBaseContext( contexto: Context ) {
        super.attachBaseContext( contexto )
        Localizacion.arrancar( contexto )
    }

    override fun onCreate( savedInstanceState: Bundle?) {

        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate( savedInstanceState )

        setContent {

            TemaNubeiro {
                PantallaPrincipal()
            }

        }

    }

}

fun reiniciarAplicacion( contexto: Context ) {

    val intento = contexto.packageManager.getLaunchIntentForPackage( contexto.packageName )

    intento?.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK )
    contexto.startActivity( intento )
    Runtime.getRuntime().exit( 0 )

}