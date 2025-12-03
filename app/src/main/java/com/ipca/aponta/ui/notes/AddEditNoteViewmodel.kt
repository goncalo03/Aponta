package com.ipca.aponta.ui.notes

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.ipca.aponta.models.Note

data class AddEditNoteUiState(
    val title: String = "",
    val content: String = "",
    val colorIndex: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaveSuccess: Boolean = false,
    val isOwner: Boolean = false
)

class AddEditNoteViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    var uiState = mutableStateOf(AddEditNoteUiState())
        private set

    // --- CARREGAR NOTA ---
    fun loadNote(noteId: String) {
        uiState.value = AddEditNoteUiState(isLoading = true)

        if (noteId == "new") {
            uiState.value = uiState.value.copy(isLoading = false, isOwner = true)
            return
        }

        db.collection("notes").document(noteId).get()
            .addOnSuccessListener { document ->
                val note = document.toObject(Note::class.java)
                if (note != null) {
                    val currentUserId = auth.currentUser?.uid
                    val isOwner = (note.userId == currentUserId)

                    uiState.value = uiState.value.copy(
                        title = note.title,
                        content = note.content,
                        colorIndex = note.colorIndex,
                        isOwner = isOwner,
                        isLoading = false
                    )
                }
            }
            .addOnFailureListener {
                uiState.value = uiState.value.copy(isLoading = false, error = "Erro ao carregar")
            }
    }

    // --- GUARDAR (CORRIGIDO) ---
    fun saveNote(noteId: String?) {
        val userId = auth.currentUser?.uid ?: return
        val state = uiState.value

        if (state.title.isBlank() && state.content.isBlank()) {
            uiState.value = state.copy(error = "A nota não pode estar vazia")
            return
        }

        uiState.value = state.copy(isLoading = true, error = null)

        // *** AQUI ESTAVA O ERRO ***
        // Adicionámos <String, Any> para permitir guardar Listas junto com Textos
        val noteData = hashMapOf<String, Any>(
            "title" to state.title,
            "content" to state.content,
            "colorIndex" to state.colorIndex,
            "timestamp" to System.currentTimeMillis()
        )

        if (noteId == null || noteId == "new") {
            // CRIAR NOVA
            noteData["userId"] = userId
            noteData["sharedWith"] = emptyList<String>() // Agora isto já não dá erro

            db.collection("notes").add(noteData)
                .addOnSuccessListener {
                    uiState.value = uiState.value.copy(isLoading = false, isSaveSuccess = true)
                }
                .addOnFailureListener {
                    uiState.value = uiState.value.copy(isLoading = false, error = it.message)
                }
        } else {
            // EDITAR EXISTENTE
            db.collection("notes").document(noteId).update(noteData)
                .addOnSuccessListener {
                    uiState.value = uiState.value.copy(isLoading = false, isSaveSuccess = true)
                }
                .addOnFailureListener {
                    uiState.value = uiState.value.copy(isLoading = false, error = it.message)
                }
        }
    }

    // --- APAGAR ---
    fun deleteNote(noteId: String, onSuccess: () -> Unit) {
        if (!uiState.value.isOwner) {
            uiState.value = uiState.value.copy(error = "Não tens permissão para apagar esta nota.")
            return
        }

        db.collection("notes").document(noteId).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { uiState.value = uiState.value.copy(error = it.message) }
    }

    // --- MUDAR COR ---
    fun onColorChange(noteId: String?, newIndex: Int) {
        uiState.value = uiState.value.copy(colorIndex = newIndex)
        if (noteId != null && noteId != "new") {
            db.collection("notes").document(noteId).update("colorIndex", newIndex)
        }
    }

    // --- PARTILHAR (Envia para Pendentes) ---
    fun shareNote(noteId: String, emailToShare: String, onSuccess: () -> Unit) {
        if (emailToShare.isBlank()) return

        db.collection("notes").document(noteId)
            .update("pendingShares", FieldValue.arrayUnion(emailToShare))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { uiState.value = uiState.value.copy(error = it.message) }
    }
    fun onTitleChange(newTitle: String) {
        uiState.value = uiState.value.copy(title = newTitle)
    }

    fun onContentChange(newContent: String) {
        uiState.value = uiState.value.copy(content = newContent)
    }

    fun resetSaveState() {
        uiState.value = uiState.value.copy(isSaveSuccess = false)
    }
}