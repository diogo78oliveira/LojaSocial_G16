package com.example.g16_lojasocial.views


import com.example.g16_lojasocial.views.ViewsViewModel
import com.example.g16_lojasocial.model.ModelPage
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
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Eventos(
    modifier: Modifier = Modifier,
    isVoluntario: Boolean,
    viewsViewModel: ViewsViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    var nome by remember { mutableStateOf("") }
    var estado = "decorrer"
    var descricao by remember { mutableStateOf("") }
    var diaEvento by remember { mutableStateOf("") }
    val context = LocalContext.current

    val calendar = Calendar.getInstance()
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                diaEvento = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Eventos Page", fontSize = 40.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Is Voluntário: $isVoluntario", fontSize = 20.sp)
        }

        if (!isVoluntario) {
            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Text("Adicionar Evento")
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Adicionar Evento") },
                text = {
                    Column {
                        OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome") })
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = descricao, onValueChange = { descricao = it }, label = { Text("Descrição") })
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = diaEvento,
                            onValueChange = {},
                            label = { Text("Dia do evento") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { datePickerDialog.show() },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { datePickerDialog.show() }) {
                                    Icon(Icons.Filled.DateRange, contentDescription = "Calendar Icon")
                                }
                            }
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewsViewModel.addEvent(nome, descricao, diaEvento, estado) { success ->
                                if (success) {
                                    Toast.makeText(context, "Evento adicionado com sucesso!", Toast.LENGTH_SHORT).show()
                                    showDialog = false
                                } else {
                                    Toast.makeText(context, "Erro ao adicionar evento!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        enabled = nome.isNotBlank() && descricao.isNotBlank() && diaEvento.isNotBlank() // Disable button if fields are empty
                    ) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

