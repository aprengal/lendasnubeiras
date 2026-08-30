package com.aprengal.lendasnubeiras.ui.pantallas

import android.util.Patterns.EMAIL_ADDRESS
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.aprengal.lendasnubeiras.data.localizacion.L10nSingular
import com.aprengal.lendasnubeiras.data.usuarios.SesionActual.crearSesionAnonima
import com.aprengal.lendasnubeiras.ui.navegacion.Navegacion
import com.aprengal.lendasnubeiras.ui.navegacion.Ruta
import com.aprengal.lendasnubeiras.ui.reutilizables.BotonPrincipal
import com.aprengal.lendasnubeiras.ui.reutilizables.BotonSecundario
import com.aprengal.lendasnubeiras.ui.reutilizables.EspazadorAlto
import com.aprengal.lendasnubeiras.ui.reutilizables.Texto
import com.aprengal.lendasnubeiras.ui.reutilizables.TextoEnlazado

@Composable
fun PantallaBenvida( navegacion: Navegacion ) {

    val textoAcceso = L10nSingular.BOTON_ACCESO
    val accionAcceso = { navegacion.engadir( Ruta.Acceso ) }
    val textoAnonimo = L10nSingular.BOTON_ANONIMO
    val accionAnonimo = { crearSesionAnonima() }

    Column {
        BotonPrincipal( textoAcceso, accionAcceso, Modifier.fillMaxWidth() )
        EspazadorAlto()
        BotonSecundario( textoAnonimo, accionAnonimo, Modifier.fillMaxWidth() )
    }

}

@Composable
fun PantallaAcceso( navegacion: Navegacion ) {

    val correo = rememberTextFieldState()

    var mensaxe by rememberSaveable { mutableStateOf<L10nSingular?>( null ) }
    var activado = rememberSaveable { true }

    val campoCorreo = L10nSingular.CORREO_ELECTRONICO
    val botonAcceso = L10nSingular.BOTON_ACCESO
    val correoValido = EMAIL_ADDRESS.matcher( correo.text ).matches()

    val botonAccion = {
        mensaxe = L10nSingular.COMPROBANDO_CORREO
        activado = false
    }

    val textoRexistro = L10nSingular.CREAR_CONTA
    val enlace = mapOf( L10nSingular.ENLACE_CREAR_CONTA to { navegacion.engadir( Ruta.Rexistro ) } )

    Column {

        mensaxe?.let { contido ->
            Texto( contido )
            EspazadorAlto()
        }

        OutlinedTextField(
            state = correo,
            enabled = activado,
            label = { Texto( campoCorreo ) },
            lineLimits = TextFieldLineLimits.SingleLine,
            keyboardOptions = KeyboardOptions( keyboardType = KeyboardType.Email ),
            modifier = Modifier.fillMaxWidth()
        )

        EspazadorAlto()
        BotonPrincipal( botonAcceso, botonAccion, Modifier.fillMaxWidth(), correoValido )
        EspazadorAlto()
        TextoEnlazado( textoRexistro, enlace )

    }

}

@Composable
fun PantallaRexistro( navegacion: Navegacion ) {

    Text( "Cando é a recuperación?" )

    val atras = L10nSingular.VOLVER_ACCESO
    val enlace = mapOf( L10nSingular.VOLVER_ACCESO to { navegacion.quitarUltimo() } )

    TextoEnlazado( atras, enlace )

}