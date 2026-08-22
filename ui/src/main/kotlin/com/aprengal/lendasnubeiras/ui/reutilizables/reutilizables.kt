package com.aprengal.lendasnubeiras.ui.reutilizables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aprengal.lendasnubeiras.data.configuracion.haiLector
import com.aprengal.lendasnubeiras.ui.navegacion.Navegacion.collerPantallas
import com.aprengal.lendasnubeiras.ui.R
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import com.aprengal.lendasnubeiras.data.configuracion.corrutina
import com.aprengal.lendasnubeiras.data.localizacion.L10nPlural
import com.aprengal.lendasnubeiras.data.localizacion.L10nSingular
import com.aprengal.lendasnubeiras.ui.tema.Tema.escollerVarianteImaxe

@Composable
fun Espazador( multiplicador: Int = 1 ) {
    Spacer( modifier = Modifier.height( 10.dp * multiplicador ) )
}

@Composable
fun Logo( tamano: Dp = 30.dp ) {

    val logo = escollerVarianteImaxe( isSystemInDarkTheme(), R.drawable.logo_claro, R.drawable.logo_escuro )

    Image(
        painter = painterResource( logo ),
        modifier = Modifier.size( tamano ),
        contentDescription = L10nSingular.NOME_APP.texto()
    )

}

@Composable
fun <T> BotonOpcion(
    clave: String,
    avisoDialogo: Boolean = false,
    opcions: List<T>,
    valorInicial: T,
    nomeUI: (T) -> String,
    accion: suspend ( T ) -> Boolean
) {

    var amosarDialogo by rememberSaveable { mutableStateOf( false ) }
    var valorActual by rememberSaveable { mutableStateOf( valorInicial ) }
    val traducirOpcions = clave != "cambio_idioma"

    Button(
        onClick = { amosarDialogo = true },
        modifier = Modifier
            .fillMaxWidth()
            .padding( top = 10.dp )
    ) { Text( L10nSingular.buscar( "boton_$clave" ).texto() ) }

    if ( amosarDialogo ) {

        val aviso = LocalAviso.current
        val contexto = LocalContext.current
        val haiLector = remember { contexto.haiLector() }
        var procesando by rememberSaveable { mutableStateOf( false ) }

        DialogoSeleccion(
            clave = clave,
            opcions = opcions,
            opcionActual = valorActual,
            nomeUI = nomeUI,
            traducirOpcions = traducirOpcions,
            subtitulo = haiLector && avisoDialogo,
            procesando = procesando,
            aceptar = { novoValor -> procesando = true

                corrutina {

                    while ( true ) {

                        val resultado = accion( novoValor )
                        amosarDialogo = false

                        if ( resultado ) {
                            valorActual = novoValor
                            procesando = false
                            break
                        }

                        val claveL10n = L10nSingular.buscar( "gardado_fallido_$clave" )
                        val resultadoAviso = amosarAviso( aviso, claveL10n, true )

                        if ( resultadoAviso != SnackbarResult.ActionPerformed ) {
                            procesando = false
                            break
                        }

                    }

                }

            },
            rexeitar = { amosarDialogo = false }
        )

    }

}

suspend fun amosarAviso( aviso: SnackbarHostState, claveMensaxe: L10nSingular, repetir: Boolean ): SnackbarResult {

    aviso.currentSnackbarData?.dismiss()

    val reintentar = if ( repetir ) L10nSingular.buscar( "reintentar_accion" ).texto() else null
    val duracion = if ( repetir ) SnackbarDuration.Long else SnackbarDuration.Short

    return aviso.showSnackbar( claveMensaxe.texto(), reintentar, repetir, duracion )

}

@Composable
fun <T> DialogoSeleccion(
    clave: String,
    subtitulo: Boolean,
    opcions: List<T>,
    opcionActual: T,
    nomeUI: (T) -> String,
    traducirOpcions: Boolean,
    procesando: Boolean,
    aceptar: (T) -> Unit,
    rexeitar: () -> Unit
) {

    var seleccionado by rememberSaveable { mutableStateOf( opcionActual ) }

    AlertDialog(
        title = { Text( L10nSingular.buscar( "dialogo_$clave" ).texto() ) },
        text = {

            Column( Modifier.selectableGroup() ) {

                if ( subtitulo ) {

                    Text(
                        text = L10nSingular.buscar( "subtitulo_$clave" ).texto(),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding( bottom = 12.dp )
                    )

                }

                for ( opcion in opcions ) {

                    val textoUI = nomeUI( opcion )
                    val texto = if ( traducirOpcions ) L10nSingular.buscar( "${ clave }_${ textoUI }" ).texto() else textoUI

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = seleccionado == opcion,
                                enabled = !procesando,
                                onClick = { seleccionado = opcion },
                                role = Role.RadioButton
                            )
                            .padding( vertical = 8.dp ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton( selected = seleccionado == opcion, enabled = !procesando, onClick = null )
                        Spacer( Modifier.width( 12.dp ) )
                        Text( texto )
                    }

                }

            }

        },
        confirmButton = {
            TextButton( enabled = !procesando, onClick = { aceptar( seleccionado ) } ) {
                Text( L10nSingular.ACEPTAR.texto() )
            }
        },
        onDismissRequest = { if ( !procesando ) rexeitar() },
        dismissButton = {
            TextButton( enabled = !procesando, onClick = rexeitar ) {
                Text( L10nSingular.CANCELAR.texto() )
            }
        }
    )

}

@Composable
fun Texto( elemento: L10nSingular, modifier: Modifier = Modifier, estilo: TextStyle = LocalTextStyle.current ) {
    Text( elemento.texto(), modifier, style = estilo )
}

@Composable
fun TextoPlural( elemento: L10nPlural, cantidade: Int, modifier: Modifier = Modifier, estilo: TextStyle = LocalTextStyle.current ) {
    Text( elemento.texto( cantidade ), modifier, style = estilo )
}

@Composable
fun comprobarPermisos(): Boolean {

    val pantallaActual = LocalPantallaActual.current
    if ( pantallaActual.permiso() ) return true

    Log.wtf( "PERMISO", "Tratouse de realizar un acceso indebido" )
    val pantallaInicial = collerPantallas().first
    val controlador = LocalControlador.current

    LaunchedEffect( Unit ) {
        controlador.navigate( pantallaInicial.ruta ) { popUpTo( 0 ) }
    }

    return false

}