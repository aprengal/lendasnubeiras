package org.aprengal.lendasnubeiras.data.axustes

internal sealed class Opcion<T : Any>( val nome: String, val predeterminado: T ) {

    object SesionUsuario: Opcion<String>( "sesion-usuario", "" )
    object SesionAnonima: Opcion<Boolean>( "sesion-anonima",false )
    object Tema: Opcion<String>( "tema", "predeterminado" )
    object IdDispositivo: Opcion<String> ( "id-dispositivo", "" )

}