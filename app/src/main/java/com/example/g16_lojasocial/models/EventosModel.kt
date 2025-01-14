package com.example.g16_lojasocial.models

import android.util.Log
import com.example.g16_lojasocial.dataClasses.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await


class EventosModel(

    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    // Adds a new event to the "Eventos" collection in Firestore.
    // Returns true if the event is added successfully or false otherwise.
    suspend fun addEvent(
        nome: String,
        descricao: String,
        diaEvento: String,
        imageUrl: String
    ): Boolean {
        return try {
            val eventData = mapOf(
                "nome" to nome,
                "descricao" to descricao,
                "diaEvento" to diaEvento,
                "imageUrl" to imageUrl,
                "estado" to "decorrer"
            )
            firestore.collection("Eventos")
                .add(eventData)
                .await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Fetches all events from the "Eventos" collection in Firestore, ordered by event date.
    // Returns a list of Event objects or an empty list in case of failure.
    suspend fun getEvents(): List<Event> {
        return try {
            val events = firestore.collection("Eventos")
                .orderBy("diaEvento", Query.Direction.ASCENDING)
                .get()
                .await()
                .documents
                .mapNotNull { doc ->
                    doc.toObject(Event::class.java)?.apply {
                        id = doc.id
                    }
                }
            events
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Deletes an event from the "Eventos" collection in Firestore by its ID.
    suspend fun deleteEvent(eventId: String) {
        try {
            FirebaseFirestore.getInstance()
                .collection("Eventos")
                .document(eventId)
                .delete()
                .await()
        } catch (e: Exception) {
            Log.e("EventModel", "Error deleting event", e)
            throw e
        }
    }

    // Updates the status of an event to "terminado" in the "Eventos" collection.
    // Returns true if the status is updated successfully or false otherwise.
    suspend fun updateEventStatus(eventId: String): Boolean {
        return try {
            firestore.collection("Eventos")
                .document(eventId)
                .update("estado", "terminado")
                .await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

}