package com.example.g16_lojasocial.views

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.example.g16_lojasocial.authentication.AuthViewModel
import com.example.g16_lojasocial.views.ViewsViewModel

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
fun SignupPage(modifier: Modifier = Modifier, navController: NavController, viewsViewModel: ViewsViewModel) {
    var passwordVisible by remember { mutableStateOf(false) }

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }
    var nome by remember {
        mutableStateOf("")
    }

    var telemovel by remember {
        mutableStateOf("")
    }

    var codigoPostal by remember {
        mutableStateOf("")
    }

    val respostaAjuda by remember {
        mutableStateOf("Não")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
    ){
        Column(
            modifier = modifier.fillMaxSize()
                .padding(horizontal = 32.dp)
                .background(Color(0xFFFFFFFF)),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LogoImage()

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = {
                    Text(
                        text = "Insira o email",
                        style = TextStyle(color = Color(0xFF101214))
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
                    focusedIndicatorColor = Color(0xFF101214),
                    unfocusedIndicatorColor = Color(0xFF101214),
                    disabledIndicatorColor = Color(0xFF101214),
                    cursorColor = Color(0xFF101214),
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = {
                    Text(
                        text = "Insira a palavra-passe",
                        style = TextStyle(color = Color(0xFF101214))
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
                    focusedIndicatorColor = Color(0xFF101214),
                    unfocusedIndicatorColor = Color(0xFF101214),
                    disabledIndicatorColor = Color(0xFF101214),
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

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = nome,
                onValueChange = {
                    nome = it
                },
                label = {
                    Text(
                        text = "Insira o nome",
                        style = TextStyle(color = Color(0xFF101214))
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
                    focusedIndicatorColor = Color(0xFF101214),
                    unfocusedIndicatorColor = Color(0xFF101214),
                    disabledIndicatorColor = Color(0xFF101214),
                    cursorColor = Color(0xFF101214),
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = telemovel,
                onValueChange = {
                    telemovel = it
                },
                label = {
                    Text(
                        text = "Insira o telemovel",
                        style = TextStyle(color = Color(0xFF101214))
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
                    focusedIndicatorColor = Color(0xFF101214),
                    unfocusedIndicatorColor = Color(0xFF101214),
                    disabledIndicatorColor = Color(0xFF101214),
                    cursorColor = Color(0xFF101214),
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = codigoPostal,
                onValueChange = {
                    codigoPostal = it
                },
                label = {
                    Text(
                        text = "Insira o codigo postal",
                        style = TextStyle(color = Color(0xFF101214))
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
                    focusedIndicatorColor = Color(0xFF101214),
                    unfocusedIndicatorColor = Color(0xFF101214),
                    disabledIndicatorColor = Color(0xFF101214),
                    cursorColor = Color(0xFF101214),
                )
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                val currentEmail = email
                val currentPassword = password
                val currentNome = nome
                val currentTelemovel = telemovel
                val currentCodigoPostal = codigoPostal
                val currentrespostaAjuda = respostaAjuda

                viewsViewModel.registo(currentEmail, currentPassword, currentNome, currentTelemovel, currentCodigoPostal, currentrespostaAjuda)
            },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004EBB))
            ) {
                Text("Registar voluntario", fontSize = 16.sp, color = Color(0xFFFFFFFF))
            }
        }
    }
}