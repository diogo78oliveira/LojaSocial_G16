package com.example.g16_lojasocial.views

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.g16_lojasocial.ModelPage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewsViewModel(private val modelPage: ModelPage) : ViewModel() {

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _beneficiariosList = MutableLiveData<List<String>>()
    val beneficiariosList: LiveData<List<String>> = _beneficiariosList

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

    fun saveUserData(
        nome: String,
        dataNascimento: String,
        email: String,
        telemovel: String,
        morada: String,
        codigoPostal: String,
        nacionalidade: String
    ) {
        _authState.value = AuthState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            modelPage.saveUserData(
                nome, dataNascimento, email, telemovel, morada, codigoPostal, nacionalidade,
                onSuccess = {
                    _authState.postValue(AuthState.Authenticated)
                },
                onError = { errorMessage ->
                    _authState.postValue(AuthState.Error(errorMessage))
                }
            )
        }
    }

    fun fetchBeneficiarios() {
        modelPage.getAllBeneficiarios(
            onSuccess = { nomesList ->
                _beneficiariosList.postValue(nomesList)
            },
            onError = { errorMessage ->
                // Handle error (show a message, etc.)
            }
        )
    }

    sealed class AuthState {
        object Authenticated : AuthState()
        object Unauthenticated : AuthState()
        object Loading : AuthState()
        data class Error(val message: String) : AuthState()
    }
}
