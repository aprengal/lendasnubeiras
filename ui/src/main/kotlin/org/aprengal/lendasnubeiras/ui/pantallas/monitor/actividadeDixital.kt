package org.aprengal.lendasnubeiras.ui.pantallas.monitor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.aprengal.lendasnubeiras.data.actividades.datos.dixitais.Dificultade.Companion.escollerDificultade
import org.aprengal.lendasnubeiras.data.bd.operacions.Actividades.collerActividade
import org.aprengal.lendasnubeiras.ui.navegacion.Ruta
import org.aprengal.lendasnubeiras.ui.pantallas.monitor.actividadesDixitais.DadosHistoriosos

/**
 * Mostra a pantalla da actividade dixital correspondente á clave do título e á dificultade escollida.
 *
 * Obtén a actividade e a dificultade a partir das claves recibidas e mostra o contido da actividade
 * dixital correspondente. Só se navega a esta pantalla desde actividades dixitais que teñen unha
 * implementación asociada; se non existe ningunha para a actividade, considérase un erro de programación.
 *
 * @param clave Clave do título da actividade, a mesma que leva [Ruta.ActividadeDixital].
 * @param claveDificultade Clave da dificultade escollida.
 * @throws IllegalStateException Se a actividade non ten ningunha implementación asociada.
 */
@Composable
fun PantallaActividadeDixital( clave: String, claveDificultade: String ) {

    val actividade = remember( clave ) { collerActividade( clave )!! }
    val dificultade = remember( claveDificultade ) { escollerDificultade( claveDificultade ) }

    when ( actividade.claveTitulo ) {
        "dados-historiosos" -> DadosHistoriosos( dificultade, actividade.textos )
        else -> error( "${ actividade.claveTitulo } non ten ningunha actividade dixital asociada" )
    }

}