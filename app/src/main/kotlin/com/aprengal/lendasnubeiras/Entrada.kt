package com.aprengal.lendasnubeiras

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.core.os.LocaleListCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle as escoitarEstado
import com.aprengal.lendasnubeiras.data.bd.clases.BD
import com.aprengal.lendasnubeiras.data.api.ConexionApi
import com.aprengal.lendasnubeiras.data.axustes.Axustes
import com.aprengal.lendasnubeiras.data.localizacion.Idioma
import com.aprengal.lendasnubeiras.data.localizacion.Localizacion
import com.aprengal.lendasnubeiras.data.usuarios.SesionActual
import com.aprengal.lendasnubeiras.data.usuarios.SesionActual.collerSesionActual
import com.aprengal.lendasnubeiras.ui.tema.Tema
import com.aprengal.lendasnubeiras.ui.tema.TemaNubeiro
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.rememberNavBackStack
import com.aprengal.lendasnubeiras.ui.navegacion.Enlaces
import com.aprengal.lendasnubeiras.ui.navegacion.Navegacion
import com.aprengal.lendasnubeiras.ui.navegacion.Navegacion.Companion.RexistrarNavegacion
import com.aprengal.lendasnubeiras.ui.navegacion.Navegacion.Companion.gardarNavegacion
import com.aprengal.lendasnubeiras.ui.navegacion.Navegacion.Companion.rutaInicial
import com.aprengal.lendasnubeiras.ui.navegacion.Ruta

class Entrada : AppCompatActivity() {

    override fun attachBaseContext( contexto: Context ) {
        super.attachBaseContext( contexto )
        arrancarConfiguracion( contexto )
    }

    private lateinit var idioma: StateFlow<Idioma>
    private lateinit var navegacion: Navegacion

    private fun arrancarConfiguracion( contexto: Context ) {

        runBlocking {
            Axustes.arrancar( contexto )
            ConexionApi.arrancar( contexto )
            BD.arrancar( contexto )
            idioma = Localizacion.arrancar( contexto )
            Tema.arrancar()
            SesionActual.arrancar()
        }

    }

    @SuppressLint( "UnusedContentLambdaTargetStateParameter" )
    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate( savedInstanceState )

        setContent {

            TemaNubeiro {

                @Suppress( "DEPRECATION" )
                window.navigationBarColor = MaterialTheme.colorScheme.surfaceContainer.toArgb()

                val sesion by collerSesionActual().escoitarEstado()
                val idiomaActual by idioma.escoitarEstado()

                Surface( modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background ) {

                    AnimatedContent( targetState = sesion, transitionSpec = { fadeIn() togetherWith fadeOut() } ) {

                        @Suppress( "UNCHECKED_CAST" )
                        val traza = rememberNavBackStack( rutaInicial() ) as NavBackStack<Ruta>
                        navegacion = rememberSaveable( saver = gardarNavegacion( traza ) ) { Navegacion( traza ) }

                        RexistrarNavegacion( idiomaActual, navegacion )

                    }

                }

            }

        }

    }

    override fun onNewIntent( intent: Intent ) {
        super.onNewIntent( intent )
        Enlaces( intent ).confirmar()?.let { enlace -> navegacion.engadir( enlace ) }
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