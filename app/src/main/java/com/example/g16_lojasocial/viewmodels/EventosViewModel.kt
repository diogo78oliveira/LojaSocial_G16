package com.example.g16_lojasocial.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.g16_lojasocial.dataClasses.Event
import com.example.g16_lojasocial.models.EventosModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class EventosViewModel(public val eventosModel: EventosModel) : ViewModel() {

    fun addEvent(nome: String, descricao: String, diaEvento: String, imageUrl: String,  onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = eventosModel.addEvent(nome, descricao, diaEvento, imageUrl)
            onResult(success)
        }
    }

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events

    fun loadEvents() {
        viewModelScope.launch {
            val allEvents = eventosModel.getEvents()
            val ongoingEvents = allEvents
            _events.value = ongoingEvents
        }
    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            try {
                // Call the model to delete the event
                eventosModel.deleteEvent(eventId)

                // Reload events after deletion
                loadEvents()
            } catch (e: Exception) {
                Log.e("EventosViewModel", "Error deleting event", e)
            }
        }
    }

    fun updateEventStatus(id: String) {
        viewModelScope.launch {
            try {
                eventosModel.updateEventStatus(id)

                loadEvents()
            } catch (e: Exception) {
                Log.e("EventosViewModel", "Error deleting event", e)
            }
        }
    }

}