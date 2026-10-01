package org.aprengal.lendasnubeiras.ui.tema

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver

/**
 * Cores da aplicación.
 *
 * Define as paletas dos temas claro e escuro como esquemas de cor de
 * Material 3 ([ColorScheme]). Ambos parten de só catro cores base, e as
 * superficies obtéñense mesturando o fondo oposto cunha opacidade baixa.
 */
internal object Cores {

    /** Fondo do tema escuro: azul moi escuro. Tamén se usa como cor de realce no tema claro. */
    private val fondoEscuro = Color( 0xFF00093D )

    /** Cor do texto sobre fondos claros: negro. */
    private val textoEscuro = Color( 0xFF000000 )

    /** Fondo do tema claro: gris moi claro. Tamén se usa como cor de realce no tema escuro. */
    private val fondoClaro = Color( 0xFFF4F3F4 )

    /** Cor do texto sobre fondos escuros: branco. */
    private val textoClaro = Color( 0xFFFFFFFF )

    /**
     * Esquema de cores do tema claro.
     *
     * Organízase en tres capas: o fondo xeral, os elementos elevados
     * (superficies) e os elementos de contraste forte (botóns, indicadores
     * e bordos). Cada propiedade indica nun comentario o compoñente ao que se aplica.
     */
    private val coresClaras = lightColorScheme(

        // Capa 0: fondo xeral da app (Surface raíz, Scaffold)
        background = fondoClaro,
        onBackground = textoEscuro,

        // Capa 1: elementos elevados
        // ListItem, OutlinedCard
        surface = fondoEscuro.copy( alpha = 0.1f ).compositeOver( fondoClaro ),
        // texto sobre surface
        onSurface = textoEscuro,
        // fondo do OutlinedTextField
        surfaceVariant = fondoClaro,
        // ListItem leading/supporting, bordo do TextField, elemento non seleccionado da BottomBar
        onSurfaceVariant = textoEscuro,
        // fondo da NavigationBar (BottomBar)
        surfaceContainer = fondoEscuro.copy( alpha = 0.1f ).compositeOver( fondoClaro ),
        // fondo do AlertDialog
        surfaceContainerHigh = fondoEscuro.copy( alpha = 0.08f ).compositeOver( fondoClaro ),
        // fondo da Card
        surfaceContainerHighest = fondoEscuro.copy( alpha = 0.1f ).compositeOver( fondoClaro ),

        // Capa 2: contraste forte
        // fondo do Button énchido
        primary = fondoEscuro,
        // texto/icona do Button
        onPrimary = textoClaro,
        // fondo do FAB
        primaryContainer = fondoEscuro,
        // icona/texto do FAB
        onPrimaryContainer = textoClaro,
        // indicador (píldora) do elemento seleccionado na BottomBar
        secondaryContainer = fondoEscuro,
        // icona/texto do elemento seleccionado na BottomBar
        onSecondaryContainer = textoClaro,

        // bordos (OutlinedButton, OutlinedTextField)
        outline = fondoEscuro,
        // Divider, liñas separadoras
        outlineVariant = textoEscuro

    )

    /**
     * Esquema de cores do tema escuro.
     *
     * Ten a mesma estrutura que [coresClaras], pero coas cores invertidas:
     * fondo escuro, texto claro e realces en gris claro.
     */
    private val coresEscuras = darkColorScheme(

        // Capa 0
        background = fondoEscuro,
        onBackground = textoClaro,

        // Capa 1
        // ListItem, OutlinedCard
        surface = fondoClaro.copy( alpha = 0.1f ).compositeOver( fondoEscuro ),
        onSurface = textoClaro,
        // fondo do OutlinedTextField
        surfaceVariant = fondoEscuro,
        // ListItem leading/supporting, bordo do TextField, elemento non seleccionado da BottomBar
        onSurfaceVariant = textoClaro,
        // fondo da NavigationBar (BottomBar)
        surfaceContainer = fondoClaro.copy( alpha = 0.1f ).compositeOver( fondoEscuro ),
        // fondo do AlertDialog
        surfaceContainerHigh = fondoClaro.copy( alpha = 0.08f ).compositeOver( fondoEscuro ),
        // Card
        surfaceContainerHighest = fondoClaro.copy( alpha = 0.1f ).compositeOver( fondoEscuro ),

        // Capa 2
        // fondo do Button énchido
        primary = fondoClaro,
        onPrimary = textoEscuro,
        // fondo do FAB
        primaryContainer = fondoClaro,
        onPrimaryContainer = textoEscuro,
        // indicador do elemento seleccionado na BottomBar
        secondaryContainer = fondoClaro,
        onSecondaryContainer = textoEscuro,

        outline = fondoClaro,
        outlineVariant = textoClaro

    )

    /**
     * Devolve o esquema de cores do tema claro.
     *
     * @return O [ColorScheme] que se pasa ao tema da aplicación cando está no modo claro.
     */
    fun collerTemaClaro(): ColorScheme {
        return coresClaras
    }

    /**
     * Devolve o esquema de cores do tema escuro.
     *
     * @return O [ColorScheme] que se pasa ao tema da aplicación cando está no modo escuro.
     */
    fun collerTemaEscuro(): ColorScheme {
        return coresEscuras
    }

}