package com.aprengal.lendasnubeiras.data.bd

import com.aprengal.lendasnubeiras.data.bd.taboas.Taboa

//Pendente para transformar en BD

data class SelectSQL(
    val columnas: Set<String>,
    val alias: String = "",
    val joins: List<CombinacionSQL> = emptyList(),
    val onde: Map<String, Condicion> = emptyMap(),
    val ordenar: Map<String, Orde> = emptyMap(),
    val limite: List<Int> = emptyList()
)

data class CombinacionSQL(
    val tipo: TipoCombinacion,
    val principal: String,
    val secundaria: String,
    val taboaJoin: Taboa
)

enum class TipoCombinacion( val nome: String ) {
    INNER( "INNER" ),
    LEFT( "LEFT" ),
    RIGHT( "RIGHT" ),
    CROSS( "CROSS" )
}

enum class Orde( val nome: String ) { ASC( "ASC" ), DESC( "DESC" ) }

sealed interface Condicion {

    data class Simple( val valor: Any, val operador: OperadorSimple = OperadorSimple.IGUAL ) : Condicion
    data class En( val valores: Set<Any> ) : Condicion
    data class Entre( val minimo: Any, val maximo: Any ) : Condicion

}

enum class OperadorSimple( val simbolo: String ) {
    IGUAL( "=" ),
    DISTINTO( "!=" ),
    MENOR( "<" ),
    MENOR_IGUAL( "<=" ),
    MAIOR( ">" ),
    MAIOR_IGUAL( ">=" ),
    COINCIDE( "MATCH" ),
    SEMELLANTE( "LIKE" ),
    NON_SEMELLANTE( "NOT LIKE" )
}