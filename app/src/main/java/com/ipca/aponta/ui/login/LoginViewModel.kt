package com.ipca.aponta.ui.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    fun login(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {

        // 1. Verificar se está vazio (Básico)
        if (email.isBlank() || pass.isBlank()) {
            onError("Preencha todos os campos")
            return
        }

        // 2. Verificar se o email é válido (Melhoria)
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            onError("O email introduzido não é válido")
            return
        }

        // 3. Verificar tamanho da password (Melhoria)
        if (pass.length < 6) {
            onError("A password deve ter pelo menos 6 caracteres")
            return
        }

        // 4. Tentar Login
        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                // 5. Tratamento de Erros Específicos do Firebase (Melhoria)
                val errorMessage = when (exception) {
                    is FirebaseAuthInvalidUserException -> "Esta conta não existe."
                    is FirebaseAuthInvalidCredentialsException -> "A password está incorreta."
                    else -> "Erro no login: ${exception.localizedMessage}" // Erro genérico (ex: sem internet)
                }
                onError(errorMessage)
            }
    }
}