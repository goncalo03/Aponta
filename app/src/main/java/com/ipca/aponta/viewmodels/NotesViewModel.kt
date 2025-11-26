package com.ipca.aponta.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ipca.aponta.models.Note

class NotesViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    var isLoading by mutableStateOf(false)

    var notes by mutableStateOf<List<Note>>(emptyList())
        private set

    init {
        loadNotes()
    }

    fun loadNotes() {
        val userId = auth.currentUser?.uid ?: return

        isLoading = true

        db.collection("notes")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, _ ->
                // Desativa o loading quando chegam dados
                isLoading = false

                if (snapshot != null) {
                    notes = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Note::class.java)?.copy(id = doc.id)
                    }.sortedByDescending { it.timestamp }
                }
            }
    }

    fun addNote(title: String, content: String, onSuccess: () -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val newDocRef = db.collection("notes").document()
        val newNote = Note(id = newDocRef.id, title = title, content = content, userId = userId, timestamp = System.currentTimeMillis())
        newDocRef.set(newNote).addOnSuccessListener { onSuccess() }
    }

    fun updateNote(noteId: String, title: String, content: String, onSuccess: () -> Unit) {
        val updates = mapOf("title" to title, "content" to content, "timestamp" to System.currentTimeMillis())
        db.collection("notes").document(noteId).update(updates).addOnSuccessListener { onSuccess() }
    }

    fun deleteNote(noteId: String) {
        db.collection("notes").document(noteId).delete()
    }

    fun getNoteById(noteId: String): Note? {
        return notes.find { it.id == noteId }
    }
}