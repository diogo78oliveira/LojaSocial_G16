package com.example.g16_lojasocial.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.g16_lojasocial.models.SignUpModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SignUpViewModel(public val signUpModel: SignUpModel)  : ViewModel() {

    public val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    fun registo(email: String, password: String, nome: String, telemovel: String, codigoPostal: String, respostaAjuda:String) {
        _authState.value = AuthState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            signUpModel.registerUser(
                email, password, nome, telemovel, codigoPostal, respostaAjuda,
                onSuccess = {
                    _authState.postValue(AuthState.Authenticated)
                },
                onError = { errorMessage ->
                    _authState.postValue(AuthState.Error(errorMessage))
                }
            )
        }
    }

}