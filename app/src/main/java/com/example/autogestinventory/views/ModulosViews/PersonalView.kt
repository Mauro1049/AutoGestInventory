package com.example.autogestinventory.views.ModulosViews

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.autogestinventory.AuthRepository.RegisterUser
import com.example.autogestinventory.Client.SupabaseClient.supabase
import com.example.autogestinventory.model.Empresa
import com.example.autogestinventory.model.Usuario
import com.example.autogestinventory.supabase.crudEmpresa
import com.example.autogestinventory.supabase.crudUsuarios
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PersonalView(navController: NavController) {
    val userId = supabase.auth.currentUserOrNull()?.id
    var idEmpresa by remember { mutableStateOf<Int?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var usuarios by remember { mutableStateOf<List<Usuario>>(emptyList()) }

    // Form fields
    var nombres by remember { mutableStateOf("") }
    var nroDoc by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var tipoDoc by remember { mutableStateOf("") }
    var tipoUser by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    // Empresa
    var empresas by remember { mutableStateOf<List<Empresa>>(emptyList()) }
    var empresaSeleccionada by remember { mutableStateOf<Empresa?>(null) }
    var expanded by remember { mutableStateOf(false) }

    // Load empresas y usuarios
    LaunchedEffect(Unit) {
        userId?.let {
            val empresaResult = crudEmpresa().getEmpresaDelUsuarioActual(userId)
            empresaResult.onSuccess { empresa ->
                empresa.id?.let { id ->
                    idEmpresa = id
                    usuarios = crudUsuarios().obtenerPersonalTodos(id)
                    empresas = crudEmpresa().obtenerTodasLasEmpresas() // Nueva función para listar todas las empresas
                }
            }
        }
    }

    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxSize()) {
        Text("Registrar Personal", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(value = nombres, onValueChange = { nombres = it }, label = { Text("Nombres") })
        OutlinedTextField(value = nroDoc, onValueChange = { nroDoc = it }, label = { Text("Nro. Documento") })
        OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") })
        OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") })
        OutlinedTextField(value = tipoDoc, onValueChange = { tipoDoc = it }, label = { Text("Tipo de Documento") })
        OutlinedTextField(value = tipoUser, onValueChange = { tipoUser = it }, label = { Text("Tipo de Usuario") })
        OutlinedTextField(value = correo, onValueChange = { correo = it }, label = { Text("Correo") })
        OutlinedTextField(value = contrasena, onValueChange = { contrasena = it }, label = { Text("Contraseña") })

        Spacer(modifier = Modifier.height(8.dp))

        // Dropdown para seleccionar empresa
        Text("Selecciona la Empresa:")
        Box {
            OutlinedTextField(
                value = empresaSeleccionada?.nombre ?: "",
                onValueChange = {},
                label = { Text("Empresa") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Button(onClick = { expanded = !expanded }) {
                        Text("Seleccionar")
                    }
                }
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                empresas.forEach { empresa ->
                    DropdownMenuItem(text = { Text(empresa.nombre) }, onClick = {
                        empresaSeleccionada = empresa
                        expanded = false
                    })
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            coroutineScope.launch {
                if (empresaSeleccionada != null) {
                    val result = crudUsuarios().createUser(
                        email = correo,
                        password = contrasena,
                        nombres = nombres,
                        nroDoc = nroDoc,
                        telefono = telefono,
                        direccion = direccion,
                        tipodoc = tipoDoc,
                        tipouser = tipoUser,
                        idEmpresa = empresaSeleccionada!!.id!!
                    )

                    result.onSuccess {
                        usuarios = crudUsuarios().obtenerPersonalTodos(idEmpresa!!)
                        nombres = ""
                        nroDoc = ""
                        telefono = ""
                        direccion = ""
                        tipoDoc = ""
                        tipoUser = ""
                        correo = ""
                        contrasena = ""
                        empresaSeleccionada = null
                    }
                }
            }
        }) {
            Text("Registrar Usuario")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Lista de personal", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(usuarios) { usuario ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("ID: ${usuario.id}")
                        Text("Nombres: ${usuario.nombres}")
                        Text("Tipo de Usuario: ${usuario.tipouser}")
                        Text("Estado: ${usuario.estado}")
                        Text("Correo: ${usuario.correo}")
                        Text("Nro. Documento: ${usuario.nro_doc}")
                        Text("Teléfono: ${usuario.telefono}")
                        Text("Dirección: ${usuario.direccion}")
                        Text("Tipo de Doc.: ${usuario.tipodoc}")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            navController.navigate("configuracion")
        }) {
            Text("Configuración")
        }
    }
}
