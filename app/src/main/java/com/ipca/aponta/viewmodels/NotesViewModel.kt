package com.ipca.aponta.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.ipca.aponta.models.Note

data class NotesUiState(
    val notes: List<Note> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "" // Texto da pesquisa
)

class NotesViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var noteListener: ListenerRegistration? = null

    // Cache local com TODAS as notas (para filtrar rápido)
    private var allNotesCache: List<Note> = emptyList()

    var uiState = mutableStateOf(NotesUiState())
        private set

    val currentUserEmail: String?
        get() = auth.currentUser?.email

    init {
        loadNotes()
    }

    fun loadNotes() {
        val user = auth.currentUser
        if (user == null) {
            uiState.value = NotesUiState()
            return
        }

        noteListener?.remove()
        uiState.value = uiState.value.copy(isLoading = true)

        val query = db.collection("notes")
            .where(
                Filter.or(
                    Filter.equalTo("userId", user.uid),
                    Filter.arrayContains("sharedWith", user.email ?: ""),
                    Filter.arrayContains("pendingShares", user.email ?: "")
                )
            )

        noteListener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                uiState.value = uiState.value.copy(isLoading = false, error = error.message)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val fetchedNotes = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Note::class.java)?.copy(id = doc.id)
                }.sortedByDescending { it.timestamp }

                // 1. Guardamos tudo na cache
                allNotesCache = fetchedNotes

                // 2. Atualizamos a lista visível (aplicando filtro se houver texto escrito)
                updateFilteredList(uiState.value.searchQuery)
            }
        }
    }

    // --- PESQUISA ---
    fun onSearchQueryChange(query: String) {
        updateFilteredList(query)
    }

    private fun updateFilteredList(query: String) {
        val filtered = if (query.isBlank()) {
            allNotesCache // Se vazio, mostra tudo
        } else {
            // Filtra por Título OU Conteúdo
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

    // --- AÇÕES ---
    fun acceptInvite(note: Note) {
        val myEmail = auth.currentUser?.email ?: return
        db.collection("notes").document(note.id).update(
            mapOf(
                "pendingShares" to FieldValue.arrayRemove(myEmail),
                "sharedWith" to FieldValue.arrayUnion(myEmail)
            )
        )
    }

    fun declineInvite(note: Note) {
        val myEmail = auth.currentUser?.email ?: return
        db.collection("notes").document(note.id)
            .update("pendingShares", FieldValue.arrayRemove(myEmail))
    }

    override fun onCleared() {
        super.onCleared()
        noteListener?.remove()
    }
}