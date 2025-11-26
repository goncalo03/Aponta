package com.ipca.aponta.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.ipca.aponta.models.User

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    var currentUser by mutableStateOf<FirebaseUser?>(auth.currentUser)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        if (email.isBlank() || pass.isBlank()) {
            errorMessage = "Preencha todos os campos"
            return
        }
        isLoading = true
        errorMessage = null

        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    currentUser = auth.currentUser
                    onSuccess()
                } else {
                    errorMessage = "Erro ao entrar: ${task.exception?.localizedMessage}"
                }
            }
    }

    fun register(email: String, pass: String, confirmPass: String, onSuccess: () -> Unit) {
        if (email.isBlank() || pass.isBlank() || confirmPass.isBlank()) {
            errorMessage = "Preencha todos os campos"
            return
        }
        if (pass != confirmPass) {
            errorMessage = "As passwords não coincidem"
            return
        }

        isLoading = true
        errorMessage = null

        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    firebaseUser?.let { user ->
                        val newUser = User(
                            id = user.uid,
                            email = email,
                            name = email.substringBefore("@")
                        )

                        saveUserToFirestore(newUser) {
                            isLoading = false
                            currentUser = user
                            onSuccess()
                        }
                    }
                } else {
                    isLoading = false
                    errorMessage = "Erro no registo: ${task.exception?.localizedMessage}"
                }
            }
    }

    private fun saveUserToFirestore(user: User, onComplete: () -> Unit) {
        db.collection("users").document(user.id)
            .set(user)
            .addOnSuccessListener { onComplete() }
            .addOnFailureListener {
                onComplete()
            }
    }

    fun logout() {
        auth.signOut()
        currentUser = null
    }
}