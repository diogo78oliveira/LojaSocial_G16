package com.example.g16_lojasocial.views


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.g16_lojasocial.authentication.AuthState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore



class ViewsViewModel : ViewModel() {

    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState


    fun registo(email: String, password: String, nome: String, telemovel: String, codigoPostal: String) {
        if (email.isEmpty() || password.isEmpty() || nome.isEmpty() || telemovel.isEmpty() || codigoPostal.isEmpty()) {
            _authState.value = AuthState.Error("Todos os campos devem ser preenchidos")
            return
        }

        _authState.value = AuthState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    if (user != null) {
                        val uid = user.uid // Get the created user UID

                        val userData = hashMapOf(
                            "nome" to nome,
                            "telemovel" to telemovel,
                            "codPostal" to codigoPostal
                        )

                        // Save user data to Firestore
                        val firestore = FirebaseFirestore.getInstance()
                        firestore.collection("Voluntarios").document(uid)
                            .set(userData)
                            .addOnSuccessListener {
                                _authState.value = AuthState.Authenticated
                            }
                            .addOnFailureListener { exception ->
                                _authState.value = AuthState.Error("Falha ao salvar dados no Firestore: ${exception.message}")
                            }
                    }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Erro ao registrar")
                }
            }
    }

    sealed class AuthState{
        object Authenticated : AuthState()
        object Unauthenticaded : AuthState()
        object Loading : AuthState()
        data class Error(val message : String) : AuthState()

    }

}