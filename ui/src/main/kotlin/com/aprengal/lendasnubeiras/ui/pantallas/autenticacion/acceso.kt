package com.aprengal.lendasnubeiras.ui.pantallas.autenticacion

import android.util.Patterns.EMAIL_ADDRESS
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.aprengal.lendasnubeiras.data.localizacion.claves.singular.L10nAutenticacion
import com.aprengal.lendasnubeiras.data.localizacion.claves.singular.L10nSingular
import com.aprengal.lendasnubeiras.data.localizacion.claves.singular.L10nValidacion
import com.aprengal.lendasnubeiras.ui.navegacion.Locais.LocalNavegacion
import com.aprengal.lendasnubeiras.ui.navegacion.Ruta
import com.aprengal.lendasnubeiras.ui.reutilizables.clases.DatosCampoTexto
import com.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.BotonPrincipal
import com.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.CampoTexto
import com.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.Texto
import com.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.TextoEnlazado
import com.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.EspazadorAlto

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

    val erro: L10nValidacion? by rememberSaveable {

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