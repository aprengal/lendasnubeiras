package com.aprengal.lendasnubeiras.ui.reutilizables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.aprengal.lendasnubeiras.configuracion.corrutina
import com.aprengal.lendasnubeiras.configuracion.haiLector
import com.aprengal.lendasnubeiras.localizacion.Localizacion.l10n

@Composable
fun Espazador( multiplicador: Int = 1 ) {
    Spacer( modifier = Modifier.height( 10.dp * multiplicador ) )
}

@Composable
fun <T> BotonOpcion(
    textoBoton: String,
    tituloDialogo: String,
    avisoDialogo: String? = null,
    opcions: List<T>,
    valorInicial: T,
    obterNome: (T) -> String,
    accion: suspend (T) -> Unit
) {

    var amosarDialogo by rememberSaveable { mutableStateOf( false ) }
    var valorActual by remember { mutableStateOf( valorInicial ) }

    Button(
        onClick = { amosarDialogo = true },
        modifier = Modifier
            .fillMaxWidth()
            .padding( top = 10.dp )
    ) { Text( textoBoton ) }

    if ( amosarDialogo ) {

        val contexto = LocalContext.current
        val haiLector = remember { contexto.haiLector() }

        DialogoSeleccion(
            titulo = tituloDialogo,
            opcions = opcions,
            opcionActual = valorActual,
            obterTexto = { texto -> obterNome( texto ) },
            cabeceira = if ( haiLector ) avisoDialogo else null,
            aceptar = { novoValor ->

                corrutina {
                    accion( novoValor )
                    valorActual = novoValor
                    amosarDialogo = false
                }

            },
            rexeitar = { amosarDialogo = false }
        )

    }

}

@Composable
fun <T> DialogoSeleccion(
    titulo: String,
    cabeceira: String? = null,
    opcions: List<T>,
    opcionActual: T,
    obterTexto: (T) -> String,
    aceptar: (T) -> Unit,
    rexeitar: () -> Unit
) {

    var opcionSeleccionada by rememberSaveable( opcionActual ) { mutableStateOf(opcionActual ) }

    AlertDialog(
        title = { Text( titulo ) },
        text = {

            Column( Modifier.selectableGroup() ) {

                if ( cabeceira != null ) {

                    Text(
                        text = cabeceira,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding( bottom = 12.dp )
                    )

                }

                for ( opcion in opcions ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = opcion == opcionSeleccionada,
                                onClick = { opcionSeleccionada = opcion },
                                role = Role.RadioButton
                            )
                            .padding( vertical = 8.dp ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton( selected = opcion == opcionSeleccionada, onClick = null )
                        Spacer( Modifier.width( 12.dp ) )
                        Text( obterTexto( opcion ) )
                    }

                }

            }

        },
        confirmButton = {
            TextButton( onClick = { aceptar( opcionSeleccionada ) } ) {
                Text( l10n( "aceptar", "test" ) )
            }
        },
        onDismissRequest = rexeitar,
        dismissButton = {
            TextButton( onClick = rexeitar ) {
                Text( l10n( "cancelar", "test" ) )
            }
        }
    )

}