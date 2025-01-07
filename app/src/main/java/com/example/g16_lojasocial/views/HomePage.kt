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
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.PersonAddAlt
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.core.graphics.toColorInt
import com.example.g16_lojasocial.R
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
    var showDeletePopup by remember { mutableStateOf(false) }
    var showListaArtigosLevadosPopup by remember { mutableStateOf(false) }

    var selectedBeneficiaryId by remember { mutableStateOf("") }
    var selectedBeneficiaryData by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var searchNome by remember { mutableStateOf("") }
    var searchContacto by remember { mutableStateOf("") }



    LaunchedEffect(selectedBeneficiaryId) {
        if (selectedBeneficiaryId.isNotEmpty()) {
            viewsViewModel.fetchArtigosByBeneficiario(selectedBeneficiaryId)
        }
    }


    // Fetch Beneficiarios data when the page is displayed
    LaunchedEffect(Unit) {
        viewsViewModel.fetchBeneficiarios()
    }

    // Observe the Beneficiarios list
    val beneficiariosList by viewsViewModel.beneficiariosList.observeAsState(emptyList())

    // Filtered list based on search text
    val filteredBeneficiarios = beneficiariosList.filter { beneficiario ->
        beneficiario.nome.contains(searchNome, ignoreCase = true) ||
                beneficiario.telemovel.contains(searchNome, ignoreCase = true)
    }

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticaded -> navController.navigate("login")
            else -> Unit
        }
    }

    Box(modifier = modifier.fillMaxSize().background(Color(0xFFFFFFFF))) {
        TextButton(
            onClick = { authViewModel.signout() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text(text = "Sair", fontSize = 16.sp, color = Color(0xFFFF6A6A))
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

        if (showDeletePopup) {
            DeletePopupDialog(
                selectedBeneficiaryId = selectedBeneficiaryId,
                onDismiss = { showDeletePopup = false },
                onConfirm = { itemsLevados ->
                    Log.d("HomePage", "Items levados: $itemsLevados")
                    showDeletePopup = false
                },
                viewModel = viewsViewModel
            )
        }

        if (showListaArtigosLevadosPopup) {
            ArtigoListWithDialog(
                selectedBeneficiaryId = selectedBeneficiaryId,
                showListaArtigosLevadosPopup = showListaArtigosLevadosPopup,
                onDismiss = { showListaArtigosLevadosPopup = false },
                viewModel = viewsViewModel
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(32.dp, 85.dp, 32.dp, 0.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(modifier = modifier.fillMaxWidth()
                .background(Color(0xFFFFFFFF))
                .shadow(
                    elevation = 20.dp,
                    spotColor = Color(0xFF000000).copy(alpha = 0.55f),
                    shape = RoundedCornerShape(0.dp),
                )){
                // Search bar
                OutlinedTextField(
                    value = searchNome,
                    onValueChange = { searchNome = it },
                    placeholder = { Text("Pesquisar Beneficiário", color = Color(0xFFA9B3C1)) },
                    textStyle = TextStyle(color = Color(0xFF101214)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color(0xFFFFFFFF)),
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.search),
                            contentDescription = "Search Icon",
                            modifier = Modifier.size(35.dp).padding(0.dp, 0.dp, 10.dp, 0.dp),
                            tint = Color(0xFFA9B3C1)
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        cursorColor = Color(0xFF101214),
                    )
                )
            }

            Spacer(modifier = Modifier.size(5.dp))

            Box(modifier = modifier.fillMaxWidth().background(Color(0xFFFFFFFF))){
                TextButton(
                    onClick = { showPopup = true },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color(0xFF004EBB))
                        .size(125.dp, 40.dp),
                    colors = ButtonDefaults.textButtonColors(containerColor = Color.Transparent)
                ) {
                    Text(text = "Adicionar", color = Color(0xFFFFFFFF))
                    Spacer(modifier = Modifier.size(10.dp))
                    Icon(
                        painter = painterResource(R.drawable.adduser),
                        contentDescription = "person Icon",
                        tint = Color(0xFFFFFFFF)
                    )
                }
            }

            Spacer(modifier = Modifier.size(5.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 20.dp,
                        spotColor = Color(0xFF000000).copy(alpha = 0.55f),
                        shape = RoundedCornerShape(0.dp),
                    )
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0xFFFFFFFF))
            ) {
                itemsIndexed(filteredBeneficiarios) { index, beneficiario ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (index % 2 == 0) Color(0xFFFFFFFF) else Color(0xFFF3F4F6))
                            .size(60.dp),
                        contentAlignment = Alignment.Center
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
                                    .padding(start = 16.dp),
                                color = Color(0xFF101214)
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(end = 16.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.edit),
                                    contentDescription = "Edit",
                                    tint = Color(0xFF101214),
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
                                    painter = painterResource(R.drawable.check),
                                    contentDescription = "Delete",
                                    tint = Color(0xFF101214),
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable {
                                            selectedBeneficiaryId = beneficiario.id
                                            showDeletePopup = true
                                        }
                                )
                                Icon(
                                    painter = painterResource(R.drawable.list),
                                    contentDescription = "Menu",
                                    tint = Color(0xFF101214),
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable {
                                            selectedBeneficiaryId = beneficiario.id
                                            showListaArtigosLevadosPopup = true
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

    val context = LocalContext.current

    fun isValidTelemovel(telemovel: String): Boolean {
        return telemovel.length == 9 && telemovel.all { it.isDigit() }
    }

    fun isValidCodigoPostal(codigoPostal: String): Boolean {
        return codigoPostal.count { it == '-' } == 1 && codigoPostal.length == 8
    }

    fun isValidEmail(email: String): Boolean {
        return email.contains("@")
    }

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun validateAndSave() {
        if (nome.isEmpty() || dataNascimento.isEmpty() || email.isEmpty() ||
            telemovel.isEmpty() || morada.isEmpty() || codigoPostal.isEmpty() || nacionalidade.isEmpty()) {
            showToast("Todos os campos devem ser preenchidos!")
            return
        }

        if (!isValidTelemovel(telemovel)) {
            showToast("O número de telemóvel deve ter 9 dígitos e conter apenas números.")
            return
        }

        if (!isValidCodigoPostal(codigoPostal)) {
            showToast("O código postal deve ter o formato correto (ex: 1234-567).")
            return
        }

        if (!isValidEmail(email)) {
            showToast("O email deve conter o caractere '@'.")
            return
        }

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
        showToast("Beneficiário adicionado com sucesso!")
        onDismiss()
    }

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            dataNascimento = "$selectedDay/${selectedMonth + 1}/$selectedYear"
        },
        year,
        month,
        day
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFFFFFFF),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.background(Color(0xFFFFFFFF)),
        confirmButton = {
            TextButton(onClick = { validateAndSave() }, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
                Text("Confirmar", color = Color(0xFF004EBB))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
                Text("Cancelar", color = Color(0xFFFF6A6A))
            }
        },
        title = { Text("Adicionar Beneficiário", color = Color(0xFF101214)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome", style = TextStyle(color = Color(0xFFA9B3C1))) },
                    modifier = Modifier.fillMaxWidth().background(Color(0xFFFFFFFF)),
                    textStyle = TextStyle(color = Color(0xFF101214), fontSize = 16.sp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFFFFFFF),
                        unfocusedContainerColor = Color(0xFFFFFFFF),
                        cursorColor = Color(0xFF101214),
                    )
                )

                OutlinedTextField(
                    value = dataNascimento,
                    onValueChange = {},
                    label = { Text("Data de Nascimento", style = TextStyle(color = Color(0xFFA9B3C1))) },
                    modifier = Modifier.fillMaxWidth().clickable { datePickerDialog.show() }.background(Color(0xFFFFFFFF)),
                    textStyle = TextStyle(color = Color(0xFF101214), fontSize = 16.sp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFFFFFFF),
                        unfocusedContainerColor = Color(0xFFFFFFFF),
                        cursorColor = Color(0xFF101214),
                    ),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(imageVector = Icons.Filled.DateRange, contentDescription = "Calendar Icon", tint = Color(0xFF004EBB))
                        }
                    }
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email", style = TextStyle(color = Color(0xFFA9B3C1))) },
                    modifier = Modifier.fillMaxWidth().background(Color(0xFFFFFFFF)),
                    textStyle = TextStyle(color = Color(0xFF101214), fontSize = 16.sp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFFFFFFF),
                        unfocusedContainerColor = Color(0xFFFFFFFF),
                        cursorColor = Color(0xFF101214),
                    )
                )

                OutlinedTextField(
                    value = telemovel,
                    onValueChange = { telemovel = it },
                    label = { Text("Telemóvel", style = TextStyle(color = Color(0xFFA9B3C1))) },
                    modifier = Modifier.fillMaxWidth().background(Color(0xFFFFFFFF)),
                    textStyle = TextStyle(color = Color(0xFF101214), fontSize = 16.sp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFFFFFFF),
                        unfocusedContainerColor = Color(0xFFFFFFFF),
                        cursorColor = Color(0xFF101214),
                    )
                )

                OutlinedTextField(
                    value = morada,
                    onValueChange = { morada = it },
                    label = { Text("Morada", style = TextStyle(color = Color(0xFFA9B3C1))) },
                    modifier = Modifier.fillMaxWidth().background(Color(0xFFFFFFFF)),
                    textStyle = TextStyle(color = Color(0xFF101214), fontSize = 16.sp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFFFFFFF),
                        unfocusedContainerColor = Color(0xFFFFFFFF),
                        cursorColor = Color(0xFF101214),
                    )
                )

                OutlinedTextField(
                    value = codigoPostal,
                    onValueChange = { codigoPostal = it },
                    label = { Text("Codigo Postal", style = TextStyle(color = Color(0xFFA9B3C1))) },
                    modifier = Modifier.fillMaxWidth().background(Color(0xFFFFFFFF)),
                    textStyle = TextStyle(color = Color(0xFF101214), fontSize = 16.sp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFFFFFFF),
                        unfocusedContainerColor = Color(0xFFFFFFFF),
                        cursorColor = Color(0xFF101214),
                    )
                )

                OutlinedTextField(
                    value = nacionalidade,
                    onValueChange = { nacionalidade = it },
                    label = { Text("Nacionalidade", style = TextStyle(color = Color(0xFFA9B3C1))) },
                    modifier = Modifier.fillMaxWidth().background(Color(0xFFFFFFFF)),
                    textStyle = TextStyle(color = Color(0xFF101214), fontSize = 16.sp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFFFFFFF),
                        unfocusedContainerColor = Color(0xFFFFFFFF),
                        cursorColor = Color(0xFF101214),
                    )
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

    fun isValidTelemovel(telemovel: String): Boolean {
        return telemovel.length == 9 && telemovel.all { it.isDigit() }
    }

    fun isValidCodigoPostal(codigoPostal: String): Boolean {
        return codigoPostal.count { it == '-' } == 1 && codigoPostal.length == 8
    }

    fun isValidEmail(email: String): Boolean {
        return email.contains("@")
    }

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val formattedDate = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
                dataNascimento = formattedDate
            },
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
        containerColor = Color(0xFFFFFFFF),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.background(Color(0xFFFFFFFF)),
        title = { Text("Editar Beneficiário", color = Color(0xFF101214)) },
        text = {
            Column {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome", style = TextStyle(color = Color(0xFFA9B3C1))) },
                    modifier = Modifier.fillMaxWidth().background(Color(0xFFFFFFFF)),
                    textStyle = TextStyle(color = Color(0xFF101214), fontSize = 16.sp),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFFFFFFF), unfocusedContainerColor = Color(0xFFFFFFFF), cursorColor = Color(0xFF101214))
                )
                OutlinedTextField(
                    value = dataNascimento,
                    onValueChange = {},
                    label = { Text("Data de Nascimento", style = TextStyle(color = Color(0xFFA9B3C1))) },
                    modifier = Modifier.fillMaxWidth().clickable { datePickerDialog.show() }.background(Color(0xFFFFFFFF)),
                    textStyle = TextStyle(color = Color(0xFF101214), fontSize = 16.sp),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFFFFFFF), unfocusedContainerColor = Color(0xFFFFFFFF), cursorColor = Color(0xFF101214)),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(imageVector = Icons.Filled.DateRange, contentDescription = "Calendar Icon", tint = Color(0xFF004EBB))
                        }
                    }
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email", style = TextStyle(color = Color(0xFFA9B3C1))) },
                    modifier = Modifier.fillMaxWidth().background(Color(0xFFFFFFFF)),
                    textStyle = TextStyle(color = Color(0xFF101214), fontSize = 16.sp),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFFFFFFF), unfocusedContainerColor = Color(0xFFFFFFFF), cursorColor = Color(0xFF101214))
                )
                OutlinedTextField(
                    value = telemovel,
                    onValueChange = { telemovel = it },
                    label = { Text("Telemóvel", style = TextStyle(color = Color(0xFFA9B3C1))) },
                    modifier = Modifier.fillMaxWidth().background(Color(0xFFFFFFFF)),
                    textStyle = TextStyle(color = Color(0xFF101214), fontSize = 16.sp),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFFFFFFF), unfocusedContainerColor = Color(0xFFFFFFFF), cursorColor = Color(0xFF101214))
                )
                OutlinedTextField(
                    value = morada,
                    onValueChange = { morada = it },
                    label = { Text("Morada", style = TextStyle(color = Color(0xFFA9B3C1))) },
                    modifier = Modifier.fillMaxWidth().background(Color(0xFFFFFFFF)),
                    textStyle = TextStyle(color = Color(0xFF101214), fontSize = 16.sp),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFFFFFFF), unfocusedContainerColor = Color(0xFFFFFFFF), cursorColor = Color(0xFF101214))
                )
                OutlinedTextField(
                    value = codigoPostal,
                    onValueChange = { codigoPostal = it },
                    label = { Text("Código Postal", style = TextStyle(color = Color(0xFFA9B3C1))) },
                    modifier = Modifier.fillMaxWidth().background(Color(0xFFFFFFFF)),
                    textStyle = TextStyle(color = Color(0xFF101214), fontSize = 16.sp),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFFFFFFF), unfocusedContainerColor = Color(0xFFFFFFFF), cursorColor = Color(0xFF101214))
                )
                OutlinedTextField(
                    value = nacionalidade,
                    onValueChange = { nacionalidade = it },
                    label = { Text("Nacionalidade", style = TextStyle(color = Color(0xFFA9B3C1))) },
                    modifier = Modifier.fillMaxWidth().background(Color(0xFFFFFFFF)),
                    textStyle = TextStyle(color = Color(0xFF101214), fontSize = 16.sp),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFFFFFFF), unfocusedContainerColor = Color(0xFFFFFFFF), cursorColor = Color(0xFF101214))
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (nome.isBlank() || dataNascimento.isBlank() || !isValidEmail(email) || !isValidTelemovel(telemovel) || !isValidCodigoPostal(codigoPostal)) {
                    showToast("Verifique os campos.")
                } else {
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
                            viewModel.fetchBeneficiarios()
                            onDismiss()
                        },
                        onError = { errorMessage ->
                            showToast(errorMessage)
                        }
                    )
                }
            }, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
                Text("Guardar", color = Color(0xFF004EBB))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
                Text("Cancelar", color = Color(0xFFFF6A6A))
            }
        }
    )
}


