package com.ipca.aponta.ui.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
    noteId: String? = null,
    viewModel: AddEditNoteViewModel, // Confirma se este nome bate certo com o teu ficheiro ViewModel
    onNavigateBack: () -> Unit
) {
    // 1. Estados
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // 2. Carregar nota
    LaunchedEffect(noteId) {
        if (noteId != null && noteId != "new") {
            viewModel.loadNote(noteId) { note ->
                if (note != null) {
                    title = note.title
                    content = note.content
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (noteId == "new") "Nova Nota" else "Editar Nota") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    // Botão Apagar
                    if (noteId != null && noteId != "new") {
                        IconButton(onClick = {
                            viewModel.deleteNote(noteId) { onNavigateBack() }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Apagar", tint = Color.Red)
                        }
                    }
                    // Botão Guardar
                    IconButton(onClick = {
                        viewModel.saveNote(
                            noteId = noteId,
                            title = title,
                            content = content,
                            onSuccess = { onNavigateBack() },
                            onError = { errorMessage = it }
                        )
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Guardar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            // --- CAMPO TÍTULO ---
            // Simplifiquei os argumentos para garantir compatibilidade
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) } // Ícone básico seguro
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- CAMPO CONTEÚDO ---
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Escreve aqui...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) } // Ícone básico seguro
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}