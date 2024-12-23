package com.example.g16_lojasocial

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.g16_lojasocial.MainScreen
import com.example.g16_lojasocial.authentication.AuthViewModel
import com.example.g16_lojasocial.authentication.pages.LoginPage
import com.example.g16_lojasocial.views.ViewsViewModel

@Composable
fun MyAppNavigation(modifier: Modifier = Modifier,authViewModel: AuthViewModel, viewsViewModel: ViewsViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login", builder = {
        composable("login"){
            LoginPage(modifier,navController,authViewModel)
        }

        composable("mainScreen") {
            MainScreen(modifier, navController, authViewModel, viewsViewModel)
        }
    })
}