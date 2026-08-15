package com.aprengal.lendasnubeiras.ui.pantallas
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.aprengal.lendasnubeiras.localizacion.Localizacion
import com.aprengal.lendasnubeiras.localizacion.Localizacion.gardarIdioma
import com.aprengal.lendasnubeiras.localizacion.Localizacion.l10n
import com.aprengal.lendasnubeiras.ui.tema.Tema
import com.aprengal.lendasnubeiras.ui.tema.Tema.gardarTema
import com.aprengal.lendasnubeiras.localizacion.Idioma
import com.aprengal.lendasnubeiras.ui.reutilizables.BotonOpcion
import com.aprengal.lendasnubeiras.ui.reutilizables.Espazador
import com.aprengal.lendasnubeiras.ui.reutilizables.LocalAviso
import com.aprengal.lendasnubeiras.usuarios.Permisos.podePecharSesion
import com.aprengal.lendasnubeiras.usuarios.SesionActual.collerUsuarioActual
import com.aprengal.lendasnubeiras.usuarios.SesionActual.pecharSesion

//Axustes: idioma, modo escuro, desactivar animacións
@Composable
fun PantallaAxustes() {

    val aviso = LocalAviso.current

    Column {

        //Se se combina con Pantalla Base, igual hai que quitar isto
        Espazador()

        BotonOpcion(
            textoBoton = l10n( "boton_cambio_idioma", "test" ),
            tituloDialogo = l10n( "dialogo_cambio_idioma", "test" ),
            avisoDialogo = l10n( "mensaxe_reiniciar_idioma", "test" ),
            opcions = Idioma.entries.filter { idioma -> idioma != Idioma.NADA },
            valorInicial = Localizacion.idiomaActual.value,
            obterNome = { opcion -> opcion.nome },
            accion = { novoIdioma -> gardarIdioma( novoIdioma ) }
        )

        Espazador()

        BotonOpcion(
            textoBoton = l10n( "boton_cambio_tema", "test" ),
            tituloDialogo = l10n( "dialogo_cambio_tema", "test" ),
            opcions = Tema.Variante.entries,
            valorInicial = Tema.temaActual.value,
            obterNome = { elemento -> l10n( elemento.clave, "test" ) },
            accion = { novoTema -> gardarTema( novoTema ) }
        )

        if ( podePecharSesion( collerUsuarioActual() ) ) {

            Espazador()

            BotonOpcion(
                textoBoton = l10n( "boton_cambio_tema", "test" ),
                tituloDialogo = l10n( "dialogo_cambio_tema", "test" ),
                opcions = listOf( "afirmar", "denegar" ),
                valorInicial = Tema.temaActual.value,
                obterNome = { elemento -> l10n( elemento.toString(), "test" ) },
                accion = { _ ->

                    val mensaxe = pecharSesion()

                    if ( mensaxe.isNotBlank() ) {
                        aviso.showSnackbar( mensaxe )
                    }

                }
            )

        }

    }

}