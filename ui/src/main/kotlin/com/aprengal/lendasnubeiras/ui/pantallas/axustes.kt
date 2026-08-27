package com.aprengal.lendasnubeiras.ui.pantallas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.aprengal.lendasnubeiras.ui.reutilizables.ListaOpcions
import com.aprengal.lendasnubeiras.ui.reutilizables.LocalAviso
import com.aprengal.lendasnubeiras.ui.reutilizables.LocalIdioma
import com.aprengal.lendasnubeiras.ui.reutilizables.Texto
import com.aprengal.lendasnubeiras.ui.reutilizables.amosarAviso
import com.aprengal.lendasnubeiras.ui.tema.Variante
import com.aprengal.lendasnubeiras.ui.tema.escollerVarianteImaxe

@Composable
fun PantallaAxustes() {

    Column( modifier = Modifier.fillMaxWidth().padding( 10.dp ) ) {

        Card {

            ContidoOpcion(
                clave = "cambio_idioma",
                icona = Icona.IDIOMA,
                descricionExtra = true,
                opcions = Idioma.entries.filter { idioma -> idioma != Idioma.NADA },
                valorInicial = LocalIdioma.current,
                nomeUI = { idioma -> idioma.nome },
                accion = { novoIdioma -> cambiarIdioma( novoIdioma ) }
            )

            HorizontalDivider()

            ContidoOpcion(
                clave = "cambio_tema",
                icona = escollerVarianteImaxe( isSystemInDarkTheme(), Icona.TEMA_CLARO, Icona.TEMA_ESCURO ),
                opcions = Variante.entries,
                valorInicial = Tema.temaActual,
                nomeUI = { variante -> variante.nome },
                accion = { novoTema -> gardarTema( novoTema ) }
            )

        }

        if ( PodePecharSesion( usuarioActual() ) ) {

            EspazadorAlto( 2 )

            val modificador = Modifier.fillMaxWidth().clickable( onClick = intentoPecheSesion() ).padding( horizontal = 16.dp, vertical = 12.dp )

            Card {

                //Valorar se aquí se pode empregar unha alerta para confirmar o peche de sesión
                Column( modifier = modificador, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center ) {
                    Texto( L10nSingular.BOTON_PECHE_SESION )
                }

            }

        }

    }

}

@Composable
fun <T> ContidoOpcion(
    clave: String, icona: Icona? = null, descricionExtra: Boolean = false, opcions: List<T>,
    valorInicial: T, nomeUI: (T) -> String, accion: suspend ( T ) -> Boolean
) {

    var amosarDialogo by rememberSaveable { mutableStateOf( false ) }
    var valorActual by rememberSaveable { mutableStateOf( valorInicial ) }
    val haiLector = LocalContext.current.haiLector()
    val tituloOpcion = L10nSingular.buscar( "boton_$clave" )
    val textoUI = if ( clave == "cambio_idioma" ) nomeUI( valorActual ) else L10nSingular.buscar( "${ clave }_${ nomeUI( valorActual ) }" ).texto()
    val contido = @Composable {

        Column {
            Text( textoUI )
            if ( haiLector && descricionExtra ) { Texto( L10nSingular.buscar( "subtitulo_$clave" ) ) }
        }

    }

    ElementoLista( { amosarDialogo = true }, tituloOpcion, icona, contido )

    if ( amosarDialogo ) {

        val localizar = clave != "cambio_idioma"
        val aviso = LocalAviso.current
        var escollido by rememberSaveable { mutableStateOf( valorActual ) }
        val claveLoc = L10nSingular.buscar( "gardado_fallido_$clave" )

        val confirmado: () -> Unit = {

            amosarDialogo = false

            corrutina {
                val erro: suspend () -> SnackbarResult = { amosarAviso( aviso, claveLoc, true ) }
                if ( executarAccion( { accion( escollido ) }, erro ) ) { valorActual = escollido }
            }

        }

        val titulo = L10nSingular.buscar( "dialogo_$clave" )
        val contido = @Composable {
            ListaOpcions( clave, opcions, escollido, nomeUI, localizar ) { novoValor -> escollido = novoValor }
        }

        AlertaDialogo( titulo, { amosarDialogo = false }, contido = contido, confirmado = confirmado )

    }

}

suspend fun executarAccion( accion: suspend () -> Boolean, erro: suspend () -> SnackbarResult ): Boolean {

    while ( !accion() ) {
        if ( erro() != SnackbarResult.ActionPerformed ) { return false }
    }

    return true

}

@Composable
private fun intentoPecheSesion(): () -> Unit {

    var amosarDialogo by rememberSaveable { mutableStateOf( false ) }
    val aviso = LocalAviso.current

    if ( amosarDialogo ) {

        val accion = {

            amosarDialogo = false

            corrutina {
                val clave = L10nSingular.PECHE_SESION_FALLIDO
                val erro: suspend () -> SnackbarResult = { amosarAviso( aviso, clave, true ) }
                executarAccion( { pecharSesion() }, erro )
            }

        }

        AlertaDialogo( L10nSingular.DIALOGO_PECHE_SESION, { amosarDialogo = false }, confirmado = accion )

    }

    return { amosarDialogo = true }

}