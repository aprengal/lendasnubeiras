package com.aprengal.lendasnubeiras.ui.pantallas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.aprengal.lendasnubeiras.data.configuracion.corrutina
import com.aprengal.lendasnubeiras.data.configuracion.haiLector
import com.aprengal.lendasnubeiras.data.localizacion.Localizacion.cambiarIdioma
import com.aprengal.lendasnubeiras.ui.tema.Tema
import com.aprengal.lendasnubeiras.ui.tema.Tema.gardarTema
import com.aprengal.lendasnubeiras.data.localizacion.Idioma
import com.aprengal.lendasnubeiras.data.localizacion.L10nSingular
import com.aprengal.lendasnubeiras.ui.reutilizables.EspazadorAlto
import com.aprengal.lendasnubeiras.data.usuarios.PodePecharSesion
import com.aprengal.lendasnubeiras.data.usuarios.SesionActual.usuarioActual
import com.aprengal.lendasnubeiras.data.usuarios.SesionActual.pecharSesion
import com.aprengal.lendasnubeiras.ui.reutilizables.AlertaDialogo
import com.aprengal.lendasnubeiras.ui.reutilizables.ElementoLista
import com.aprengal.lendasnubeiras.ui.reutilizables.Icona
import com.aprengal.lendasnubeiras.ui.reutilizables.LocalAviso
import com.aprengal.lendasnubeiras.ui.reutilizables.LocalIdioma
import com.aprengal.lendasnubeiras.ui.reutilizables.OpcionsDialogo
import com.aprengal.lendasnubeiras.ui.reutilizables.Texto
import com.aprengal.lendasnubeiras.ui.reutilizables.amosarAviso
import com.aprengal.lendasnubeiras.ui.tema.Variante
import com.aprengal.lendasnubeiras.ui.tema.escollerVarianteImaxe

@Composable
fun PantallaAxustes() {

    Column {

        Card( modifier = Modifier.fillMaxWidth().padding( 16.dp ) ) {

            ContidoOpcion(
                clave = "cambio_idioma",
                icona = Icona.IDIOMA,
                avisoDialogo = true,
                opcions = Idioma.entries.filter { idioma -> idioma != Idioma.NADA },
                valorInicial = LocalIdioma.current,
                nomeUI = { idioma -> idioma.nome },
                accion = { novoIdioma -> cambiarIdioma( novoIdioma ) }
            )

            HorizontalDivider()

            ContidoOpcion(
                clave = "cambio_tema",
                icona = escollerVarianteImaxe( isSystemInDarkTheme(), Icona.TEMA_CLARO, Icona.TEMA_ESCURO ), //ESTO DEBERÏA ALTERNAR ENTE tema claro u oscuro
                opcions = Variante.entries,
                valorInicial = Tema.temaActual,
                nomeUI = { variante -> variante.nome },
                accion = { novoTema -> gardarTema( novoTema ) }
            )

        }

        if ( PodePecharSesion( usuarioActual() ) ) {

            EspazadorAlto()

            Card( modifier = Modifier.fillMaxWidth().padding( 16.dp ) ) {

                ContidoOpcion(
                    clave = "peche_sesion",
                    opcions = listOf("si", "non"),
                    valorInicial = "si",
                    nomeUI = { texto -> texto },
                    accion = { _ -> pecharSesion() }
                )

            }
        }

    }

}

@Composable
fun <T> ContidoOpcion(
    clave: String, icona: Icona? = null, avisoDialogo: Boolean = false, opcions: List<T>,
    valorInicial: T, nomeUI: (T) -> String, accion: suspend ( T ) -> Boolean
) {

    var amosarDialogo by rememberSaveable { mutableStateOf( false ) }
    var valorActual by rememberSaveable { mutableStateOf( valorInicial ) }
    var procesando by rememberSaveable { mutableStateOf( false ) }
    val contexto = LocalContext.current
    val haiLector = remember { contexto.haiLector() }

    val tituloOpcion = L10nSingular.buscar( "boton_$clave" )

    val textoUI = when ( clave ) {
        "peche_sesion" -> null
        "cambio_idioma" -> nomeUI( valorActual )
        else -> L10nSingular.buscar( "${ clave }_${ nomeUI( valorActual ) }" ).texto()
    }

    ElementoLista( { amosarDialogo = true }, tituloOpcion, textoUI, icona )

    if ( haiLector && avisoDialogo ) {

        /** isto non está localizado. Igual con usar [Texto] vale */
        Text(
            text = L10nSingular.buscar( "subtitulo_$clave" ).texto(),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding( bottom = 12.dp )
        )

    }

    if ( amosarDialogo && !procesando ) {
        DialogoOpcion( clave, opcions, valorActual, nomeUI, accion,
            { novoValor -> valorActual = novoValor }, { amosarDialogo = it }, { procesando = it }
        )
    }

}

@Composable
private fun <T> DialogoOpcion(
    clave: String, opcions: List<T>, valorActual: T, nomeUI: ( T ) -> String, accion: suspend ( T ) -> Boolean,
    cambiarValor: ( T ) -> Unit, cambiarVisibilidade: ( Boolean ) -> Unit, cambiarEstado: ( Boolean ) -> Unit
) {

    val traducirOpcions = clave != "cambio_idioma"
    val aviso = LocalAviso.current
    var seleccionado by rememberSaveable { mutableStateOf( valorActual ) }

    val aceptado: () -> Unit = {

        cambiarVisibilidade( false )
        cambiarEstado( true )

        corrutina {

            while ( true ) {

                val resultado = accion( seleccionado )

                if ( resultado ) {
                    cambiarValor( seleccionado )
                    cambiarEstado( false )
                    break
                }

                val claveL10n = L10nSingular.buscar( "gardado_fallido_$clave" )
                val resultadoAviso = amosarAviso( aviso, claveL10n, true )

                if ( resultadoAviso != SnackbarResult.ActionPerformed ) {
                    cambiarEstado( false )
                    break
                }

            }

        }

    }

    val tituloDialogo = L10nSingular.buscar( "dialogo_$clave" )
    val contidoDialogo = @Composable {
        OpcionsDialogo( clave, opcions, seleccionado, nomeUI, traducirOpcions ) { novoValor -> seleccionado = novoValor }
    }

    AlertaDialogo(
        titulo = tituloDialogo,
        contido = contidoDialogo,
        confirmado = aceptado,
        cancelado = { cambiarVisibilidade( false ) },
        rexeitado = { cambiarVisibilidade( false ) }
    )

}