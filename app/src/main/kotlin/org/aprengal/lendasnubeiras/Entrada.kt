package org.aprengal.lendasnubeiras

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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.core.os.LocaleListCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle as escoitarEstado
import org.aprengal.lendasnubeiras.data.bd.clases.BD
import org.aprengal.lendasnubeiras.data.api.ConexionApi
import org.aprengal.lendasnubeiras.data.axustes.Axustes
import org.aprengal.lendasnubeiras.data.localizacion.clases.Idioma
import org.aprengal.lendasnubeiras.data.localizacion.clases.Localizacion
import org.aprengal.lendasnubeiras.data.usuarios.SesionActual
import org.aprengal.lendasnubeiras.data.usuarios.SesionActual.collerSesionActual
import org.aprengal.lendasnubeiras.ui.tema.Tema.TemaNubeiro
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.rememberNavBackStack
import org.aprengal.lendasnubeiras.ui.navegacion.Enlaces
import org.aprengal.lendasnubeiras.ui.navegacion.Navegacion
import org.aprengal.lendasnubeiras.ui.navegacion.Navegacion.Companion.RexistrarNavegacion
import org.aprengal.lendasnubeiras.ui.navegacion.Navegacion.Companion.gardarNavegacion
import org.aprengal.lendasnubeiras.ui.navegacion.Navegacion.Companion.rutaInicial
import org.aprengal.lendasnubeiras.ui.navegacion.Ruta
import org.aprengal.lendasnubeiras.ui.tema.Tema

/**
 * Actividade única da aplicación e punto de entrada do módulo `app`.
 *
 * Arranca a configuración da aplicación, constrúe a interface con Compose e
 * xestiona as ligazóns directas. A navegación entre pantallas non se fai con
 * máis actividades, senón mediante [Navegacion].
 */
class Entrada : AppCompatActivity() {

    /**
     * Prepara o contexto base da actividade e arranca a configuración.
     *
     * Execútase antes de [onCreate], polo que a configuración queda lista
     * antes de crear a interface.
     *
     * @param contexto Contexto base da actividade.
     */
    override fun attachBaseContext( contexto: Context ) {
        super.attachBaseContext( contexto )
        arrancarConfiguracion( contexto )
    }

    /** Idioma actual da aplicación, en forma de fluxo de estado observable. */
    private lateinit var idioma: StateFlow<Idioma>

    /** Xestor de navegación da pantalla actual. Crease ao construír a interface. */
    private lateinit var navegacion: Navegacion

    /**
     * Inicializa os servizos que a aplicación necesita antes de mostrar nada.
     *
     * Arranca, por orde: axustes, conexión coa API, base de datos,
     * localización (obtendo o fluxo do idioma), tema e sesión actual.
     * Execútase con [runBlocking], así que bloquea o fío ata que remata.
     *
     * @param contexto Contexto co que se inicializan os servizos que o precisan.
     */
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

    /**
     * Crea a actividade e constrúe a interface.
     *
     * Se a actividade se abre por primeira vez (non se está restaurando), le a
     * ligazón directa co que se abriu, se a hai. A interface:
     * 1. Aplica o tema ([TemaNubeiro]) e a cor da barra de navegación do sistema.
     * 2. Observa a sesión e o idioma actuais.
     * 3. Cando cambia a sesión, reconstrúe a navegación cun fundido gradual.
     * 4. Elixe a pantalla inicial: a da ligazón directa, se a hai, ou
     *    [rutaInicial] en caso contrario. A ligazón úsase só unha vez.
     * 5. Rexistra a navegación ([RexistrarNavegacion]).
     *
     * @param savedInstanceState Estado gardado, ou `null` se é a primeira vez.
     */
    @SuppressLint( "UnusedContentLambdaTargetStateParameter" )
    override fun onCreate( savedInstanceState: Bundle? ) {

        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate( savedInstanceState )

        var rutaEnlace: Ruta? = if ( savedInstanceState == null ) Enlaces( intent ).confirmar() else null

        setContent {

            TemaNubeiro {

                @Suppress( "DEPRECATION" )
                window.navigationBarColor = MaterialTheme.colorScheme.surfaceContainer.toArgb()

                val sesion by collerSesionActual().escoitarEstado()
                val idiomaActual by idioma.escoitarEstado()

                Surface( modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background ) {

                    AnimatedContent( targetState = sesion, transitionSpec = { fadeIn() togetherWith fadeOut() } ) {

                        val ruta = remember { rutaEnlace.also { rutaEnlace = null } ?: rutaInicial() }

                        @Suppress( "UNCHECKED_CAST" )
                        val traza = rememberNavBackStack( ruta ) as NavBackStack<Ruta>
                        navegacion = rememberSaveable( saver = gardarNavegacion( traza ) ) { Navegacion( traza ) }

                        RexistrarNavegacion( idiomaActual, navegacion )

                    }

                }

            }

        }

    }

    /**
     * Xestiona unha ligazón directa recibida mentres a aplicación xa está aberta.
     *
     * Se a ligazón é válida, engade a súa pantalla á pila de navegación.
     *
     * @param intent Intent coa ligazón recibida.
     */
    override fun onNewIntent( intent: Intent ) {
        super.onNewIntent( intent )
        Enlaces( intent ).confirmar()?.let { enlace -> navegacion.engadir( enlace ) }
    }

    /**
     * Sincroniza o idioma da aplicación co do sistema cando a actividade deixa de ser visible.
     *
     * Se o idioma actual da aplicación é distinto do rexistrado en [AppCompatDelegate], actualízao.
     */
    override fun onStop() {

        super.onStop()

        val idiomaAplicacion = LocaleListCompat.forLanguageTags( idioma.value.codigoRexion.replace( "_", "-" ) )
        val idiomaOpcions = AppCompatDelegate.getApplicationLocales().get( 0 )

        if ( idiomaAplicacion.get( 0 ) != idiomaOpcions ) {
            AppCompatDelegate.setApplicationLocales( idiomaAplicacion )
        }

    }

}