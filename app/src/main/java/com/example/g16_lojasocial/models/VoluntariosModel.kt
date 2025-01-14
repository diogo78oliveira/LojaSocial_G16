package com.example.g16_lojasocial.models

import com.example.g16_lojasocial.dataClasses.Voluntario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class VoluntariosModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    // Saves or updates a specific day associated with a user's ID
    fun saveOrUpdateDia(date: String, onComplete: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val ajudaRef: DocumentReference = firestore.collection("Ajuda").document(userId)

            ajudaRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    // If the document exists, update the "Dia" field
                    ajudaRef.update("Dia", date).addOnCompleteListener { task ->
                        onComplete(task.isSuccessful)
                    }
                } else {
                    // If the document doesn't exist, create a new document with the "Dia" field
                    ajudaRef.set(mapOf("Dia" to date)).addOnCompleteListener { task ->
                        onComplete(task.isSuccessful)
                    }
                }
            }.addOnFailureListener {
                onComplete(false)
            }
        } else {
            // If no user is logged in
            onComplete(false)
        }
    }

    // Retrieves the "Dia" field from the "Ajuda" collection
    fun getDia(onResult: (String?) -> Unit) {
        val ajudaRef: CollectionReference = firestore.collection("Ajuda")

        ajudaRef.get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                // Iterates through documents in the "Ajuda" collection
                for (document in querySnapshot.documents) {
                    val dia = document.getString("Dia")
                    // Return the Dia value (assumes there's only one document or you want the first one)
                    onResult(dia)
                    return@addOnSuccessListener
                }
            } else {
                // No documents in the collection
                onResult(null)
            }
        }.addOnFailureListener {
            // Error in getting data
            onResult(null)
        }
    }

    // Updates the "respostaAjuda" field for the current user in the Firestore collection.
    // Executes the provided callback with a boolean indicating success or failure.
    fun updateRespostaAjuda(resposta: String, onComplete: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val voluntarioRef: DocumentReference =
                firestore.collection("Voluntarios").document(userId)

            voluntarioRef.update("respostaAjuda", resposta)
                .addOnCompleteListener { task ->
                    onComplete(task.isSuccessful)
                }
                .addOnFailureListener {
                    onComplete(false)
                }
        } else {
            // If no user is logged in
            onComplete(false)
        }
    }

    // Retrieves the "respostaAjuda" field for the current user from the Firestore collection.
    // Executes the provided callback with the result or null in case of failure.
    fun getRespostaAjuda(onResult: (String?) -> Unit) {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val voluntarioRef: DocumentReference =
                firestore.collection("Voluntarios").document(userId)
            voluntarioRef.get()
                .addOnSuccessListener { document ->
                    onResult(document.getString("respostaAjuda"))
                }
                .addOnFailureListener {
                    onResult(null)
                }
        } else {
            // No user logged in
            onResult(null)
        }
    }

    // Fetches all volunteers with "respostaAjuda" set to "Sim" from the Firestore collection.
    // Executes the provided callback with the list of volunteers or an empty list in case of failure.
    fun getVoluntariosSim(onResult: (List<Voluntario>) -> Unit) {
        firestore.collection("Voluntarios")
            .whereEqualTo("respostaAjuda", "Sim")
            .get()
            .addOnSuccessListener { documents ->
                val voluntariosList = documents.map { document ->
                    Voluntario(
                        id = document.id,
                        nome = document.getString("nome") ?: "",
                        telemovel = document.getString("telemovel") ?: "",
                        respostaAjuda = document.getString("respostaAjuda") ?: ""
                    )
                }
                onResult(voluntariosList)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    // Updates the "respostaAjuda" field for all documents in the "Voluntarios" collection.
    // Executes the provided callback with a boolean indicating success or failure.
    fun updateRespostaAjudaForAllUsersModel(newResponse: String, callback: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("Voluntarios")

        usersCollection.get().addOnSuccessListener { querySnapshot ->
            val batch = db.batch()
            for (document in querySnapshot.documents) {
                val userRef = document.reference
                batch.update(userRef, "respostaAjuda", newResponse)
            }

            // Commit the batch
            batch.commit().addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
        }.addOnFailureListener {
            callback(false)
        }
    }

}