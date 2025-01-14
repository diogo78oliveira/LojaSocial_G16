package com.example.g16_lojasocial.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g16_lojasocial.dataClasses.Voluntario
import com.example.g16_lojasocial.models.VoluntariosModel
import kotlinx.coroutines.launch

class VoluntariosViewModel(public val voluntariosModel: VoluntariosModel) : ViewModel() {

    // Function to save or update the "Dia" field in the "Ajuda" collection
    fun saveDateToFirebase(date: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            voluntariosModel.saveOrUpdateDia(date, onComplete)
        }
    }

    var respostaAjuda by mutableStateOf<String?>(null)

    // Function to update "respostaAjuda" in Firestore
    fun updateRespostaAjuda(resposta: String, onComplete: (Boolean) -> Unit) {
        voluntariosModel.updateRespostaAjuda(resposta) { success ->
            if (success) {
                respostaAjuda = resposta // Update the state after successful update
            }
            onComplete(success)
        }
    }

    // Function to load the current "respostaAjuda" from Firestore
    fun loadRespostaAjuda() {
        voluntariosModel.getRespostaAjuda { resposta ->
            respostaAjuda = resposta
        }
    }

    var voluntariosSim by mutableStateOf<List<Voluntario>>(emptyList())
        private set

    // Function to load Voluntarios with "Sim" in respostaAjuda
    fun loadVoluntariosSim() {
        voluntariosModel.getVoluntariosSim { voluntarios ->
            voluntariosSim = voluntarios
        }
    }

    fun updateRespostaAjudaForAllUsers(newResponse: String, callback: (Boolean) -> Unit) {

        voluntariosModel.updateRespostaAjudaForAllUsersModel(
            newResponse = newResponse,
            callback = callback
        )

    }

}