package com.aprengal.lendasnubeiras.ui.tema

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import com.aprengal.lendasnubeiras.data.configuracion.Axustes.collerOpcion
import com.aprengal.lendasnubeiras.data.configuracion.Axustes.gardarOpcion
import androidx.compose.material3.Typography
import com.aprengal.lendasnubeiras.data.configuracion.Opcion
import com.aprengal.lendasnubeiras.ui.R
import com.aprengal.lendasnubeiras.ui.tema.Tema.temaActual

// ============================================================
// 1 Tipografías
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

    // Títulos de secciones principais e compoñentes importantes
    titleLarge = TextStyle(
        fontFamily = ExpletusSans,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = ( -0.0415625 ).em
    ),
    // Títulos de componentes
    titleMedium = TextStyle(
        fontFamily = ExpletusSans,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = ( -0.0415625 ).em
    ),
    // Títulos pequenos e secundarios
    titleSmall = TextStyle(
        fontFamily = ExpletusSans,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = ( -0.0415625 ).em
    ),

    // Parágrafo (corpo de texto normal)
    bodyLarge = TextStyle(
        fontFamily = Ubuntu,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 26.sp
    ),
    // Parágrafo (antes tiña negriña, pero aplicarase no propio compoñente)
    bodyMedium = TextStyle(
        fontFamily = Ubuntu,
        fontWeight = FontWeight.Normal,
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
    ),
    // Etiquetas de controis compactos, tabs e chips
    labelMedium = TextStyle(
        fontFamily = Ubuntu,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    // Etiquetas auxiliares e de tamaño reducido
    labelSmall = TextStyle(
        fontFamily = Ubuntu,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 16.sp
    )
)

// ============================================================
// 2. Formas — usadas en Surface/Card/Button/etc.
// ============================================================

private val Bordos = Shapes(
    extraSmall = RoundedCornerShape( 4.dp ),
    small = RoundedCornerShape( 6.dp ),
    medium = RoundedCornerShape( 8.dp ),   // botóns principal/secundario
    large = RoundedCornerShape( 12.dp ),
    extraLarge = RoundedCornerShape( 50.dp )
)

// ============================================================
// 3. Cores
// ============================================================

private val FondoEscuro = Color( 0xFF00093D )
private val TextoEscuro = Color( 0xFF000000 )

private val FondoClaro = Color( 0xFFF4F3F4 )//Color( 0xFFCCCCCC )
private val TextoClaro = Color( 0xFFFFFFFF )

//private val CorLogo = Color( 0xFF3BFFFF )

//private val CorErro = Color( 0xFFC62828 )

/*

//Usanse nos elementos directamente
private val CorExito = Color( 0xFF2E7D32 )
*/

// ============================================================
// Tema — ColorScheme claro/escuro + composable TemaNubeiro
// ============================================================

private val PaletaClara = lightColorScheme(

    // Capa 0: fondo general de la app (Surface raíz, Scaffold)
    background = FondoClaro,
    onBackground = TextoEscuro,

    // Capa 1: elementos elevados
    // ListItem, OutlinedCard
    surface = FondoEscuro.copy( alpha = 0.1f ).compositeOver( FondoClaro ),
    // texto sobre surface
    onSurface = TextoEscuro,
    // OutlinedTextField fondo
    surfaceVariant = FondoClaro,
    // ListItem leading/supporting, borde TextField, item no seleccionado del BottomBar
    onSurfaceVariant = TextoEscuro,
    // NavigationBar (BottomBar) fondo
    surfaceContainer = FondoEscuro.copy( alpha = 0.1f ).compositeOver( FondoClaro ),
    // AlertDialog fondo
    surfaceContainerHigh = FondoEscuro.copy( alpha = 0.08f ).compositeOver( FondoClaro ),
    // Card fondo
    surfaceContainerHighest = FondoEscuro.copy( alpha = 0.1f ).compositeOver( FondoClaro ),

    // Capa 2: contraste fuerte
    // Button relleno fondo
    primary = FondoEscuro,
    // texto/icono del Button
    onPrimary = TextoClaro,
    // FAB fondo
    primaryContainer = FondoEscuro,
    // icono/texto del FAB
    onPrimaryContainer = TextoClaro,
    // indicador (píldora) del item seleccionado en BottomBar
    secondaryContainer = FondoEscuro,
    // icono/texto del item seleccionado en BottomBar
    onSecondaryContainer = TextoClaro,

    // bordes (OutlinedButton, OutlinedTextField)
    outline = FondoEscuro,
    // Divider, líneas separadoras
    outlineVariant = TextoEscuro

)

