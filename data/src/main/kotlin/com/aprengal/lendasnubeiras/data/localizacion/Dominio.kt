package com.aprengal.lendasnubeiras.data.localizacion

//Para tratar de descargar un dominio ao saír dun elemento composable
/*DisposableEffect( clave ) {
    onDispose {
        descargarDominio( Dominio.Actividade( clave ) )
    }
}*/

sealed class Dominio( val nome: String ) {

    object Base : Dominio( "base" )
    object Autenticacion : Dominio( "autenticacion" )
    object Titulos : Dominio( "titulos" )
    object Menu : Dominio( "menu" )
    object Test : Dominio( "test" )
    object Opcions: Dominio( "opcions" )

    //object Actividade : Dominio( "actividade" )

    //object AxustesXogos: Dominio( "xogos/axustes" )
    //object XogoDados: Dominio( "xogos/dados" )

}