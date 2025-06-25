package com.example.autogestinventory.views.ModulosViews

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.autogestinventory.AuthRepository.LoginUser
import com.example.autogestinventory.Client.SupabaseClient.supabase
import com.example.autogestinventory.model.KardexDetalle
import com.example.autogestinventory.model.Producto
import com.example.autogestinventory.supabase.crudEmpresa
import com.example.autogestinventory.supabase.crudKardex
import com.example.autogestinventory.supabase.crudProductos
import com.example.autogestinventory.supabase.crudUsuarios
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KardexView(navController: NavController) {
    var expanded by remember { mutableStateOf(false) }
    val userId = supabase.auth.currentUserOrNull()?.id
    var idEmpresa by remember { mutableStateOf<Int?>(null) }
    var listaKardex by remember { mutableStateOf<List<KardexDetalle>>(emptyList()) }

    var showDialog by remember { mutableStateOf(false) }
    var tipoMovimiento by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        userId?.let {
            val empresa = crudEmpresa().getEmpresaDelUsuarioActual(userId)
            empresa.onSuccess {
                idEmpresa = it.id
                val resultado = crudKardex().obtenerKardexPorEmpresa(idEmpresa!!)
                resultado.onSuccess { listaKardex = it }
            }

            crudUsuarios().getUsuarioById(userId).onSuccess { usuarioObtenido ->
                var usuario = usuarioObtenido
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Kardex", fontWeight = FontWeight.Bold, color = Color.White)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
                actions = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Opciones", tint = Color.White)
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.ExitToApp, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Cerrar Sesión")
                                }
                            },
                            onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    LoginUser().signOut()
                                    withContext(Dispatchers.Main) {
                                        navController.navigate("login") {
                                            popUpTo("modulos") { inclusive = true }
                                        }
                                    }
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Home, contentDescription = "Configuracion", tint = MaterialTheme.colorScheme.error)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Configuracion.", color = MaterialTheme.colorScheme.onSurface)
                                }
                            },
                            onClick = {
                                navController.navigate("configuracion")
                            }
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    tipoMovimiento = "entrada"
                    showDialog = true
                }, modifier = Modifier.weight(1f)) {
                    Text("Registrar Entrada")
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(onClick = {
                    tipoMovimiento = "salida"
                    showDialog = true
                }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Text("Registrar Salida")
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(listaKardex) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Producto: ${item.descripcion}", fontWeight = FontWeight.Bold)
                            Text("Cantidad: ${item.cantidad}")
                            Text("Tipo: ${item.tipo}")
                            Text("Fecha: ${item.fecha}")
                            Text("Responsable: ${item.nombres}")
                            Text("Stock: ${item.stock}")
                            Text("Estado: ${item.estado}")
                        }
                    }
                }
            }

            if (showDialog) {
                BuscarProductoDialog(onDismiss = { showDialog = false }, tipo = tipoMovimiento)
            }
        }
    }
}

@Composable
fun BuscarProductoDialog(onDismiss: () -> Unit, tipo: String) {
    var searchQuery by remember { mutableStateOf("") }
    var productos by remember { mutableStateOf(listOf<Producto>()) }
    var selectedProducto by remember { mutableStateOf<Producto?>(null) }
    var cantidad by remember { mutableStateOf("") }
    val userId = supabase.auth.currentUserOrNull()?.id
    var idEmpresa by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        userId?.let {
            val empresa = crudEmpresa().getEmpresaDelUsuarioActual(userId)
            empresa.onSuccess {
                idEmpresa = it.id
                val resultadoProductos = crudProductos().getProductosPorEmpresa(idEmpresa!!)
                resultadoProductos.onSuccess { lista ->
                    productos = lista
                }
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                selectedProducto?.let {
                    CoroutineScope(Dispatchers.IO).launch {
                        val resultado = crudKardex().registrarMovimiento(
                            fecha = "6/17/2025",
                            tipo = tipo,
                            cantidad = cantidad.toIntOrNull() ?: 0,
                            idProducto = it.id,
                            idEmpresa = idEmpresa ?: 0,
                            idUsuario = userId.toString(),
                            detalle = tipo
                        )
                        resultado.onSuccess {
                            withContext(Dispatchers.Main) {
                                onDismiss()
                            }
                        }.onFailure { error ->
                            withContext(Dispatchers.Main) {
                                println("Error al registrar movimiento: ${error.message}")
                            }
                        }
                        withContext(Dispatchers.Main) {
                            onDismiss()
                        }
                        print("boton Guardar")
                    }
                }
            }, enabled = selectedProducto != null && cantidad.isNotBlank()) {
                Text("Guardar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        title = { Text("${tipo.replaceFirstChar { it.uppercase() }} de Producto") },
        text = {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar producto") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.height(120.dp)) {
                    items(productos.filter {
                        it.descripcion.contains(searchQuery, ignoreCase = true)
                    }) { producto ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedProducto = producto }
                                .background(if (producto == selectedProducto) Color.LightGray else Color.Transparent)
                                .padding(8.dp)
                        ) {
                            Text(producto.descripcion)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                selectedProducto?.let { producto ->
                    Card(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Producto: ${producto.descripcion}", fontWeight = FontWeight.Bold)
                            Text("Stock actual: ${producto.stock}")
                            OutlinedTextField(
                                value = cantidad,
                                onValueChange = { cantidad = it },
                                label = { Text("Cantidad") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    )
}
