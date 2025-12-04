package com.ipca.aponta.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ipca.aponta.ui.notes.NoteCard
import com.ipca.aponta.viewmodels.NotesViewModel

@Composable
fun HomeScreen(
    viewModel: NotesViewModel,
    onAddNoteClick: () -> Unit,
    onNoteClick: (String) -> Unit,
    onProfileClick: () -> Unit
) {
    val state = viewModel.uiState.value
    val currentUserEmail = viewModel.currentUserEmail

    // Estado local para saber se estamos em modo de pesquisa
    var isSearchActive by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,

        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddNoteClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                modifier = Modifier.size(70.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(32.dp))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // --- CABEÇALHO DINÂMICO ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isSearchActive) {
                    // --- BARRA DE PESQUISA ---
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                        Spacer(modifier = Modifier.width(8.dp))
                        BasicTextField(
                            value = state.searchQuery,
                            onValueChange = { viewModel.onSearchQueryChange(it) },
                            singleLine = true,
                            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 18.sp),
                            modifier = Modifier.weight(1f),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
                        )
                        if (state.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Botão CANCELAR PESQUISA
                    TextButton(onClick = {
                        isSearchActive = false
                        viewModel.onSearchQueryChange("") // Limpa o filtro ao fechar
                    }) {
                        Text("Cancelar")
                    }

                } else {
                    // --- TÍTULO E BOTÕES NORMAIS ---
                    Text(
                        text = "Notes",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Botão Lupa -> Abre a pesquisa
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(15.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { isSearchActive = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        // Botão Perfil
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(15.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { onProfileClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = "Profile", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- LISTA DE NOTAS ---
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (state.notes.isEmpty()) {
                val message = if (state.searchQuery.isNotEmpty()) "Nenhuma nota encontrada." else "Create your first note !"

                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(imageVector = Icons.Default.Description, contentDescription = null, modifier = Modifier.size(100.dp), colorFilter = ColorFilter.tint(Color.Gray))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = message, color = Color.Gray, style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Light))
                    }
                }
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    verticalItemSpacing = 16.dp,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(items = state.notes, key = { it.id }) { note ->
                        NoteCard(
                            note = note,
                            currentUserEmail = currentUserEmail,
                            onClick = {
                                // *** CORREÇÃO AQUI ***
                                // 1. Limpa o texto da pesquisa
                                viewModel.onSearchQueryChange("")
                                // 2. Fecha a barra de pesquisa
                                isSearchActive = false
                                // 3. Navega para os detalhes
                                onNoteClick(note.id)
                            },
                            onAccept = { viewModel.acceptInvite(note) },
                            onDecline = { viewModel.declineInvite(note) }
                        )
                    }
                }
            }
        }
    }
}