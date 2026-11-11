package org.aprengal.lendasnubeiras.ui.pantallas.autenticacion

import android.util.Patterns.EMAIL_ADDRESS
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nAutenticacion
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nSingular
import org.aprengal.lendasnubeiras.data.usuarios.AccionsUsuario.iniciarSesion
import org.aprengal.lendasnubeiras.ui.navegacion.Locais.LocalIdioma
import org.aprengal.lendasnubeiras.ui.navegacion.Locais.LocalNavegacion
import org.aprengal.lendasnubeiras.ui.navegacion.Ruta
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.DatosCampoTexto
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.BotonPrincipal
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.CampoTexto
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.Texto
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.TextoEnlazado
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.EspazadorAlto

@Composable
fun PantallaAcceso() {

    val navegacion = LocalNavegacion.current
    val teclado = LocalSoftwareKeyboardController.current
    val idioma = LocalIdioma.current
    val correo = rememberTextFieldState()

    var mensaxe by rememberSaveable { mutableStateOf<L10nSingular?>( null ) }
    var activado by rememberSaveable { mutableStateOf( true ) }

    //Para impedir que se poida navegar cara atrás mentres se está procesando a petición
    BackHandler( enabled = !activado ) {}

    val campoCorreo = L10nAutenticacion.CorreoElectronico
    val botonAcceso = L10nAutenticacion.BotonAcceso
    val correoValido = EMAIL_ADDRESS.matcher( correo.text ).matches()
    val tecladoCorreo = KeyboardOptions( keyboardType = KeyboardType.Email )

    val botonAccion = {
        teclado?.hide()
        iniciarSesion( correo.text.toString(), idioma ) {
            novaMensaxe, novoEstadoActivado ->
            mensaxe = novaMensaxe
            activado = novoEstadoActivado
        }
    }

    //val erro: L10nValidacion? by remember { derivedStateOf { validarCorreo( correo.text.toString() ) } }
    val datos = DatosCampoTexto( correo, campoCorreo, 255, Modifier.fillMaxWidth(), activado, teclado = tecladoCorreo/*, erro = erro*/ )

    val textoRexistro = L10nAutenticacion.CrearConta
    val enlace = mapOf( L10nAutenticacion.EnlaceCrearConta to { if ( activado ) navegacion.engadir( Ruta.Rexistro ) } )

    Column {

        mensaxe?.let { contido ->
            Texto( contido )
            EspazadorAlto()
        }

        CampoTexto( datos )

        EspazadorAlto()
        BotonPrincipal( botonAcceso, botonAccion, Modifier.fillMaxWidth(), correoValido && activado )
        EspazadorAlto()
        TextoEnlazado( textoRexistro, enlace )

    }

}