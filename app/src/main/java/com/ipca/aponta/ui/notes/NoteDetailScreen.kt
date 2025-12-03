package com.ipca.aponta.ui.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ipca.aponta.ui.theme.NoteColors
// import com.ipca.aponta.ui.theme.getNoteColor // Já não precisamos disto aqui para o fundo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    noteId: String,
    viewModel: AddEditNoteViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    val state = viewModel.uiState.value
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showColorSheet by remember { mutableStateOf(false) }

    // Carregar a nota ao abrir
    LaunchedEffect(noteId) {
        viewModel.loadNote(noteId)
    }

    // Popup de Apagar
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Apagar Nota") },
            text = { Text("Tens a certeza?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteNote(noteId) {
                        showDeleteDialog = false
                        onNavigateBack()
                    }
                }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                    Text("Apagar")
                }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") } }
        )
    }

    Scaffold(
        // --- ALTERAÇÃO AQUI ---
        // Antes estava: containerColor = getNoteColor(state.colorIndex)
        // Agora usamos a cor padrão do sistema (branco/escuro) para não mudar o fundo
        containerColor = MaterialTheme.colorScheme.background,

        topBar = {
            TopAppBar(
                title = { Text("Detalhes") },
                // Voltamos à cor padrão da barra também
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { showColorSheet = true }) {
                        Icon(Icons.Default.Palette, contentDescription = "Mudar Cor")
                    }
                    IconButton(onClick = { onNavigateToEdit(noteId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Apagar", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                Text(
                    text = state.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = state.content,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // --- MENU DE CORES ---
        if (showColorSheet) {
            ModalBottomSheet(
                onDismissRequest = { showColorSheet = false },
                containerColor = Color(0xFF252525) // Fundo escuro da gaveta
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Escolher cor da nota", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
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
                                        // Isto atualiza a base de dados, mas como o fundo do ecrã
                                        // agora é fixo, o user não vê a cor mudar aqui (só na Home).
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