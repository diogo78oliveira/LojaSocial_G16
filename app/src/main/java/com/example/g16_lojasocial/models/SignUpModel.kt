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

class SignUpModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

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

}