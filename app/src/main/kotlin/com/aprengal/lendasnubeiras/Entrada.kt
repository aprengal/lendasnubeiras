package com.aprengal.lendasnubeiras

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.core.os.LocaleListCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.aprengal.lendasnubeiras.data.configuracion.db.DB
import com.aprengal.lendasnubeiras.data.configuracion.api.Conexion
import com.aprengal.lendasnubeiras.data.configuracion.Axustes
import com.aprengal.lendasnubeiras.data.localizacion.Idioma
import com.aprengal.lendasnubeiras.data.localizacion.Localizacion
import com.aprengal.lendasnubeiras.data.usuarios.SesionActual.collerSesionActual
import com.aprengal.lendasnubeiras.ui.navegacion.CargarNavegacion
import com.aprengal.lendasnubeiras.ui.tema.Tema
import com.aprengal.lendasnubeiras.ui.tema.TemaNubeiro
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class Entrada : AppCompatActivity() {

    override fun attachBaseContext( contexto: Context ) {
        super.attachBaseContext( contexto )
        arrancarConfiguracion( contexto )
    }

    private lateinit var idioma: StateFlow<Idioma>
    private lateinit var sesion: StateFlow<String>

    private fun arrancarConfiguracion( contexto: Context ) {

        runBlocking {
            Axustes.arrancar( contexto )
            Conexion.arrancar( contexto )
            DB.arrancar( contexto )
            idioma = Localizacion.arrancar( contexto )
            Tema.arrancar()
        }

    }

    @SuppressLint( "UnusedContentLambdaTargetStateParameter" )
    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate( savedInstanceState )

        lifecycleScope.launch {
            sesion = collerSesionActual()
        }

        setContent {

            TemaNubeiro {

                @Suppress( "DEPRECATION" )
                window.navigationBarColor = MaterialTheme.colorScheme.surfaceContainer.toArgb()

                val sesion by sesion.collectAsState()
                val idiomaActual by idioma.collectAsState()

                Surface( modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background ) {
                    AnimatedContent( targetState = sesion, transitionSpec = { fadeIn() togetherWith fadeOut() } ) {
                        CargarNavegacion( idiomaActual )
                    }
                }

            }

        }

    }

    override fun onStop() {

        super.onStop()

        val idiomaAplicacion = LocaleListCompat.forLanguageTags( idioma.value.codigoRexion.replace( "_", "-" ) )
        val idiomaOpcions = AppCompatDelegate.getApplicationLocales().get( 0 )

        if ( idiomaAplicacion.get( 0 ) != idiomaOpcions ) {
            AppCompatDelegate.setApplicationLocales( idiomaAplicacion )
        }

    }

}