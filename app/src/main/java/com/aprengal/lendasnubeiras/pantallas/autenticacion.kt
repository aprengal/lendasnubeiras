package com.aprengal.lendasnubeiras.pantallas

import android.util.Patterns.EMAIL_ADDRESS
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.aprengal.lendasnubeiras.configuracion.api.Conexion
import com.aprengal.lendasnubeiras.localizacion.Localizacion.l10n
import com.aprengal.lendasnubeiras.tema.Espazador
import com.aprengal.lendasnubeiras.tema.Logo


@Composable
private fun PantallaAcceso( tituloPantalla: String, tituloBoton: String, amosarCheckbox: Boolean, botonPulsado: ( correo: String ) -> String ) {

    var correo by rememberSaveable { mutableStateOf( "" ) }
    var aceptaTerminos by rememberSaveable { mutableStateOf( false ) }
    var texto by rememberSaveable { mutableStateOf( "" ) }

    val correoValido = EMAIL_ADDRESS.matcher( correo ).matches()
    val podeContinuar = correoValido && ( !amosarCheckbox || aceptaTerminos )

    Column(
        modifier = Modifier.fillMaxSize()
            .windowInsetsPadding( WindowInsets.safeDrawing )
            .verticalScroll( rememberScrollState() )
            .padding( horizontal = 5.dp, vertical = 10.dp ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Logo( 90.dp )

        Espazador( 2 )

        Text( l10n( tituloPantalla, "test" ) , style = MaterialTheme.typography.headlineLarge )
        Espazador()

        if ( texto != "" ) {
            Text( texto )
            Espazador()
        }

        OutlinedTextField(
            value = correo,
            onValueChange = { novoCoreo -> correo = novoCoreo },
            label = { Text( "Correo electrónico" ) },
            singleLine = true,
            keyboardOptions = KeyboardOptions( keyboardType = KeyboardType.Email ),
            modifier = Modifier.fillMaxWidth()
        )

        Espazador()

        if ( amosarCheckbox ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = aceptaTerminos,
                    onCheckedChange = { estado -> aceptaTerminos = estado }
                )
                Text( "Acepto os termos e condicións" )
            }

            Espazador()

        }

        Button(
            onClick = { texto = botonPulsado( correo ) },
            enabled = podeContinuar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text( l10n( tituloBoton, "test" ) )
        }

    }

}

@Composable
fun PantallaRexistro() {

    PantallaAcceso(
        tituloPantalla = "titulo_rexistro",
        tituloBoton = "boton_rexistro",
        amosarCheckbox = true,
        botonPulsado = { correo ->
            when {
                correo.contains( "erd248@", ignoreCase = true ) -> "O furacán C7w6so acaba de pasar por aquí"
                correo.contains( "n" ) -> "N de ninguén"
                else -> "As malas linguas din que usar o teu correo trae un bo regalo"
            }
        }
    )

}

@Composable
fun PantallaIniciarSesion() {

    PantallaAcceso(
        tituloPantalla = "titulo_iniciar_sesion",
        tituloBoton = "boton_iniciar_sesion",
        amosarCheckbox = false,
        botonPulsado = { correo ->
            "Comprobando correo..." // aquí irá a lóxica real de login
        }
    )

}

@Composable
fun PantallaApertura() {

    var apertura: String by rememberSaveable { mutableStateOf( "Esperando resposta..." ) }

    LaunchedEffect( Unit ) {

        val resultado = Conexion.get( "peido.php", emptyMap() )

        //Isto vale para indicar que a operación foi exitosa
        //if ( resultado.optBoolean( "exito" ) )

        apertura = resultado.optString( "mensaxe", "Escachou o servidor" ).toString()

    }

    PantallaAcceso(
        tituloPantalla = "titulo_benvida",
        tituloBoton = apertura,
        amosarCheckbox = false,
        botonPulsado = { correo ->

            if ( apertura.startsWith( "Esperando" ) ) "Meh" else "Chi"

        }

    )

}