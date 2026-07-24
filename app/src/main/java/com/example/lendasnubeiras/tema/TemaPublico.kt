package com.example.lendasnubeiras.tema

import androidx.compose.ui.unit.dp

//Constantes relacionadas co tema que se poden usar directamente noutros arquivos

// ============================================================
// Espazos — escala de espaciado uniforme (padding, márgenes, gaps)
// Baseada nun sistema de 4dp, común en Material Design.
// ============================================================

object Espazos {
    val extraPequeno = 4.dp   // separación mínima entre elementos moi próximos
    val pequeno = 8.dp        // padding interno de compoñentes pequenos (chips, iconas)
    val medio = 16.dp         // padding estándar de pantalla / entre seccións
    val grande = 24.dp        // separación entre bloques de contido
    val extraGrande = 32.dp   // marxes de pantalla amplas, separación de seccións grandes
    val enorme = 48.dp        // espazos moi amplos (p.ex. antes/despois de cabeceiras)
}

// ============================================================
// Alturas e tamaños específicos de compoñentes (2.3 / 2.4)
// ============================================================

object Tamanos {
    // Botóns
    val alturaBoton = 48.dp
    val alturaBotonGhost = 56.dp   // circular, necesita máis espazo
    val anchoMinBoton = 120.dp

    // Campos de texto
    val alturaCampoTexto = 56.dp

    // Grosores de borde (2.3 / 2.4 — estados "Enfocado" e "Pulsado")
    val bordoNormal = 1.dp
    val bordoEnfocado = 2.dp
    val bordoPulsado = 2.dp

    // Iconas
    val iconaPequena = 18.dp
    val iconaMedia = 24.dp

}