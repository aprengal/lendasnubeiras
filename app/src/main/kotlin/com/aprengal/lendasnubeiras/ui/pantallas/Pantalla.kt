package com.aprengal.lendasnubeiras.ui.pantallas

sealed class Pantalla( val tipo: TIPO, val enlaces: Boolean ) {

    enum class TIPO { SCAFFOLD, SOSUPERIOR, SEN_MENUS }

    open val ruta: String = this::class.simpleName!!.lowercase()

    //Autenticación
    object Apertura: Pantalla( TIPO.SEN_MENUS, false )
    object Rexistro: Pantalla( TIPO.SEN_MENUS, false )
    object IniciarSesion: Pantalla( TIPO.SEN_MENUS, false )

    //Lectura
    object Inicio: Pantalla( TIPO.SCAFFOLD, false )
    object Axustes: Pantalla( TIPO.SOSUPERIOR, true )
    object Actividades: Pantalla( TIPO.SOSUPERIOR, true )

    object ActividadeDetalle: Pantalla( TIPO.SOSUPERIOR, true ) {
        override val ruta: String = "${super.ruta}/{id}"
    }

    /*object Detalle : Pantalla( TIPO.SCAFFOLD, true ) {
        override val ruta: String = "${super.ruta}/{id}/{test}"
    }*/

    object Buscar : Pantalla( TIPO.SCAFFOLD, true ) {
        override val ruta: String = "${super.ruta}/{termo}"
    }

    //object Mapa: Pantalla( TIPO.SCAFFOLD, true )
    object Idioma: Pantalla( TIPO.SCAFFOLD, true )
    object Animacions: Pantalla( TIPO.SCAFFOLD, true )

    //Creación
    object ListarActividades: Pantalla( TIPO.SOSUPERIOR, false  )
    object CrearActividade: Pantalla( TIPO.SOSUPERIOR, false )
    object ModificarActividade: Pantalla( TIPO.SOSUPERIOR, false  )

    //Administración
    object Administrar: Pantalla( TIPO.SOSUPERIOR, false )

    fun crearRuta( vararg valores: Any ): String {

        var rutaModificable = ruta

        val aperturas = rutaModificable.count{ c -> c == '{' }
        val cierres = rutaModificable.count { c -> c == '}' }

        check( aperturas == cierres ) { "Ruta mal formada: $ruta" }
        check( valores.size == aperturas ) { "Número incorrecto de argumentos para a ruta: $ruta (${ valores.contentToString() })" }

        for ( valor in valores ) {

            val inicio = rutaModificable.indexOf( "{" )
            val fin = rutaModificable.indexOf( "}" )

            rutaModificable = rutaModificable.replaceRange( inicio, fin + 1, valor.toString() )

        }

        return rutaModificable

    }

}