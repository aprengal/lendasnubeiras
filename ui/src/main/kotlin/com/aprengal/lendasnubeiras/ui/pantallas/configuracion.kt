package com.aprengal.lendasnubeiras.ui.pantallas
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.aprengal.lendasnubeiras.ui.tema.Tema.Variante
import com.aprengal.lendasnubeiras.data.localizacion.Localizacion.cambiarIdioma
import com.aprengal.lendasnubeiras.ui.tema.Tema
import com.aprengal.lendasnubeiras.ui.tema.Tema.gardarTema
import com.aprengal.lendasnubeiras.data.localizacion.Idioma
import com.aprengal.lendasnubeiras.ui.reutilizables.BotonOpcion
import com.aprengal.lendasnubeiras.ui.reutilizables.Espazador
import com.aprengal.lendasnubeiras.data.usuarios.PodePecharSesion
import com.aprengal.lendasnubeiras.data.usuarios.SesionActual.usuarioActual
import com.aprengal.lendasnubeiras.data.usuarios.SesionActual.pecharSesion
import com.aprengal.lendasnubeiras.ui.reutilizables.LocalIdioma

//Axustes: idioma, modo escuro, desactivar animacións
@Composable
fun PantallaAxustes() {

    Column {

        //Se se combina con Pantalla Base, igual hai que quitar isto
        Espazador()

        BotonOpcion(
            clave = "cambio_idioma",
            avisoDialogo = true,
            opcions = Idioma.entries.filter { idioma -> idioma != Idioma.NADA },
            valorInicial = LocalIdioma.current,
            nomeUI = { idioma -> idioma.nome },
            accion = { novoIdioma -> cambiarIdioma( novoIdioma ) }
        )

        Espazador()

        BotonOpcion(
            clave = "cambio_tema",
            opcions = Variante.entries,
            valorInicial = Tema.temaActual,
            nomeUI = { variante -> variante.nome },
            accion = { novoTema -> gardarTema( novoTema ) }
        )

        if ( PodePecharSesion( usuarioActual() ) ) {

            Espazador()

            BotonOpcion(
                clave = "peche_sesion",
                opcions = listOf( "si", "non" ),
                valorInicial = "si",
                nomeUI = { texto -> texto },
                accion = { _ -> pecharSesion() }
            )

        }

    }

}