@Composable
fun DeletePopupDialog(
    selectedBeneficiaryId: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    viewModel: ViewsViewModel
) {
    var itemsLevados by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFFFFFFF),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.background(Color(0xFFFFFFFF)),
        title = { Text("Itens levados pelo beneficiário", fontSize = 16.sp, color = Color(0xFF101214)) },
        text = {
            Column {
                OutlinedTextField(
                    value = itemsLevados,
                    onValueChange = { itemsLevados = it },
                    placeholder = { Text("Items levados", style = TextStyle(color = Color(0xFFA9B3C1))) },
                    modifier = Modifier.fillMaxWidth()
                        .background(Color(0xFFFFFFFF)),
                    textStyle = TextStyle(
                        color = Color(0xFF101214),
                        fontSize = 16.sp
                    ),
                    colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFFFFFFF),
                                unfocusedContainerColor = Color(0xFFFFFFFF),
                                cursorColor = Color(0xFF101214),
                            )
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (itemsLevados.isNotBlank()) {
                    viewModel.saveArtigosLevados(
                        idBeneficiario = selectedBeneficiaryId,
                        descArtigo = itemsLevados,
                        onSuccess = {
                            onConfirm(itemsLevados)
                            onDismiss()
                        },
                        onError = { error ->
                            Log.e("DeletePopupDialog", "Error saving data: $error")
                        }
                    )
                }
            },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Text("Confirmar", color = Color(0xFF004EBB))
            }
        },
        dismissButton = {

            TextButton(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
                Text("Cancelar", color = Color(0xFFFF6A6A))
            }
        }
    )
}

