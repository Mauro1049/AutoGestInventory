package com.example.autogestinventory.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.autogestinventory.AuthRepository.LoginUser
import com.example.autogestinventory.Client.SupabaseClient.supabase
import com.example.autogestinventory.components.CustomOutlinedTextField
import com.example.autogestinventory.components.MessageCard
import com.example.autogestinventory.supabase.crudModulos
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LoginScreen(navController: NavController) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var isLoggingIn by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    var passwordVisible by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Iniciar Sesión",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Bienvenido de nuevo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(Modifier.height(16.dp))

                CustomOutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = null
                    },
                    label = "Email",
                    leadingIcon = Icons.Filled.Email,
                    error = emailError != null,
                    errorMessage = emailError
                )


                CustomOutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = null
                    },
                    label = "Contraseña",
                    leadingIcon = Icons.Filled.Lock,
                    error = passwordError != null,
                    errorMessage = passwordError,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val visibilityIcon = if (passwordVisible) Icons.Filled.Lock else Icons.Filled.Lock
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = visibilityIcon, contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña")
                        }
                    }
                )


                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        emailError = null
                        passwordError = null
                        message = null

                        if (!isValidEmail(email) || email.isBlank()) {
                            emailError = "Correo electrónico no válido/está vacio."
                        }
                        if (password.isBlank()) {
                            passwordError = "La contraseña no puede estar vacía."
                        }

                        if (emailError == null && passwordError == null) {
                            isLoggingIn = true
                            CoroutineScope(Dispatchers.IO).launch {
                                val result = LoginUser().loginUser(email, password)
                                withContext(Dispatchers.Main) {
                                    isLoggingIn = false
                                    if (result.isSuccess) {
                                        navController.navigate("tuempresa")
                                    } else {
                                        val errorMsg = result.exceptionOrNull()?.message
                                        message = when {
                                            errorMsg?.contains("invalid_login_credentials", ignoreCase = true) == true ||
                                                    errorMsg?.contains("Invalid login credentials", ignoreCase = true) == true -> {
                                                "Correo o contraseña incorrectos."
                                            }
                                            else -> {
                                                "Ha ocurrido un error. Inténtalo nuevamente."
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    enabled = !isLoggingIn
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Ingresar",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (isLoggingIn) {
                            Spacer(modifier = Modifier.width(8.dp))
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = {
                    navController.navigate("register")
                }) {
                    Text(
                        "¿No tienes cuenta? Regístrate aquí",
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 16.sp
                    )
                }

                message?.let {
                    Spacer(Modifier.height(16.dp))
                    MessageCard(message = it)
                }
            }
        }
    }
}

fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
