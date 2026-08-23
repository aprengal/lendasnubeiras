package com.aprengal.lendasnubeiras.ui.pantallas

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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aprengal.lendasnubeiras.data.localizacion.L10nSingular
import com.aprengal.lendasnubeiras.data.usuarios.SesionActual.crearSesionAnonima
import com.aprengal.lendasnubeiras.ui.navegacion.Pantalla
import com.aprengal.lendasnubeiras.ui.reutilizables.EspazadorAlto
import com.aprengal.lendasnubeiras.ui.reutilizables.Logo

@Composable
private fun PantallaAcceso( controlador: NavHostController, tituloBoton: L10nSingular, amosarCheckbox: Boolean, botonPulsado: (correo: String ) -> String ) {

    var correo by rememberSaveable { mutableStateOf( "" ) }
    var aceptaTerminos by rememberSaveable { mutableStateOf( false ) }
    var texto by rememberSaveable { mutableStateOf( "" ) }

    val correoValido = EMAIL_ADDRESS.matcher( correo ).matches()
    val podeContinuar = correoValido && ( !amosarCheckbox || aceptaTerminos )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 5.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Logo( 90.dp )

        EspazadorAlto( 2 )

        AmosarTitulo()
        EspazadorAlto()

        if ( texto != "" ) {
            Text( texto )
            EspazadorAlto()
        }

        OutlinedTextField(
            value = correo,
            onValueChange = { novoCoreo -> correo = novoCoreo },
            label = { Text( "Correo electrónico" ) },
            singleLine = true,
            keyboardOptions = KeyboardOptions( keyboardType = KeyboardType.Email ),
            modifier = Modifier.fillMaxWidth()
        )

        EspazadorAlto()

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

            EspazadorAlto()

        }

        Button(
            onClick = { texto = botonPulsado( correo ) },
            enabled = podeContinuar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text( tituloBoton.texto() )
        }

    }

}

@Composable
fun PantallaRexistro( controlador: NavHostController ) {

    PantallaAcceso(
        controlador = controlador,
        tituloBoton = L10nSingular.BOTON_REXISTRO,
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
fun PantallaAcceso( controlador: NavHostController ) {

    PantallaAcceso(
        controlador = controlador,
        tituloBoton = L10nSingular.BOTON_ACCESO,
        amosarCheckbox = false,
        botonPulsado = { correo ->
            "Comprobando correo..." // aquí irá a lóxica real de login
        }
    )

}

@Composable
fun PantallaBenvida( controlador: NavHostController ) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding( WindowInsets.safeDrawing )
            .padding( horizontal = 24.dp, vertical = 16.dp ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Logo( 90.dp )
        EspazadorAlto( 2 )

        AmosarTitulo()
        EspazadorAlto()

        Button( onClick = { controlador.navigate( Pantalla.IniciarSesion ) }, modifier = Modifier.fillMaxWidth() ) {
            Text( L10nSingular.BOTON_ACCESO.texto() )
        }

        EspazadorAlto()

        OutlinedButton( onClick = { crearSesionAnonima() }, modifier = Modifier.fillMaxWidth() ) {
            Text( L10nSingular.BOTON_ANONIMO.texto() )
        }

    }

}

/*@Composable
private fun PantallaAutenticacion(
    //tituloPantalla: L10nSingular,
    amosarCheckbox: Boolean,
    amosarRexistrarse: Boolean,
    rutaApiSolicitar: RutaApi,
    rutaVerificar: String = "",
    aoTerminarConExito: () -> Unit,
    aoIrARexistro: ( () -> Unit )? = null
) {
    var correo by rememberSaveable { mutableStateOf( "" ) }
    var codigo by rememberSaveable { mutableStateOf( "" ) }
    var codigoSolicitado by rememberSaveable { mutableStateOf( false ) }
    var aceptaTerminos by rememberSaveable { mutableStateOf( false ) }
    var enviando by rememberSaveable { mutableStateOf( false ) }
    var texto by rememberSaveable { mutableStateOf( "" ) }

    val correoValido = EMAIL_ADDRESS.matcher( correo ).matches()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 5.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Logo( 90.dp )
        EspazadorAlto( 2 )

        AmosarTitulo()
        EspazadorAlto()

        if ( texto != "" ) {
            Text( texto )
            EspazadorAlto()
        }

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text( "Correo electrónico" ) },
            singleLine = true,
            keyboardOptions = KeyboardOptions( keyboardType = KeyboardType.Email ),
            enabled = !codigoSolicitado && !enviando,
            modifier = Modifier.fillMaxWidth()
        )
        EspazadorAlto()

        if ( !codigoSolicitado ) {

            if ( amosarCheckbox ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox( checked = aceptaTerminos, onCheckedChange = { aceptaTerminos = it } )
                    Text( "Acepto os termos e condicións" )
                }
                EspazadorAlto()
            }

            /*Button(
                onClick = {
                    enviando = true
                    texto = ""
                    scope.launch {
                        val resultado = enviar( rutaApiSolicitar, mapOf( "correo" to correo ) )
                        enviando = false
                        if ( resultado.optBoolean( "exito" ) ) {
                            codigoSolicitado = true
                        } else {
                            texto = resultado.optString( "mensaxe", "Escachou o servidor" )
                        }
                    }
                },
                enabled = correoValido && ( !amosarCheckbox || aceptaTerminos ) && !enviando,
                modifier = Modifier.fillMaxWidth()
            ) {
                if ( enviando ) CircularProgressIndicator( Modifier.size( 20.dp ), strokeWidth = 2.dp )
                else Text( "Enviar código" )
            }*/

            if ( amosarRexistrarse && aoIrARexistro != null ) {
                EspazadorAlto()
                TextButton( onClick = aoIrARexistro, modifier = Modifier.fillMaxWidth() ) {
                    Text( "Rexistrarse" )
                }
            }

        } else {

            OutlinedTextField(
                value = codigo,
                onValueChange = { codigo = it },
                label = { Text( "Código de autorización" ) },
                singleLine = true,
                keyboardOptions = KeyboardOptions( keyboardType = KeyboardType.Number ),
                enabled = !enviando,
                modifier = Modifier.fillMaxWidth()
            )
            EspazadorAlto()

            /*Button(
                onClick = {
                    enviando = true
                    texto = ""
                    scope.launch {
                        val resultado = enviar( rutaVerificar, mapOf( "correo" to correo, "codigo" to codigo ) )
                        enviando = false
                        if ( resultado.optBoolean( "exito" ) ) {
                            aoTerminarConExito()
                        } else {
                            texto = resultado.optString( "mensaxe", "Código incorrecto" )
                        }
                    }
                },
                enabled = codigo.isNotBlank() && !enviando,
                modifier = Modifier.fillMaxWidth()
            ) {
                if ( enviando ) CircularProgressIndicator( Modifier.size( 20.dp ), strokeWidth = 2.dp )
                else Text( "Verificar código" )
            }*/
        }

    }

}*/