package org.aprengal.lendasnubeiras.ui.tema

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import org.aprengal.lendasnubeiras.ui.R

/**
 * Define as familias tipográficas e os estilos de texto empregados na aplicación.
 *
 * Configura as fontes Expletus Sans e Ubuntu e establece os estilos de
 * [Typography] de Material 3 utilizados polos compoñentes da interface.
 */
internal object Tipografias {

    /**
     * Familia Expletus Sans, empregada nas cabeceiras e nos títulos.
     *
     * Utiliza o mesmo ficheiro de fonte variable para os pesos normal e negriña.
     */
    private val expletusSans = FontFamily(
        Font( R.font.expletus_sans_variable, FontWeight.Normal ),
        Font( R.font.expletus_sans_variable, FontWeight.Bold )
    )

    /**
     * Familia Ubuntu, empregada nos textos do corpo e nas etiquetas.
     *
     * Dispón de ficheiros independentes para os estilos normal, negriña e cursiva.
     */
    private val ubuntu = FontFamily(
        Font( R.font.ubuntu_regular, FontWeight.Normal ),
        Font( R.font.ubuntu_bold, FontWeight.Bold ),
        Font( R.font.ubuntu_italic, style = FontStyle.Italic )
    )

    /**
     * Configura os estilos tipográficos da aplicación mediante [Typography] de Material 3.
     *
     * - Cabeceiras e títulos: Expletus Sans en negriña, con tamaños de 14 sp a 26 sp.
     * - Texto do corpo: Ubuntu en peso normal, con tamaños de 12 sp e 18 sp.
     * - Etiquetas: Ubuntu en negriña, con tamaños de 11 sp a 16 sp.
     *
     * Os estilos establecen tamén a altura de liña e, nos títulos, o espazamento
     * entre caracteres.
     */
    private val tipografias = Typography(
        // Cabeceiras
        headlineLarge = TextStyle(
            fontFamily = expletusSans,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            lineHeight = 40.sp,
            letterSpacing = (-0.0415625).em
        ),
        headlineMedium = TextStyle(
            fontFamily = expletusSans,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp,
            lineHeight = 35.sp,
            letterSpacing = (-0.0415625).em
        ),
        headlineSmall = TextStyle(
            fontFamily = expletusSans,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            lineHeight = 34.sp,
            letterSpacing = (-0.0415625).em
        ),

        // Títulos de seccións e compoñentes
        titleLarge = TextStyle(
            fontFamily = expletusSans,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = (-0.0415625).em
        ),
        titleMedium = TextStyle(
            fontFamily = expletusSans,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = (-0.0415625).em
        ),
        titleSmall = TextStyle(
            fontFamily = expletusSans,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = (-0.0415625).em
        ),

        // Texto de corpo
        bodyLarge = TextStyle(
            fontFamily = ubuntu,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            lineHeight = 26.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = ubuntu,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp
        ),
        bodySmall = TextStyle(
            fontFamily = ubuntu,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp
        ),

        // Texto de botóns e etiquetas
        labelLarge = TextStyle(
            fontFamily = ubuntu,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            lineHeight = 22.sp
        ),
        labelMedium = TextStyle(
            fontFamily = ubuntu,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            lineHeight = 16.sp
        ),
        labelSmall = TextStyle(
            fontFamily = ubuntu,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            lineHeight = 16.sp
        )
    )

    /**
     * Obtén a configuración tipográfica da aplicación.
     *
     * @return Configuración de [Typography] utilizada polo tema da aplicación.
     */
    fun collerTipografias(): Typography {
        return tipografias
    }

}