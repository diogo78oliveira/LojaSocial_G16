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

class ModelPage(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    // Handles login operation
    // Uses Firebase Authentication to sign in a user
    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    // Calls onError with the error message
                    onError(task.exception?.message ?: "Erro ao autenticar.")
                }
            }
    }

    // Checks if the user is a "Voluntário"
    // Looks for a document with the user's UID in the "Voluntarios" collection
    fun checkIfVoluntario(
        uid: String,
        onResult: (Boolean) -> Unit
    ) {
        firestore.collection("Voluntarios")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                // If the document exists, the user is a Voluntário
                onResult(document.exists())
            }
            .addOnFailureListener {
                // In case of an error, return false
                onResult(false)
            }
    }

    // Gets the current logged-in user
    fun getCurrentUser() = auth.currentUser

    // Logs out the currently authenticated user
    fun signout() {
        auth.signOut()
    }

    // Registers a new user and saves their details to Firestore
    fun registerUser(
        email: String,
        password: String,
        nome: String,
        telemovel: String,
        codigoPostal: String,
        respostaAjuda: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // Validates input fields
        if (email.isEmpty() || password.isEmpty() || nome.isEmpty() || telemovel.isEmpty() || codigoPostal.isEmpty()) {
            onError("Todos os campos devem ser preenchidos")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    if (user != null) {
                        val uid = user.uid
                        // Prepares user data to be stored in Firestore
                        val userData = hashMapOf(
                            "nome" to nome,
                            "telemovel" to telemovel,
                            "codPostal" to codigoPostal,
                            "respostaAjuda" to respostaAjuda
                        )

                        // Save user data to the "Voluntarios" collection
                        firestore.collection("Voluntarios").document(uid)
                            .set(userData)
                            .addOnSuccessListener {
                                onSuccess()
                            }
                            .addOnFailureListener { exception ->
                                // Handles Firestore save failure
                                onError("Falha ao salvar dados no Firestore: ${exception.message}")
                            }
                    }
                } else {
                    // Handles authentication failure
                    onError(task.exception?.message ?: "Erro ao registrar")
                }
            }
    }

    // Retrieves all beneficiaries from Firestore
    fun getAllBeneficiarios(
        onSuccess: (List<Beneficiario>) -> Unit,
        onError: (String) -> Unit
    ) {
        firestore.collection("Beneficiarios")
            .get()
            .addOnSuccessListener { documents ->
                val beneficiariosList = documents.map { document ->
                    Beneficiario(
                        id = document.id,
                        nome = document.getString("nome") ?: "",
                        dataNascimento = document.getString("dataNascimento") ?: "",
                        email = document.getString("email") ?: "",
                        telemovel = document.getString("telemovel") ?: "",
                        morada = document.getString("morada") ?: "",
                        codigoPostal = document.getString("codigoPostal") ?: "",
                        nacionalidade = document.getString("nacionalidade") ?: "",
                        cor = document.getString("cor") ?: ""
                    )
                }
                // Calls onSuccess with the list of beneficiaries
                onSuccess(beneficiariosList)
            }
            .addOnFailureListener { exception ->
                // Handles Firestore retrieval failure
                onError("Falha ao obter dados: ${exception.message}")
            }
    }

    // Saves user data to Firestore in the "Beneficiarios" collection
    fun saveUserData(
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
        val currentUser = auth.currentUser
        if (currentUser == null) {
            onError("Utilizador não autenticado.")
            return
        }

        // Creates a map with user data
        val userData = hashMapOf(
            "nome" to nome,
            "dataNascimento" to dataNascimento,
            "email" to email,
            "telemovel" to telemovel,
            "morada" to morada,
            "codigoPostal" to codigoPostal,
            "nacionalidade" to nacionalidade,
            "cor" to cor
        )

        // Saves data to Firestore using the "add" method
        firestore.collection("Beneficiarios")
            .add(userData)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                // Handles failure in saving data
                onError("Falha ao salvar dados: ${exception.message}")
            }
    }

    // Updates existing user data in Firestore
    fun updateUserData(
        documentId: String,
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
        val userData = hashMapOf(
            "nome" to nome,
            "dataNascimento" to dataNascimento,
            "email" to email,
            "telemovel" to telemovel,
            "morada" to morada,
            "codigoPostal" to codigoPostal,
            "nacionalidade" to nacionalidade
        )

        // Updates the document in Firestore
        firestore.collection("Beneficiarios").document(documentId)
            .set(userData)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                // Handles update failure
                onError("Falha ao atualizar dados: ${exception.message}")
            }
    }

    // Saves data related to articles taken by a beneficiary
    fun saveArtigosLevados(
        idBeneficiario: String,
        descArtigo: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val data = mapOf(
            "IDBenificiario" to idBeneficiario,
            "data" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
            "descArtigo" to descArtigo
        )

        // Adds the data to the "ArtigosLevados" collection
        firestore.collection("ArtigosLevados")
            .add(data)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onError("Falha ao salvar dados: ${exception.message}")
            }
    }

    // Fetches all articles associated with a specific beneficiary
    fun getArtigosByBeneficiario(
        idBeneficiario: String,
        onSuccess: (List<Map<String, Any>>) -> Unit,
        onError: (String) -> Unit
    ) {
        firestore.collection("ArtigosLevados")
            .whereEqualTo("IDBenificiario", idBeneficiario)
            .get()
            .addOnSuccessListener { documents ->
                val artigosList = documents.map { document ->
                    mapOf(
                        "data" to document.getString("data").orEmpty(),
                        "descArtigo" to document.getString("descArtigo").orEmpty()
                    )
                }
                onSuccess(artigosList)
            }
            .addOnFailureListener { exception ->
                onError("Erro ao procurar artigos: ${exception.message}")
            }
    }

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

    // Fetches "Avisos" for a specific beneficiary from the Firestore collection.
    // Executes the provided success callback with a list of data-description pairs or the error callback in case of failure.
    fun fetchAvisosForBeneficiary(
        idBeneficiario: String,
        onSuccess: (List<Pair<String, String>>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        firestore.collection("Avisos")
            .whereEqualTo("IDBeneficiario", idBeneficiario)
            .get()
            .addOnSuccessListener { documents ->
                val avisosList = documents.mapNotNull { document ->
                    val descAviso = document.getString("descAviso")
                    val data = document.getString("data")
                    if (descAviso != null && data != null) {
                        data to descAviso
                    } else {
                        null
                    }
                }
                onSuccess(avisosList)
            }
            .addOnFailureListener { exception ->
                onError(exception)
            }
    }

    // Saves a new "Aviso" for a beneficiary in the Firestore collection.
    // Executes the success callback if the operation succeeds, otherwise executes the error callback.

    fun saveAvisos(
        idBeneficiario: String,
        descAviso: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val aviso = hashMapOf(
            "IDBeneficiario" to idBeneficiario,
            "data" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
            "descAviso" to descAviso
        )

        firestore.collection("Avisos")
            .add(aviso)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onError(exception) }
    }

    // Updates the "cor" field of a specific beneficiary in the Firestore collection.
    // Executes the success callback if the operation succeeds, otherwise executes the error callback.
    fun updateBeneficiaryColor(
        beneficiaryId: String,
        newColor: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val beneficiaryRef = firestore.collection("Beneficiarios").document(beneficiaryId)

        beneficiaryRef.update("cor", newColor)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onError(exception)
            }
    }
}

