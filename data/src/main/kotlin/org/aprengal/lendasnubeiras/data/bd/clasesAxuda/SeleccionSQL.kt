package org.aprengal.lendasnubeiras.data.bd.clasesAxuda

import org.aprengal.lendasnubeiras.data.bd.clases.BD.Orde

/**
 * Representa os parámetros dunha consulta SQL de selección (SELECT).
 *
 * Permite definir as columnas que se obterán, aplicar DISTINCT, establecer
 * un alias para a táboa principal, combinar outras táboas mediante JOIN,
 * especificar condicións de filtrado, establecer a ordenación dos resultados
 * e limitar o número de rexistros devoltos.
 *
 * @property columnas Conxunto de columnas que se obterán na consulta.
 * @property distinto Indica se se deben eliminar os resultados duplicados mediante DISTINCT.
 * @property alias Alias asignado á táboa principal da consulta.
 * @property combinacions Lista de combinacións JOIN con outras táboas.
 * @property onde Condicións que se aplicarán na cláusula WHERE.
 * @property ordenar Columnas polas que se ordenarán os resultados e o sentido da ordenación.
 * @property limite Valores empregados na cláusula LIMIT para limitar os resultados.
 */
data class SeleccionSQL(
    val columnas: Set<String>,
    val distinto: Boolean = false,
    val alias: String = "",
    val combinacions: List<CombinacionSQL> = emptyList(),
    val onde: Map<String, Condicion> = emptyMap(),
    val ordenar: Map<String, Orde> = emptyMap(),
    val limite: List<Int> = emptyList()
)