@Composable
fun ArtigoListWithDialog(
    selectedBeneficiaryId: String,  // Receive this parameter
    showListaArtigosLevadosPopup: Boolean,  // State for showing the popup
    onDismiss: () -> Unit,  // Callback to dismiss the dialog
    viewModel: ViewsViewModel  // The ViewModel to call fetchArtigosByBeneficiario
) {
    // Observe the artigosList LiveData
    val artigosList by viewModel.artigosList.observeAsState(emptyList())  // Default to empty list if no data

    // Trigger fetchArtigosByBeneficiario when the popup is shown
    if (showListaArtigosLevadosPopup) {
        LaunchedEffect(selectedBeneficiaryId) {
            // Fetch artigos when the popup is shown for the first time or beneficiary is changed
            viewModel.fetchArtigosByBeneficiario(selectedBeneficiaryId)
        }

        // Show AlertDialog with LazyColumn displaying artigos
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = Color(0xFFFFFFFF),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.background(Color(0xFFFFFFFF)),
            title = { Text("Artigos Levados", color = Color(0xFF101214)) },
            text = {
                LazyColumn {
                    items(artigosList) { artigo ->
                        Text(
                            text = "Data: ${artigo["data"]}", color = Color(0xFF8C98AB)
                        )
                        Text(
                            text = "Descrição: ${artigo["descArtigo"]}", color = Color(0xFF101214),
                            modifier = Modifier.padding(bottom = 8.dp),

                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Text("Fechar", color = Color(0xFF004EBB))
                }
            }
        )
    }
}

