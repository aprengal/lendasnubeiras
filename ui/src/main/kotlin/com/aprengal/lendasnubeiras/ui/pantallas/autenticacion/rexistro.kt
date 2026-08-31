package com.aprengal.lendasnubeiras.ui.pantallas.autenticacion

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.aprengal.lendasnubeiras.data.localizacion.claves.L10nSingular
import com.aprengal.lendasnubeiras.ui.navegacion.Locais.LocalNavegacion
import com.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.TextoEnlazado

@Composable
fun PantallaRexistro() {

    val navegacion = LocalNavegacion.current

    Text( "Cando é a recuperación?" )

    val atras = L10nSingular.VOLVER_ACCESO
    val enlace = mapOf( L10nSingular.VOLVER_ACCESO to { navegacion.quitarUltimo() } )

    TextoEnlazado( atras, enlace )

}