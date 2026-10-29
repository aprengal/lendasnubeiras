package org.aprengal.lendasnubeiras.ui.pantallas.autenticacion

import android.util.Patterns.EMAIL_ADDRESS
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nAutenticacion
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nSingular
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nValidacion
import org.aprengal.lendasnubeiras.data.usuarios.AccionsUsuario.rexistrarse
import org.aprengal.lendasnubeiras.data.utilidades.Validacion.validarCorreo
import org.aprengal.lendasnubeiras.ui.navegacion.Locais.LocalIdioma

import org.aprengal.lendasnubeiras.ui.navegacion.Locais.LocalNavegacion
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.DatosCampoTexto
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.BotonPrincipal
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.CampoTexto
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.Texto
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.TextoEnlazado
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.EspazadorAlto

@Composable
fun PantallaRexistro() {

    val navegacion = LocalNavegacion.current
    val idioma = LocalIdioma.current
    val correo = rememberTextFieldState()

    var mensaxe by rememberSaveable { mutableStateOf<L10nSingular?>( null ) }
    var activado by rememberSaveable { mutableStateOf( true ) }

    val campoCorreo = L10nAutenticacion.CorreoElectronico
    val botonRexistro = L10nAutenticacion.BotonRexistro // O texto do botón para rexistrarse
    val correoValido = EMAIL_ADDRESS.matcher( correo.text ).matches()
    val tecladoCorreo = KeyboardOptions( keyboardType = KeyboardType.Email )

    val botonAccion = {
        rexistrarse(
            correo.text.toString(),
            idioma,
            { novaMensaxe, novoEstadoActivado ->
                mensaxe = novaMensaxe
                activado = novoEstadoActivado
            },
            { navegacion.reiniciar() }
        )
    }

    val erro: L10nValidacion? by remember { derivedStateOf { validarCorreo( correo.text.toString() ) } }

    val datos = DatosCampoTexto( correo, campoCorreo, 255, Modifier.fillMaxWidth(), activado, teclado = tecladoCorreo, erro = erro )

    val atras = L10nAutenticacion.VolverAcceso
    val enlaceAtras = mapOf( L10nAutenticacion.VolverAcceso to { navegacion.quitarUltimo() } )

    Column {

        mensaxe?.let { contido ->
            Texto( contido )
            EspazadorAlto()
        }

        CampoTexto( datos )
        EspazadorAlto()
        BotonPrincipal( botonRexistro, botonAccion, Modifier.fillMaxWidth(), correoValido && activado )
        EspazadorAlto()
        TextoEnlazado( atras, enlaceAtras )

    }

}