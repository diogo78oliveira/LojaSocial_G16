package com.example.g16_lojasocial.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ModelPage(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    // Handles login operation
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
                    onError(task.exception?.message ?: "Erro ao autenticar.")
                }
            }
    }

    // Checks if the user is a "Voluntário"
    fun checkIfVoluntario(
        uid: String,
        onResult: (Boolean) -> Unit
    ) {
        firestore.collection("Voluntarios")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                onResult(document.exists())
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun getCurrentUser() = auth.currentUser

    fun signout() {
        auth.signOut()
    }

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
                        val userData = hashMapOf(
                            "nome" to nome,
                            "telemovel" to telemovel,
                            "codPostal" to codigoPostal,
                            "respostaAjuda" to respostaAjuda
                        )

                        // Save user data to Firestore
                        firestore.collection("Voluntarios").document(uid)
                            .set(userData)
                            .addOnSuccessListener {
                                onSuccess()
                            }
                            .addOnFailureListener { exception ->
                                onError("Falha ao salvar dados no Firestore: ${exception.message}")
                            }
                    }
                } else {
                    onError(task.exception?.message ?: "Erro ao registrar")
                }
            }
    }


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
                        nacionalidade = document.getString("nacionalidade") ?: ""
                    )
                }
                onSuccess(beneficiariosList)
            }
            .addOnFailureListener { exception ->
                onError("Falha ao obter dados: ${exception.message}")
            }
    }

    fun saveUserData(
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
        val currentUser = auth.currentUser
        if (currentUser == null) {
            onError("Usuário não autenticado.")
            return
        }

        val userData = hashMapOf(
            "nome" to nome,
            "dataNascimento" to dataNascimento,
            "email" to email,
            "telemovel" to telemovel,
            "morada" to morada,
            "codigoPostal" to codigoPostal,
            "nacionalidade" to nacionalidade
        )

        firestore.collection("Beneficiarios")
            .add(userData) // Using `.add` to generate a unique document ID automatically
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onError("Falha ao salvar dados: ${exception.message}")
            }
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

        firestore.collection("Beneficiarios").document(documentId)
            .set(userData)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onError("Falha ao atualizar dados: ${exception.message}")
            }
    }

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

        firestore.collection("ArtigosLevados")
            .add(data)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onError("Falha ao salvar dados: ${exception.message}")
            }
    }

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
                onError("Erro ao buscar artigos: ${exception.message}")
            }
    }

    fun saveOrUpdateDia(date: String, onComplete: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val ajudaRef: DocumentReference = firestore.collection("Ajuda").document(userId)

            ajudaRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    // If the document exists, update the "Dia" field
                    ajudaRef.update("Dia", date).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onComplete(true)
                        } else {
                            onComplete(false)
                        }
                    }
                } else {
                    // If the document doesn't exist, create a new document with the "Dia" field
                    ajudaRef.set(mapOf("Dia" to date)).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onComplete(true)
                        } else {
                            onComplete(false)
                        }
                    }
                }
            }.addOnFailureListener {
                onComplete(false)
            }
        } else {
            onComplete(false) // If no user is logged in
        }
    }

    fun getDia(onResult: (String?) -> Unit) {
        val ajudaRef: CollectionReference = firestore.collection("Ajuda")

        ajudaRef.get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                // Iterate through documents in the "Ajuda" collection
                for (document in querySnapshot.documents) {
                    val dia = document.getString("Dia")
                    // Return the Dia value (assumes there's only one document or you want the first one)
                    onResult(dia)
                    return@addOnSuccessListener
                }
            } else {
                onResult(null) // No documents in the collection
            }
        }.addOnFailureListener {
            onResult(null) // Error in getting data
        }
    }

    fun updateRespostaAjuda(resposta: String, onComplete: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val voluntarioRef: DocumentReference = firestore.collection("Voluntarios").document(userId)

            voluntarioRef.update("respostaAjuda", resposta)
                .addOnCompleteListener { task ->
                    onComplete(task.isSuccessful)
                }
                .addOnFailureListener {
                    onComplete(false)
                }
        } else {
            onComplete(false) // If no user is logged in
        }
    }

    // Function to get "respostaAjuda" from Firestore
    fun getRespostaAjuda(onResult: (String?) -> Unit) {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val voluntarioRef: DocumentReference = firestore.collection("Voluntarios").document(userId)
            voluntarioRef.get()
                .addOnSuccessListener { document ->
                    onResult(document.getString("respostaAjuda"))
                }
                .addOnFailureListener {
                    onResult(null)
                }
        } else {
            onResult(null) // No user logged in
        }
    }

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
                onResult(emptyList())  // Return an empty list in case of failure
            }
    }

fun updateRespostaAjudaForAllUsersModel(newResponse: String, callback: (Boolean) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val usersCollection = db.collection("Voluntarios") // Replace "users" with your collection name

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

    suspend fun addEvent(nome: String, descricao: String, diaEvento: String, estado: String): Boolean {
        return try {
            val eventData = mapOf(
                "nome" to nome,
                "descricao" to descricao,
                "diaEvento" to diaEvento,
                "estado" to estado
            )
            firestore.collection("Eventos")
                .add(eventData)
                .await() // Wait for the task to complete
            true // Return true if successful
        } catch (e: Exception) {
            e.printStackTrace()
            false // Return false if an error occurs
        }
    }

}



