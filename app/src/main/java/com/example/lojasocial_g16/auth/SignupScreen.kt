package com.example.lojasocial_g16.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.lojasocial_g16.FireBaseViewModel
import com.example.lojasocial_g16.R

@Composable
fun SignupScreen(navController: NavController, vm: FireBaseViewModel) {
    val emty by remember { mutableStateOf("") }
    val email by remember { mutableStateOf("") }
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

    }



}