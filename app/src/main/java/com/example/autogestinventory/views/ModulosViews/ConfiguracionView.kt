package com.example.autogestinventory.views.ModulosViews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.autogestinventory.AuthRepository.LoginUser
import com.example.autogestinventory.Client.SupabaseClient.supabase
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.autogestinventory.components.ConfiguracionItem
import com.example.autogestinventory.supabase.crudModulos

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionView(navController: NavController) {

    val  allowedModules = remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        val idAuth = supabase.auth.currentUserOrNull()?.id
        if (idAuth != null) {
            try {
                val modulos = crudModulos().obtenerModulosAsignados(idAuth)
                allowedModules.value = modulos.map { it.trim() }.filter { it.isNotBlank() }
                println("Módulos limpios: $allowedModules")
            } catch (e: Exception) {
                println("Error al obtener módulos: ${e.message}")
            }
        }
    }


    val user = remember {
        mutableStateOf(supabase.auth.currentUserOrNull()?.email ?: "Usuario")
    }

    var expanded by remember { mutableStateOf(false) }

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
                                    Icon(Icons.Filled.ExitToApp, contentDescription = "Usuario Config.", tint = MaterialTheme.colorScheme.error)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Usuario Config.", color = MaterialTheme.colorScheme.onSurface)
                                }
                            },
                            onClick = {
                            }
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Gestion del Inventario",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Administra las diferentes secciones de tu inventario.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    if("Productos" in allowedModules.value){
                        ConfiguracionItem(
                            text = "Productos",
                            icon = Icons.Filled.Person,
                            onClick = { navController.navigate("productos") }
                        )
                    }
                    if("Categoria de productos" in allowedModules.value){
                        ConfiguracionItem(
                            text = "Categoría",
                            icon = Icons.Filled.AddCircle, // Ícono más corto
                            onClick = { navController.navigate("categorias") }
                        )
                    }
                    if ("Marca de productos" in allowedModules.value) {
                        ConfiguracionItem(
                            text = "Marca",
                            icon = Icons.Filled.Build,
                            onClick = { navController.navigate("marca") }
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Administración",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Gestiona el personal y la información de tu empresa.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if ("Personal" in allowedModules.value) {
                        ConfiguracionItem(
                            text = "Personal",
                            icon = Icons.Filled.Person,
                            onClick = { navController.navigate("personal") }
                        )
                    }
                    if ("Tu empresa" in allowedModules.value) {
                        ConfiguracionItem(
                            text = "Empresa",
                            icon = Icons.Filled.LocationOn,
                            onClick = { navController.navigate("tuempresa") }
                        )
                    }
                    if ("Salidas Varias" in allowedModules.value) {
                        ConfiguracionItem(
                            text = "Salidas Varias",
                            icon = Icons.Filled.LocationOn,
                            onClick = { }
                        )
                    }
                    if ("Kardex" in allowedModules.value) {
                        ConfiguracionItem(
                            text = "Kardex",
                            icon = Icons.Filled.LocationOn,
                            onClick = { }
                        )
                    }
                }
            }
        }
    }
}