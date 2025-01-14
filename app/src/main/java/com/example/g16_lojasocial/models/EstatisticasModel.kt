package com.example.g16_lojasocial.models

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.tasks.await

class EstatisticasModel(

    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val artigosLevadosCollection = firestore.collection("ArtigosLevados")
    private val artigosLevadosTimeChart = firestore.collection("ArtigosLevados")
    private val beneficiariosCollection = firestore.collection("Beneficiarios")


    // Fetches the count of documents in the "ArtigosLevados" collection.
    // Returns the count or 0 in case of an error.
    suspend fun getArtigosLevadosCount(): Int {
        return try {
            val querySnapshot = artigosLevadosCollection.get().await()
            querySnapshot.size()
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    // Fetches and counts occurrences of "nacionalidade" in the "Beneficiarios" collection.
    // Returns a map of nationalities and their counts or an empty map in case of failure.
    suspend fun getNacionalidadeCounts(): Map<String, Int> {
        return try {
            val snapshot = beneficiariosCollection.get().await()
            val nacionalidadeCounts = mutableMapOf<String, Int>()
            for (document in snapshot.documents) {
                val nacionalidade = document.getString("nacionalidade") ?: "Unknown"
                nacionalidadeCounts[nacionalidade] =
                    nacionalidadeCounts.getOrDefault(nacionalidade, 0) + 1
            }
            nacionalidadeCounts
        } catch (e: Exception) {
            e.printStackTrace()
            emptyMap()
        }
    }

    // Retrieves the count of "ArtigosLevados" documents grouped by hour.
    // Returns a map where the keys are hours and values are counts.
    suspend fun getArtigosByHour(): Map<Int, Int> {
        return try {
            val snapshot = artigosLevadosTimeChart.get().await()
            val hourCounts = mutableMapOf<Int, Int>()

            for (document in snapshot.documents) {
                val data = document.getString("data")
                data?.let {
                    val hour = it.substring(11, 13).toIntOrNull()!!
                    hourCounts[hour] = hourCounts.getOrDefault(hour, 0) + 1
                }
            }
            hourCounts
        } catch (e: Exception) {
            e.printStackTrace()
            emptyMap()
        }
    }
}