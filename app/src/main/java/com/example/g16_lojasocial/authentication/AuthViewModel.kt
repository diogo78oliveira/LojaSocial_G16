package com.example.g16_lojasocial.authentication

import android.graphics.ColorSpace.Model
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g16_lojasocial.model.ModelPage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class AuthViewModel(private val modelPage: ModelPage) : ViewModel() {

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> get() = _authState

    private val _isVoluntario = MutableLiveData<Boolean>()
    val isVoluntario: LiveData<Boolean> get() = _isVoluntario



    // Check if user is logged in and their role
    fun checkAuthStatus() {
        val user = modelPage.getCurrentUser()
        if (user == null) {
            _authState.value = AuthState.Unauthenticaded
        } else {
            _authState.value = AuthState.Authenticated
            checkIfVoluntario(user.uid)
        }
    }

    // Login logic
    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email e Password não podem estar vazios.")
            return
        }

        _authState.value = AuthState.Loading

        modelPage.login(
            email = email,
            password = password,
            onSuccess = {
                _authState.value = AuthState.Authenticated
                checkAuthStatus()
            },
            onError = { error ->
                _authState.value = AuthState.Error(error)
            }
        )
    }

    // Check if the user is a "Voluntário"
    private fun checkIfVoluntario(uid: String) {
        viewModelScope.launch {
            modelPage.checkIfVoluntario(uid) { isVoluntario ->
                _isVoluntario.postValue(isVoluntario)
            }
        }
    }

    // Logout logic
    fun signout() {
        modelPage.signout()
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