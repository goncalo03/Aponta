package com.ipca.aponta.ui.profile

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ipca.aponta.models.User

data class ProfileUiState(
    val name: String = "",
    val email: String = "",
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val error: String? = null
)

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    var uiState = mutableStateOf(ProfileUiState())
        private set

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid ?: return
        uiState.value = uiState.value.copy(isLoading = true)

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                if (user != null) {
                    uiState.value = uiState.value.copy(
                        name = user.name,
                        email = user.email,
                        isLoading = false
                    )
                } else {
                    // Fallback se não houver dados no Firestore
                    uiState.value = uiState.value.copy(email = auth.currentUser?.email ?: "", isLoading = false)
                }
            }
    }

    fun updateName(newName: String) {
        uiState.value = uiState.value.copy(name = newName)
    }

    fun saveProfile() {
        val userId = auth.currentUser?.uid ?: return
        uiState.value = uiState.value.copy(isLoading = true, successMessage = null, error = null)

        db.collection("users").document(userId)
            .update("name", uiState.value.name)
            .addOnSuccessListener {
                uiState.value = uiState.value.copy(isLoading = false, successMessage = "Perfil atualizado com sucesso!")
            }
            .addOnFailureListener {
                uiState.value = uiState.value.copy(isLoading = false, error = "Erro ao guardar: ${it.localizedMessage}")
            }
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        auth.signOut()
        onLogoutSuccess()
    }
}