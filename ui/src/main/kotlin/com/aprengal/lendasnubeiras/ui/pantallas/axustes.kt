package com.aprengal.lendasnubeiras.ui.pantallas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.aprengal.lendasnubeiras.ui.reutilizables.BotonAuxiliar
import com.aprengal.lendasnubeiras.ui.reutilizables.BotonPrincipal
import com.aprengal.lendasnubeiras.ui.reutilizables.LocalAviso
import com.aprengal.lendasnubeiras.ui.reutilizables.LocalIdioma
import com.aprengal.lendasnubeiras.ui.reutilizables.OpcionsDialogo
import com.aprengal.lendasnubeiras.ui.reutilizables.Texto
import com.aprengal.lendasnubeiras.ui.reutilizables.amosarAviso
import com.aprengal.lendasnubeiras.ui.tema.Variante

@Composable
fun PantallaAxustes() {

    Column {

        BotonOpcion(
            clave = "cambio_idioma",
            avisoDialogo = true,
            opcions = Idioma.entries.filter { idioma -> idioma != Idioma.NADA },
            valorInicial = LocalIdioma.current,
            nomeUI = { idioma -> idioma.nome },
            accion = { novoIdioma -> cambiarIdioma( novoIdioma ) }
        )

        EspazadorAlto()

        BotonOpcion(
            clave = "cambio_tema",
            opcions = Variante.entries,
            valorInicial = Tema.temaActual,
            nomeUI = { variante -> variante.nome },
            accion = { novoTema -> gardarTema( novoTema ) }
        )

        if ( PodePecharSesion( usuarioActual() ) ) {

            EspazadorAlto()

            BotonOpcion(
                clave = "peche_sesion",
                opcions = listOf( "si", "non" ),
                valorInicial = "si",
                nomeUI = { texto -> texto },
                accion = { _ -> pecharSesion() }
            )

        }

    }

}

@Composable
fun <T> BotonOpcion(
    clave: String, avisoDialogo: Boolean = false, opcions: List<T>,
    valorInicial: T, nomeUI: (T) -> String, accion: suspend ( T ) -> Boolean
) {

    var amosarDialogo by rememberSaveable { mutableStateOf( false ) }
    var valorActual by rememberSaveable { mutableStateOf( valorInicial ) }
    val contexto = LocalContext.current
    val haiLector = remember { contexto.haiLector() }

    val textoBoton = L10nSingular.buscar( "boton_$clave" )
    val modificadorBoton = Modifier.fillMaxWidth().padding( top = 10.dp )

    BotonPrincipal( textoBoton, { amosarDialogo = true }, modificadorBoton )

    if ( haiLector && avisoDialogo ) {

        /** isto non está localizado. Igual con usar [Texto] vale */
        Text(
            text = L10nSingular.buscar( "subtitulo_$clave" ).texto(),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding( bottom = 12.dp )
        )

    }

    if ( amosarDialogo ) {
        DialogoOpcion( clave, opcions, valorActual, nomeUI, accion,
            { novoValor -> valorActual = novoValor }, { amosarDialogo = it }
        )
    }

}

@Composable
private fun <T> DialogoOpcion(
    clave: String, opcions: List<T>, valorActual: T, nomeUI: ( T ) -> String,
    accion: suspend ( T ) -> Boolean, cambiarValor: ( T ) -> Unit, cambiarVisibilidade: ( Boolean ) -> Unit
) {

    val traducirOpcions = clave != "cambio_idioma"
    val aviso = LocalAviso.current
    var procesando by rememberSaveable { mutableStateOf( false ) }
    var seleccionado by rememberSaveable { mutableStateOf( valorActual ) }

    val aceptar: () -> Unit = {

        cambiarVisibilidade( false )
        procesando = true

        corrutina {

            while ( true ) {

                val resultado = accion( seleccionado )

                if ( resultado ) {
                    cambiarValor( seleccionado )
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

    }

    val tituloDialogo = L10nSingular.buscar( "dialogo_$clave" )
    val contidoDialogo = @Composable {
        OpcionsDialogo( clave, opcions, seleccionado, nomeUI, traducirOpcions, procesando ) { novoValor -> seleccionado = novoValor }
    }

    val confirmacion: @Composable () -> Unit = {
        BotonPrincipal( L10nSingular.ACEPTAR, aceptar, habilitado = !procesando )
    }

    val cancelacion: @Composable () -> Unit = {
        BotonAuxiliar( L10nSingular.CANCELAR, { cambiarVisibilidade( false ) }, !procesando )
    }

    val rexeitar = { if ( !procesando ) { cambiarVisibilidade( false ) } }

    AlertaDialogo(
        titulo = tituloDialogo,
        contido = contidoDialogo,
        confirmacion = confirmacion,
        cancelacion = cancelacion,
        rexeitar = rexeitar
    )

}