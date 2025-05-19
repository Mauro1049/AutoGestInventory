import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.autogestinventory.AuthRepository.RegisterUser
import com.example.autogestinventory.components.CustomOutlinedTextField
import com.example.autogestinventory.components.MessageCard
import kotlinx.coroutines.*

@Composable
fun RegisterScreen(navController: NavController) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nombres by remember { mutableStateOf("") }
    var nroDoc by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var tipouser by remember { mutableStateOf("") }
    var tipodoc by remember { mutableStateOf("") }

    var message by remember { mutableStateOf<String?>(null) }
    var isRegistering by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

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
                        onValueChange = { nombres = it },
                        label = "Nombres"
                    )
                    CustomOutlinedTextField(
                        value = nroDoc,
                        onValueChange = { nroDoc = it },
                        label = "N° Documento"
                    )
                    CustomOutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        label = "Teléfono"
                    )
                    CustomOutlinedTextField(
                        value = direccion,
                        onValueChange = { direccion = it },
                        label = "Dirección"
                    )
                    CustomOutlinedTextField(
                        value = tipodoc,
                        onValueChange = { tipodoc = it },
                        label = "Tipo de documento"
                    )
                    CustomOutlinedTextField(
                        value = tipouser,
                        onValueChange = { tipouser = it },
                        label = "Tipo de usuario"
                    )
                    CustomOutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        leadingIcon = Icons.Filled.Email
                    )
                    CustomOutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Contraseña",
                        leadingIcon = Icons.Filled.Lock
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            isRegistering = true
                            CoroutineScope(Dispatchers.IO).launch {
                                val result = RegisterUser().registerUser(email, password, nombres, nroDoc, telefono, direccion, tipodoc, tipouser)
                                withContext(Dispatchers.Main) {
                                    isRegistering = false
                                    message = result.getOrNull() ?: result.exceptionOrNull()?.message
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

