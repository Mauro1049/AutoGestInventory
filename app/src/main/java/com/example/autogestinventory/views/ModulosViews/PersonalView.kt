package com.example.autogestinventory.views.ModulosViews

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.autogestinventory.AuthRepository.RegisterUser
import com.example.autogestinventory.Client.SupabaseClient.supabase
import com.example.autogestinventory.model.Empresa
import com.example.autogestinventory.model.Modulos
import com.example.autogestinventory.model.Usuario
import com.example.autogestinventory.supabase.crudEmpresa
import com.example.autogestinventory.supabase.crudModulos
import com.example.autogestinventory.supabase.crudProductos
import com.example.autogestinventory.supabase.crudUsuarios
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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

    // Modulo
    var modulos by remember { mutableStateOf<List<Modulos>>(emptyList()) }
    var modulosSeleccionados by remember { mutableStateOf(listOf<Modulos>()) }
    var expandedModulos by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    var isNuevoUsuarioVisible by remember { mutableStateOf(false) }

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
            modulos = crudModulos().obtenerTodosLosModulos()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Productos", fontWeight = FontWeight.Bold, color = Color.White)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { isNuevoUsuarioVisible = !isNuevoUsuarioVisible }) {
                Icon(Icons.Filled.Add, "Añadir nuevo Usuario")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            AnimatedVisibility(
                visible = isNuevoUsuarioVisible,
                enter = slideInVertically(initialOffsetY = { -it }),
                exit = slideOutVertically(targetOffsetY = { -it })
            ) {

                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        item {
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

                            Text("Selecciona los módulos:")
                        }

                        items(modulos) { modulo ->
                            val isSelected = modulosSeleccionados.contains(modulo)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        modulosSeleccionados = if (isSelected) {
                                            modulosSeleccionados - modulo
                                        } else {
                                            modulosSeleccionados + modulo
                                        }
                                    }
                                    .padding(8.dp)
                            ) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = {
                                        modulosSeleccionados = if (isSelected) {
                                            modulosSeleccionados - modulo
                                        } else {
                                            modulosSeleccionados + modulo
                                        }
                                    }
                                )
                                Text(modulo.nombre)
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
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
                                                idEmpresa = empresaSeleccionada!!.id!!,
                                                modulosSeleccionados = modulosSeleccionados
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
                                                modulosSeleccionados = emptyList()
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Registrar Usuario")
                            }
                        }
                    }

                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = !isNuevoUsuarioVisible,
                enter = slideInVertically(initialOffsetY = { -it }),
                exit = slideOutVertically(targetOffsetY = { -it })
            ) {
                Text("Lista de personal", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Lista de Productos", style = MaterialTheme.typography.titleLarge)
                            Icon(Icons.Filled.List, contentDescription = "Lista de productos", tint = MaterialTheme.colorScheme.primary)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        LazyColumn {
                            items(usuarios) { usuario ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(
                                        Modifier.padding(16.dp)
                                            .fillMaxWidth()
                                    ) {
                                        Text("ID: ${usuario.id}")
                                        Text("Nombres: ${usuario.nombres}")
                                        Text("Tipo de Usuario: ${usuario.tipouser}")
                                        Text("Estado: ${usuario.estado}")
                                        Text("Correo: ${usuario.correo}")
                                        Text("Nro. Documento: ${usuario.nro_doc}")
                                        Text("Teléfono: ${usuario.telefono}")
                                        Text("Dirección: ${usuario.direccion}")
                                        Text("Tipo de Doc.: ${usuario.tipodoc}")

                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                            Button(
                                                onClick = {},
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = Color.White)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Editar", color = Color.White)
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Button(
                                                onClick = {
                                                    coroutineScope.launch {
                                                        if(usuario.id != null){
                                                            val result = crudUsuarios().elimarUsuario(usuario.id)
                                                        }
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = Color.White)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Eliminar", color = Color.White)
                                            }
                                        }

                                    }
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
        }
    }
}
