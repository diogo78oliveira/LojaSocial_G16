package com.example.lojasocial_g16.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lojasocial_g16.FireBaseViewModel
import com.example.lojasocial_g16.R

@Composable
fun SignupScreen(navController: NavController, vm: FireBaseViewModel) {
    val emty by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val password by remember { mutableStateOf("") }
    val cpassword by remember { mutableStateOf("") }
    val passwordVisibility by remember { mutableStateOf(false) }
    val cpasswordVisibility by remember { mutableStateOf(false) }

    val erroE by remember { mutableStateOf(false) }
    val erroP by remember { mutableStateOf(false) }
    val erroCP by remember { mutableStateOf(false) }
    val erroC by remember { mutableStateOf(false) }
    val plenght by remember { mutableStateOf(false) }



    Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = Modifier.fillMaxSize()
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (vm.inProgress.value) {
            CircularProgressIndicator()
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 150.dp)
            .verticalScroll(
                rememberScrollState()
            )
    ) {
        Text(
            text = "Sair",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp
        )
        Spacer(modifier = Modifier.height(50.dp))
        if (erroE) {
            Text(
                text = "Insira o Email",
                color = Color.Red,
                modifier = Modifier.padding(end = 100.dp)
                )
        }
        TextField(
            value = email,
            onValueChange = {
                email = it
            },
            label = {
                Text(text = "Email")
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_person_24),
                    contentDescription =null
                )
            },
            trailingIcon = {
                if (email.isNotEmpty()) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_close_24),
                        contentDescription = null,
                        Modifier.clickable { email = emty }
                    )
                }
            }

        )
    }
}