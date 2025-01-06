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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

    Box(modifier = modifier.fillMaxSize().background(Color(0xFFFFFFFF))) {
        Column() {
            if (!isVoluntario) {
                Row(
                    modifier = Modifier
                        .padding(16.dp, 32.dp, 16.dp, 0.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Eventos a decorrer",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF101214)
                    )

                    Button(
                        onClick = { showDialog = true },
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color(0xFF004EBB))
                            .size(120.dp, 40.dp),
                        colors = ButtonDefaults.textButtonColors(containerColor = Color.Transparent)
                    ) {
                        Text("Novo Evento", color = Color(0xFFFFFFFF), fontSize = 12.sp)
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.padding(16.dp, 40.dp, 16.dp, 40.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(ongoingEvents.chunked(2)) { rowEvents ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        rowEvents.forEach { event ->
                            EventCard(
                                event = event,
                                viewsViewModel = viewsViewModel,
                                isVoluntario = isVoluntario,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (rowEvents.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Text(
                text = "Eventos acabados",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF101214),
                modifier = Modifier.padding(start = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.padding(16.dp, 40.dp, 16.dp, 40.dp),

            ) {
                items(pastEvents.chunked(2)) { rowEvents ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        rowEvents.forEach { event ->
                            EventCard(
                                event = event,
                                viewsViewModel = viewsViewModel,
                                isVoluntario = isVoluntario,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (rowEvents.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
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
fun EventCard(event: Event, viewsViewModel: ViewsViewModel, isVoluntario: Boolean, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .shadow(
                elevation = 20.dp,
                spotColor = Color(0xFF000000).copy(alpha = 0.25f),
                shape = RoundedCornerShape(0.dp),
            )
            .clip(RoundedCornerShape(5.dp))
            .background(Color.White),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (event.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = event.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(0.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text(event.nome, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF101214))
                Text(event.diaEvento, fontSize = 14.sp, color = Color(0xFF8C98AB))
                Text(event.descricao, fontSize = 14.sp, color = Color(0xFFC8CED7))

                if (!isVoluntario) {
                    IconButton(onClick = { viewsViewModel.deleteEvent(event.id) }) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Delete Event",
                            tint = Color(0xFFFF6A6A)
                        )
                    }
                }
            }


        }
    }
}


