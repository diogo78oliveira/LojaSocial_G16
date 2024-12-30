package com.example.g16_lojasocial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.g16_lojasocial.authentication.AuthViewModel
import com.example.g16_lojasocial.model.ModelPage
import com.example.g16_lojasocial.views.ViewsViewModel
import com.example.g16_lojasocial.ui.theme.G16_LojaSocialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val modelPage = ModelPage() // Initialize the repository
        val authViewModel = AuthViewModel(modelPage) // Pass repository to ViewModel
        val viewsViewModel = ViewsViewModel(modelPage) // Keep as it was

        setContent {
            G16_LojaSocialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MyAppNavigation(
                        modifier = Modifier.padding(innerPadding),
                        authViewModel = authViewModel,
                        viewsViewModel = viewsViewModel
                    )
                }
            }
        }
    }
}

