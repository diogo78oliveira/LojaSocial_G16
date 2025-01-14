package com.example.g16_lojasocial.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.g16_lojasocial.models.HomePageModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.g16_lojasocial.dataClasses.Beneficiario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class HomePageViewModel(public val homePageModel: HomePageModel) : ViewModel() {

    public val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    public val _beneficiariosList = MutableLiveData<List<Beneficiario>>()
    val beneficiariosList: LiveData<List<Beneficiario>> = _beneficiariosList

    fun saveUserData(
        nome: String,
        dataNascimento: String,
        email: String,
        telemovel: String,
        morada: String,
        codigoPostal: String,
        nacionalidade: String,
        cor: String
    ) {
        _authState.value = AuthState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            homePageModel.saveUserData(
                nome, dataNascimento, email, telemovel, morada, codigoPostal, nacionalidade, cor,
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
        homePageModel.getAllBeneficiarios(
            onSuccess = { beneficiarios ->
                _beneficiariosList.postValue(beneficiarios)
            },
            onError = { errorMessage ->
            }
        )
    }

    fun updateUserData(
        documentId: String,
        nome: String,
        dataNascimento: String,
        email: String,
        telemovel: String,
        morada: String,
        codigoPostal: String,
        nacionalidade: String,
        cor: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            homePageModel.updateUserData(
                documentId,
                nome,
                dataNascimento,
                email,
                telemovel,
                morada,
                codigoPostal,
                nacionalidade,
                cor,
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
        homePageModel.saveArtigosLevados(
            idBeneficiario = idBeneficiario,
            descArtigo = descArtigo,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    private val _artigosList = MutableLiveData<List<Map<String, Any>>>()
    val artigosList: LiveData<List<Map<String, Any>>> = _artigosList

    fun fetchArtigosByBeneficiario(idBeneficiario: String) {
        homePageModel.getArtigosByBeneficiario(
            idBeneficiario = idBeneficiario,
            onSuccess = { artigos ->
                _artigosList.postValue(artigos)
            },
            onError = { errorMessage ->
                // Handle error (e.g., log or show a message)
            }
        )
    }


    private val _avisos = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val avisos: StateFlow<List<Pair<String, String>>> get() = _avisos

    fun loadAvisos(idBeneficiario: String) {
        homePageModel.fetchAvisosForBeneficiary(
            idBeneficiario = idBeneficiario,
            onSuccess = { avisosList ->
                _avisos.value = avisosList
            },
            onError = { exception ->
                Log.e("ViewsViewModel", "Error loading avisos: $exception")
            }
        )
    }

    fun saveAviso(
        idBeneficiario: String,
        descAviso: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        homePageModel.saveAvisos(
            idBeneficiario = idBeneficiario,
            descAviso = descAviso,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun updateBeneficiaryColor(
        beneficiaryId: String,
        newColor: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        homePageModel.updateBeneficiaryColor(beneficiaryId, newColor, onSuccess, onError)
    }



}