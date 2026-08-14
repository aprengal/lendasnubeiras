import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.aprengal.lendasnubeiras.R

// 1. Estructura de datos para definir cada región
data class RegionPoligono(
    val nombre: String,
    val subRegiones: List<List<Offset>> // Soporta islas y trozos separados
)

fun regionesMapaMundo(): List<RegionPoligono> {

    val lista = listOf(
        RegionPoligono(
            nombre = "Norteamérica",
            subRegiones = listOf(
                // 1. Masa continental principal
                listOf(
                    Offset(0.10f, 0.10f),
                    Offset(0.375f, 0.10f),
                    Offset(0.35f, 0.20f),
                    Offset(0.33f, 0.29f),
                    Offset(0.32f, 0.36f),
                    Offset(0.30f, 0.38f),
                    Offset(0.27f, 0.41f),
                    Offset(0.25f, 0.39f),
                    Offset(0.22f, 0.36f),
                    Offset(0.18f, 0.36f),
                    Offset(0.11f, 0.15f),
                    Offset(0.10f, 0.10f)
                ),
                // 2. Islas árticas de Canadá
                listOf(
                    Offset(0.15f, 0.00f),
                    Offset(0.375f, 0.00f),
                    Offset(0.375f, 0.10f),
                    Offset(0.15f, 0.10f),
                    Offset(0.15f, 0.00f)
                ),
                // 3. Islas Hawái
                listOf(
                    Offset(0.02f, 0.40f),
                    Offset(0.03f, 0.40f),
                    Offset(0.03f, 0.44f),
                    Offset(0.02f, 0.44f),
                    Offset(0.02f, 0.40f)
                )
            )
        ),
        RegionPoligono(
            // ⚠️ CORREGIDO: el nombre estaba mal escrito ("Sudámerica" con tilde en la á).
            // El mapa de colores usa "Sudamérica" (tilde en la é). Esta discrepancia de string
            // causaba ~24.850 falsos positivos por sí sola.
            nombre = "Sudamérica",
            subRegiones = listOf(
                // 1. Continente principal
                listOf(
                    Offset(0.27f, 0.41f), // Conexión con Panamá
                    Offset(0.31f, 0.39f), // Norte de Venezuela
                    Offset(0.39f, 0.49f), // Extremo este de Brasil
                    Offset(0.35f, 0.81f), // Punta sur (Tierra del Fuego)
                    Offset(0.29f, 0.81f), // Suroeste de Chile
                    Offset(0.26f, 0.65f), // Costa de Perú / Chile
                    Offset(0.25f, 0.44f), // Costa de Ecuador / Colombia
                    Offset(0.27f, 0.41f)  // Cierre
                ),
                // 2. Islas Malvinas
                listOf(
                    Offset(0.31f, 0.77f), Offset(0.35f, 0.77f),
                    Offset(0.35f, 0.81f), Offset(0.31f, 0.81f),
                    Offset(0.31f, 0.77f)  // Cierre
                )
            )
        ),
        RegionPoligono(
            nombre = "Europa",
            subRegiones = listOf(
                // 1. Bloque continental europeo
                // ⚠️ REVERTIDO: extender el techo a y=0.00 cubría muchísimo mar del Atlántico
                // norte/Ártico que no es costa real, empeorando el resultado neto (+9.511
                // fallos). Se vuelve a y=0.15 (el hueco de Escandinavia/UK sigue existiendo,
                // pero es preferible a inflar el error por el lado del mar).
                listOf(
                    Offset(0.48f, 0.15f),
                    Offset(0.617f, 0.15f),
                    Offset(0.617f, 0.16f),
                    Offset(0.58f, 0.16f),
                    Offset(0.58f, 0.245f),
                    Offset(0.48f, 0.245f),
                    Offset(0.48f, 0.15f)
                ),
                // 2. Islandia
                listOf(
                    Offset(0.44f, 0.10f),
                    Offset(0.47f, 0.10f),
                    Offset(0.47f, 0.14f),
                    Offset(0.44f, 0.14f),
                    Offset(0.44f, 0.10f)
                ),
                // 3. Islas Canarias
                listOf(
                    Offset(0.43f, 0.42f),
                    Offset(0.45f, 0.42f),
                    Offset(0.45f, 0.45f),
                    Offset(0.43f, 0.45f),
                    Offset(0.43f, 0.42f)
                ),
                // 4. Groenlandia
                listOf(
                    Offset(0.35f, 0.00f),
                    Offset(0.42f, 0.00f),
                    Offset(0.42f, 0.08f),
                    Offset(0.38f, 0.10f),
                    Offset(0.35f, 0.05f),
                    Offset(0.35f, 0.00f)
                )
            )
        ),
        RegionPoligono(
            nombre = "Asia",
            subRegiones = listOf(
                // 1. Asia Continental — sin cambios, ya empieza en x=0.617 coincidiendo
                // con el nuevo borde este de Europa.
                listOf(
                    Offset(0.617f, 0.01f),
                    Offset(0.89f, 0.01f),
                    Offset(0.89f, 0.35f),
                    Offset(0.78f, 0.38f),
                    Offset(0.71f, 0.43f),
                    Offset(0.60f, 0.38f),
                    Offset(0.58f, 0.30f),
                    Offset(0.58f, 0.16f),
                    Offset(0.617f, 0.16f),
                    Offset(0.617f, 0.01f)
                ),
                // 2. Japón
                listOf(
                    Offset(0.83f, 0.18f), Offset(0.88f, 0.18f),
                    Offset(0.88f, 0.28f), Offset(0.83f, 0.28f),
                    Offset(0.83f, 0.18f)
                ),
                // 3. Sudeste Asiático (Indonesia / Filipinas)
                listOf(
                    Offset(0.76f, 0.35f), Offset(0.89f, 0.35f),
                    Offset(0.89f, 0.52f), Offset(0.76f, 0.52f),
                    Offset(0.76f, 0.35f)
                )
            )
        ),
        RegionPoligono(
            nombre = "África",
            subRegiones = listOf(
                // 1. Continente principal
                // ⚠️ REVERTIDO parcialmente: subir el techo a 0.245 y ensanchar la base sur
                // cubría más mar del que costa ganaba (+8.929 fallos netos). Se mantiene solo
                // el bulto del Cuerno de África (el más ajustado, no tan costoso en mar) y se
                // revierte el techo/base a los valores anteriores.
                listOf(
                    Offset(0.420f, 0.280f),
                    Offset(0.550f, 0.280f),
                    Offset(0.620f, 0.350f),
                    Offset(0.640f, 0.420f), // bulto Cuerno de África (se mantiene)
                    Offset(0.580f, 0.650f),
                    Offset(0.480f, 0.600f),
                    Offset(0.380f, 0.350f),
                    Offset(0.420f, 0.280f)
                ),
                // 2. Madagascar
                listOf(
                    Offset(0.630f, 0.550f),
                    Offset(0.660f, 0.550f),
                    Offset(0.650f, 0.650f),
                    Offset(0.620f, 0.650f),
                    Offset(0.630f, 0.550f)
                )
            )
        ),
        RegionPoligono(
            nombre = "Oceanía",
            subRegiones = listOf(
                // 1. Australia
                listOf(
                    Offset(0.75f, 0.53f), Offset(0.89f, 0.53f),
                    Offset(0.89f, 0.71f), Offset(0.75f, 0.71f),
                    Offset(0.75f, 0.53f)
                ),
                // 2. Nueva Zelanda
                listOf(
                    Offset(0.90f, 0.68f), Offset(0.96f, 0.68f),
                    Offset(0.96f, 0.78f), Offset(0.90f, 0.78f),
                    Offset(0.90f, 0.68f)
                )
                // ⚠️ ELIMINADO: la subregión "Melanesia" (rectángulo x[0.85,1.0] y[0.45,0.72])
                // cubría un hueco real de islas, pero al ser un rectángulo grande sobre el
                // Pacífico, cubría muchísimo más mar vacío del que costa ganaba (+25.353
                // fallos netos, el mayor causante del retroceso de esta ronda). Se retira hasta
                // tener una forma más ajustada a las islas reales.
            )
        ),
        RegionPoligono(
            nombre = "Antártida",
            subRegiones = listOf(
                listOf(
                    Offset(0.01f, 0.90f),
                    Offset(0.25f, 0.90f),
                    Offset(0.30f, 0.83f),
                    Offset(0.34f, 0.83f),
                    Offset(0.36f, 0.92f),
                    Offset(0.45f, 0.86f),
                    Offset(0.85f, 0.85f),
                    Offset(0.99f, 0.88f),
                    Offset(0.95f, 0.95f),
                    Offset(0.50f, 0.96f),
                    Offset(0.10f, 0.96f),
                    Offset(0.01f, 0.90f)
                )
            )
        )
    )

    return lista

}


