/*import androidx.test.core.app.ApplicationProvider
import com.example.lendasnubeiras.R
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import kotlin.math.roundToInt*/

/*@RunWith(RobolectricTestRunner::class)
@Config( sdk = [33], manifest = Config.DEFAULT_MANIFEST_NAME )
@GraphicsMode( GraphicsMode.Mode.NATIVE )
class MapaColorVsPoligonoTest {
    private val colorAContinente: Map<Int, String> = mapOf(
        Color.parseColor("#e8a33d") to "África",
        Color.parseColor("#c1666b") to "Asia",
        Color.parseColor("#6c91c2") to "Europa",
        Color.parseColor("#7fb685") to "Norteamérica",
        Color.parseColor("#e4c05a") to "Sudamérica",
        Color.parseColor("#8e7cc3") to "Oceanía",
        Color.parseColor("#4a6984") to "Antártida"
    )

    fun merdaCores(): Map<Int, String> {
        return colorAContinente
    }

    @Test
    fun comprobacion_areas_mapa_mundial() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val drawable = AppCompatResources.getDrawable(context, R.drawable.world_continents_png)!!
        val regiones: List<RegionPoligono> = regionesMapaMundo()
        val ancho = drawable.intrinsicWidth
        val alto = drawable.intrinsicHeight

        val bitmap = Bitmap.createBitmap(ancho, alto, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.drawFilter = PaintFlagsDrawFilter(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG, 0)
        drawable.setBounds(0, 0, ancho, alto)
        drawable.draw(canvas)

        // Limpieza inicial
        val paletaValida = colorAContinente.keys.toIntArray()
        val pixels = IntArray(ancho * alto)
        bitmap.getPixels(pixels, 0, ancho, 0, 0, ancho, alto)
        for (i in pixels.indices) {
            if (pixels[i] !in paletaValida) { pixels[i] = 0 }
        }
        bitmap.setPixels(pixels, 0, ancho, 0, 0, ancho, alto)

        val fallos = mutableListOf<String>()
        var comprobados = 0
        val archivos = mapOf(
            "Norteamérica" to File("C:\\Users\\Diego\\Desktop\\ops\\fallos_Norteamerica.txt"),
            "Sudámerica" to File("C:\\Users\\Diego\\Desktop\\ops\\fallos_Sudamerica.txt"),
            "Europa" to File("C:\\Users\\Diego\\Desktop\\ops\\fallos_Europa.txt"),
            "Asia" to File("C:\\Users\\Diego\\Desktop\\ops\\fallos_Asia.txt"),
            "África" to File("C:\\Users\\Diego\\Desktop\\ops\\fallos_Africa.txt"),
            "Oceanía" to File("C:\\Users\\Diego\\Desktop\\ops\\fallos_Oceania.txt"),
            "Antártida" to File("C:\\Users\\Diego\\Desktop\\ops\\fallos_Antartida.txt"),
            "Ninguna" to File("C:\\Users\\Diego\\Desktop\\ops\\fallos_Ninguna.txt")
        )
        archivos.values.forEach { it.writeText("") }
        val coresSenAsignar = mutableMapOf<String, MutableSet<Int>>()

        var x = 0f
        while (x < 1f) {
            var y = 0f
            while (y < 1f) {
                val px = (x * ancho).roundToInt().coerceIn(0, ancho - 1)
                val py = (y * alto).roundToInt().coerceIn(0, alto - 1)
                val colorPixel = bitmap.getPixel(px, py)

                val continenteEsperado = colorAContinente[colorPixel] ?: "Ninguna"

                val punto = Offset(x, y)
                val regionEncontrada = regiones.flatMap { region ->
                    region.subRegiones.mapIndexed { index, subRegion -> Triple(region.nombre, index, subRegion) }
                }.firstOrNull { (_, _, subRegion) -> esPuntoEnPoligono(punto, subRegion) }

                val continenteDetectado = regionEncontrada?.first ?: "Ninguna"
                val subRegionDetectada = regionEncontrada?.second ?: -1

                comprobados++

                if (continenteEsperado != "Ninguna" && continenteEsperado != continenteDetectado) {
                    coresSenAsignar.getOrPut(continenteEsperado) { mutableSetOf() }.add(colorPixel)
                    val linea = "($x, $y): esperado=$continenteEsperado, poligono=$continenteDetectado, subRegion=$subRegionDetectada"
                    fallos += linea
                    archivos[continenteDetectado]?.appendText("$linea\n")
                }

                y += 0.001f

            }

            x += 0.001f

        }

        val porcentajeFallo = if (comprobados > 0) fallos.size.toDouble() / comprobados * 100 else 0.0
        println( "Comprobados: $comprobados, fallos: ${fallos.size} (${"%.2f".format(porcentajeFallo)}%)" )

        val archivo = File( "C:\\Users\\Diego\\Desktop\\ops\\colores_int.txt" )
        archivo.writeText(merdaCores().toString() + "\n")
        coresSenAsignar.forEach { (c, col) -> archivo.appendText("Continente: $c\n   ${col.joinToString(", ")}\n\n") }

        assert(porcentajeFallo < 2.0) { "Demasiadas discrepancias: ${fallos.size} de $comprobados" }

    }

}*/