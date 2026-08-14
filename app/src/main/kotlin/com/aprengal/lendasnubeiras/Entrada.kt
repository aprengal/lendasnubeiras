package com.aprengal.lendasnubeiras

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.aprengal.lendasnubeiras.configuracion.api.Conexion
import com.aprengal.lendasnubeiras.configuracion.db.DB
import com.aprengal.lendasnubeiras.usuarios.Usuario
import com.aprengal.lendasnubeiras.usuarios.UsuarioActual.collerUsuarioActual
import com.aprengal.lendasnubeiras.localizacion.Localizacion
import com.aprengal.lendasnubeiras.navegacion.Navegacion.collerPantallas
import com.aprengal.lendasnubeiras.ui.pantallas.Contido
import com.aprengal.lendasnubeiras.ui.tema.Tema
import com.aprengal.lendasnubeiras.ui.tema.TemaNubeiro
import com.aprengal.lendasnubeiras.usuarios.UsuarioActual.validarSesion
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
            validarSesion()
        }

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

    @Composable
    private fun IniciarAplicacion() {

        var usuarioActual: Usuario by remember { mutableStateOf( collerUsuarioActual() ) }

        key( usuarioActual ) {
            val controlador = rememberNavController()
            val ( pantallaInicial, pantallas ) = collerPantallas()
            Contido( controlador, pantallaInicial, pantallas )
        }

    }

}