@Preview(showBackground = true)
@Composable
fun MapaConNombres() {

    var regionPulsada by remember { mutableStateOf( "Ninguna" ) }
    var tamanoMapa by remember { mutableStateOf( androidx.compose.ui.geometry.Size.Zero ) }
    val regiones: List<RegionPoligono> = regionesMapaMundo()

    val mapaPainter = painterResource( id = R.drawable.world_continents )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align( Alignment.Center ) // Centrado en el espacio disponible
                .clipToBounds()
                .onGloballyPositioned { coordinates -> tamanoMapa = coordinates.size.toSize() }
                .pointerInput(Unit) {
                    detectTapGestures { offsetToque ->
                        if (tamanoMapa.width > 0 && tamanoMapa.height > 0) {
                            val puntoNormalizado = Offset(offsetToque.x / tamanoMapa.width, offsetToque.y / tamanoMapa.height)
                            val regionDetectada = regiones.firstOrNull { region ->
                                region.subRegiones.any { subPoligono -> esPuntoEnPoligono(puntoNormalizado, subPoligono) }
                            }
                            regionPulsada = regionDetectada?.nombre ?: "Ninguna"
                        }
                    }
                }
        ) {
            Image(
                painter = mapaPainter,
                contentDescription = "Mapa",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .height(80.dp)
                .padding(15.dp)
                .align( Alignment.CenterStart ),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "Región:\n$regionPulsada",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }

}

fun esPuntoEnPoligono(punto: Offset, poligono: List<Offset>): Boolean {

    var intersecta = false
    val x = punto.x
    val y = punto.y
    var j = poligono.size - 1

    for (i in poligono.indices) {
        val xi = poligono[i].x
        val yi = poligono[i].y
        val xj = poligono[j].x
        val yj = poligono[j].y

        // Comprueba si el rayo horizontal cruza el segmento verticalmente
        val cruzaVertical = (yi > y) != (yj > y)

        if (cruzaVertical) {
            // Fórmula corregida de interpolación lineal para evitar inversiones de signo en el eje X
            val interseccionX = xi + (y - yi) * (xj - xi) / (yj - yi)
            if (x < interseccionX) {
                intersecta = !intersecta
            }
        }
        j = i
    }

    return intersecta

}