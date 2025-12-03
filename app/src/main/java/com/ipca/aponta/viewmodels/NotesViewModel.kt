package com.ipca.aponta.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.ipca.aponta.models.Note

// Adicionamos 'searchQuery' ao estado
data class NotesUiState(
    val notes: List<Note> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "" // <--- NOVO
)

class NotesViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var noteListener: ListenerRegistration? = null

    // Lista completa em cache para não ir ao Firebase sempre que pesquisamos
    private var allNotesCache: List<Note> = emptyList()

    var uiState = mutableStateOf(NotesUiState())
        private set

    init {
        loadNotes()
    }

    fun loadNotes() {
        val currentUser = auth.currentUser ?: return
        noteListener?.remove()
        uiState.value = uiState.value.copy(isLoading = true)

        noteListener = db.collection("notes")
            .whereEqualTo("userId", currentUser.uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    uiState.value = uiState.value.copy(isLoading = false, error = error.message)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val fetchedNotes = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Note::class.java)?.copy(id = doc.id)
                    }.sortedByDescending { it.timestamp }

                    // Guardamos a lista original
                    allNotesCache = fetchedNotes

                    // Atualizamos a UI (aplicando o filtro se já houver texto)
                    updateFilteredList(uiState.value.searchQuery)
                }
            }
    }

    // --- NOVA FUNÇÃO DE PESQUISA ---
    fun onSearchQueryChange(query: String) {
        updateFilteredList(query)
    }

    private fun updateFilteredList(query: String) {
        val filtered = if (query.isBlank()) {
            allNotesCache
        } else {
            allNotesCache.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.content.contains(query, ignoreCase = true)
            }
        }

        uiState.value = uiState.value.copy(
            searchQuery = query,
            notes = filtered,
            isLoading = false
        )
    }
    override fun onCleared() {
        super.onCleared()
        noteListener?.remove()
    }
}