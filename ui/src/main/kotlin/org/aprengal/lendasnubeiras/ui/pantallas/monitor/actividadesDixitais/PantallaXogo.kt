package org.aprengal.lendasnubeiras.ui.pantallas.monitor.actividadesDixitais

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.aprengal.lendasnubeiras.data.actividades.datos.dixitais.Dificultade.Companion.escollerDificultade
import org.aprengal.lendasnubeiras.data.bd.operacions.Actividades.collerActividade
import org.aprengal.lendasnubeiras.data.actividades.datos.base.Actividade
import org.aprengal.lendasnubeiras.ui.navegacion.Ruta
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.AmosarTitulo

/**
 * Pantalla que lanza o xogo dunha actividade coa dificultade escollida.
 *
 * O xogo que corresponde a cada actividade escóllese pola súa [Actividade.claveTitulo]. Só se
 * navega aquí desde actividades con xogo, polo que unha clave sen xogo asociado é un erro de
 * programación e lanza unha excepción, como en [AmosarTitulo].
 *
 * @param clave Clave do título da actividade, a mesma que leva [Ruta.Xogo].
 * @param claveDificultade Clave da dificultade escollida.
 */
@Composable
fun PantallaXogo( clave: String, claveDificultade: String ) {

    val actividade = remember( clave ) { collerActividade( clave )!! }
    val dificultade = remember( claveDificultade ) { escollerDificultade( claveDificultade ) }

    when ( actividade.claveTitulo ) {
        "dados-historiosos" -> XogoDados( dificultade, actividade.textos )
        else -> error( "A actividade ${ actividade.claveTitulo } non ten xogo asociado" )
    }

}