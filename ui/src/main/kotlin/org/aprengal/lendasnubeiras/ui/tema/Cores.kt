package org.aprengal.lendasnubeiras.ui.tema

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver

internal object Cores {

    private val fondoEscuro = Color( 0xFF00093D )
    private val textoEscuro = Color( 0xFF000000 )

    private val fondoClaro = Color( 0xFFF4F3F4 )
    private val textoClaro = Color( 0xFFFFFFFF )

    private val coresClaras = lightColorScheme(

        // Capa 0: fondo general de la app (Surface raíz, Scaffold)
        background = fondoClaro,
        onBackground = textoEscuro,

        // Capa 1: elementos elevados
        // ListItem, OutlinedCard
        surface = fondoEscuro.copy( alpha = 0.1f ).compositeOver( fondoClaro ),
        // texto sobre surface
        onSurface = textoEscuro,
        // OutlinedTextField fondo
        surfaceVariant = fondoClaro,
        // ListItem leading/supporting, borde TextField, item no seleccionado del BottomBar
        onSurfaceVariant = textoEscuro,
        // NavigationBar (BottomBar) fondo
        surfaceContainer = fondoEscuro.copy( alpha = 0.1f ).compositeOver( fondoClaro ),
        // AlertDialog fondo
        surfaceContainerHigh = fondoEscuro.copy( alpha = 0.08f ).compositeOver( fondoClaro ),
        // Card fondo
        surfaceContainerHighest = fondoEscuro.copy( alpha = 0.1f ).compositeOver( fondoClaro ),

        // Capa 2: contraste fuerte
        // Button relleno fondo
        primary = fondoEscuro,
        // texto/icono del Button
        onPrimary = textoClaro,
        // FAB fondo
        primaryContainer = fondoEscuro,
        // icono/texto del FAB
        onPrimaryContainer = textoClaro,
        // indicador (píldora) del item seleccionado en BottomBar
        secondaryContainer = fondoEscuro,
        // icono/texto del item seleccionado en BottomBar
        onSecondaryContainer = textoClaro,

        // bordes (OutlinedButton, OutlinedTextField)
        outline = fondoEscuro,
        // Divider, líneas separadoras
        outlineVariant = textoEscuro

    )

    private val coresEscuras = darkColorScheme(

        // Capa 0
        background = fondoEscuro,
        onBackground = textoClaro,

        // Capa 1
        // ListItem, OutlinedCard
        surface = fondoClaro.copy( alpha = 0.1f ).compositeOver( fondoEscuro ),
        onSurface = textoClaro,
        // OutlinedTextField fondo
        surfaceVariant = fondoEscuro,
        // ListItem leading/supporting, borde TextField, item no seleccionado del BottomBar
        onSurfaceVariant = textoClaro,
        // NavigationBar (BottomBar) fondo
        surfaceContainer = fondoClaro.copy( alpha = 0.1f ).compositeOver( fondoEscuro ),
        // AlertDialog fondo
        surfaceContainerHigh = fondoClaro.copy( alpha = 0.08f ).compositeOver( fondoEscuro ),
        // Card
        surfaceContainerHighest = fondoClaro.copy( alpha = 0.1f ).compositeOver( fondoEscuro ),

        // Capa 2
        // Button relleno fondo
        primary = fondoClaro,
        onPrimary = textoEscuro,
        // FAB fondo
        primaryContainer = fondoClaro,
        onPrimaryContainer = textoEscuro,
        // indicador seleccionado BottomBar
        secondaryContainer = fondoClaro,
        onSecondaryContainer = textoEscuro,

        outline = fondoClaro,
        outlineVariant = textoClaro

    )

    fun collerTemaClaro(): ColorScheme {
        return coresClaras
    }

    fun collerTemaEscuro(): ColorScheme {
        return coresEscuras
    }

}