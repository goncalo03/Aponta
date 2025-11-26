package com.ipca.aponta.ui.register

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ipca.aponta.models.User

class RegisterViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun register(email: String, pass: String, confirmPass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        // 1. Validações
        if (email.isBlank() || pass.isBlank() || confirmPass.isBlank()) {
            onError("Preencha todos os campos")
            return
        }
        if (pass != confirmPass) {
            onError("As passwords não coincidem")
            return
        }
        if (pass.length < 6) {
            onError("A password deve ter pelo menos 6 caracteres")
            return
        }

        // 2. Criar utilizador no Firebase Auth
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener { result ->
                val firebaseUser = result.user

                // 3. Guardar dados extra no Firestore
                if (firebaseUser != null) {
                    val newUser = User(
                        id = firebaseUser.uid,
                        email = email,
                        name = email.substringBefore("@")
                    )

                    db.collection("users").document(firebaseUser.uid).set(newUser)
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener {
                            onSuccess()
                        }
                }
            }
            .addOnFailureListener {
                onError(it.localizedMessage ?: "Erro ao criar conta")
            }
    }
}