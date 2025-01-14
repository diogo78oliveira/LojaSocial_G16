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
class AuthModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
)

    {

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

    }