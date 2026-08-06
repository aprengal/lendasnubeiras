package com.aprengal.lendasnubeiras

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aprengal.lendasnubeiras.tema.BotonGhost
import com.aprengal.lendasnubeiras.tema.BotonPrincipal
import com.aprengal.lendasnubeiras.tema.BotonSecundario
import com.aprengal.lendasnubeiras.tema.Espazador


private val CorExito = Color( 0xFF2E7D32 )

// =====================================================================
// CUERPO DE PRUEBA — botones (principal/secundario/ghost) + input con
// sus 3 estados, para validar visualmente el tema completo
// =====================================================================
@Composable
fun ContenidoPrueba(modifier: Modifier = Modifier) {

    var textoInput by remember { mutableStateOf("") }
    var estadoInput by remember { mutableStateOf(EstadoInput.NORMAL) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll( rememberScrollState() ),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("Botones", style = MaterialTheme.typography.titleMedium)

        BotonPrincipal(texto = "Botón principal", onClick = { })
        BotonSecundario(texto = "Botón secundario", onClick = { })
        BotonGhost(texto = "Botón ghost", onClick = { })

        HorizontalDivider( modifier = Modifier.padding( vertical = 8.dp ), color = MaterialTheme.colorScheme.primary )

        Text("Input con estados", style = MaterialTheme.typography.titleMedium)

        // Selector rápido de estado, solo para esta pantalla de prueba
        Row( horizontalArrangement = Arrangement.spacedBy( 8.dp ) ) {
            EstadoInput.entries.forEach { estado ->
                FilterChip(
                    selected = estadoInput == estado,
                    onClick = { estadoInput = estado },
                    label = { Text(estado.etiqueta) }
                )
            }
        }

        val colorBorde = when (estadoInput) {
            EstadoInput.NORMAL -> MaterialTheme.colorScheme.outline
            EstadoInput.ERROR -> MaterialTheme.colorScheme.error
            EstadoInput.EXITO -> CorExito
        }

        OutlinedTextField(
            value = textoInput,
            onValueChange = { contido -> textoInput = contido },
            label = { Text( "Campo de ejemplo" ) },
            isError = estadoInput == EstadoInput.ERROR,
            supportingText = {
                when (estadoInput) {
                    EstadoInput.ERROR -> Text(
                        "Este campo tiene un error",
                        color = MaterialTheme.colorScheme.error
                    )
                    EstadoInput.EXITO -> Text("Campo válido", color = CorExito)
                    EstadoInput.NORMAL -> Text( "EHHHHHHHH", color = CorExito )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorBorde,
                unfocusedBorderColor = colorBorde,
                errorBorderColor = MaterialTheme.colorScheme.error,

                focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                errorLabelColor = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private enum class EstadoInput(val etiqueta: String) {
    NORMAL("Normal"),
    ERROR("Error"),
    EXITO("Éxito")
}

@Composable
fun PantallaDetalle( id: Int, test: String ) {

    var contador by remember { mutableIntStateOf( 0 ) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Detalle del Elemento $id cun bo $test",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Veces que has pulsado: $contador",
            style = MaterialTheme.typography.bodyLarge
        )

        Espazador( 2 )

        Button( onClick = { contador++ } ) {
            Text(text = "Incrementar contador")
        }
    }
}