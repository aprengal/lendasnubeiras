package com.aprengal.lendasnubeiras.ui.pantallas.lector

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aprengal.lendasnubeiras.data.localizacion.Localizacion.l10n
import com.aprengal.lendasnubeiras.data.localizacion.Localizacion.l10nPlural
import com.aprengal.lendasnubeiras.ui.tema.PantallaBase

@Composable
fun PantallaBuscador( termo: String) {

    println( termo  )
    TODO("Not yet implemented")

}

@Composable
fun PantallaActividadeDetalle( id: Long ) {

    Text( "DEtalle de $id" )

}

@Composable
fun PantallaActividade() {

    Text( "EHHHHHHHHHHHHHHHHHHHHHHH" )

}

@Composable
fun PantallaInicio() {

    //var idiomaActual by remember { mutableStateOf(idiomaActual ) }
    //val esGalego = idiomaActual.value.codigo == "gl"
    //var mostrarDialogo by rememberSaveable { mutableStateOf(false) }
    //val contexto = LocalContext.current

    //val nuevoIdioma = if (esGalego) Idioma.CASTELAN else Idioma.GALEGO

    PantallaBase {

        item {


            Column {

                Text(
                    text = l10n( "carla", "test" )
                )

                Text(
                    text = l10n( "natasha", "test" ),
                    style = MaterialTheme.typography.headlineLarge
                )

                Text(
                    text = l10nPlural( "mensaxes_novas", "test", 1 )
                )

                Text(
                    text = l10nPlural( "mensaxes_novas", "test", 5 )
                )

                Text(
                    text = l10nPlural( "mensaxes_novas", "test", 0 )
                )

                Text(
                    text = l10nPlural( "mensaxes_novas", "test", 100 )
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = MaterialTheme.colorScheme.primary
                )

            }

        }

    }

}