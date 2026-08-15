package com.aprengal.lendasnubeiras.ui.tema

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.aprengal.lendasnubeiras.configuracion.Axustes.collerOpcion
import com.aprengal.lendasnubeiras.configuracion.Axustes.gardarOpcion
import com.aprengal.lendasnubeiras.configuracion.Opcion.Tema
import com.aprengal.lendasnubeiras.R
import com.aprengal.lendasnubeiras.ui.tema.Tema.collerCoresTema

//Constantes relacionadas co tema que non se poden usar directamente noutros arquivos

// ============================================================
// 2.1 Tipografías
// ============================================================

private val ExpletusSans = FontFamily(
    Font( R.font.expletus_sans_variable, FontWeight.Normal ),
    Font( R.font.expletus_sans_variable, FontWeight.Bold )
)

private val Ubuntu = FontFamily(
    Font( R.font.ubuntu_regular, FontWeight.Normal ),
    Font( R.font.ubuntu_bold, FontWeight.Bold ),
    Font( R.font.ubuntu_italic, style = FontStyle.Italic )
)

private val Tipografias = Typography(
    // Cabeceira 1
    headlineLarge = TextStyle(
        fontFamily = ExpletusSans,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 40.sp,
        letterSpacing = ( -0.0415625 ).em
    ),
    // Cabeceira 2
    headlineMedium = TextStyle(
        fontFamily = ExpletusSans,
        fontWeight = FontWeight.Bold,
        fontSize = 25.sp,
        lineHeight = 35.sp,
        letterSpacing = ( -0.0415625 ).em
    ),
    // Cabeceira 3
    headlineSmall = TextStyle(
        fontFamily = ExpletusSans,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 34.sp,
        letterSpacing = ( -0.0415625 ).em
    ),
    // Parágrafo (corpo de texto normal)
    bodyLarge = TextStyle(
        fontFamily = Ubuntu,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 26.sp
    ),
    // Parágrafo negriña
    bodyMedium = TextStyle(
        fontFamily = Ubuntu,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 26.sp
    ),
    // Anotacións
    bodySmall = TextStyle(
        fontFamily = Ubuntu,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    // Texto de botóns / etiquetas
    labelLarge = TextStyle(
        fontFamily = Ubuntu,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    )
)

// ============================================================
// Formas — usadas por defecto en Surface/Card/Button/etc.
// ============================================================

private val Bordos = Shapes(
    extraSmall = RoundedCornerShape( 4.dp ),
    small = RoundedCornerShape( 6.dp ),
    medium = RoundedCornerShape( 8.dp ),   // botóns principal/secundario
    large = RoundedCornerShape( 12.dp ),
    extraLarge = RoundedCornerShape( 50.dp )  // botón ghost (circular)
)

// ============================================================
// 2.2 Cores
// ============================================================

private val FondoEscuro = Color( 0xFF00093D )
private val TextoEscuro = Color( 0xFF000000 )

private val FondoClaro = Color( 0xFFF4F3F4 )//Color( 0xFFCCCCCC )
private val TextoClaro = Color( 0xFFFFFFFF )

private val CorLogo = Color( 0xFF3BFFFF )

private val CorErro = Color( 0xFFC62828 )

/*

//Usanse nos elementos directamente
private val CorExito = Color( 0xFF2E7D32 )

private val CorGhost = Color( 0xFFB6F29A )
*/


// ============================================================
// Tema — ColorScheme claro/escuro + composable TemaNubeiro
// ============================================================

object Tema {

    private val PaletaClara = lightColorScheme(
        primary = FondoEscuro,
        onPrimary = TextoClaro,
        secondary = CorLogo,
        onSecondary = TextoEscuro,
        background = FondoClaro,
        onBackground = TextoEscuro,
        surface = FondoClaro,
        onSurface = TextoEscuro,
        surfaceContainer = FondoEscuro.copy( alpha = 0.3f ).compositeOver( FondoClaro ),
        error = CorErro,
        onError = TextoClaro,
        outline = FondoEscuro
    )

    private val PaletaEscura = darkColorScheme(
        primary = FondoClaro,
        onPrimary = TextoEscuro,
        secondary = CorLogo,
        onSecondary = TextoEscuro,
        background = FondoEscuro,
        onBackground = TextoClaro,
        surface = FondoEscuro,
        onSurface = TextoClaro,
        surfaceContainer = FondoClaro.copy( alpha = 0.6f ).compositeOver( FondoEscuro ),
        error = CorErro,
        onError = TextoClaro,
        outline = CorLogo
    )

    // Valor inicial por defecto para que la app no pete al arrancar mientras lee el disco
    var temaActual: MutableState<Variante> = mutableStateOf( Variante.PREDETERMINADO )
        private set

    private var temaCargado = false

    enum class Variante( val clave: String ) {
        CLARO( "tema_claro" ),
        ESCURO( "tema_escuro" ),
        PREDETERMINADO( "tema_predeterminado" );

        companion object {
            fun buscar( clave: String ): Variante {
                return entries.find { variante -> variante.clave == clave } ?: PREDETERMINADO
            }
        }

    }

    fun arrancar() {

        require( !temaCargado ) { "A aplicación xa estaba inicializada!" }
        temaCargado = true

        val claveGuardada = collerOpcion( Tema )
        temaActual.value = Variante.buscar( claveGuardada )

    }

    suspend fun gardarTema( novoTema: Variante ) {
        gardarOpcion( Tema, novoTema.clave )
        temaActual.value = novoTema
    }

    fun collerCoresTema( temaEscuro: Boolean ): ColorScheme {

        val esquemaCores = when( temaActual.value ) {
            Variante.CLARO -> PaletaClara
            Variante.ESCURO -> PaletaEscura
            Variante.PREDETERMINADO -> if ( temaEscuro ) PaletaEscura else PaletaClara
        }

        return esquemaCores

    }

}

@Composable
fun TemaNubeiro(
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colorScheme = collerCoresTema( isSystemInDarkTheme() ),
        typography = Tipografias,
        shapes = Bordos,
        content = content
    )

}