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
}

