package org.aprengal.lendasnubeiras.ui.reutilizables.clases

import org.aprengal.lendasnubeiras.data.actividades.datos.base.Categoria
import org.aprengal.lendasnubeiras.data.actividades.datos.base.Destinatario
import org.aprengal.lendasnubeiras.data.actividades.datos.buscador.CampoBusca
import org.aprengal.lendasnubeiras.data.bd.clases.BD
import java.io.Serializable


/**
 * Almacena os parámetros da última busca realizada, permitindo manter os mesmos
 * resultados e criterios de busca ata que se execute unha nova busca.
 *
 * @property termo Texto empregado para buscar nas actividades.
 * @property campoBusca Campos nos que se realiza a busca do termo (título, descrición ou ambos).
 * @property categoria Categoría seleccionada como filtro. Se é null, non se aplica ningún filtro por categoría.
 * @property destinatario Destinatario seleccionado como filtro. Se é null, non se aplica ningún filtro por destinatario.
 * @property orde Criterio de ordenación aplicado aos resultados da busca.
 */
internal data class ParametrosBusca(
    val termo: String,
    val campoBusca: CampoBusca,
    val categoria: Categoria?,
    val destinatario: Destinatario?,
    val orde: BD.OrdeBusca
) : Serializable