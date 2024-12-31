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
import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.remember
import java.util.Calendar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    viewsViewModel: ViewsViewModel
) {
    val authState = authViewModel.authState.observeAsState()
    var showPopup by remember { mutableStateOf(false) }
    var showEditPopup by remember { mutableStateOf(false) }
    var selectedBeneficiaryId by remember { mutableStateOf("") }
    var selectedBeneficiaryData by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var searchText by remember { mutableStateOf("") } // Search bar state

    // Fetch Beneficiarios data when the page is displayed
    LaunchedEffect(Unit) {
        viewsViewModel.fetchBeneficiarios()
    }

    // Observe the Beneficiarios list
    val beneficiariosList by viewsViewModel.beneficiariosList.observeAsState(emptyList())

    // Filtered list based on search text
    val filteredBeneficiarios = beneficiariosList.filter {
        it.nome.contains(searchText, ignoreCase = true)
    }

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

        if (showEditPopup) {
            EditPopupDialog(
                selectedBeneficiaryId = selectedBeneficiaryId,
                selectedBeneficiaryData = selectedBeneficiaryData,
                viewModel = viewsViewModel,
                onDismiss = { showEditPopup = false }
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Lista de beneficiários", fontSize = 32.sp)

            // Search bar
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Procurar por nome") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                }
            )

            // Display the filtered list of Beneficiarios
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, shape = MaterialTheme.shapes.extraSmall)
            ) {
                items(filteredBeneficiarios) { beneficiario ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = beneficiario.nome,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 16.dp)
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(end = 16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = "Edit",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable {
                                            selectedBeneficiaryId = beneficiario.id
                                            selectedBeneficiaryData = mapOf(
                                                "nome" to beneficiario.nome,
                                                "dataNascimento" to beneficiario.dataNascimento,
                                                "email" to beneficiario.email,
                                                "telemovel" to beneficiario.telemovel,
                                                "morada" to beneficiario.morada,
                                                "codigoPostal" to beneficiario.codigoPostal,
                                                "nacionalidade" to beneficiario.nacionalidade
                                            )
                                            showEditPopup = true
                                        }
                                )
                                Icon(
                                    imageVector = Icons.Filled.ExitToApp,
                                    contentDescription = "Delete",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable {
                                            // Handle delete action
                                        }
                                )
                                Icon(
                                    imageVector = Icons.Filled.Menu,
                                    contentDescription = "Menu",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable {
                                            // Handle additional actions
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
                viewModel.fetchBeneficiarios()
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
                            Icon(
                                imageVector = Icons.Filled.DateRange,
                                contentDescription = "Calendar Icon",
                                tint = Color.Gray
                            )
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

@Composable
fun EditPopupDialog(
    selectedBeneficiaryId: String,
    selectedBeneficiaryData: Map<String, String>,
    viewModel: ViewsViewModel,
    onDismiss: () -> Unit
) {
    var nome by remember { mutableStateOf(selectedBeneficiaryData["nome"] ?: "") }
    var dataNascimento by remember { mutableStateOf(selectedBeneficiaryData["dataNascimento"] ?: "") }
    var email by remember { mutableStateOf(selectedBeneficiaryData["email"] ?: "") }
    var telemovel by remember { mutableStateOf(selectedBeneficiaryData["telemovel"] ?: "") }
    var morada by remember { mutableStateOf(selectedBeneficiaryData["morada"] ?: "") }
    var codigoPostal by remember { mutableStateOf(selectedBeneficiaryData["codigoPostal"] ?: "") }
    var nacionalidade by remember { mutableStateOf(selectedBeneficiaryData["nacionalidade"] ?: "") }

    val context = LocalContext.current

    // Date Picker Dialog
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val formattedDate = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
                dataNascimento = formattedDate
            },
            // Set default date to today or existing value
            Calendar.getInstance().apply {
                time = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dataNascimento) ?: Date()
            }.let {
                it.get(Calendar.YEAR)
            },
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Beneficiário") },
        text = {
            Column {
                TextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome") })
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
                            Icon(
                                imageVector = Icons.Filled.DateRange,
                                contentDescription = "Calendar Icon",
                                tint = Color.Gray
                            )
                        }
                    }
                )
                TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
                TextField(value = telemovel, onValueChange = { telemovel = it }, label = { Text("Telemóvel") })
                TextField(value = morada, onValueChange = { morada = it }, label = { Text("Morada") })
                TextField(value = codigoPostal, onValueChange = { codigoPostal = it }, label = { Text("Código Postal") })
                TextField(value = nacionalidade, onValueChange = { nacionalidade = it }, label = { Text("Nacionalidade") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                // Update user data
                viewModel.updateUserData(
                    documentId = selectedBeneficiaryId,
                    nome = nome,
                    dataNascimento = dataNascimento,
                    email = email,
                    telemovel = telemovel,
                    morada = morada,
                    codigoPostal = codigoPostal,
                    nacionalidade = nacionalidade,
                    onSuccess = {
                        viewModel.fetchBeneficiarios() // Refresh the list
                        onDismiss() // Close the dialog
                    },
                    onError = { errorMessage ->
                        // Show error message (optional)
                        Log.e("EditPopupDialog", "Error updating user: $errorMessage")
                    }
                )
            }) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}