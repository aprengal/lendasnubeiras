package org.aprengal.lendasnubeiras.ui.pantallas.abertas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.aprengal.lendasnubeiras.data.axustes.DatosTema.escollerVarianteImaxe
import org.aprengal.lendasnubeiras.data.axustes.DatosTema.Variante
import org.aprengal.lendasnubeiras.data.bd.clases.BD.actualizarCatalogo
import org.aprengal.lendasnubeiras.data.localizacion.clases.Localizacion.cambiarIdioma
import org.aprengal.lendasnubeiras.data.localizacion.clases.Idioma
import org.aprengal.lendasnubeiras.data.localizacion.clases.Localizacion.obterTexto
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nOpcions
import org.aprengal.lendasnubeiras.data.usuarios.AccionsUsuario.borrarConta
import org.aprengal.lendasnubeiras.data.usuarios.Permiso.PodeBorrarConta
import org.aprengal.lendasnubeiras.data.usuarios.Permiso.PodePecharSesion
import org.aprengal.lendasnubeiras.data.usuarios.SesionActual.usuarioActual
import org.aprengal.lendasnubeiras.data.usuarios.SesionActual.pecharSesion
import org.aprengal.lendasnubeiras.data.utilidades.Corrutinas.corrutina
import org.aprengal.lendasnubeiras.data.utilidades.Contexto.haiLector
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
import org.aprengal.lendasnubeiras.ui.tema.Tema.cambiarTema
import org.aprengal.lendasnubeiras.ui.tema.Tema.temaActual
import org.aprengal.lendasnubeiras.ui.utilidades.Accions.amosarAviso
import org.aprengal.lendasnubeiras.ui.utilidades.Accions.executarAccion

@Composable
fun PantallaAxustes() {

    val idiomaActual = LocalIdioma.current

    Column( modifier = Modifier.fillMaxWidth().padding( 10.dp ) ) {

        if ( usuarioActual().existe ) {

            Card {

                Column( Modifier.fillMaxWidth().padding( vertical = 10.dp ), Arrangement.Center, Alignment.CenterHorizontally ) {
                    Texto( L10nOpcions.UsuarioAutenticado, estilo = MaterialTheme.typography.titleLarge )
                    EspazadorAlto()
                    Text( usuarioActual().correo )
                }

            }

            EspazadorAlto( 2 )

        }

        Card {

            val datosIdioma = DatosOpcion(
                clave = "cambio_idioma",
                titulo = L10nOpcions.TituloCambioIdioma,
                gardadoFallido = L10nOpcions.GardadoFallidoCambioIdioma,
                tipo = TipoOpcion.ALERTA,
                icona = Icona.Idioma,
                valorInicial = idiomaActual,
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
                icona = escollerVarianteImaxe( temaActual, isSystemInDarkTheme(), Icona.TemaClaro, Icona.TemaEscuro ),
                valorInicial = temaActual,
                accion = { novoTema -> cambiarTema( novoTema ) },
                tituloAlerta = L10nOpcions.DialogoCambioTema,
                opcions = Variante.entries
            )

            ContidoOpcion( datosTema )

        }

        //A seguinte funcionalidade púxose en opcións, pero igual sería mellor que estivese
        //na pantalla de edición de actividades, pero como non se vai implementar, deixase aquí como mostra.
        //Cando se abre a aplicación xa se realiza unha actualización do catálog
        //Non ten sentido que o usuario poida volver actualizar isto
        if ( usuarioActual().existe ) {

            val ambito = CoroutineScope( SupervisorJob() + Dispatchers.IO )
            val accionCatalogo = intentarAccion(
                L10nOpcions.DialogoActualizacionCatalogo,
                L10nOpcions.ContidoActualizacionCatalogo,
                L10nOpcions.ActualizacionCatalogoFallido
            ) { actualizarCatalogo( ambito ) }

            val modificadorCatalogo = Modifier.fillMaxWidth().clickable( onClick = accionCatalogo ).padding( vertical = 12.dp )

            EspazadorAlto( 2 )

            Card {

                Column( modificadorCatalogo, Arrangement.Center, Alignment.CenterHorizontally ) {
                    Texto( L10nOpcions.TituloActualizacionCatalogo )
                }

            }

        }

        if ( PodeBorrarConta( usuarioActual() ) ) {

            val accionBorrado = intentarAccion(
                L10nOpcions.DialogoBorradoConta,
                L10nOpcions.ContidoBorradoConta,
                L10nOpcions.BorradoContaFallido,
            ) { borrarConta( idiomaActual ) }

            val modificador = Modifier.fillMaxWidth().clickable( onClick = accionBorrado ).padding( horizontal = 16.dp, vertical = 12.dp )

            EspazadorAlto( 2 )

            Card {

                Column( modificador, Arrangement.Center, Alignment.CenterHorizontally ) {
                    Texto( L10nOpcions.TituloBorradoConta )
                }

            }

        }

        if ( PodePecharSesion( usuarioActual() ) ) {

            EspazadorAlto( 2 )

            val accionPeche = intentarAccion(
                titulo = L10nOpcions.DialogoPecheSesion,
                erro   = L10nOpcions.PecheSesionFallido,
                accion = { pecharSesion() }
            )

            val modificador = Modifier.fillMaxWidth().clickable( onClick = accionPeche ).padding( horizontal = 16.dp, vertical = 12.dp )

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
fun intentarAccion( titulo: L10nOpcions, contido: L10nOpcions? = null, erro: L10nOpcions, accion: suspend () -> Boolean ): () -> Unit {

    var amosarDialogo by rememberSaveable { mutableStateOf( false ) }
    val aviso = LocalAviso.current

    if ( amosarDialogo ) {

        val confirmar = {

            amosarDialogo = false

            corrutina {
                val mostrarErro: suspend () -> SnackbarResult = { amosarAviso( aviso, erro, true ) }
                executarAccion( accion, mostrarErro )
            }

        }

        val contidoDialogo = contido?.let { clave -> @Composable { Texto( clave ) } }
        val datosAlerta = DatosAlerta( titulo, { amosarDialogo = false }, confirmado = confirmar, contido = contidoDialogo )

        AlertaDialogo( datosAlerta )

    }

    return { amosarDialogo = true }

}