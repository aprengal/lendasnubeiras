package org.aprengal.lendasnubeiras.ui.pantallas.autenticacion

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.aprengal.lendasnubeiras.data.localizacion.singulares.L10nAutenticacion

import org.aprengal.lendasnubeiras.ui.navegacion.Locais.LocalNavegacion
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.TextoEnlazado

@Composable
fun PantallaRexistro() {

    val navegacion = LocalNavegacion.current

    Text( "Cando é a recuperación?" )

    val atras = L10nAutenticacion.VolverAcceso
    val enlace = mapOf( L10nAutenticacion.VolverAcceso to { navegacion.quitarUltimo() } )

    TextoEnlazado( atras, enlace )

}