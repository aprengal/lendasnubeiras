package com.aprengal.lendasnubeiras.ui.tema

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

//Exemplos de modificadores seguindo estilos
//Funciones de Extensión de Modificadores

// 1. Un estilo estándar para contenedores de tarjetas o secciones
fun Modifier.tarjetaNubeira(): Modifier = this
    .padding(8.dp)
    .clip(RoundedCornerShape(12.dp))
    .background(Color.White) // Aquí podrías usar tus colores definidos en Cores.kt
    .padding(16.dp)

// 2. Un estilo para botones de acción con formato unificado
fun Modifier.botonPrincipal(): Modifier = this
    .padding(vertical = 8.dp, horizontal = 16.dp)
    .clip(RoundedCornerShape(50)) // Botón redondeado tipo "pill"
    .background(Color.Black)

// 3. Un efecto visual para campos de texto que se repitan
fun Modifier.campoFormulario(): Modifier = this
    .padding(8.dp)
    .clip(RoundedCornerShape(8.dp))
    .background(Color.LightGray)