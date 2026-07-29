package com.aprengal.lendasnubeiras.pantallas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aprengal.lendasnubeiras.localizacion.Localizacion
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import com.aprengal.lendasnubeiras.accesibilidade.haiLector
import com.aprengal.lendasnubeiras.localizacion.Idioma

//Axustes: idioma, modo escuro, desactivar animacións
@Composable
fun PantallaAxustes() {

    var idiomaActual by remember { mutableStateOf( Localizacion.idiomaActual ) }

    Column {

        Text( "En construción" )

        Text( "Deixade de ler isto antes de que marchedes a Saturno" )

        BotonCambioIdioma(
            idiomaActual = idiomaActual,
            onIdiomaActualChange = { nuevo -> idiomaActual = nuevo }
        )

    }

}

// 2. Diálogo de selección de idioma (lista completa)
// ---------------------------------------------------------
@Composable
fun DialogoSeleccionIdioma(
    idiomaActual: Idioma,
    onSeleccionar: ( Idioma ) -> Unit,
    onDismiss: () -> Unit,
) {
    val contexto = LocalContext.current
    val hayLector = remember { haiLector( contexto ) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text( "Seleccionar idioma" ) },
        text = {
            Column( Modifier.selectableGroup() ) {
                if ( hayLector ) {
                    Text(
                        text = "A aplicación reiniciarase ao cambiar o idioma",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding( bottom = 12.dp )
                    )
                }

                Idioma.entries.forEach { idioma ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = idioma == idiomaActual,
                                onClick = { onSeleccionar( idioma ) },
                                role = Role.RadioButton
                            )
                            .padding( vertical = 8.dp ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton( selected = idioma == idiomaActual, onClick = null )
                        Spacer( Modifier.width( 12.dp ) )
                        Text( idioma.nome )
                    }
                }
            }
        },
        confirmButton = {
            TextButton( onClick = onDismiss ) { Text( "Cerrar" ) }
        }
    )
}


// ---------------------------------------------------------
// 3. Botón en la pantalla de ajustes que abre el diálogo
// ---------------------------------------------------------
@Composable
fun BotonCambioIdioma(
    idiomaActual: Idioma,
    onIdiomaActualChange: ( Idioma ) -> Unit,
) {
    var mostrarDialogo by remember { mutableStateOf( false ) }

    Button(
        onClick = { mostrarDialogo = true },
        modifier = Modifier
            .fillMaxWidth()
            .padding( top = 10.dp )
    ) {
        key( idiomaActual ) {
            Text( text = Localizacion.l10n( "cambio_idioma", "test" ) )
        }
    }

    if ( mostrarDialogo ) {
        DialogoSeleccionIdioma(
            idiomaActual = idiomaActual,
            onSeleccionar = { nuevo ->
                Localizacion.gardarIdioma( nuevo )
                onIdiomaActualChange( nuevo )
                mostrarDialogo = false
            },
            onDismiss = { mostrarDialogo = false }
        )
    }
}
