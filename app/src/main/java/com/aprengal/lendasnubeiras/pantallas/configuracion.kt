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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import com.aprengal.lendasnubeiras.configuracion.haiLector
import com.aprengal.lendasnubeiras.elementos.actividades.Idioma
import com.aprengal.lendasnubeiras.localizacion.Localizacion.gardarIdioma
import com.aprengal.lendasnubeiras.localizacion.Localizacion.l10n
import com.aprengal.lendasnubeiras.navegacion.corrutina
import com.aprengal.lendasnubeiras.tema.Espazador
import com.aprengal.lendasnubeiras.tema.Tema
import com.aprengal.lendasnubeiras.tema.Tema.gardarTema

//Axustes: idioma, modo escuro, desactivar animacións
@Composable
fun PantallaAxustes() {

    Column {

        //Se se combina con Panatalla Base, igual hai que quitar isto
        Espazador()

        BotonOpcion(
            textoBoton = l10n( "boton_cambio_idioma", "test" ),
            tituloDialogo = l10n( "dialogo_cambio_idioma", "test" ),
            avisoDialogo = l10n( "mensaxe_reiniciar_idioma", "test" ),
            opcions = Idioma.entries.filter { idioma -> idioma != Idioma.NADA },
            valorInicial = Localizacion.idiomaActual.value,
            obterNome = { opcion -> opcion.nome },
            accion = { novoIdioma -> gardarIdioma( novoIdioma ) }
        )

        Espazador()

        BotonOpcion(
            textoBoton = l10n( "boton_cambio_tema", "test" ),
            tituloDialogo = l10n( "dialogo_cambio_tema", "test" ),
            opcions = Tema.Variante.entries,
            valorInicial = Tema.temaActual.value,
            obterNome = { elemento -> l10n( elemento.clave, "test" ) },
            accion = { novoTema -> gardarTema( novoTema ) }
        )

    }

}

// ---------------------------------------------------------
// 3. Botón en la pantalla de ajustes que abre el diálogo
// ---------------------------------------------------------
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
        val haiLector = remember { haiLector( contexto ) }

        DialogoSeleccion(
            titulo = tituloDialogo,
            opciones = opcions,
            opcionActual = valorActual,
            obterTexto = { texto -> obterNome( texto ) },
            aviso = if ( haiLector ) avisoDialogo else null,
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
    opciones: List<T>,
    opcionActual: T,
    obterTexto: (T) -> String,
    aceptar: (T) -> Unit,
    rexeitar: () -> Unit,
    aviso: String? = null
) {

    var opcionSeleccionada by rememberSaveable( opcionActual ) { mutableStateOf(opcionActual ) }

    AlertDialog(
        onDismissRequest = rexeitar,
        title = { Text( titulo ) },
        text = {

            Column( Modifier.selectableGroup() ) {

                if ( aviso != null ) {

                    Text(
                        text = aviso,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding( bottom = 12.dp )
                    )

                }

                for ( opcion in opciones ) {

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
        dismissButton = {
            TextButton( onClick = rexeitar ) {
                Text( l10n( "cancelar", "test" ) )
            }
        }
    )

}