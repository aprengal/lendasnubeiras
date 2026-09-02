package org.aprengal.lendasnubeiras.data.axustes

sealed class Opcion<T : Any>( val nome: String, val predeterminado: T ) {

    data object SesionUsuario: Opcion<String>( "sesion-usuario", "" )
    data object SesionAnonima: Opcion<Boolean>( "sesion-anonima",false )
    data object Tema: Opcion<String>( "tema", "predeterminado" )
    data object IdDispositivo: Opcion<String> ( "id-dispositivo", "" )

}