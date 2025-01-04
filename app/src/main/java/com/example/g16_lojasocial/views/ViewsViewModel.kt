package com.example.g16_lojasocial.views

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g16_lojasocial.model.ModelPage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.g16_lojasocial.model.Beneficiario
import com.example.g16_lojasocial.model.Voluntario
import com.google.firebase.firestore.FirebaseFirestore


class ViewsViewModel(public val modelPage: ModelPage) : ViewModel() {

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _beneficiariosList = MutableLiveData<List<Beneficiario>>()
    val beneficiariosList: LiveData<List<Beneficiario>> = _beneficiariosList


    fun registo(email: String, password: String, nome: String, telemovel: String, codigoPostal: String, respostaAjuda:String) {
        _authState.value = AuthState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            modelPage.registerUser(
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
            onSuccess = { beneficiarios ->
                _beneficiariosList.postValue(beneficiarios)
            },
            onError = { errorMessage ->
                // Handle error (e.g., show a toast or log)
            }
        )
    }

    fun updateUserData(
        documentId: String, // Firestore document ID
        nome: String,
        dataNascimento: String,
        email: String,
        telemovel: String,
        morada: String,
        codigoPostal: String,
        nacionalidade: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            modelPage.updateUserData(
                documentId,
                nome,
                dataNascimento,
                email,
                telemovel,
                morada,
                codigoPostal,
                nacionalidade,
                onSuccess,
                onError
            )
        }
    }

    fun saveArtigosLevados(
        idBeneficiario: String,
        descArtigo: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        modelPage.saveArtigosLevados(
            idBeneficiario = idBeneficiario,
            descArtigo = descArtigo,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    private val _artigosList = MutableLiveData<List<Map<String, Any>>>()
    val artigosList: LiveData<List<Map<String, Any>>> = _artigosList

    fun fetchArtigosByBeneficiario(idBeneficiario: String) {
        modelPage.getArtigosByBeneficiario(
            idBeneficiario = idBeneficiario,
            onSuccess = { artigos ->
                _artigosList.postValue(artigos)
            },
            onError = { errorMessage ->
                // Handle error (e.g., log or show a message)
            }
        )
    }





        // Function to save or update the "Dia" field in the "Ajuda" collection
        fun saveDateToFirebase(date: String, onComplete: (Boolean) -> Unit) {
            viewModelScope.launch {
                modelPage.saveOrUpdateDia(date, onComplete)
            }
        }

    var respostaAjuda by mutableStateOf<String?>(null)

    // Function to update "respostaAjuda" in Firestore
    fun updateRespostaAjuda(resposta: String, onComplete: (Boolean) -> Unit) {
        modelPage.updateRespostaAjuda(resposta) { success ->
            if (success) {
                respostaAjuda = resposta // Update the state after successful update
            }
            onComplete(success)
        }
    }

    // Function to load the current "respostaAjuda" from Firestore
    fun loadRespostaAjuda() {
        modelPage.getRespostaAjuda { resposta ->
            respostaAjuda = resposta
        }
    }

    var voluntariosSim by mutableStateOf<List<Voluntario>>(emptyList())
        private set

    // Function to load Voluntarios with "Sim" in respostaAjuda
    fun loadVoluntariosSim() {
        modelPage.getVoluntariosSim { voluntarios ->
            voluntariosSim = voluntarios
        }
    }

    fun updateRespostaAjudaForAllUsers(newResponse: String, callback: (Boolean) -> Unit) {

        modelPage.updateRespostaAjudaForAllUsersModel(
            newResponse = newResponse,
            callback = callback
        )

    }

    fun addEvent(nome: String, descricao: String, diaEvento: String, estado: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = modelPage.addEvent(nome, descricao, diaEvento, estado)
            onResult(success)
        }
    }

    sealed class AuthState {
        object Authenticated : AuthState()
        object Unauthenticated : AuthState()
        object Loading : AuthState()
        data class Error(val message: String) : AuthState()
    }
}

