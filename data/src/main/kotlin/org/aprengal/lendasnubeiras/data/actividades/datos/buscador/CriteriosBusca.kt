package org.aprengal.lendasnubeiras.data.actividades.datos.buscador

import org.aprengal.lendasnubeiras.data.bd.clases.BD.Orde
import org.aprengal.lendasnubeiras.data.localizacion.clases.ElementoL10n

/**
 * Agrupa os criterios empregados para buscar actividades, permitindo combinar
 * filtros por valores pechados, ordenación dos resultados e busca por texto libre.
 *
 * Os filtros permiten limitar os resultados segundo os valores de determinados
 * campos das actividades, como categorías ou destinatarios. Estes valores deben
 * ser elementos de tipo [ElementoL10n], evitando así o uso de valores arbitrarios.
 *
 * A busca por texto libre permite localizar actividades polo seu contido textual.
 * O termo indicado en `termo` aplícase sobre o campo ou os campos seleccionados
 * mediante `campoBusca` (título, descrición ou ambos). Este filtrado realízase
 * en memoria sobre os resultados previamente obtidos de SQLite.
 *
 * A ordenación establécese mediante `colOrdenable` e `dirOrdenable`, sendo
 * descendente o sentido aplicado por defecto.
 *
 * @property colOrdenable Nome da columna pola que se ordenarán os resultados.
 * @property dirOrdenable Sentido da ordenación. Por defecto, descendente ([Orde.DESC]).
 * @property filtros Mapa de filtros por campo, onde cada clave identifica o campo
 * da actividade e cada valor debe ser un elemento de tipo [ElementoL10n].
 * Por defecto, non se aplica ningún filtro.
 * @property termo Texto libre empregado para buscar nas actividades. Se é null,
 * non se aplica ningún filtrado por texto.
 * @property campoBusca Indica os campos de texto nos que se buscará o termo:
 * título, descrición ou ambos. Por defecto, [CampoBusca.Ambos].
 * @property limite Número máximo de resultados que se devolverán na busca.
 * Se é null, non se establece ningún límite.
 */
data class CriteriosBusca(
    val colOrdenable: String,
    val dirOrdenable: Orde = Orde.DESC,
    val filtros: Map<String, Any> = emptyMap(),
    val termo: String? = null,
    val campoBusca: CampoBusca = CampoBusca.Ambos,
    val limite: Int? = null
)