package org.aprengal.lendasnubeiras.data.localizacion.clases

//Para tratar de descargar un dominio ao saír dun elemento composable
/*DisposableEffect( clave ) {
    onDispose {
        descargarDominio( Dominio.Actividade( clave ) )
    }
}*/

enum class Dominio( val nome: String ) {

    ACTIVIDADES( "actividades" ),
    AUTENTICACION( "autenticacion" ),
    BASE( "base" ),

    ICONAS( "iconas" ),
    TITULOS( "titulos" ),
    VALIDACION( "validacion" ),
    OPCIONS( "opcions" ),
    //Test( "test" )

    //Actividade( "actividade" ),
    //AxustesXogos( "xogos/axustes" ),
    //XogoDados( "xogos/dados" ),
}