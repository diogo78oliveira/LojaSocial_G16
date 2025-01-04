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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.remember
import java.util.Calendar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import com.example.g16_lojasocial.model.Event
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import coil.compose.AsyncImage


@Composable
fun Eventos(
    modifier: Modifier = Modifier,
    isVoluntario: Boolean,
    viewsViewModel: ViewsViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var diaEvento by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    val context = LocalContext.current

    val events by viewsViewModel.events.collectAsState()

    LaunchedEffect(Unit) {
        viewsViewModel.loadEvents()
    }

    val calendar = Calendar.getInstance()
    val today = calendar.timeInMillis

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                diaEvento = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = today // Restrict to today's date or later
        }
    }

    // Filter events into two categories
    val ongoingEvents = events.filter { event ->
        val eventDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(event.diaEvento)?.time ?: 0L
        eventDate >= today
    }
    val pastEvents = events.filter { event ->
        val eventDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(event.diaEvento)?.time ?: 0L
        eventDate < today
    }

    Box(modifier = modifier.fillMaxSize()) {

        if (!isVoluntario) {
            Column(
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp) // Adjust padding for top section
            ) {
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier
                        .padding(top = 8.dp) // Adjust space between title and button
                ) {
                    Text("Adicionar Evento")
                }
            }
        }

        // Main content column
        Column(
            modifier = Modifier
                .padding(top = if (isVoluntario) 16.dp else 96.dp, start = 16.dp, end = 16.dp) // Adjust padding based on whether the user is a volunteer
        ) {

            // "Eventos a decorrer" title
            Text(
                "Eventos a decorrer",
                fontSize = 24.sp, // Text size
                fontWeight = FontWeight.Bold,
                color = Color.Black // Ensure consistent text color
            )

            // Ongoing Events Section
            LazyColumn(
                modifier = Modifier.padding(top = 16.dp) // Adjust space between title and events list
            ) {
                items(ongoingEvents) { event ->
                    EventCard(event = event, viewsViewModel = viewsViewModel, isVoluntario = isVoluntario)
                }
            }

            // "Eventos acabados" Title with reduced padding
            Text(
                text = "Eventos acabados",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp) // Reduced padding between titles
            )

            // Past Events Section
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(pastEvents) { event ->
                    EventCard(event = event, viewsViewModel = viewsViewModel, isVoluntario = isVoluntario)
                }
            }
        }

        // Add event dialog
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
                        OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("URL da imagem") })
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewsViewModel.addEvent(nome, descricao, diaEvento, imageUrl) { success ->
                                if (success) {
                                    Toast.makeText(context, "Evento adicionado com sucesso!", Toast.LENGTH_SHORT).show()
                                    viewsViewModel.loadEvents() // Reload the events after adding
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


@Composable
fun EventCard(event: Event, viewsViewModel: ViewsViewModel, isVoluntario: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (event.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = event.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(event.nome, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(event.diaEvento, fontSize = 14.sp, color = Color.Gray)
                Text(event.descricao, fontSize = 14.sp)
            }
            if (!isVoluntario) {
                IconButton(onClick = { viewsViewModel.deleteEvent(event.id) }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete Event")
                }
            }
        }
    }
}

