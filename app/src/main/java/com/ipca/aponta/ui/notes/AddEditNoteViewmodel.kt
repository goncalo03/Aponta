package com.ipca.aponta.ui.notes

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ipca.aponta.models.Note

data class AddEditNoteUiState(
    val title: String = "",
    val content: String = "",
    val colorIndex: Int = 0, // Cor atual
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaveSuccess: Boolean = false
)

class AddEditNoteViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    var uiState = mutableStateOf(AddEditNoteUiState())
        private set

    // --- CORRIGIDO: A função que estava a faltar ---
    fun onColorChange(noteId: String?, newIndex: Int) {
        // 1. Atualiza a UI imediatamente (para veres a cor mudar no ecrã)
        uiState.value = uiState.value.copy(colorIndex = newIndex)

        // 2. Se a nota já existe na BD (não é null nem "new"), guarda a cor logo no Firebase
        if (noteId != null && noteId != "new") {
            db.collection("notes").document(noteId)
                .update("colorIndex", newIndex)
                .addOnFailureListener {
                    uiState.value = uiState.value.copy(error = "Erro ao mudar cor")
                }
        }
    }

    // --- Outras Funções ---

    fun onTitleChange(newTitle: String) {
        uiState.value = uiState.value.copy(title = newTitle)
    }

    fun onContentChange(newContent: String) {
        uiState.value = uiState.value.copy(content = newContent)
    }

    fun loadNote(noteId: String) {
        if (noteId == "new") return
        uiState.value = uiState.value.copy(isLoading = true)

        db.collection("notes").document(noteId).get()
            .addOnSuccessListener { document ->
                val note = document.toObject(Note::class.java)
                if (note != null) {
                    uiState.value = uiState.value.copy(
                        title = note.title,
                        content = note.content,
                        colorIndex = note.colorIndex,
                        isLoading = false
                    )
                }
            }
            .addOnFailureListener {
                uiState.value = uiState.value.copy(isLoading = false, error = "Erro ao carregar")
            }
    }

    fun saveNote(noteId: String?) {
        val userId = auth.currentUser?.uid ?: return
        val state = uiState.value

        if (state.title.isBlank() && state.content.isBlank()) {
            uiState.value = state.copy(error = "Nota vazia")
            return
        }

        uiState.value = state.copy(isLoading = true, error = null)

        val noteData = hashMapOf(
            "title" to state.title,
            "content" to state.content,
            "colorIndex" to state.colorIndex,
            "userId" to userId,
            "timestamp" to System.currentTimeMillis()
        )

        if (noteId == null || noteId == "new") {
            db.collection("notes").add(noteData)
                .addOnSuccessListener {
                    uiState.value = uiState.value.copy(isLoading = false, isSaveSuccess = true)
                }
        } else {
            db.collection("notes").document(noteId).update(noteData as Map<String, Any>)
                .addOnSuccessListener {
                    uiState.value = uiState.value.copy(isLoading = false, isSaveSuccess = true)
                }
        }
    }

    fun deleteNote(noteId: String, onSuccess: () -> Unit) {
        db.collection("notes").document(noteId).delete().addOnSuccessListener { onSuccess() }
    }
}