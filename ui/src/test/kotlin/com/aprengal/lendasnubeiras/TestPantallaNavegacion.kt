package com.aprengal.lendasnubeiras

class TestPantallaNavegacion {

    //Novos tests: Mirar se o tipo de navegación é o esperado

    /*@Test
    fun todasPantallasUsadas() {

        val subclasesReais = Pantalla::class.sealedSubclasses.toSet()
        val pantallasNavegacion = ( Navegacion.pantallasAdmin + Navegacion.pantallasAutenticacion ).map { it::class }.toSet()
        val faltanEnLista = subclasesReais - pantallasNavegacion

        assertTrue(
            "Faltan as seguintes pantallas por asignar ao Menú: ${faltanEnLista.map { e -> e.simpleName }}",
            faltanEnLista.isEmpty()
        )

    }

    fun comprobarRutaPantalla( pantalla: Pantalla ) {

        val esperado = when ( pantalla ) {
            Pantalla.ActividadeDetalle -> "actividade/detalle/{id}"
            Pantalla.Buscar -> "buscar/{termo}"
            Pantalla.Actividades -> "actividades"
            Pantalla.Administrar -> "administrar"
            Pantalla.Animacions -> "animacions"
            Pantalla.Benvida -> "apertura"
            Pantalla.Axustes -> "axustes"
            Pantalla.CrearActividade -> "crear/actividade"
            Pantalla.Idioma -> "idioma"
            Pantalla.IniciarSesion -> "acceso"
            Pantalla.Inicio -> "inicio"
            Pantalla.ListarActividades -> "listar/actividades"
            Pantalla.ModificarActividade -> "modificar/actividade"
            Pantalla.Rexistro -> "rexistro"
        }

        assertEquals( esperado, pantalla.ruta )

    }

    @Test
    fun nomePantalla() {
        Pantalla::class.sealedSubclasses.forEach { pantalla -> comprobarRutaPantalla( pantalla.objectInstance!! ) }
    }*/

    /*@Test
    fun nomeUnicoPantalla() {
        val pantallas = Pantalla::class.sealedSubclasses
        val rutas = pantallas.map { pantalla -> pantalla.objectInstance!!.ruta }
        assertEquals( "Hai rutas de pantalla duplicadas", rutas.size, rutas.toSet().size )
    }

    @Test
    fun rutaPantallaBenFormada() {

        val pantallas = Pantalla::class.sealedSubclasses.map { pantalla -> pantalla.objectInstance!! }

        for ( pantalla in pantallas ) {

            val ruta = pantalla.ruta
            val aperturas = ruta.count{ c -> c == '{' }
            val peches = ruta.count { c -> c == '}' }

            assertTrue( "Hai unha ruta baleira", ruta.isNotBlank() )
            assertEquals( "Ruta mal formada: $ruta", aperturas, peches )

            ruta.forEachIndexed { indice, caracter ->

                if ( caracter == '{' ) {
                    assertEquals( "Apertura de argumento sen '/' na ruta: $ruta", '/', ruta[ indice - 1 ] )
                }

            }

        }

    }*/

}