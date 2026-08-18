package com.aprengal.lendasnubeiras.data.localizacion

// TODO: Hai que mirar se con R8 se cargan correctamente os dominios. Nese caso, habería que engadir os nomes á man
sealed class Dominio {

    open val clave: String = this::class.simpleName!!.lowercase()

    object Autenticacion : Dominio()
    object Base : Dominio()
    object Menu : Dominio()
    object Test : Dominio()

    //Con ActividadeDetalle, só se podería obter a id porque é o unico campo inequívoco.
    class Actividade( id: Long ) : Dominio() {
        override val clave: String = "actividades/actividade-$id"
    }

}

//Para tratar de descargar un dominio ao saír dun elemento composable
/*DisposableEffect( clave ) {
    onDispose {
        descargarDominio( Dominio.Actividade( clave ) )
    }
}*/