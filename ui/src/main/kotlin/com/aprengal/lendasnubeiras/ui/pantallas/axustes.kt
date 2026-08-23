package com.aprengal.lendasnubeiras.ui.pantallas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.aprengal.lendasnubeiras.ui.tema.Tema.Variante
import com.aprengal.lendasnubeiras.data.localizacion.Localizacion.cambiarIdioma
import com.aprengal.lendasnubeiras.ui.tema.Tema
import com.aprengal.lendasnubeiras.ui.tema.Tema.gardarTema
import com.aprengal.lendasnubeiras.data.localizacion.Idioma
import com.aprengal.lendasnubeiras.data.localizacion.L10nSingular
import com.aprengal.lendasnubeiras.ui.reutilizables.BotonOpcion
import com.aprengal.lendasnubeiras.ui.reutilizables.EspazadorAlto
import com.aprengal.lendasnubeiras.data.usuarios.PodePecharSesion
import com.aprengal.lendasnubeiras.data.usuarios.SesionActual.usuarioActual
import com.aprengal.lendasnubeiras.data.usuarios.SesionActual.pecharSesion
import com.aprengal.lendasnubeiras.ui.navegacion.Pantalla
import com.aprengal.lendasnubeiras.ui.reutilizables.LocalIdioma
import com.aprengal.lendasnubeiras.ui.reutilizables.LocalPantalla
import com.aprengal.lendasnubeiras.ui.reutilizables.Texto

@Composable
fun PantallaAxustes() {

    BotonOpcion(
        clave = "cambio_idioma",
        avisoDialogo = true,
        opcions = Idioma.entries.filter { idioma -> idioma != Idioma.NADA },
        valorInicial = LocalIdioma.current,
        nomeUI = { idioma -> idioma.nome },
        accion = { novoIdioma -> cambiarIdioma( novoIdioma ) }
    )

    EspazadorAlto()

    BotonOpcion(
        clave = "cambio_tema",
        opcions = Variante.entries,
        valorInicial = Tema.temaActual,
        nomeUI = { variante -> variante.nome },
        accion = { novoTema -> gardarTema( novoTema ) }
    )

    if ( PodePecharSesion( usuarioActual() ) ) {

        EspazadorAlto()

        BotonOpcion(
            clave = "peche_sesion",
            opcions = listOf( "si", "non" ),
            valorInicial = "si",
            nomeUI = { texto -> texto },
            accion = { _ -> pecharSesion() }
        )

    }

}

@Composable
fun AmosarTitulo() {

    val titulo = when ( LocalPantalla.current ) {
        //Pantalla.Actividades -> TODO()
        Pantalla.Axustes -> L10nSingular.TITULO_AXUSTES
        Pantalla.Benvida -> L10nSingular.TITULO_BENVIDA
        //is Pantalla.Buscar -> TODO()
        //Pantalla.CrearActividade -> TODO()
        //Pantalla.Idioma -> TODO()
        Pantalla.IniciarSesion -> L10nSingular.TITULO_ACCESO
        //Pantalla.Inicio -> TODO()
        //Pantalla.ListarActividades -> TODO()
        //Pantalla.ModificarActividade -> TODO()
        Pantalla.Rexistro -> L10nSingular.TITULO_REXISTRO
        else -> error( "A pantalla ${ LocalPantalla.current::class.simpleName } non ten título asignado" )
    }

    Texto( titulo, estilo = MaterialTheme.typography.headlineLarge  )

}