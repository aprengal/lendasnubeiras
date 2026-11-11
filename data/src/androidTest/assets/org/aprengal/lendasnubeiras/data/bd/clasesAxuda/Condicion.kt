package org.aprengal.lendasnubeiras.data.bd.clasesAxuda

import org.aprengal.lendasnubeiras.data.bd.clases.BD.OperadorSimple

/**
 * Representa as distintas condicións que se poden aplicar nas cláusulas WHERE
 * ou HAVING dunha consulta SQL.
 *
 * Permite definir comparacións simples, comprobacións de pertenza a un conxunto
 * de valores e comparacións dentro dun intervalo.
 */
sealed interface Condicion {

    /**
     * Representa unha comparación entre unha columna e un valor.
     *
     * @property valor Valor co que se realizará a comparación.
     * @property operador Operador de comparación empregado. Por defecto, igualdade.
     */
    data class Simple( val valor: Any, val operador: OperadorSimple = OperadorSimple.IGUAL ) : Condicion

    /**
     * Representa unha comprobación de pertenza a un conxunto de valores mediante IN.
     *
     * @property valores Conxunto de valores cos que se comprobará a pertenza.
     */
    data class En( val valores: Set<Any> ) : Condicion

    /**
     * Representa unha comparación dentro dun intervalo mediante BETWEEN.
     *
     * @property minimo Límite inferior do intervalo.
     * @property maximo Límite superior do intervalo.
     */
    data class Entre( val minimo: Any, val maximo: Any ) : Condicion

}
