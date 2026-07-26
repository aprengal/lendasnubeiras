package com.example.lendasnubeiras

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.lendasnubeiras.localizacion.Localizacion
import com.example.lendasnubeiras.tema.TemaNubeiro
import com.example.lendasnubeiras.tema.ScaffoldBase

class Entrada : AppCompatActivity() {

    override fun attachBaseContext( contexto: Context ) {
        super.attachBaseContext( contexto )
        Localizacion.arrancar( contexto )
    }

    override fun onCreate( savedInstanceState: Bundle?) {

        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate( savedInstanceState )
        hideSystemUI()

        setContent {

            TemaNubeiro {
                ScaffoldBase()
            }

        }

    }

    private fun hideSystemUI() {

        val windowInsetsController = WindowCompat.getInsetsController( window, window.decorView )

        // Configura o comportamento para que as barras fiquen agochadas.
        // Só aparecerán de xeito transitorio (flotando por riba) se o usuario desliza dende os bordos,
        // evitando así que o contido da app se mova ou salte.
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // Esconde as barras de estado e de navegación para que a aplicación ocupe
        // absolutamente toda a pantalla.
        windowInsetsController.hide( WindowInsetsCompat.Type.systemBars() )

    }

}

fun reiniciarAplicacion( contexto: Context ) {

    val intento = contexto.packageManager.getLaunchIntentForPackage( contexto.packageName )

    intento?.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK )
    contexto.startActivity( intento )
    Runtime.getRuntime().exit( 0 )

}