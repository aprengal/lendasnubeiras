package com.example.lendasnubeiras

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.lendasnubeiras.localizacion.Localizacion
import com.example.lendasnubeiras.mapa.MapaMundial
import com.example.lendasnubeiras.tema.TemaNubeiro
import com.example.lendasnubeiras.tema.ScaffoldBase

class Entrada : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        hideSystemUI()

        Localizacion.arrancar( applicationContext )

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

@Composable
fun DebuxarInferface() {

    //MapaMundial()

    Column( Modifier.fillMaxSize().padding( top = 0.dp )/*.verticalScroll( rememberScrollState() )*/ ) {

        MapaMundial()

        //Text( text = "peido", modifier = Modifier.padding( 16.dp ), style = MaterialTheme.typography.bodyLarge )

        //MostrarTodosLosEstilos()

    }

}