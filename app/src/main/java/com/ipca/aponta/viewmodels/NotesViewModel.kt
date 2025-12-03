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
    val searchQuery: String = ""
)

class NotesViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var noteListener: ListenerRegistration? = null
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

        // BUSCAR: Minhas + Partilhadas + Pendentes
        val query = db.collection("notes")
            .where(
                Filter.or(
                    Filter.equalTo("userId", user.uid),
                    Filter.arrayContains("sharedWith", user.email ?: ""),
                    Filter.arrayContains("pendingShares", user.email ?: "") // <--- Inclui pendentes
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

                allNotesCache = fetchedNotes
                updateFilteredList(uiState.value.searchQuery)
            }
        }
    }

    // --- NOVA FUNÇÃO: ACEITAR CONVITE ---
    fun acceptInvite(note: Note) {
        val myEmail = auth.currentUser?.email ?: return

        // 1. Remove dos pendentes
        // 2. Adiciona aos partilhados
        db.collection("notes").document(note.id).update(
            mapOf(
                "pendingShares" to FieldValue.arrayRemove(myEmail),
                "sharedWith" to FieldValue.arrayUnion(myEmail)
            )
        )
    }

    // --- NOVA FUNÇÃO: RECUSAR CONVITE ---
    fun declineInvite(note: Note) {
        val myEmail = auth.currentUser?.email ?: return
        // Apenas remove dos pendentes
        db.collection("notes").document(note.id)
            .update("pendingShares", FieldValue.arrayRemove(myEmail))
    }

    // ... (Restantes funções: onSearchQueryChange, updateFilteredList, etc. mantêm-se iguais) ...
    // Copia as do ficheiro anterior para aqui se necessário

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
        uiState.value = uiState.value.copy(searchQuery = query, notes = filtered, isLoading = false)
    }

    override fun onCleared() {
        super.onCleared()
        noteListener?.remove()
    }
}