package com.example.g16_lojasocial.views


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.example.g16_lojasocial.authentication.AuthState
import com.example.g16_lojasocial.authentication.AuthViewModel

@Composable
fun HomePage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    val authState = authViewModel.authState.observeAsState()

    var showPopup by remember { mutableStateOf(false) }

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticaded -> navController.navigate("login")
            else -> Unit
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // "Sign Out" button at the top-left
        TextButton(
            onClick = { authViewModel.signout() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Text(text = "Sign Out")
        }

        // "Adicionar" button at the top-right
        TextButton(
            onClick = { showPopup = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text(text = "Adicionar")
        }

        // Center content
        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Home Page", fontSize = 32.sp)
        }

        // Popup content
        if (showPopup) {
            PopupDialog(onDismiss = { showPopup = false })
        }
    }
}

@Composable
fun PopupDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        title = { Text("Adicionar Informações") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Nome") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Data de Nascimento") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Telemóvel") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Morada") },
                    modifier = Modifier.fillMaxWidth()

                )
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Codigo Postal") },
                    modifier = Modifier.fillMaxWidth()

                )
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("Nacionalidade") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}