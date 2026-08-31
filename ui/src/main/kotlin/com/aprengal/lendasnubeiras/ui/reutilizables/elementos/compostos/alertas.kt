package com.aprengal.lendasnubeiras.ui.reutilizables.elementos.compostos

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.aprengal.lendasnubeiras.data.localizacion.claves.L10nSingular
import com.aprengal.lendasnubeiras.ui.reutilizables.clases.DatosAlerta
import com.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.BotonAuxiliar
import com.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.BotonPrincipal
import com.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual.iconas.DebuxarIcona

@Composable
fun AlertaDialogo( datos: DatosAlerta ) {

    AlertDialog(
        icon = { datos.icona?.let { icona -> DebuxarIcona( icona.codigo, icona.descricion, dimension = 50.sp ) } },
        title = { Text( datos.titulo.texto(), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center ) },
        text = { datos.contido?.let { datos.contido() } },
        onDismissRequest = datos.descartado,
        dismissButton = { BotonAuxiliar( L10nSingular.CANCELAR, datos.cancelado ) },
        confirmButton = { datos.confirmado?.let { confirmado -> BotonPrincipal( L10nSingular.ACEPTAR, confirmado ) } }
    )

}