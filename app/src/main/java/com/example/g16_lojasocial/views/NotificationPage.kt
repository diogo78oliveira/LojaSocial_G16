package com.example.g16_lojasocial.views
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import android.app.DatePickerDialog
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.remember
import java.util.Calendar
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@Composable
fun CircularCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(if (checked) Color(0xFF004EBB) else Color.White, CircleShape)
            .border(2.dp, if (checked) Color(0xFF004EBB) else Color(0xFF8C98AB), CircleShape)
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .border(2.dp, Color(0xFFFFFFFF), CircleShape)
                    .background(Color(0xFF004EBB), CircleShape)
            )
        }
    }
}

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
            .background(Color(0xFFFFFFFF))
            .padding(24.dp, 48.dp, 24.dp, 16.dp)
        ,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Log to see the current value of isVoluntario and selectedDate
        Log.d("NotificationPage", "isVoluntario: $isVoluntario, selectedDate: $selectedDate")

        Card(
            modifier = Modifier.fillMaxWidth()
                .shadow(
                    elevation = 20.dp,
                    spotColor = Color(0xFF000000).copy(alpha = 0.25f),
                    shape = RoundedCornerShape(0.dp),
                )
                .clip(RoundedCornerShape(5.dp))
                .background(Color.White)
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            // Display the question with the selected date if it's available
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Estás disposta a ajudar?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF101214)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (selectedDate != null) {
                        "$selectedDate"
                    } else {
                        "(Data não disponível)"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF8C98AB)
                )

                if (isVoluntario) {
                    // LazyColumn with options "Sim" and "Não"
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()) {
                        items(2) { index ->
                            Spacer(modifier = Modifier.height(20.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularCheckbox(
                                    checked = respostaAjuda == if (index == 0) "Sim" else "Não",
                                    onCheckedChange = {
                                        val selectedResponse = if (index == 0) "Sim" else "Não"
                                        viewsViewModel.updateRespostaAjuda(selectedResponse) { success ->
                                            if (success) {
                                                respostaAjuda = selectedResponse
                                                // Reload Voluntarios after updating respostaAjuda
                                                viewsViewModel.loadVoluntariosSim()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Erro ao salvar a resposta",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                )

                                Spacer(modifier = Modifier.width(20.dp))

                                Text(
                                    text = if (index == 0) "Sim" else "Não",
                                    fontSize = 16.sp,
                                    color = Color(0xFF101214)
                                )
                            }
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(20.dp))

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
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(2.dp, Color(0xFF004EBB), RoundedCornerShape(12.dp))
                            .background(Color(0xFF004EBB)),
                        colors = ButtonDefaults.textButtonColors(containerColor = Color(0xFF004EBB))
                    ) {
                        Text("Alterar dia", color = Color(0xFFFFFFFF))
                    }

                }
            }
        }

        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = "Voluntários que irão estar presentes",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF101214)
        )

        Spacer(modifier = Modifier.height(25.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 20.dp,
                    spotColor = Color(0xFF000000).copy(alpha = 0.55f),
                    shape = RoundedCornerShape(5.dp),
                )
                .clip(RoundedCornerShape(5.dp))
                .background(Color(0xFFFFFFFF))
        ) {
            Column() {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Nome", modifier = Modifier.weight(1f), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF101214))
                    Text("Telemóvel", modifier = Modifier.weight(1f), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF101214))
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(voluntariosSim) { index, voluntario ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (index % 2 == 0) Color(0xFFF3F4F6) else Color(0xFFFFFFFF)),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(voluntario.nome, modifier = Modifier.weight(1f).padding(16.dp, 16.dp, 0.dp, 16.dp), fontSize = 16.sp, color = Color(0xFF101214))
                            Text(voluntario.telemovel, modifier = Modifier.weight(1f), fontSize = 16.sp, color = Color(0xFF101214))
                        }
                    }
                }
            }
        }

    }
}


