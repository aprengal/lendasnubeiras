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
 * Tipografías da aplicación.
 *
 * Define as dúas familias de letra empregadas e o conxunto de estilos de texto
 * de Material 3 ([Typography]) que se aplican en toda a interface.
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
     * Familia Ubuntu, empregada nos parágrafos e nas etiquetas.
     *
     * Ten ficheiros propios para o estilo normal, a negriña e a cursiva.
     */
    private val ubuntu = FontFamily(
        Font( R.font.ubuntu_regular, FontWeight.Normal ),
        Font( R.font.ubuntu_bold, FontWeight.Bold ),
        Font( R.font.ubuntu_italic, style = FontStyle.Italic )
    )

    /**
     * Estilos de texto da aplicación.
     *
     * - Cabeceiras e títulos: Expletus Sans en negriña, con tamaños de 14 sp a 26 sp.
     * - Parágrafos: Ubuntu en peso normal, con 18 sp para o texto principal e 12 sp para anotacións.
     * - Etiquetas: Ubuntu en negriña, con tamaños de 11 sp a 16 sp.
     */
    private val tipografias = Typography(
        // Cabeceira 1
        headlineLarge = TextStyle(
            fontFamily = expletusSans,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            lineHeight = 40.sp,
            letterSpacing = (-0.0415625).em
        ),
        // Cabeceira 2
        headlineMedium = TextStyle(
            fontFamily = expletusSans,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp,
            lineHeight = 35.sp,
            letterSpacing = (-0.0415625).em
        ),
        // Cabeceira 3
        headlineSmall = TextStyle(
            fontFamily = expletusSans,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            lineHeight = 34.sp,
            letterSpacing = (-0.0415625).em
        ),

        // Títulos de secciones principais e compoñentes importantes
        titleLarge = TextStyle(
            fontFamily = expletusSans,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = (-0.0415625).em
        ),
        // Títulos de componentes
        titleMedium = TextStyle(
            fontFamily = expletusSans,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = (-0.0415625).em
        ),
        // Títulos pequenos e secundarios
        titleSmall = TextStyle(
            fontFamily = expletusSans,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = (-0.0415625).em
        ),

        // Parágrafo (corpo de texto normal)
        bodyLarge = TextStyle(
            fontFamily = ubuntu,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            lineHeight = 26.sp
        ),
        // Parágrafo (antes tiña negriña, pero aplicarase no propio compoñente)
        bodyMedium = TextStyle(
            fontFamily = ubuntu,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            lineHeight = 26.sp
        ),
        // Anotacións
        bodySmall = TextStyle(
            fontFamily = ubuntu,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp
        ),

        // Texto de botóns / etiquetas
        labelLarge = TextStyle(
            fontFamily = ubuntu,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            lineHeight = 22.sp
        ),
        // Etiquetas de controis compactos, tabs e chips
        labelMedium = TextStyle(
            fontFamily = ubuntu,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            lineHeight = 16.sp
        ),
        // Etiquetas auxiliares e de tamaño reducido
        labelSmall = TextStyle(
            fontFamily = ubuntu,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            lineHeight = 16.sp
        )
    )

    /**
     * Devolve os estilos de texto da aplicación.
     *
     * @return O conxunto de [Typography] que se pasa ao tema da aplicación.
     */
    fun collerTipografias(): Typography {
        return tipografias
    }

}