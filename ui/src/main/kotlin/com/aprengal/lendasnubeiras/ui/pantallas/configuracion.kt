package com.aprengal.lendasnubeiras.ui.pantallas
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.aprengal.lendasnubeiras.data.Variante
import com.aprengal.lendasnubeiras.data.localizacion.Localizacion.gardarIdioma
import com.aprengal.lendasnubeiras.ui.tema.Tema
import com.aprengal.lendasnubeiras.ui.tema.Tema.gardarTema
import com.aprengal.lendasnubeiras.data.localizacion.Idioma
import com.aprengal.lendasnubeiras.ui.reutilizables.BotonOpcion
import com.aprengal.lendasnubeiras.ui.reutilizables.Espazador
import com.aprengal.lendasnubeiras.data.usuarios.Permisos.podePecharSesion
import com.aprengal.lendasnubeiras.data.usuarios.SesionActual.collerUsuarioActual
import com.aprengal.lendasnubeiras.data.usuarios.SesionActual.pecharSesion

//Axustes: idioma, modo escuro, desactivar animacións
@Composable
fun PantallaAxustes() {

    Column {

        //Se se combina con Pantalla Base, igual hai que quitar isto
        Espazador()

        BotonOpcion(
            clave = "cambio_idioma",
            dominio = "opcions",
            avisoDialogo = true,
            opcions = Idioma.entries.filter { idioma -> idioma != Idioma.NADA },
            valorInicial = _root_ide_package_.com.aprengal.lendasnubeiras.data.localizacion.Localizacion.idiomaActual,
            nomeUI = { idioma -> idioma.nome },
            //saver = idiomaSaver,
            accion = { novoIdioma -> gardarIdioma( novoIdioma ) }
        )

        Espazador()

        BotonOpcion(
            clave = "cambio_tema",
            dominio = "opcions",
            opcions = Variante.entries,
            valorInicial = Tema.temaActual,
            nomeUI = { variante -> variante.nome },
            //saver = varianteSaver,
            accion = { novoTema -> gardarTema( novoTema ) }
        )

        if ( podePecharSesion( collerUsuarioActual() ) ) {

            Espazador()

            BotonOpcion(
                clave = "peche_sesion",
                dominio = "opcions",
                opcions = listOf( "afirmar", "denegar" ),
                valorInicial = "afirmar",
                nomeUI = { texto -> texto },
                accion = { _ -> pecharSesion() }
            )

        }

    }

}