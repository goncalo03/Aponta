package com.ipca.aponta.ui.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ipca.aponta.models.Note
import com.ipca.aponta.ui.theme.getNoteColor // Importa a função de cores que criámos

@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit
) {
    // 1. Obtém a cor correta baseada no índice salvo na nota
    val backgroundColor = getNoteColor(note.colorIndex)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp) // Espaçamento exterior na grelha
            .height(110.dp) // Altura fixa (estilo Grid) ou usa wrapContentHeight() para altura variável
            .clip(RoundedCornerShape(10.dp)) // Cantos arredondados
            .background(backgroundColor) // Aplica a cor de fundo
            .clickable { onClick() } // Torna clicável
            .padding(16.dp) // Padding interior para o texto não colar às bordas
    ) {
        Column {
            // TÍTULO (Só aparece se não for vazio)
            if (note.title.isNotEmpty()) {
                Text(
                    text = note.title,
                    color = Color.Black, // Forçamos preto para ler bem nas cores pastel
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // CONTEÚDO
            if (note.content.isNotEmpty()) {
                Text(
                    text = note.content,
                    color = Color.Black.copy(alpha = 0.8f), // Preto ligeiramente transparente
                    fontSize = 14.sp,
                    maxLines = 3, // Limita o texto para caber no cartão
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}