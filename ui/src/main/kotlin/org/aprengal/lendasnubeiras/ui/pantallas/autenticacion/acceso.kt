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
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nValidacion
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
    val correo = rememberTextFieldState()

    var mensaxe by rememberSaveable { mutableStateOf<L10nAutenticacion?>( null ) }
    var activado = rememberSaveable { true }

    val campoCorreo = L10nAutenticacion.CorreoElectronico
    val botonAcceso = L10nAutenticacion.BotonAcceso
    val correoValido = EMAIL_ADDRESS.matcher( correo.text ).matches()
    val tecladoCorreo = KeyboardOptions( keyboardType = KeyboardType.Email )

    val botonAccion = {
        mensaxe = L10nAutenticacion.ComprobandoCorreo
        activado = false
    }

    val erro: L10nValidacion? by remember {

        derivedStateOf {

            val texto = correo.text.toString()

            when {
                texto.isEmpty() -> null
                !texto.contains( "@" ) -> L10nValidacion.ErroFaltaArroba
                !correoValido -> L10nValidacion.ErroFormatoCorreo
                else -> null
            }
        }

    }

    val datos = DatosCampoTexto( correo, campoCorreo, 255, Modifier.fillMaxWidth(), activado, teclado = tecladoCorreo, erro = erro )

    val textoRexistro = L10nAutenticacion.CrearConta
    val enlace = mapOf( L10nAutenticacion.EnlaceCrearConta to { navegacion.engadir( Ruta.Rexistro ) } )

    Column {

        mensaxe?.let { contido ->
            Texto( contido )
            EspazadorAlto()
        }

        CampoTexto( datos )

        EspazadorAlto()
        BotonPrincipal( botonAcceso, botonAccion, Modifier.fillMaxWidth(), correoValido )
        EspazadorAlto()
        TextoEnlazado( textoRexistro, enlace )

    }

}