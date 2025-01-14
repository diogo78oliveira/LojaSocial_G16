package com.example.g16_lojasocial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.g16_lojasocial.viewmodels.AuthViewModel
import com.example.g16_lojasocial.models.AuthModel
import com.example.g16_lojasocial.ui.theme.G16_LojaSocialTheme
import com.example.g16_lojasocial.viewmodels.EstatisticasViewModel
import com.example.g16_lojasocial.models.EstatisticasModel
import com.example.g16_lojasocial.viewmodels.EventosViewModel
import com.example.g16_lojasocial.models.EventosModel
import com.example.g16_lojasocial.models.HomePageModel
import com.example.g16_lojasocial.models.SignUpModel
import com.example.g16_lojasocial.models.VoluntariosModel
import com.example.g16_lojasocial.viewmodels.HomePageViewModel
import com.example.g16_lojasocial.viewmodels.SignUpViewModel
import com.example.g16_lojasocial.viewmodels.VoluntariosViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val estatisticasModelPage = EstatisticasModel()
        val eventosModelPage = EventosModel()
        val homePageModelPage = HomePageModel()
        val signUpModelPage = SignUpModel()
        val voluntariosModelPage = VoluntariosModel()
        val authModelPage = AuthModel()



        val authViewModel = AuthViewModel(authModelPage) // Pass repository to ViewModel

        val estatisticasViewModel = EstatisticasViewModel(estatisticasModelPage)
        val eventosViewModel = EventosViewModel(eventosModelPage)
        val homePageViewModel = HomePageViewModel(homePageModelPage)
        val signUpViewModel = SignUpViewModel(signUpModelPage)
        val voluntariosViewModel = VoluntariosViewModel(voluntariosModelPage)



        setContent {
            G16_LojaSocialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MyAppNavigation(
                        modifier = Modifier.padding(innerPadding),
                        authViewModel = authViewModel,
                        estatisticasViewModel = estatisticasViewModel,
                        eventosViewModel = eventosViewModel,
                        voluntariosViewModel = voluntariosViewModel,
                        homePageViewModel = homePageViewModel,
                        signUpViewModel = signUpViewModel
                    )
                }
            }
        }
    }
}

