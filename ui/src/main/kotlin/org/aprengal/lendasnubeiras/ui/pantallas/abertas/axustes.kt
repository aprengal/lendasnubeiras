package org.aprengal.lendasnubeiras.ui.pantallas.abertas

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
import org.aprengal.lendasnubeiras.data.utilidades.Corrutinas.corrutina
import org.aprengal.lendasnubeiras.data.utilidades.Contexto.haiLector
import org.aprengal.lendasnubeiras.data.localizacion.Localizacion.cambiarIdioma
import org.aprengal.lendasnubeiras.data.localizacion.Idioma
import org.aprengal.lendasnubeiras.data.localizacion.Localizacion.obterTexto
import org.aprengal.lendasnubeiras.data.localizacion.claves.singular.L10nOpcions
import org.aprengal.lendasnubeiras.data.usuarios.Permiso.PodePecharSesion
import org.aprengal.lendasnubeiras.data.usuarios.SesionActual.usuarioActual
import org.aprengal.lendasnubeiras.data.usuarios.SesionActual.pecharSesion
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.DatosListaOpcions
import org.aprengal.lendasnubeiras.ui.navegacion.Locais.LocalAviso
import org.aprengal.lendasnubeiras.ui.navegacion.Locais.LocalIdioma
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.DatosAlerta
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.DatosElementoLista
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.DatosOpcion
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.Icona
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.TipoOpcion
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.Texto
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.EspazadorAlto
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.compostos.AlertaDialogo
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.compostos.ElementoLista
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.compostos.ListaOpcions
import org.aprengal.lendasnubeiras.ui.tema.Tema.escollerVarianteImaxe
import org.aprengal.lendasnubeiras.ui.tema.Tema.gardarTema
import org.aprengal.lendasnubeiras.ui.tema.Tema.temaActual
import org.aprengal.lendasnubeiras.ui.tema.Tema.Variante
import org.aprengal.lendasnubeiras.ui.utilidades.Accions.amosarAviso
import org.aprengal.lendasnubeiras.ui.utilidades.Accions.executarAccion

@Composable
fun PantallaAxustes() {

    Column( modifier = Modifier.fillMaxWidth().padding( 10.dp ) ) {

        Card {

            val datosIdioma = DatosOpcion(
                clave = "cambio_idioma",
                titulo = L10nOpcions.TituloCambioIdioma,
                gardadoFallido = L10nOpcions.GardadoFallidoCambioIdioma,
                tipo = TipoOpcion.ALERTA,
                icona = Icona.Idioma,
                valorInicial = LocalIdioma.current,
                accion = { novoIdioma -> cambiarIdioma( novoIdioma ) },
                tituloAlerta = L10nOpcions.DialogoCambioIdioma,
                opcions = Idioma.entries.filter { idioma -> idioma != Idioma.Nada },
                descricionExtra = L10nOpcions.SubtituloCambioIdioma
            )

            ContidoOpcion( datosIdioma )
            HorizontalDivider()

            val datosTema = DatosOpcion(
                clave = "cambio_tema",
                titulo = L10nOpcions.TituloCambioTema,
                gardadoFallido = L10nOpcions.GardadoFallidoCambioTema,
                tipo = TipoOpcion.ALERTA,
                icona = escollerVarianteImaxe( isSystemInDarkTheme(), Icona.TemaClaro, Icona.TemaEscuro ),
                valorInicial = temaActual,
                accion = { novoTema -> gardarTema( novoTema ) },
                tituloAlerta = L10nOpcions.DialogoCambioTema,
                opcions = Variante.entries
            )

            ContidoOpcion( datosTema )

        }

        if ( PodePecharSesion( usuarioActual() ) ) {

            EspazadorAlto( 2 )

            val modificador = Modifier.fillMaxWidth().clickable( onClick = intentoPecheSesion() ).padding( horizontal = 16.dp, vertical = 12.dp )

            Card {

                Column( modificador, Arrangement.Center, Alignment.CenterHorizontally ) {
                    Texto( L10nOpcions.TituloPecheSesion )
                }

            }

        }

    }

}

@Composable
private fun <T> ContidoOpcion( datos: DatosOpcion<T> ) {

    var activo by rememberSaveable { mutableStateOf( false ) }
    var valorActual by rememberSaveable { mutableStateOf( datos.valorInicial ) }
    val haiLector = haiLector( LocalContext.current )
    val textoUI = obterTexto( valorActual )

    val contido = @Composable {

        Column {
            Text( textoUI )
            datos.descricionExtra?.takeIf { haiLector }?.let { texto -> Texto( texto ) }
        }

    }

    when( datos.tipo ) {

        TipoOpcion.INTERRUPTOR -> {
            val datosLista = DatosElementoLista( { activo = !activo }, datos.titulo, datos.icona, contido )
            ElementoLista( datosLista )
        }

        TipoOpcion.ALERTA -> {

            val datosLista = DatosElementoLista( { activo = true }, datos.titulo, datos.icona, contido )

            ElementoLista( datosLista )

            if ( activo ) {
                DebuxarAlerta( datos, { estado -> activo = estado }, valorActual, { novoValor -> valorActual = novoValor } )
            }

        }

    }

}

@Composable
private fun <T> DebuxarAlerta( datos: DatosOpcion<T>, amosar: ( Boolean ) -> Unit, actual: T, seleccionar: ( T ) -> Unit ) {

    require( datos.opcions != null && datos.opcions.size > 1 ) { "Unha alerta con opcións debe existir e ter 2 ou máis elementos" }

    val aviso = LocalAviso.current
    var escollido by rememberSaveable { mutableStateOf( actual ) }

    val confirmado: () -> Unit = {

        amosar( false )

        corrutina {
            val erro: suspend () -> SnackbarResult = { amosarAviso( aviso, datos.gardadoFallido, true ) }
            if ( executarAccion( { datos.accion( escollido ) }, erro ) ) { seleccionar( escollido ) }
        }

    }

    val datosLista = DatosListaOpcions( datos.opcions, escollido ) {
        novoValor -> escollido = novoValor
    }

    val titulo = datos.tituloAlerta!!
    val contido = @Composable { ListaOpcions( datosLista ) }

    val datosAlerta = DatosAlerta( titulo, { amosar( false ) }, contido = contido, confirmado = confirmado )

    AlertaDialogo( datosAlerta )

}

@Composable
fun intentoPecheSesion(): () -> Unit {

    var amosarDialogo by rememberSaveable { mutableStateOf( false ) }
    val aviso = LocalAviso.current

    if ( amosarDialogo ) {

        val accion = {

            amosarDialogo = false

            corrutina {
                val clave = L10nOpcions.PecheSesionFallido
                val erro: suspend () -> SnackbarResult = { amosarAviso( aviso, clave, true ) }
                executarAccion( { pecharSesion() }, erro )
            }

        }

        val datosAlerta = DatosAlerta( L10nOpcions.DialogoPecheSesion, { amosarDialogo = false }, confirmado = accion )
        AlertaDialogo( datosAlerta )

    }

    return { amosarDialogo = true }

}