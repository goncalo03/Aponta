package com.ipca.aponta.ui.theme

import androidx.compose.ui.graphics.Color

// Lista de cores pastel para as notas
val NoteColors = listOf(
    Color(0xFFFFFFFF), // Branco (Padrão)
    Color(0xFFFD99FF), // Rosa
    Color(0xFFFF9E9E), // Vermelho
    Color(0xFF91F48F), // Verde
    Color(0xFFFFF599), // Amarelo
    Color(0xFF9EFFFF), // Ciano
    Color(0xFFB69CFF)  // Roxo
)

fun getNoteColor(index: Int): Color {
    return if (index in NoteColors.indices) NoteColors[index] else NoteColors[0]
}