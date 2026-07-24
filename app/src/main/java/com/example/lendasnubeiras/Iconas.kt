package com.example.lendasnubeiras

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.ui.platform.LocalDensity


data class ItemNavegacion(
    val index: Int,
    val etiqueta: String,
    val icono: @Composable () -> Unit
)

object Iconas {

    private val fontFamily = FontFamily(
        Font(R.font.ubuntu_iconas_nerd, FontWeight.Bold)
    )

    // Códigos privados: inaccesibles desde fuera
    private const val INICIO      = "\uDB80\uDEDC" // U+F02DC
    private const val PERFIL      = "\uDB80\uDC04" // U+F0004

    private const val BUSCAR      = "\uDB80\uDF49" // U+F0349
    private const val AXUSTES     = "\uDB82\uDCBB" // U+F08BB
    private const val IDIOMA      = "\uDB81\uDD9F" // U+F059F
    private const val MAPA        = "\uDB80\uDF4E" // U+F034E

    private const val ATRAS       = "\uDB83\uDCDE" // U+F0CDE
    private const val MENU        = "\uDB80\uDF5C" // U+F035C
    private const val ENGADIR     = "\uDB81\uDC15" // U+F0415

    private const val OLLOABERTO = "\uDB80\uDE08" // U+F0208

    private const val OLLOPECHADO  = "\uDB80\uDE09" // U+F0209


    private val iconasActividades = setOf(
        INICIO, PERFIL, BUSCAR, AXUSTES, IDIOMA, ENGADIR,
        "\uDB85\uDFF3", "\uDB82\uDF59", "\uDB80\uDC1D", "\uDB83\uDDAC", "\uDB85\uDC77",
        "\uDB82\uDC9A", "\uDB83\uDE7C", "\uDB80\uDC9E", "\uDB84\uDD1F", "\uDB80\uDCA3",
        "\uDB85\uDDC6", "\uDB80\uDCAD", "\uDB82\uDF63", "\uDB80\uDCD3", "\uDB81\uDE17",
        "\uDB80\uDCE4", "\uDB80\uDCE6", "\uDB80\uDCE7", "\uDB80\uDCEB", "\uDB82\uDF6A",
        "\uDB83\uDEDD", "\uDB86\uDCAA", "\uDB83\uDDAB", "\uDB80\uDD0B", "\uDB80\uDD1A",
        "\uDB80\uDD1B", "\uDB80\uDD1E", "\uDB82\uDC57", "\uDB82\uDC5A", "\uDB82\uDC16",
        "\uDB80\uDD76", "\uDB80\uDD8B", "\uDB82\uDE43", "\uDB86\uDCB4", "\uDB84\uDC23",
        "\uDB81\uDFC2", "\uDB86\uDC1F", "\uDB83\uDD02", "\uDB80\uDDE7", "\uDB83\uDC71",
        "\uDB83\uDC76", "\uDB80\uDE04", "\uDB81\uDE43", "\uDB84\uDC77", "\uDB83\uDEF2",
        "\uDB80\uDE3A", "\uDB83\uDD08", "\uDB80\uDE5B", "\uDB85\uDC1F", "\uDB81\uDDF2",
        "\uDB86\uDC4B", "\uDB86\uDC36", "\uDB86\uDC97", "\uDB84\uDC46", "\uDB84\uDC47",
        "\uDB80\uDE97", "\uDB80\uDEA0", "\uDB80\uDEA1", "\uDB81\uDF71", "\uDB84\uDF9F",
        "\uDB86\uDC21", "\uDB80\uDECB", "\uDB84\uDF4A", "\uDB83\uDEF9", "\uDB80\uDED1",
        "\uDB82\uDEC2", "\uDB82\uDE58", "\uDB85\uDDBF", "\uDB80\uDEE1", "\uDB85\uDD81",
        "\uDB80\uDEE7", "\uDB81\uDE49", "\uDB81\uDE4D", "\uDB84\uDDE9", "\uDB84\uDF8D",
        "\uDB86\uDD81", "\uDB84\uDC4F", "\uDB80\uDF0C", "\uDB85\uDD75", "\uDB82\uDC2D",
        "\uDB80\uDF2A", "\uDB86\uDDE3", "\uDB85\uDC0B", "\uDB83\uDFC6", "\uDB86\uDC44",
        "\uDB82\uDE01", "\uDB80\uDF70", "\uDB83\uDC99", "\uDB83\uDF65", "\uDB81\uDF5A",
        "\uDB81\uDF74", "\uDB84\uDF93", "\uDB82\uDC2E", "\uDB83\uDDE0", "\uDB82\uDD91",
        "\uDB84\uDC55", "\uDB81\uDE02", "\uDB81\uDE7D", "\uDB81\uDC05", "\uDB82\uDE08",
        "\uDB81\uDC09", "\uDB86\uDE61", "\uDB85\uDE9D", "\uDB85\uDE9F", "\uDB85\uDEA1",
        "\uDB85\uDEA3", "\uDB85\uDF19", "\uDB85\uDEA6", "\uDB85\uDEA5", "\uDB81\uDEA9",
        "\uDB85\uDCDE", "\uDB86\uDEEF", "\uDB83\uDEC8", "\uDB81\uDE09", "\uDB81\uDC74",
        "\uDB86\uDD9A", "\uDB83\uDD9A", "\uDB82\uDC33", "\uDB85\uDD0E", "\uDB81\uDF17",
        "\uDB81\uDF1F", "\uDB82\uDDA5", "\uDB81\uDCDA", "\uDB81\uDCE0", "\uDB86\uDDA5",
        "\uDB81\uDCE5", "\uDB81\uDD2C", "\uDB81\uDD31", "\uDB81\uDD3B", "\uDB84\uDC96",
        "\uDB86\uDE81", "\uDB85\uDF1B", "\uDB84\uDCC5", "\uDB81\uDE0F", "\uDB85\uDC81",
        "\uDB81\uDF8D", "\uDB81\uDD90", "\uDB82\uDC98", "\uDB81\uDD93", "\uDB83\uDF31",
        "\uDB81\uDD96", "\uDB81\uDD97", "\uDB83\uDF36", "\uDB81\uDD9D", "\uDB81\uDDA8",
        "\u0026", "\u0041", "\u0042", "\u0043", "\u0044", "\u0038", "\u00A1", "\u0021",
        "\u0024", "\u0045", "\u0035", "\u0034", "\u0046", "\u0047", "\u004B", "\u004C",
        "\u0048", "\u0049", "\u004A", "\u004D", "\u0039", "\u00D1", "\u004E", "\u26A1",
        "\u00BD", "\u00BC", "\u0031", "\u004F", "\u0050", "\u0051", "\u0052", "\u0037",
        "\u00BF", "\u003F", "\u0036", "\u0053", "\u00BE", "\u0033", "\u0032", "\u0054",
        "\u0055", "\u0056", "\u0057", "\u0058", "\u0059", "\u0030", "\u005A"
    )

