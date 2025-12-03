package com.ipca.aponta.ui.notes

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ipca.aponta.ui.theme.NoteColors
import com.ipca.aponta.ui.theme.getNoteColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    noteId: String,
    viewModel: AddEditNoteViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    val state = viewModel.uiState.value
    val context = LocalContext.current

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showColorSheet by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }
    var emailToShare by remember { mutableStateOf("") }

    val noteColor = getNoteColor(state.colorIndex)

    LaunchedEffect(noteId) {
        viewModel.loadNote(noteId)
    }

    // --- DIALOG PARTILHA ---
    if (showShareDialog) {
        AlertDialog(
            onDismissRequest = { showShareDialog = false },
            title = { Text("Partilhar Nota") },
            text = {
                Column {
                    Text("Insere o email do utilizador:")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = emailToShare,
                        onValueChange = { emailToShare = it },
                        label = { Text("Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (emailToShare.isNotBlank()) {
                        viewModel.shareNote(noteId, emailToShare) {
                            showShareDialog = false
                            Toast.makeText(context, "Convite enviado para $emailToShare", Toast.LENGTH_LONG).show()
                            emailToShare = ""
                        }
                    } else {
                        Toast.makeText(context, "Escreve um email válido", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Enviar")
                }
            },
            dismissButton = { TextButton(onClick = { showShareDialog = false }) { Text("Cancelar") } }
        )
    }

    // --- DIALOG APAGAR ---
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Apagar Nota") },
            text = { Text("Tens a certeza? Esta ação não pode ser desfeita.") },
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
        containerColor = MaterialTheme.colorScheme.background,

        topBar = {
            TopAppBar(
                title = { Text("Detalhes") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Voltar") }
                },
                actions = {

                    // --- PROTEÇÃO DE DONO (PARTILHA E APAGAR) ---
                    // Apenas o dono pode ver estes botões
                    if (state.isOwner) {
                        IconButton(onClick = { showShareDialog = true }) {
                            Icon(Icons.Default.Share, contentDescription = "Partilhar")
                        }
                    }

                    // Botão Cor (Todos podem mudar a cor)
                    IconButton(onClick = { showColorSheet = true }) {
                        Icon(Icons.Default.Palette, contentDescription = "Cor")
                    }

                    // Botão Editar (Todos podem editar o texto)
                    IconButton(onClick = { onNavigateToEdit(noteId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }

                    // Apenas o dono pode apagar
                    if (state.isOwner) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Apagar", tint = MaterialTheme.colorScheme.error)
                        }
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = noteColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(text = state.title, style = MaterialTheme.typography.headlineMedium, color = Color.Black)
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color.Black.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = state.content, style = MaterialTheme.typography.bodyLarge, color = Color.Black.copy(alpha = 0.8f))
                }
            }
        }

        // Menu de Cores
        if (showColorSheet) {
            ModalBottomSheet(
                onDismissRequest = { showColorSheet = false },
                containerColor = Color(0xFF252525)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Escolher cor", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        itemsIndexed(NoteColors) { index, color ->
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(width = if (state.colorIndex == index) 3.dp else 0.dp, color = if (state.colorIndex == index) Color.White else Color.Transparent, shape = CircleShape)
                                    .clickable { viewModel.onColorChange(noteId, index) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}