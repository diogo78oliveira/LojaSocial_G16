package com.example.g16_lojasocial.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.example.g16_lojasocial.authentication.AuthViewModel
import com.example.g16_lojasocial.views.ViewsViewModel

@Composable
fun SignupPage(modifier: Modifier = Modifier, navController: NavController, viewsViewModel: ViewsViewModel) {

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }
    var nome by remember {
        mutableStateOf("")
    }

    var telemovel by remember {
        mutableStateOf("")
    }

    var codigoPostal by remember {
        mutableStateOf("")
    }

    val respostaAjuda by remember {
        mutableStateOf("Não")
    }


    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Registo", fontSize = 32.sp)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            label = {
                Text(text = "Email")
            }
        )

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = {
                Text(text = "Password")
            }
        )

        OutlinedTextField(
            value = nome,
            onValueChange = {
                nome = it
            },
            label = {
                Text(text = "Nome")
            }
        )

        OutlinedTextField(
            value = telemovel,
            onValueChange = {
                telemovel = it
            },
            label = {
                Text(text = "Telemóvel")
            }
        )


        OutlinedTextField(
            value = codigoPostal,
            onValueChange = {
                codigoPostal = it
            },
            label = {
                Text(text = "Código Postal")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))



        Button(onClick = {
            val currentEmail = email
            val currentPassword = password
            val currentNome = nome
            val currentTelemovel = telemovel
            val currentCodigoPostal = codigoPostal
            val currentrespostaAjuda = respostaAjuda

            viewsViewModel.registo(currentEmail, currentPassword, currentNome, currentTelemovel, currentCodigoPostal, currentrespostaAjuda)
        }) {
            Text(text = "Registo")
        }

        Spacer(modifier = Modifier.height(8.dp))


    }
}