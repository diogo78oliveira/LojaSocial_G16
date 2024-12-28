package com.example.g16_lojasocial.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _isVoluntario = MutableLiveData<Boolean>()
    val isVoluntario: LiveData<Boolean> = _isVoluntario

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        if (auth.currentUser == null) {
            _authState.value = AuthState.Unauthenticaded
        } else {
            _authState.value = AuthState.Authenticated
            checkIfVoluntario(auth.currentUser!!.uid)
        }
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email e Password nao podem estar vazios")
            return
        }

        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                    checkIfVoluntario(auth.currentUser!!.uid)
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Erro")
                }
            }
    }

    private fun checkIfVoluntario(uid: String) {
        firestore.collection("Voluntarios")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                _isVoluntario.value = document.exists()
            }
            .addOnFailureListener {
                _isVoluntario.value = false
            }
    }

    fun signout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticaded
        _isVoluntario.value = false
    }
}


sealed class AuthState{
    object Authenticated : AuthState()
    object Unauthenticaded : AuthState()
    object Loading : AuthState()
    data class Error(val message : String) : AuthState()

}