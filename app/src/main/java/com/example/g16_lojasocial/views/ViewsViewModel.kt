package com.example.g16_lojasocial.views

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.g16_lojasocial.model.ModelPage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewsViewModel(private val modelPage: ModelPage) : ViewModel() {

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    fun registo(email: String, password: String, nome: String, telemovel: String, codigoPostal: String) {
        _authState.value = AuthState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            modelPage.registerUser(
                email, password, nome, telemovel, codigoPostal,
                onSuccess = {
                    _authState.postValue(AuthState.Authenticated)
                },
                onError = { errorMessage ->
                    _authState.postValue(AuthState.Error(errorMessage))
                }
            )
        }
    }

    sealed class AuthState {
        object Authenticated : AuthState()
        object Unauthenticated : AuthState()
        object Loading : AuthState()
        data class Error(val message: String) : AuthState()
    }
}
