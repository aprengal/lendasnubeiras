package com.example.lendasnubeiras.mapa

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.example.lendasnubeiras.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Preview(showBackground = true)
@Composable
fun MapaMundial() {

    val context = LocalContext.current
    var rexionPulsada by remember { mutableStateOf( "Ningunha" ) }
    var selectorRexion by remember { mutableStateOf<SelectorRexion?>( null ) }

    LaunchedEffect( Unit ) {
        selectorRexion = withContext( Dispatchers.Default ) {
            SelectorRexion( context, R.drawable.world_continents )
        }
    }

    Box( modifier = Modifier.fillMaxSize() ) {

        BoxWithConstraints(
            modifier = Modifier.fillMaxSize().padding( 16.dp ),
            contentAlignment = Alignment.Center
        ) {

            val imaxe = painterResource( id = R.drawable.world_continents )
            val ratioMapa = imaxe.intrinsicSize.width / imaxe.intrinsicSize.height
            val modifierAjustado = if ( maxWidth.value / maxHeight.value > ratioMapa ) {
                Modifier.fillMaxHeight()
            } else {
                Modifier.fillMaxWidth()
            }

            Image(
                painter = imaxe,
                contentDescription = "Mapa del Mundo",
                contentScale = ContentScale.Fit,
                modifier = modifierAjustado
                    .aspectRatio( ratioMapa )
                    .pointerInput( selectorRexion ) {

                        detectTapGestures { offsetToque ->

                            val tester = selectorRexion ?: return@detectTapGestures

                            if ( size.width > 0 && size.height > 0 ) {

                                val puntoNormalizado = Offset(
                                    x = offsetToque.x / size.width,
                                    y = offsetToque.y / size.height
                                )

                                rexionPulsada = tester.collerRexion( puntoNormalizado ) ?: "Ningunha"

                                //Aquí faríase o cambio se a rexión fose diferente a Ningunha
                                Log.d( "EHHH", rexionPulsada )

                            }

                        }

                    }
            )
        }

        //Isto borraríase porque xa non tería sentido máis adiante
        Box(
            modifier = Modifier
                .fillMaxSize()
                .height(80.dp)
                .padding(15.dp)
                .align(Alignment.CenterStart),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = buildAnnotatedString {

                    append( "Rexión:\n" )

                    withStyle( style = SpanStyle( fontWeight = FontWeight.Bold ) ) {
                        append( rexionPulsada )
                    }

                },
                style = MaterialTheme.typography.bodyLarge
            )
        }

    }

}