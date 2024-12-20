package com.example.g16_lojasocial.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {

    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        checkAuthStatus()
    }


    fun checkAuthStatus(){
        if(auth.currentUser==null){
            _authState.value = AuthState.Unauthenticaded
        }else{
            _authState.value = AuthState.Authenticated
        }
    }

    fun login(email : String,password : String){

        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email e Password nao podem estar vazios")
            return
        }

        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener{task->
                if(task.isSuccessful){
                    _authState.value = AuthState.Authenticated
                }else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Erro")
                }
            }
    }

    fun registo(email : String,password : String){

        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email e Password nao podem estar vazios")
            return
        }

        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{task->
                if(task.isSuccessful){
                    _authState.value = AuthState.Authenticated
                }else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Erro")
                }
            }
    }


    fun signout(){
        auth.signOut()
        _authState.value = AuthState.Unauthenticaded
    }

}


sealed class AuthState{
    object Authenticated : AuthState()
    object Unauthenticaded : AuthState()
    object Loading : AuthState()
    data class Error(val message : String) : AuthState()

}