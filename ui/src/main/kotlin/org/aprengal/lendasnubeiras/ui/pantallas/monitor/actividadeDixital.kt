package org.aprengal.lendasnubeiras.ui.pantallas.monitor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.aprengal.lendasnubeiras.data.actividades.datos.dixitais.Dificultade
import org.aprengal.lendasnubeiras.data.bd.operacions.Actividades.collerActividade
import org.aprengal.lendasnubeiras.ui.actividadesDixitais.AplicacionSinala
import org.aprengal.lendasnubeiras.ui.navegacion.Ruta
import org.aprengal.lendasnubeiras.ui.actividadesDixitais.DadosHistoriosos
import org.aprengal.lendasnubeiras.ui.actividadesDixitais.PalabrasEncadeadas

/**
 * Mostra a actividade dixital correspondente á clave recibida e á dificultade escollida.
 *
 * Busca a actividade mediante a súa clave e mostra a implementación correspondente,
 * pasándolle a dificultade escollida e os textos asociados á actividade.
 *
 * @param clave Clave do título da actividade, coincidente coa definida en [Ruta.ActividadeDixital].
 * @param dificultade Dificultade escollida para a actividade.
 * @throws IllegalStateException Se non existe unha implementación dixital asociada á actividade.
 */
@Composable
fun PantallaActividadeDixital( clave: String, dificultade: Dificultade ) {

    val actividade = remember( clave ) { collerActividade( clave )!! }
    val textos = actividade.textos

    when ( actividade.claveTitulo ) {
        "dados-historiosos" -> DadosHistoriosos( dificultade, textos )
        "palabras-encadeadas" -> PalabrasEncadeadas( dificultade, textos )
        "aplicacion-sinala" -> AplicacionSinala( dificultade, textos )
        else -> error( "${ actividade.claveTitulo } non ten ningunha actividade dixital asociada" )
    }

}