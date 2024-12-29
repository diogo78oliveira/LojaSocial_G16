package com.example.g16_lojasocial.views


import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.g16_lojasocial.authentication.AuthState
import com.example.g16_lojasocial.authentication.AuthViewModel
import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import java.util.Calendar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    viewsViewModel: ViewsViewModel
) {
    val authState = authViewModel.authState.observeAsState()
    var showPopup by remember { mutableStateOf(false) }

    // Fetch Beneficiarios data when the page is displayed
    LaunchedEffect(Unit) {
        viewsViewModel.fetchBeneficiarios()
    }

    // Observe the Beneficiarios list
    val beneficiariosList by viewsViewModel.beneficiariosList.observeAsState(emptyList())

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticaded -> navController.navigate("login")
            else -> Unit
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // "Sign Out" button
        TextButton(
            onClick = { authViewModel.signout() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Text(text = "Sign Out")
        }

        // "Adicionar" button
        TextButton(
            onClick = { showPopup = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text(text = "Adicionar")
        }

        if (showPopup) {
            PopupDialog(viewModel = viewsViewModel, onDismiss = { showPopup = false })
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Lista de beneficiários", fontSize = 32.sp)

            // Display the list of Beneficiarios
            LazyColumn(
                modifier = Modifier
                    .padding(16.dp)
                    .shadow(2.dp, shape = MaterialTheme.shapes.extraSmall) // Adding shadow here
            ) {
                items(beneficiariosList) { beneficiario ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween, // To space the icons to the right
                            verticalAlignment = Alignment.CenterVertically // To vertically align the icons and text
                        ) {
                            // Name Text
                            Text(
                                text = beneficiario,
                                modifier = Modifier
                                    .weight(1f) // Allow the name to take the remaining space
                                    .padding(start = 16.dp) // Padding to the left
                            )


                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp), // Space between the icons
                                modifier = Modifier.padding(end = 16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Edit, // Example icon
                                    contentDescription = "Icon 1",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable {

                                        }
                                )
                                Icon(
                                    imageVector = Icons.Filled.ExitToApp, // Example icon
                                    contentDescription = "Icon 2",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable {

                                        }
                                )
                                Icon(
                                    imageVector = Icons.Filled.Menu, // Example icon
                                    contentDescription = "Icon 3",
                                    modifier = Modifier.size(24.dp)
                                        .clickable {

                                        }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PopupDialog(viewModel: ViewsViewModel, onDismiss: () -> Unit) {
    var nome by remember { mutableStateOf("") }
    var dataNascimento by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telemovel by remember { mutableStateOf("") }
    var morada by remember { mutableStateOf("") }
    var codigoPostal by remember { mutableStateOf("") }
    var nacionalidade by remember { mutableStateOf("") }

    // State to show or hide the date picker
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            // Update the selected date
            dataNascimento = "$selectedDay/${selectedMonth + 1}/$selectedYear"
        },
        year,
        month,
        day
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                viewModel.saveUserData(
                    nome = nome,
                    dataNascimento = dataNascimento,
                    email = email,
                    telemovel = telemovel,
                    morada = morada,
                    codigoPostal = codigoPostal,
                    nacionalidade = nacionalidade
                )
                onDismiss()
            }) {
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
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Replace DataNascimento input with a calendar picker
                OutlinedTextField(
                    value = dataNascimento,
                    onValueChange = {},
                    label = { Text("Data de Nascimento") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(imageVector = Icons.Filled.DateRange, contentDescription = "Calendar Icon", tint = Color.Gray)
                        }
                    }
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = telemovel,
                    onValueChange = { telemovel = it },
                    label = { Text("Telemóvel") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = morada,
                    onValueChange = { morada = it },
                    label = { Text("Morada") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = codigoPostal,
                    onValueChange = { codigoPostal = it },
                    label = { Text("Codigo Postal") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = nacionalidade,
                    onValueChange = { nacionalidade = it },
                    label = { Text("Nacionalidade") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )


}