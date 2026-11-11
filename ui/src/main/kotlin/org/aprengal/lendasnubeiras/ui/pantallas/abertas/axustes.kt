package org.aprengal.lendasnubeiras.ui.pantallas.abertas

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
import org.aprengal.lendasnubeiras.data.api.ConexionApi.MetodoApi
import org.aprengal.lendasnubeiras.data.api.ConexionApi.procesarPeticion
import org.aprengal.lendasnubeiras.data.api.RutasApi
import org.aprengal.lendasnubeiras.data.api.resposta.RespostaXenerica
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

/**
 * Mostra a pantalla de configuración da aplicación.
 *
 * Permite consultar e modificar distintas preferencias, como o idioma
 * e o tema visual da aplicación, mediante as opcións correspondentes.
 *
 * Cando existe unha sesión de usuario activa, mostra tamén información
 * da conta e habilita funcionalidades adicionais relacionadas coa
 * actualización do catálogo, a limpeza de datos, o borrado da conta
 * e o peche de sesión, segundo os permisos e condicións establecidos
 * para cada operación.
 *
 * As operacións que requiren confirmación ou poden producir erros
 * execútanse mediante diálogos de confirmación e avisos.
 */
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

            val modificador = Modifier.fillMaxWidth().padding( vertical = 12.dp )

            EspazadorAlto( 2 )

            Card( onClick = accionCatalogo ) {

                Column( modificador, Arrangement.Center, Alignment.CenterHorizontally ) {
                    Texto( L10nOpcions.TituloActualizacionCatalogo )
                }

            }

            EspazadorAlto( 2 )

            val limpezaDatos: suspend () -> Boolean = {
                val resposta = procesarPeticion( MetodoApi.GET, RutasApi.LIMPAR, emptyMap() ) as RespostaXenerica
                resposta.exito
            }

            val accionLimpeza = intentarAccion( L10nOpcions.DialogoLimpezaDatos, erro = L10nOpcions.LimpezaDatosFallido ) { limpezaDatos() }

            Card( onClick = accionLimpeza ) {

                Column( modificador, Arrangement.Center, Alignment.CenterHorizontally ) {
                    Texto( L10nOpcions.TituloLimpezaDatos )
                }

            }

        }

        if ( PodeBorrarConta( usuarioActual() ) ) {

            val accionBorrado = intentarAccion(
                L10nOpcions.DialogoBorradoConta,
                L10nOpcions.ContidoBorradoConta,
                L10nOpcions.BorradoContaFallido,
            ) { borrarConta( idiomaActual ) }

            val modificador = Modifier.fillMaxWidth().padding( horizontal = 16.dp, vertical = 12.dp )

            EspazadorAlto( 2 )

            Card( onClick = accionBorrado ) {

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

            val modificador = Modifier.fillMaxWidth().padding( horizontal = 16.dp, vertical = 12.dp )

            Card( onClick = accionPeche ) {

                Column( modificador, Arrangement.Center, Alignment.CenterHorizontally ) {
                    Texto( L10nOpcions.TituloPecheSesion )
                }

            }

        }

    }

}

/**
 * Mostra o contido visual dunha opción de configuración.
 *
 * Presenta o título, a icona e o valor actual da opción mediante
 * [ElementoLista], adaptando o seu comportamento ao tipo de opción.
 *
 * No caso das opcións de tipo [TipoOpcion.INTERRUPTOR], permite
 * alternar o seu estado.
 *
 * No caso das opcións de tipo [TipoOpcion.ALERTA], permite abrir
 * un diálogo no que seleccionar un novo valor. A descrición adicional
 * só se mostra cando o dispositivo dispón dun lector compatible.
 *
 * O valor actual e o estado de activación consérvanse mediante
 * [rememberSaveable].
 *
 * @param datos Datos da opción de configuración, incluíndo o seu título,
 *              icona, tipo, valor inicial, acción e información adicional.
 */
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

/**
 * Mostra un diálogo de selección para modificar o valor dunha opción.
 *
 * Presenta as opcións dispoñibles mediante [ListaOpcions], permitindo
 * seleccionar un novo valor antes de confirmar o cambio.
 *
 * Esixe que [DatosOpcion.opcions] conteña polo menos dous elementos.
 *
 * Ao confirmar, péchase o diálogo e execútase a acción asociada á opción
 * co valor seleccionado. Se a operación se realiza correctamente,
 * actualízase o valor seleccionado mediante o callback recibido.
 *
 * Se a operación falla, móstrase un aviso mediante [amosarAviso],
 * mantendo sen cambios o valor actual.
 *
 * @param datos Datos da opción que se vai modificar, incluíndo as opcións
 *              dispoñibles, o título do diálogo, a acción que se executará
 *              e a mensaxe de erro correspondente.
 * @param amosar Función que permite controlar a visibilidade do diálogo.
 * @param actual Valor actualmente establecido para a opción.
 * @param seleccionar Función encargada de actualizar o valor da opción
 *                    tras completar correctamente a operación.
 */
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

/**
 * Prepara unha acción que require confirmación antes da súa execución.
 *
 * Devolve unha función que, ao ser invocada, mostra un diálogo de
 * confirmación co título e o contido opcionais proporcionados.
 *
 * Cando o usuario confirma a operación, péchase o diálogo e execútase
 * a acción nunha corrutina. Se a operación falla, móstrase un aviso
 * coa mensaxe de erro correspondente.
 *
 * A execución da acción e a xestión dos posibles erros realízanse
 * mediante [executarAccion].
 *
 * @param titulo Título do diálogo de confirmación.
 * @param contido Contido textual opcional que se mostra no diálogo.
 * @param erro Mensaxe que se mostra se a acción non se completa correctamente.
 * @param accion Operación suspendida que se vai executar tras a
 * confirmación e que indica mediante un booleano
 * se se completou correctamente.
 *
 * @return Función que permite solicitar a confirmación e amosar o diálogo correspondente.
 */
@Composable
private fun intentarAccion( titulo: L10nOpcions, contido: L10nOpcions? = null, erro: L10nOpcions, accion: suspend () -> Boolean ): () -> Unit {

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