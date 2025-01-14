package com.example.g16_lojasocial.models

import com.example.g16_lojasocial.dataClasses.Beneficiario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomePageModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
)
    {

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
            cor: String,
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
                "nacionalidade" to nacionalidade,
                "cor" to cor
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


