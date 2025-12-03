package com.ipca.aponta.ui.login

import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

// 1. O Estado da UI (Dados agrupados)
data class LoginUiState(
    var isLoading: Boolean = false,
    var error: String? = null,
    var isSuccess: Boolean = false
)

class LoginViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    // 2. A variável única que a UI vai observar
    var uiState = mutableStateOf(LoginUiState())
        private set

    fun login(email: String, pass: String) {
        // Reset do erro e ativa loading
        uiState.value = uiState.value.copy(error = null, isLoading = true)

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            uiState.value = uiState.value.copy(isLoading = false, error = "Email inválido")
            return
        }
        if (pass.length < 6) {
            uiState.value = uiState.value.copy(isLoading = false, error = "Password curta demais")
            return
        }

        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener {
                // Sucesso!
                uiState.value = uiState.value.copy(isLoading = false, isSuccess = true)
            }
            .addOnFailureListener { e ->
                // Erro!
                uiState.value = uiState.value.copy(isLoading = false, error = e.localizedMessage ?: "Erro no login")
            }
    }

    fun resetState() {
        uiState.value = LoginUiState()
    }
}