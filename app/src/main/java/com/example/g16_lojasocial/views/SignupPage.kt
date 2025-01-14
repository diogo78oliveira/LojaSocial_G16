package com.example.g16_lojasocial.views

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.g16_lojasocial.viewmodels.SignUpViewModel
import kotlinx.coroutines.delay

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
            contentDescription = "Logotype",
            modifier = Modifier.size(250.dp)
        )
    } else {
        Text(text = "Image not found", color = Color.Red)
    }
}

@Composable
fun SignupPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    signUpViewModel: SignUpViewModel
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nome by remember { mutableStateOf("") }
    var telemovel by remember { mutableStateOf("") }
    var codigoPostal by remember { mutableStateOf("") }
    var respostaAjuda by remember { mutableStateOf("Não") }
    var isSuccess by remember { mutableStateOf(false) }

    // Error states
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var telemovelError by remember { mutableStateOf("") }
    var codigoPostalError by remember { mutableStateOf("") }

    // Email validation function
    fun isValidEmail(email: String): Boolean {
        return email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"))
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    // Check if all fields are valid
    val isFormValid = email.isNotBlank() &&
            emailError.isEmpty() &&
            password.isNotBlank() &&
            passwordError.isEmpty() &&
            nome.isNotBlank() &&
            telemovelError.isEmpty() && telemovel.isNotBlank() &&
            codigoPostalError.isEmpty() && codigoPostal.isNotBlank()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .background(Color(0xFFFFFFFF)),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LogoImage()

            Spacer(modifier = Modifier.height(20.dp))

            // Email input
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = if (isValidEmail(email)) {
                        ""
                    } else {
                        "Formato de email inválido."
                    }
                },
                placeholder = {
                    Text(
                        text = "Insira o email",
                        style = TextStyle(color = Color(0xFFA9B3C1))
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0xFFFFFFFF)),
                textStyle = TextStyle(
                    color = Color(0xFF101214),
                    fontSize = 16.sp
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Color(0xFF101214),
                ),
                isError = emailError.isNotEmpty()
            )
            if (emailError.isNotEmpty()) {
                Text(
                    text = emailError,
                    color = Color.Red,
                    style = TextStyle(fontSize = 12.sp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Password input
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = if (isValidPassword(password)) {
                        ""
                    } else {
                        "A palavra-passe deve ter pelo menos 6 caracteres."
                    }
                },
                placeholder = {
                    Text(
                        text = "Insira a palavra-passe",
                        style = TextStyle(color = Color(0xFFA9B3C1))
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0xFFFFFFFF)),
                textStyle = TextStyle(
                    color = Color(0xFF101214),
                    fontSize = 16.sp
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
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
                            )
                        } else {
                            Text(text = "Icon not found", color = Color.Red)
                        }
                    }
                },
                isError = passwordError.isNotEmpty()
            )
            if (passwordError.isNotEmpty()) {
                Text(
                    text = passwordError,
                    color = Color.Red,
                    style = TextStyle(fontSize = 12.sp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Nome input
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                placeholder = {
                    Text(
                        text = "Insira o nome",
                        style = TextStyle(color = Color(0xFFA9B3C1))
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0xFFFFFFFF)),
                textStyle = TextStyle(
                    color = Color(0xFF101214),
                    fontSize = 16.sp
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Color(0xFF101214),
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Telemovel input
            OutlinedTextField(
                value = telemovel,
                onValueChange = {
                    telemovel = it
                    telemovelError = if (telemovel.matches(Regex("^\\d{9}$"))) {
                        ""
                    } else {
                        "O telemovel deve ter exatamente 9 dígitos."
                    }
                },
                placeholder = {
                    Text(
                        text = "Insira o telemovel",
                        style = TextStyle(color = Color(0xFFA9B3C1))
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0xFFFFFFFF)),
                textStyle = TextStyle(
                    color = Color(0xFF101214),
                    fontSize = 16.sp
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Color(0xFF101214),
                ),
                isError = telemovelError.isNotEmpty()
            )
            if (telemovelError.isNotEmpty()) {
                Text(
                    text = telemovelError,
                    color = Color.Red,
                    style = TextStyle(fontSize = 12.sp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // CodigoPostal input
            OutlinedTextField(
                value = codigoPostal,
                onValueChange = {
                    codigoPostal = it
                    codigoPostalError = if (codigoPostal.matches(Regex("^\\d{4}-\\d{3}$"))) {
                        ""
                    } else {
                        "O código postal deve estar no formato 'nnnn-nnn'."
                    }
                },
                placeholder = {
                    Text(
                        text = "Insira o codigo postal",
                        style = TextStyle(color = Color(0xFFA9B3C1))
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0xFFFFFFFF)),
                textStyle = TextStyle(
                    color = Color(0xFF101214),
                    fontSize = 16.sp
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Color(0xFF101214),
                ),
                isError = codigoPostalError.isNotEmpty()
            )
            if (codigoPostalError.isNotEmpty()) {
                Text(
                    text = codigoPostalError,
                    color = Color.Red,
                    style = TextStyle(fontSize = 12.sp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Button
            Button(
                onClick = {
                    signUpViewModel.registo(
                        email,
                        password,
                        nome,
                        telemovel,
                        codigoPostal,
                        respostaAjuda
                    )
                    isSuccess = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSuccess) Color(0xFF00FF00) else Color(0xFF004EBB)
                ),
                enabled = isFormValid
            ) {
                Text(
                    text = "Registar",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            LaunchedEffect(isSuccess) {
                if (isSuccess) {
                    delay(500L) // Wait for half a second
                    isSuccess = false // Reset success state
                    email = ""
                    password = ""
                    nome = ""
                    telemovel = ""
                    codigoPostal = ""
                }
            }
        }
    }
}
