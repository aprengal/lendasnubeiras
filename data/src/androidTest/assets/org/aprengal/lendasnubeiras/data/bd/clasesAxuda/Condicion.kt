package org.aprengal.lendasnubeiras.data.bd.clasesAxuda

import org.aprengal.lendasnubeiras.data.bd.clases.BD.OperadorSimple

sealed interface Condicion {

    data class Simple( val valor: Any, val operador: OperadorSimple = OperadorSimple.IGUAL ) : Condicion
    data class En( val valores: Set<Any> ) : Condicion
    data class Entre( val minimo: Any, val maximo: Any ) : Condicion

}