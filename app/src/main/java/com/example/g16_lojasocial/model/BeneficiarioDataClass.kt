package com.example.g16_lojasocial.model

data class Beneficiario(
    val id: String = "",              // Firestore document ID
    val nome: String = "",            // Name of the beneficiary
    val dataNascimento: String = "",  // Date of birth
    val email: String = "",           // Email address
    val telemovel: String = "",       // Phone number
    val morada: String = "",          // Address
    val codigoPostal: String = "",    // Postal code
    val nacionalidade: String = ""    // Nationality
)