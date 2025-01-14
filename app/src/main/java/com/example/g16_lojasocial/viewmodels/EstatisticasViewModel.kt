package com.example.g16_lojasocial.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g16_lojasocial.models.EstatisticasModel
import kotlinx.coroutines.launch

class EstatisticasViewModel(public val estatisticasModel: EstatisticasModel) : ViewModel() {

    private val _artigosLevadosCount = MutableLiveData<Int>()
    val artigosLevadosCount: LiveData<Int> get() = _artigosLevadosCount

    // Function to fetch the count of articles
    fun loadArtigosLevadosCount() {
        viewModelScope.launch {
            val count = estatisticasModel.getArtigosLevadosCount()
            _artigosLevadosCount.value = count
        }
    }


    private val _nacionalidadeCounts = MutableLiveData<Map<String, Int>>()
    val nacionalidadeCounts: LiveData<Map<String, Int>> get() = _nacionalidadeCounts

    // Fetch and update the nacionalidade counts
    fun loadNacionalidadeCounts() {
        viewModelScope.launch {
            val counts = estatisticasModel.getNacionalidadeCounts()
            _nacionalidadeCounts.value = counts
        }
    }


    private val _artigosByHour = MutableLiveData<Map<Int, Int>>()
    val artigosByHour: LiveData<Map<Int, Int>> get() = _artigosByHour

    fun loadArtigosByHour() {
        viewModelScope.launch {
            val counts = estatisticasModel.getArtigosByHour()
            _artigosByHour.value = counts
        }
    }
}