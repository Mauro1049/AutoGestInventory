import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
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
import com.example.autogestinventory.AuthRepository.RegisterUser
import com.example.autogestinventory.Screens.isValidEmail
import com.example.autogestinventory.components.CustomOutlinedTextField
import com.example.autogestinventory.components.MessageCard
import kotlinx.coroutines.*

@Composable
fun RegisterScreen(navController: NavController) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var nombres by remember { mutableStateOf("") }
    var nroDoc by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var tipouser by remember { mutableStateOf("") }
    var tipodoc by remember { mutableStateOf("") }

    var message by remember { mutableStateOf<String?>(null) }
    var isRegistering by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    var nombresError by remember { mutableStateOf<String?>(null) }
    var nroDocError by remember { mutableStateOf<String?>(null) }
    var telefonoError by remember { mutableStateOf<String?>(null) }
    var direccionError by remember { mutableStateOf<String?>(null) }
    var tipoDocumentoError by remember { mutableStateOf<String?>(null) }
    var tipoUsuarioError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf(null) }

    var passwordVisible by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Crea tu Cuenta",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Únete a AutoGest Inventory y simplifica tu gestión.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(Modifier.height(4.dp))

                    CustomOutlinedTextField(
                        value = nombres,
                        onValueChange = {
                            nombres = it
                            nombresError = null
                        },
                        label = "Nombres",
                        leadingIcon = Icons.Filled.Person,
                        error = nombresError != null,
                        errorMessage = nombresError
                    )

                    CustomOutlinedTextField(
                        value = telefono,
                        onValueChange = {
                            telefono = it
                            telefonoError = null
                        },
                        label = "Telefono",
                        leadingIcon = Icons.Filled.Email,
                        error = telefonoError != null,
                        errorMessage = telefonoError
                    )

                    CustomOutlinedTextField(
                        value = direccion,
                        onValueChange = {
                            direccion = it
                            direccionError = null
                        },
                        label = "Direccion",
                        leadingIcon = Icons.Filled.Email,
                        error = direccionError != null,
                        errorMessage = direccionError
                    )

                    CustomOutlinedTextField(
                        value = tipodoc,
                        onValueChange = {
                            tipodoc = it
                            tipoDocumentoError = null
                        },
                        label = "Tipo de Documente",
                        leadingIcon = Icons.Filled.Email,
                        error = tipoDocumentoError != null,
                        errorMessage = tipoDocumentoError
                    )
                    CustomOutlinedTextField(
                        value = nroDoc,
                        onValueChange = {
                            nroDoc = it
                            nroDocError = null
                        },
                        label = "N° Documento",
                        leadingIcon = Icons.Filled.Email,
                        error = nroDocError != null,
                        errorMessage = nroDocError
                    )
                    CustomOutlinedTextField(
                        value = tipouser,
                        onValueChange = {
                            tipouser = it
                            tipoUsuarioError = null
                        },
                        label = "Tipo de usuario",
                        leadingIcon = Icons.Filled.Email,
                        error = tipoUsuarioError != null,
                        errorMessage = tipoUsuarioError
                    )
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
                    CustomOutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            confirmPasswordError = null
                        },
                        label = "Confirmar Contraseña",
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

                    Spacer(Modifier.height(16.dp))

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
                            if(password != confirmPassword){
                                passwordError = "Las contraseñas no coinciden"
                            }
                            if(emailError == null && passwordError == null){
                                isRegistering = true
                                CoroutineScope(Dispatchers.IO).launch {
                                    val result = RegisterUser().registerUser(email, password, nombres, nroDoc, telefono, direccion, tipodoc, tipouser)
                                    if(result.isSuccess){
                                        isRegistering = false
                                        message = result.getOrNull() ?: result.exceptionOrNull()?.message
                                    }else{
                                        val messageError = result.exceptionOrNull()?.message ?: "Error desconocido."
                                        message = when {
                                            messageError.contains("anonymous_provider_disabled", ignoreCase = true) ->
                                                "Por favor, completa el correo y la contraseña para registrarte."
                                            messageError.contains("invalid_email", ignoreCase = true) ->
                                                "El correo electrónico no es válido."
                                            messageError.contains("user already registered", ignoreCase = true) ->
                                                "Este correo ya está registrado."
                                            else -> "Error al registrarse: $messageError"
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        enabled = !isRegistering
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "Registrarse",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (isRegistering) {
                                Spacer(modifier = Modifier.width(8.dp))
                                CircularProgressIndicator(
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    TextButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Text(
                            "¿Ya tienes una cuenta? Inicia sesión",
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 16.sp
                        )
                    }

                    message?.let {
                        Spacer(Modifier.height(8.dp))
                        MessageCard(message = it)
                    }
                }
            }
        }
    }
}

