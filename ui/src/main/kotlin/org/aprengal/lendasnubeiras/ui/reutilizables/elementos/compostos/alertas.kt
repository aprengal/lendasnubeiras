package org.aprengal.lendasnubeiras.ui.reutilizables.elementos.compostos

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import org.aprengal.lendasnubeiras.data.localizacion.claves.singular.L10nBase
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.DatosAlerta
import org.aprengal.lendasnubeiras.ui.reutilizables.clases.Icona.Companion.DebuxarIcona
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.BotonAuxiliar
import org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.textual.BotonPrincipal

@Composable
fun AlertaDialogo( datos: DatosAlerta ) {

    AlertDialog(
        icon = { datos.icona?.let { icona -> DebuxarIcona( icona.clave, icona.nome, dimension = 50.sp ) } },
        title = { Text( datos.titulo.texto(), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center ) },
        text = { datos.contido?.let { datos.contido() } },
        onDismissRequest = datos.descartado,
        dismissButton = { BotonAuxiliar( L10nBase.Cancelar, datos.cancelado ) },
        confirmButton = { datos.confirmado?.let { confirmado -> BotonPrincipal( L10nBase.Aceptar, confirmado ) } }
    )

}