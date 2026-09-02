package com.aprengal.lendasnubeiras.ui.pantallas.autenticacion

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.aprengal.lendasnubeiras.data.localizacion.claves.singular.L10nAutenticacion
import com.aprengal.lendasnubeiras.data.usuarios.SesionActual.crearSesionAnonima
import com.aprengal.lendasnubeiras.ui.navegacion.Ruta
import com.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.BotonPrincipal
import com.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.BotonSecundario
import com.aprengal.lendasnubeiras.ui.navegacion.Locais.LocalNavegacion
import com.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.EspazadorAlto

@Composable
fun PantallaBenvida() {

    val navegacion = LocalNavegacion.current
    val textoAcceso = L10nAutenticacion.BotonAcceso
    val accionAcceso = { navegacion.engadir( Ruta.Acceso ) }
    val textoAnonimo = L10nAutenticacion.BotonAnonimo
    val accionAnonimo = { crearSesionAnonima() }

    Column {
        BotonPrincipal( textoAcceso, accionAcceso, Modifier.fillMaxWidth() )
        EspazadorAlto()
        BotonSecundario( textoAnonimo, accionAnonimo, Modifier.fillMaxWidth() )
    }

}