package org.aprengal.lendasnubeiras.ui.reutilizables.elementos.basicos.visual

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.aprengal.lendasnubeiras.data.axustes.DatosTema.escollerRecursoTema
import org.aprengal.lendasnubeiras.ui.R
import org.aprengal.lendasnubeiras.ui.tema.Tema.temaActual

/**
 * Mostra o logotipo da aplicación, adaptando o recurso visual ao tema actual.
 *
 * @param medida Tamaño do logotipo, aplicado tanto á súa altura como á súa anchura.
 * Por defecto, utiliza unha medida de 30 dp.
 */
@Composable
fun Logo( medida: Dp = 30.dp ) {
    val logo = escollerRecursoTema( temaActual, isSystemInDarkTheme(), R.drawable.logo_claro, R.drawable.logo_escuro )
    Image( painterResource( logo ), null, Modifier.size( medida ).clearAndSetSemantics{} )
}