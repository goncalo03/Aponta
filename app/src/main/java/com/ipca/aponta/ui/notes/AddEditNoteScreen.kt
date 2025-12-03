package com.ipca.aponta.ui.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette // Ícone da paleta
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ipca.aponta.ui.theme.NoteColors // Importa a tua lista de cores
import com.ipca.aponta.ui.theme.getNoteColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
    noteId: String? = null,
    viewModel: AddEditNoteViewModel,
    onNavigateBack: () -> Unit
) {
    val state = viewModel.uiState.value

    // Estado para controlar se o menu de cores está visível
    var showColorSheet by remember { mutableStateOf(false) }

    // Cor atual selecionada (para pintar o fundo do ecrã)
    val currentNoteColor = getNoteColor(state.colorIndex)

    LaunchedEffect(noteId) {
        if (noteId != null) viewModel.loadNote(noteId)
    }

    LaunchedEffect(state.isSaveSuccess) {
        if (state.isSaveSuccess) onNavigateBack()
    }

    Scaffold(
        // O fundo do ecrã agora muda conforme a cor escolhida!
        containerColor = currentNoteColor,

        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 24.dp, end = 24.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botão VOLTAR
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color.Black.copy(alpha = 0.1f)) // Fundo semitransparente para ver a cor da nota
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

                    // --- BOTÃO DE COR (NOVO) ---
                    IconButton(
                        onClick = { showColorSheet = true },
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color.Black.copy(alpha = 0.1f))
                    ) {
                        Icon(Icons.Default.Palette, contentDescription = "Color", tint = Color.Black)
                    }

                    // Botão GUARDAR
                    IconButton(
                        onClick = { viewModel.saveNote(noteId) },
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color.Black.copy(alpha = 0.1f))
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                        } else {
                            Icon(Icons.Default.Check, contentDescription = "Save", tint = Color.Black)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {

            // TÍTULO
            TextField(
                value = state.title,
                onValueChange = { viewModel.onTitleChange(it) },
                placeholder = {
                    Text("Title", style = TextStyle(fontSize = 48.sp, color = Color.Black.copy(alpha = 0.5f)))
                },
                textStyle = TextStyle(fontSize = 48.sp, color = Color.Black, fontWeight = FontWeight.Normal),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    cursorColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // CONTEÚDO
            TextField(
                value = state.content,
                onValueChange = { viewModel.onContentChange(it) },
                placeholder = {
                    Text("Type something...", style = TextStyle(fontSize = 23.sp, color = Color.Black.copy(alpha = 0.5f)))
                },
                textStyle = TextStyle(fontSize = 23.sp, color = Color.Black, fontWeight = FontWeight.Normal),
                modifier = Modifier.fillMaxWidth().weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    cursorColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )
        }

        // --- MENU DE ESCOLHA DE COR (Bottom Sheet) ---
        if (showColorSheet) {
            ModalBottomSheet(
                onDismissRequest = { showColorSheet = false },
                containerColor = Color(0xFF252525) // Fundo escuro para contrastar com as cores claras
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Escolher cor da nota", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        itemsIndexed(NoteColors) { index, color ->
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (state.colorIndex == index) 3.dp else 0.dp,
                                        color = if (state.colorIndex == index) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        viewModel.onColorChange(noteId, index)
                                    }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}