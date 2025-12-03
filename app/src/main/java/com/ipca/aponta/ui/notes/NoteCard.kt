package com.ipca.aponta.ui.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check // Ícone do Visto
import androidx.compose.material.icons.filled.Close // Ícone do X
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ipca.aponta.models.Note
import com.ipca.aponta.ui.theme.getNoteColor

@Composable
fun NoteCard(
    note: Note,
    currentUserEmail: String?,
    onClick: () -> Unit,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    // Cor da nota (ou cinza se estiver pendente)
    val baseColor = getNoteColor(note.colorIndex)
    val isPending = currentUserEmail != null && note.pendingShares.contains(currentUserEmail)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .height(130.dp) // Altura suficiente para os botões
            .clip(RoundedCornerShape(10.dp))
            .background(if (isPending) Color.LightGray else baseColor)
            .clickable(enabled = !isPending) { onClick() }
            .padding(16.dp)
    ) {
        if (isPending) {
            // --- MODO CONVITE ---
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Convite de Partilha",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Botões lado a lado
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {

                    // BOTÃO ACEITAR (VERDE COM VISTO)
                    Button(
                        onClick = onAccept,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), // Verde
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp), // Remove padding para ficar bola perfeita
                        modifier = Modifier.size(40.dp) // Tamanho do botão
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Aceitar",
                            tint = Color.White
                        )
                    }

                    // BOTÃO RECUSAR (VERMELHO COM X)
                    Button(
                        onClick = onDecline,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)), // Vermelho
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Recusar",
                            tint = Color.White
                        )
                    }
                }
            }
        } else {
            // --- MODO NOTA NORMAL ---
            Column {
                if (note.title.isNotEmpty()) {
                    Text(
                        text = note.title,
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                if (note.content.isNotEmpty()) {
                    Text(
                        text = note.content,
                        color = Color.Black.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}