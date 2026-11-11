package org.aprengal.lendasnubeiras.ui.reutilizables.clases

import org.aprengal.lendasnubeiras.data.actividades.datos.base.Categoria
import org.aprengal.lendasnubeiras.data.actividades.datos.base.Destinatario
import org.aprengal.lendasnubeiras.data.actividades.datos.buscador.CampoBusca
import java.io.Serializable


/** Datos da última busca realizada. Gárdanse para que os resultados non cambien ata que se volva a buscar. */
internal data class ParametrosBusca(
    val termo: String,
    val campoBusca: CampoBusca,
    val categoria: Categoria?,
    val destinatario: Destinatario?
) : Serializable
