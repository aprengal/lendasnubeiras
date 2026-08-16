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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.aprengal.lendasnubeiras.data.configuracion.Axustes.collerOpcion
import com.aprengal.lendasnubeiras.data.configuracion.Axustes.gardarOpcion
import com.aprengal.lendasnubeiras.data.configuracion.api.RutaApi
import com.aprengal.lendasnubeiras.data.usuarios.SesionActual.crearSesionAnonima
import com.aprengal.lendasnubeiras.data.localizacion.Localizacion.l10n
import com.aprengal.lendasnubeiras.ui.navegacion.Pantalla
import com.aprengal.lendasnubeiras.ui.reutilizables.Espazador
import com.aprengal.lendasnubeiras.ui.reutilizables.LocalControlador
import com.aprengal.lendasnubeiras.ui.reutilizables.Logo
import java.util.UUID


@Composable
private fun PantallaAcceso( tituloPantalla: String, tituloBoton: String, amosarCheckbox: Boolean, botonPulsado: ( correo: String ) -> String ) {

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

fun crearIdDispositivo(): String {

    var idDispositivo = collerOpcion( _root_ide_package_.com.aprengal.lendasnubeiras.data.configuracion.Opcion.IdDispositivo )
    if ( idDispositivo.isNotBlank() ) return idDispositivo

    idDispositivo = UUID.randomUUID().toString()
    _root_ide_package_.com.aprengal.lendasnubeiras.data.configuracion.corrutina {
        gardarOpcion(
            _root_ide_package_.com.aprengal.lendasnubeiras.data.configuracion.Opcion.IdDispositivo,
            idDispositivo
        )
    }

    return idDispositivo

}

/*@Composable
fun PantallaApertura() {

    var apertura: String by rememberSaveable { mutableStateOf( "Esperando resposta..." ) }

    LaunchedEffect( Unit ) { //ISto quitaríase. É un exemplo para

        val resultado = Conexion.coller( "peido.php", emptyMap() )

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

}*/

@Composable
fun PantallaApertura() {

    val controlador = LocalControlador.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding( WindowInsets.safeDrawing )
            .padding( horizontal = 24.dp, vertical = 16.dp ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Logo( 90.dp )
        Espazador( 2 )

        Text( l10n( "titulo_benvida", "autenticacion" ), style = MaterialTheme.typography.headlineLarge )
        Espazador()

        Button( onClick = { controlador.navigate( Pantalla.IniciarSesion.ruta ) }, modifier = Modifier.fillMaxWidth() ) {
            Text( l10n( "boton_iniciar_sesion", "autenticacion" ) )
        }

        Espazador()

        OutlinedButton( onClick = { crearSesionAnonima() }, modifier = Modifier.fillMaxWidth() ) {
            Text( l10n( "boton_continuar_anonimo", "autenticacion" ) )
        }

    }

}

@Composable
private fun PantallaAutenticacion(
    tituloPantalla: String,
    amosarCheckbox: Boolean,
    amosarRexistrarse: Boolean,
    rutaApiSolicitar: com.aprengal.lendasnubeiras.data.configuracion.api.RutaApi,
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
        Espazador( 2 )

        Text( l10n( tituloPantalla, "test" ), style = MaterialTheme.typography.headlineLarge )
        Espazador()

        if ( texto != "" ) {
            Text( texto )
            Espazador()
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
        Espazador()

        if ( !codigoSolicitado ) {

            if ( amosarCheckbox ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox( checked = aceptaTerminos, onCheckedChange = { aceptaTerminos = it } )
                    Text( "Acepto os termos e condicións" )
                }
                Espazador()
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
                Espazador()
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
            Espazador()

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

}

@Composable
fun PantallaIniciarSesion( aoTerminarConExito: () -> Unit, aoIrARexistro: () -> Unit ) {
    PantallaAutenticacion(
        tituloPantalla = "titulo_iniciar_sesion",
        amosarCheckbox = false,
        amosarRexistrarse = true,
        rutaApiSolicitar = _root_ide_package_.com.aprengal.lendasnubeiras.data.configuracion.api.RutaApi.INICIOSESION,//"solicitarCodigoLogin.php",
        //rutaVerificar = "verificarCodigoLogin.php",
        aoTerminarConExito = aoTerminarConExito,
        aoIrARexistro = aoIrARexistro
    )
}

@Composable
fun PantallaRexistro( aoTerminarConExito: () -> Unit ) {

    PantallaAutenticacion(
        tituloPantalla = "titulo_rexistro",
        amosarCheckbox = true,
        amosarRexistrarse = false,
        rutaApiSolicitar = _root_ide_package_.com.aprengal.lendasnubeiras.data.configuracion.api.RutaApi.INICIOSESION,
        //rutaVerificar = "verificarCodigoRexistro.php",
        aoTerminarConExito = aoTerminarConExito
    )

}