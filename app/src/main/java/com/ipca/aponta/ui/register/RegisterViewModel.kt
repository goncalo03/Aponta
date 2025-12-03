package com.ipca.aponta.ui.register

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ipca.aponta.models.User


data class RegisterUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class RegisterViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // 2. Variável observável
    var uiState = mutableStateOf(RegisterUiState())
        private set

    // 3. Função Register (sem callbacks nos parâmetros)
    fun register(email: String, pass: String, confirmPass: String) {
        // Reset estado inicial
        uiState.value = RegisterUiState(isLoading = true)

        // Validações
        if (email.isBlank() || pass.isBlank() || confirmPass.isBlank()) {
            uiState.value = uiState.value.copy(isLoading = false, error = "Preencha todos os campos")
            return
        }
        if (pass != confirmPass) {
            uiState.value = uiState.value.copy(isLoading = false, error = "As passwords não coincidem")
            return
        }
        if (pass.length < 6) {
            uiState.value = uiState.value.copy(isLoading = false, error = "A password deve ter pelo menos 6 caracteres")
            return
        }

        // Criar utilizador no Firebase Auth
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener { result ->
                val firebaseUser = result.user

                // Guardar dados extra no Firestore
                if (firebaseUser != null) {
                    val newUser = User(
                        id = firebaseUser.uid,
                        email = email,
                        name = email.substringBefore("@")
                    )

                    db.collection("users").document(firebaseUser.uid).set(newUser)
                        .addOnSuccessListener {
                            // SUCESSO FINAL
                            uiState.value = uiState.value.copy(isLoading = false, isSuccess = true)
                        }
                        .addOnFailureListener {
                            // Sucesso no Auth mas falha no Firestore (consideramos sucesso de login)
                            uiState.value = uiState.value.copy(isLoading = false, isSuccess = true)
                        }
                }
            }
            .addOnFailureListener {
                // ERRO
                uiState.value = uiState.value.copy(isLoading = false, error = it.localizedMessage ?: "Erro ao criar conta")
            }
    }
}