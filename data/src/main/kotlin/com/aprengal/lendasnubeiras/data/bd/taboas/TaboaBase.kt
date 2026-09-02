package com.aprengal.lendasnubeiras.data.bd.taboas

enum class TaboaBase( override val nome: String ) : Taboa {
    ACTIVIDADES( "actividades" ),
    GRUPOS( "grupos" ),
    XOGADORES( "xogadores" ),
    PUNTUACIONS( "puntuacions" )
}