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
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun NotificationPage(modifier: Modifier = Modifier, isVoluntario: Boolean, viewsViewModel: ViewsViewModel) {
    val context = LocalContext.current
    var selectedDate by remember { mutableStateOf<String?>(null) }
    var respostaAjuda by remember { mutableStateOf<String?>(null) }

    // Fetch the date from Firestore when `isVoluntario` changes or when the composable is launched
    LaunchedEffect(isVoluntario) {
        viewsViewModel.loadRespostaAjuda()
        viewsViewModel.modelPage.getDia { dia ->
            selectedDate = dia
        }
        // Load the Voluntarios with "Sim" response for respostaAjuda
        viewsViewModel.loadVoluntariosSim()
    }

    // Fetch current respostaAjuda state
    respostaAjuda = viewsViewModel.respostaAjuda
    // List of Voluntarios with "Sim" respostaAjuda
    val voluntariosSim = viewsViewModel.voluntariosSim

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Log to see the current value of isVoluntario and selectedDate
        Log.d("NotificationPage", "isVoluntario: $isVoluntario, selectedDate: $selectedDate")

        // Display the question with the selected date if it's available
        Text(
            text = if (selectedDate != null) {
                "Estás disposta a ajudar no dia $selectedDate?"
            } else {
                "Estás disposta a ajudar? (Data não disponível)"
            },
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (isVoluntario) {
            // LazyColumn with options "Sim" and "Não"
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                items(2) { index ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(Color.White.copy(alpha = 0.1f)),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (index == 0) "Sim" else "Não",
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Checkbox(
                            checked = respostaAjuda == if (index == 0) "Sim" else "Não",
                            onCheckedChange = { isChecked ->
                                val selectedResponse = if (index == 0) "Sim" else "Não"
                                viewsViewModel.updateRespostaAjuda(selectedResponse) { success ->
                                    if (success) {
                                        respostaAjuda = selectedResponse
                                        // Reload Voluntarios after updating respostaAjuda
                                        viewsViewModel.loadVoluntariosSim()
                                    } else {
                                        Toast.makeText(context, "Erro ao salvar a resposta", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            colors = CheckboxDefaults.colors(checkedColor = Color.Green)
                        )
                    }
                }
            }
        } else {
            // "Alterar dia" button for non-voluntarios
            Button(
                onClick = {
                    // Get the current date to set as the default date
                    val calendar = Calendar.getInstance()
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH)
                    val day = calendar.get(Calendar.DAY_OF_MONTH)

                    val datePickerDialog = DatePickerDialog(
                        context,
                        { _, selectedYear, selectedMonth, selectedDay ->
                            val formattedDate = "${selectedDay}/${selectedMonth + 1}/${selectedYear}"
                            selectedDate = formattedDate

                            // Log the selected date
                            Log.d("NotificationPage", "Selected date: $formattedDate")

                            // Save the selected date to Firestore
                            viewsViewModel.saveDateToFirebase(formattedDate) { success ->
                                if (success) {
                                    // Update respostaAjuda for all users to "Não"
                                    viewsViewModel.updateRespostaAjudaForAllUsers("Não") { updateSuccess ->
                                        if (updateSuccess) {
                                            Toast.makeText(context, "Data e resposta atualizadas com sucesso!", Toast.LENGTH_SHORT).show()
                                            // Reload Voluntarios after updating
                                            viewsViewModel.loadVoluntariosSim()
                                        } else {
                                            Toast.makeText(context, "Erro ao atualizar respostas.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Erro ao salvar a data.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        year,
                        month,
                        day
                    )
                    datePickerDialog.show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Alterar dia")
            }

        }

        // Table-like layout for Voluntarios with respostaAjuda = "Sim"
        Spacer(modifier = Modifier.height(30.dp))
        Text(text = "Voluntários que irão estar presentes", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))
        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Nome", modifier = Modifier.weight(1f), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text("Telemóvel", modifier = Modifier.weight(1f), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(voluntariosSim) { voluntario ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.LightGray),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(voluntario.nome, modifier = Modifier.weight(1f), fontSize = 16.sp)
                    Text(voluntario.telemovel, modifier = Modifier.weight(1f), fontSize = 16.sp)
                }
            }
        }
    }
}