    // Función interna reutilizable: crea el Text con la fuente ya aplicada
    @Composable
    private fun Icona( codigo: String, modifier: Modifier = Modifier, color: Color = LocalContentColor.current, tamanio: TextUnit = 24.sp ) {
        Text(
            text = codigo,
            fontFamily = fontFamily,
            color = color,
            fontSize = tamanio,
            modifier = modifier
        )
    }

    // Funciones públicas: devuelven un Composable, no un String
    @Composable
    fun Inicio(modifier: Modifier = Modifier) = Icona(INICIO, modifier )

    @Composable
    fun Perfil(modifier: Modifier = Modifier) = Icona(PERFIL, modifier )

    @Composable
    fun Buscar(modifier: Modifier = Modifier) = Icona(BUSCAR, modifier )

    @Composable
    fun Axustes(modifier: Modifier = Modifier) = Icona(AXUSTES, modifier )

    @Composable
    fun Idioma(modifier: Modifier = Modifier) = Icona(IDIOMA, modifier )

    @Composable
    fun Mapa(modifier: Modifier = Modifier) = Icona(MAPA, modifier )

    @Composable
    fun Atras(modifier: Modifier = Modifier) = Icona(ATRAS, modifier )

    @Composable
    fun Menu(modifier: Modifier = Modifier) = Icona(MENU, modifier )

    @Composable
    fun Engadir(modifier: Modifier = Modifier) = Icona(ENGADIR, modifier )

    @Composable
    fun OlloAberto(modifier: Modifier = Modifier) = Icona(OLLOABERTO, modifier )

    @Composable
    fun OlloPechado(modifier: Modifier = Modifier) = Icona(OLLOPECHADO, modifier )

    fun lanzarDados( cantidad: Int ): List<String> {
        return iconasActividades.shuffled().take( cantidad )
    }

    // Exponemos el set de solo lectura para poder iterarlo desde fuera
    fun listarIconasActividades(): Set<String> = iconasActividades

    @Composable
    fun PantallaPruebaIconas( textoIcona: String ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .aspectRatio( 1f )
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Text(
                text = textoIcona,
                fontFamily = fontFamily,
                fontSize = 36.sp
            )
        }

    }

    @Composable
    fun CasillaIcono(
        icono: String,
        modifier: Modifier = Modifier
    ) {

        BoxWithConstraints(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .aspectRatio( 1f )
                .background(
                    color = MaterialTheme.colorScheme.secondary.copy ( alpha = 0.15f ),
                    shape = RoundedCornerShape( 8.dp )
                )
        ) {

            Text(
                text = icono,
                fontFamily = fontFamily,
                fontSize = with( LocalDensity.current ) { ( maxWidth * 0.625f ).toSp() },
                color = LocalContentColor.current
            )

        }

    }

}

//@Composable
/*fun PantallaResultadoDados(resultado: List<String>) {

    FlowRow(
        verticalArrangement = Arrangement.spacedBy( 8.dp )
    ) {
        resultado.forEach { icono ->
            Iconas.CasillaIcono( icono )
        }
    }

}*/

