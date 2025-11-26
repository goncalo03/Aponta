package com.ipca.aponta.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.ipca.aponta.models.Note

class NotesViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    var notes by mutableStateOf<List<Note>>(emptyList())

    init {
        loadNotes()
    }

    // 1. LER NOTAS (Tempo Real)
    fun loadNotes() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("notes")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    notes = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Note::class.java)
                    }.sortedByDescending { it.timestamp }
                }
            }
    }

    // 2. CRIAR NOVA NOTA (Add)
    fun addNote(title: String, content: String, onSuccess: () -> Unit) {
        val userId = auth.currentUser?.uid ?: return

        val newDocRef = db.collection("notes").document()

        val newNote = Note(
            id = newDocRef.id,
            title = title,
            content = content,
            userId = userId,
            timestamp = System.currentTimeMillis()
        )

        // Grava na base de dados
        newDocRef.set(newNote).addOnCompleteListener { onSuccess() }
    }

    // 3. EDITAR NOTA EXISTENTE (Update)
    fun updateNote(noteId: String, title: String, content: String, onSuccess: () -> Unit) {
        val updates = mapOf(
            "title" to title,
            "content" to content,
            "timestamp" to System.currentTimeMillis()
        )

        // Usa .update() para não apagar outros campos (como o sharedWith)
        db.collection("notes").document(noteId)
            .update(updates)
            .addOnCompleteListener { onSuccess() }
    }

    // 4. APAGAR
    fun deleteNote(noteId: String) {
        db.collection("notes").document(noteId).delete()
    }

    // 5. PARTILHAR
    fun shareNote(noteId: String, email: String) {
        if (email.isNotEmpty()) {

            db.collection("notes").document(noteId)
                .update("sharedWith", FieldValue.arrayUnion(email))
        }
    }

    // Ajuda a sair da conta
    fun signOut() {
        auth.signOut()
    }
}