private val PaletaEscura = darkColorScheme(

    // Capa 0
    background = FondoEscuro,
    onBackground = TextoClaro,

    // Capa 1
    // ListItem, OutlinedCard
    surface = FondoClaro.copy( alpha = 0.1f ).compositeOver( FondoEscuro ),
    onSurface = TextoClaro,
    // OutlinedTextField fondo
    surfaceVariant = FondoEscuro,
    // ListItem leading/supporting, borde TextField, item no seleccionado del BottomBar
    onSurfaceVariant = TextoClaro,
    // NavigationBar (BottomBar) fondo
    surfaceContainer = FondoClaro.copy( alpha = 0.1f ).compositeOver( FondoEscuro ),
    // AlertDialog fondo
    surfaceContainerHigh = FondoClaro.copy( alpha = 0.08f ).compositeOver( FondoEscuro ),
    // Card
    surfaceContainerHighest = FondoClaro.copy( alpha = 0.1f ).compositeOver( FondoEscuro ),

    // Capa 2
    // Button relleno fondo
    primary = FondoClaro,
    onPrimary = TextoEscuro,
    // FAB fondo
    primaryContainer = FondoClaro,
    onPrimaryContainer = TextoEscuro,
    // indicador seleccionado BottomBar
    secondaryContainer = FondoClaro,
    onSecondaryContainer = TextoEscuro,

    outline = FondoClaro,
    outlineVariant = TextoClaro

)

enum class Variante( val nome: String ) {
    CLARO( "claro" ),
    ESCURO( "escuro" ),
    PREDETERMINADO( "predeterminado" );

    companion object {
        fun buscar( clave: String ): Variante {
            return entries.find { variante -> variante.nome == clave } ?: PREDETERMINADO
        }
    }

}

object Tema {

    internal var temaActual by mutableStateOf( Variante.PREDETERMINADO )
        private set

    private var temaCargado = false

    fun arrancar() {

        if ( temaCargado ) return
        temaCargado = true

        val claveGuardada = collerOpcion( Opcion.Tema )
        temaActual = Variante.buscar( claveGuardada )

    }

    internal suspend fun gardarTema( novoTema: Variante ): Boolean {

        if ( temaActual == novoTema ) return true
        if ( !gardarOpcion( Opcion.Tema, novoTema.nome ) ) return false

        temaActual = novoTema
        return true

    }

}

fun <T> escollerVarianteImaxe( temaEscuro: Boolean, claro: T, escuro: T ): T {

    val imaxe = when ( temaActual ) {
        Variante.CLARO -> claro
        Variante.ESCURO -> escuro
        Variante.PREDETERMINADO -> if ( temaEscuro ) escuro else claro
    }

    return imaxe

}

private fun collerCoresTema( temaEscuro: Boolean ): ColorScheme {

    val esquemaCores = when( temaActual ) {
        Variante.CLARO -> PaletaClara
        Variante.ESCURO -> PaletaEscura
        Variante.PREDETERMINADO -> if ( temaEscuro ) PaletaEscura else PaletaClara
    }

    return esquemaCores

}

@Composable
fun TemaNubeiro( contido: @Composable () -> Unit ) {

    val cores = collerCoresTema( isSystemInDarkTheme() )

    MaterialTheme( cores, Bordos, Tipografias ) {
        contido()
    }

}