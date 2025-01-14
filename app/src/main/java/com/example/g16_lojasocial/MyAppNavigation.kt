package com.example.g16_lojasocial

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.g16_lojasocial.viewmodels.AuthViewModel
import com.example.g16_lojasocial.views.LoginPage
import com.example.g16_lojasocial.viewmodels.EstatisticasViewModel
import com.example.g16_lojasocial.viewmodels.EventosViewModel
import com.example.g16_lojasocial.viewmodels.VoluntariosViewModel
import com.example.g16_lojasocial.viewmodels.SignUpViewModel
import com.example.g16_lojasocial.viewmodels.HomePageViewModel

@Composable
fun MyAppNavigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel, estatisticasViewModel: EstatisticasViewModel, eventosViewModel: EventosViewModel, voluntariosViewModel: VoluntariosViewModel, signUpViewModel: SignUpViewModel, homePageViewModel: HomePageViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login", builder = {
        composable("login"){
            LoginPage(modifier,navController,authViewModel)
        }

        composable("mainScreen") {
            MainScreen(modifier, navController, authViewModel, estatisticasViewModel, eventosViewModel, voluntariosViewModel, signUpViewModel, homePageViewModel)
        }
    })
}




