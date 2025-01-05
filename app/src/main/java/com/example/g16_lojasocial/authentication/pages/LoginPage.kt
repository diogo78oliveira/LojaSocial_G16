package com.example.g16_lojasocial.authentication.pages
import android.content.res.Resources.Theme
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.g16_lojasocial.authentication.AuthState
import com.example.g16_lojasocial.authentication.AuthViewModel

@Composable
fun LogoImage() {
    val context = LocalContext.current
    val imageBitmap = remember {
        try {
            val inputStream = context.assets.open("logo.png")
            BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }

    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap,
            contentDescription = "Logotipo",
            modifier = Modifier.size(350.dp)
        )
    } else {
        Text(text = "Image not found", color = Color.Red)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val authState by authViewModel.authState.observeAsState()

    // Handle Toast for errors here
    authState?.let { state ->
        if (state is AuthState.Error) {
            ShowToastMessage(state.message)
        }
    }

    // Use LaunchedEffect to trigger navigation once authentication state changes
    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            // Navigate to mainScreen only when authenticated
            navController.navigate("mainScreen") {
                // Clear the back stack to prevent navigating back to the login page
                popUpTo("loginPage") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
    ) {
        Column(
            modifier = modifier.fillMaxSize()
                .padding(horizontal = 32.dp)
                .background(Color(0xFFFFFFFF)),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LogoImage()

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = modifier.fillMaxWidth()
                .background(Color(0xFFFFFFFF))
                .shadow(
                    elevation = 15.dp,
                    spotColor = Color(0xFF000000).copy(alpha = 0.55f),
                    shape = RoundedCornerShape(0.dp),
                )){
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = {
                        Text(
                            text = "Insira o email",
                            style = TextStyle(color = Color(0xFFA9B3C1))
                        ) },
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color(0xFFFFFFFF)),
                    textStyle = TextStyle(
                        color = Color(0xFF101214),
                        fontSize = 16.sp
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        cursorColor = Color(0xFF101214),
                    )
                )
            }


            Spacer(modifier = Modifier.height(24.dp))

            Box(modifier = modifier.fillMaxWidth()
                .background(Color(0xFFFFFFFF))
                .shadow(
                    elevation = 15.dp,
                    spotColor = Color(0xFF000000).copy(alpha = 0.55f),
                    shape = RoundedCornerShape(0.dp),
                )){
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = {
                        Text(
                            text = "Insira a palavra-passe",
                            style = TextStyle(color = Color(0xFFA9B3C1))
                        ) },
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color(0xFFFFFFFF)),
                    textStyle = TextStyle(
                        color = Color(0xFF101214),
                        fontSize = 16.sp
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        cursorColor = Color(0xFF101214),
                    ),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            val context = LocalContext.current
                            val iconBitmap = remember(passwordVisible) {
                                try {
                                    val fileName = if (passwordVisible) "eyeClosed.png" else "eye.png"
                                    val inputStream = context.assets.open(fileName)
                                    BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
                                } catch (e: Exception) {
                                    null
                                }
                            }

                            if (iconBitmap != null) {
                                Image(
                                    bitmap = iconBitmap,
                                    contentDescription = if (passwordVisible) "Esconder senha" else "Mostrar senha",
                                    modifier = Modifier.size(50.dp)
                                        .absolutePadding(left = 0.dp, top = 0.dp, right = 10.dp, bottom = 0.dp)
                                )
                            } else {
                                Text(text = "Icon not found", color = Color.Red) // Fallback in case of error
                            }

                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Esqueceu-se da palavra-passe?",
                color = Color(0xFF004EBB),
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.End).clickable { /* Adicionar recuperação de senha */ }
            )

            Spacer(modifier = Modifier.height(40.dp))

            Box(modifier = modifier.fillMaxWidth()
                .background(Color(0xFFFFFFFF))
                .shadow(
                    elevation = 20.dp,
                    spotColor = Color(0xFF000000).copy(alpha = 0.45f),
                    shape = RoundedCornerShape(0.dp),
                )){
                Button(
                    onClick = { authViewModel.login(email, password) },
                    enabled = authState != AuthState.Loading,
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004EBB), disabledContainerColor = Color(0xFF247FFF))
                ) {
                    Text("LOGIN", fontSize = 16.sp, color = Color(0xFFFFFFFF))
                }
            }
        }
    }
}

@Composable
fun ShowToastMessage(message: String) {
    // This is the correct way to show Toast within a composable context
    val context = LocalContext.current
    LaunchedEffect(message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}