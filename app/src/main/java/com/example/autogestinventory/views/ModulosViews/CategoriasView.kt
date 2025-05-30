package com.example.autogestinventory.views.ModulosViews

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.autogestinventory.AuthRepository.LoginUser
import com.example.autogestinventory.Client.SupabaseClient.supabase
import com.example.autogestinventory.model.Categoria
import com.example.autogestinventory.model.Marca
import com.example.autogestinventory.supabase.crudCategorias
import com.example.autogestinventory.supabase.crudEmpresa
import com.example.autogestinventory.supabase.crudMarca
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriasView(navController: NavController){
    var categorias by remember { mutableStateOf<List<Categoria>>(emptyList()) }
    val userId = supabase.auth.currentUserOrNull()?.id
    var idEmpresa by remember { mutableStateOf<Int?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var nuevaDescripcion by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }

    var categoriaAEditar by remember { mutableStateOf<Categoria?>(null) }
    var descripcionEditada by remember { mutableStateOf("") }
    var colorEditada by remember { mutableStateOf("") }

    var isNuevaCateogiraVisible by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    suspend fun cargarCategorias(idEmpresa: Int) {
        val resultado = crudCategorias().getCategoriasPorEmpresa(idEmpresa)
        if (resultado.isSuccess) {
            categorias = resultado.getOrDefault(emptyList())
        }
    }

    LaunchedEffect(Unit) {
        userId?.let { user ->
            val empresa = crudEmpresa().getEmpresaDelUsuarioActual(userId)
            empresa.onSuccess { empresa ->
                idEmpresa = empresa.id
                empresa.id?.let {
                    idEmpresa?.let { it1 ->
                        crudCategorias().getCategoriasPorEmpresa(it1).onSuccess { lista ->
                            categorias = lista
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Ajustes",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Opciones", tint = Color.White)
                    }
                    DropdownMenu(expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.ExitToApp, contentDescription = "Cerrar Sesión", tint = MaterialTheme.colorScheme.error)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Cerrar Sesión", color = MaterialTheme.colorScheme.onSurface)
                                }
                            },
                            onClick = {
                                expanded = false
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        LoginUser().signOut()
                                        withContext(Dispatchers.Main) {
                                            navController.navigate("login") {
                                                popUpTo("modulos") { inclusive = true }
                                            }
                                        }
                                    } catch (e: Exception) {
                                        println("Error al cerrar sesión: ${e.message}")
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
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { isNuevaCateogiraVisible = !isNuevaCateogiraVisible }) {
                Icon(Icons.Filled.Add, "Añadir nuevo producto")
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
                visible = isNuevaCateogiraVisible,
                enter = slideInVertically(initialOffsetY = { -it }),
                exit = slideOutVertically(targetOffsetY = { -it })
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Nueva Categoria")

                        OutlinedTextField(
                            value = nuevaDescripcion,
                            onValueChange = { nuevaDescripcion = it },
                            label = { Text("Nueva Categoria") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = color,
                            onValueChange = { color = it },
                            label = { Text("Color") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    idEmpresa?.let { empresaId ->
                                        val resultado = crudCategorias().insertarCategoria(nuevaDescripcion, idEmpresa!!, color)
                                        if (resultado.isSuccess) {
                                            nuevaDescripcion = ""
                                            val recargar = crudCategorias().getCategoriasPorEmpresa(empresaId)
                                            if (recargar.isSuccess) {
                                                categorias = recargar.getOrDefault(emptyList())
                                            }
                                        } else {
                                            println("Error al insertar marca: ${resultado.exceptionOrNull()?.message}")
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Guardar Categoria")
                        }

                    }
                }
            }

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
                        Text("Lista de Categorias", style = MaterialTheme.typography.titleLarge)
                        Icon(Icons.Filled.List, contentDescription = "Lista de categorias", tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyColumn {
                        items(categorias) { categoria ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(
                                    Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth()
                                ) {
                                    Text(text = "ID: ${categoria.id}")
                                    Text(text = "Descripción: ${categoria.descripcion}")
                                    Text(text = "Empresa: ${categoria.id_empresa}")
                                    Text(text = "Color: ${categoria.color}")
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row (horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {

                                        Button(
                                            onClick = {
                                                categoriaAEditar = categoria
                                                descripcionEditada = categoria.descripcion
                                            },
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = Color.White)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Editar", color = Color.White)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))

                                        Button(
                                            onClick = {
                                                categoria.id.let { idCategoria ->
                                                    coroutineScope.launch {
                                                        val result = crudCategorias().elimarCategoria(idCategoria)
                                                        if (result.isSuccess){
                                                            idEmpresa?.let { cargarCategorias(it) }
                                                            println("Eliminado")
                                                        }
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
            }

            categoriaAEditar?.let {
                AlertDialog(
                    onDismissRequest = {
                        categoriaAEditar = null
                        descripcionEditada = ""
                    },
                    title = { Text("Editar Marca") },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = descripcionEditada,
                                onValueChange = { descripcionEditada = it },
                                label = { Text("Descripcion") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = colorEditada,
                                onValueChange = { colorEditada = it },
                                label = { Text("Color") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    categoriaAEditar?.id.let {
                                        val result = it?.let { it1 -> crudCategorias().editarCategoria(it1, descripcionEditada, colorEditada) }
                                        idEmpresa.let {
                                            if (it != null) {
                                                cargarCategorias(it)
                                            }
                                        }
                                        categoriaAEditar = null
                                        descripcionEditada = ""
                                    }
                                }
                            }
                        ) {
                            Text("Guardar")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                categoriaAEditar = null
                                descripcionEditada = ""
                            }
                        ) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}