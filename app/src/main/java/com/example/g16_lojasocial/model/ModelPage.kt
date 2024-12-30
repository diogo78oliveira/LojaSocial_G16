package com.example.g16_lojasocial.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ModelPage(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
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
                            "codPostal" to codigoPostal
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


}

