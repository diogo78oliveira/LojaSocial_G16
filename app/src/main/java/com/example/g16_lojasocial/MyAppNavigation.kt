package com.example.g16_lojasocial

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.g16_lojasocial.authentication.AuthViewModel
import com.example.g16_lojasocial.authentication.pages.HomePage
import com.example.g16_lojasocial.authentication.pages.LoginPage
import com.example.g16_lojasocial.authentication.pages.SignupPage

@Composable
fun MyAppNavigation(modifier: Modifier = Modifier,authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login", builder = {
        composable("login"){
            LoginPage(modifier,navController,authViewModel)
        }
        composable("Registo"){
            SignupPage(modifier,navController,authViewModel)
        }
        composable("home"){
            HomePage(modifier,navController,authViewModel)
        }
    })
}