package com.ipca.aponta.ui.notes

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ipca.aponta.models.Note

class AddEditNoteViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // --- ESTA É A FUNÇÃO QUE ESTAVA A FALTAR ---
    fun loadNote(noteId: String, onResult: (Note?) -> Unit) {
        db.collection("notes").document(noteId).get()
            .addOnSuccessListener { document ->
                val note = document.toObject(Note::class.java)
                onResult(note)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    // ... imports e outras funções ...

    fun saveNote(noteId: String?, title: String, content: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val userId = auth.currentUser?.uid

        // 1. Verificação de Segurança: Se não houver user, não faz nada
        if (userId == null) {
            onError("Erro: Utilizador não autenticado.")
            return
        }

        // 2. Validação: Campos vazios
        if (title.isBlank() && content.isBlank()) {
            onError("A nota não pode estar vazia")
            return
        }

        val noteData = hashMapOf(
            "title" to title,
            "content" to content,
            "userId" to userId,
            "timestamp" to System.currentTimeMillis()
        )

        // 3. Log para o Logcat (Para vermos se a função corre)
        println("A tentar guardar nota... ID: $noteId")

        if (noteId == null || noteId == "new") {
            // CRIAR
            db.collection("notes").add(noteData)
                .addOnSuccessListener {
                    println("Sucesso ao criar!")
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    println("Erro ao criar: ${e.message}")
                    onError(e.localizedMessage ?: "Erro ao guardar")
                }
        } else {
            // EDITAR
            db.collection("notes").document(noteId)
                .update(noteData as Map<String, Any>)
                .addOnSuccessListener {
                    println("Sucesso ao editar!")
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    println("Erro ao editar: ${e.message}")
                    onError(e.localizedMessage ?: "Erro ao atualizar")
                }
        }
    }

    fun deleteNote(noteId: String, onSuccess: () -> Unit) {
        db.collection("notes").document(noteId).delete().addOnSuccessListener { onSuccess() }
    }
}