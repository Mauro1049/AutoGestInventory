package com.example.autogestinventory.views.ModulosViews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.autogestinventory.Client.SupabaseClient.supabase
import com.example.autogestinventory.model.Categoria
import com.example.autogestinventory.model.Marca
import com.example.autogestinventory.supabase.crudCategorias
import com.example.autogestinventory.supabase.crudEmpresa
import com.example.autogestinventory.supabase.crudMarca
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

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

    Column(modifier = Modifier.padding(16.dp).fillMaxSize()){
        Column {
            Text("Categorias View")

            Spacer(modifier = Modifier.height(16.dp))

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
                }
            ) {
                Text("Insertar Categoria")
            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Text("Lista de Categorias", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(categorias) { categoria ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(text = "ID: ${categoria.id}")
                            Text(text = "Descripción: ${categoria.descripcion}")
                            Text(text = "Empresa: ${categoria.id_empresa}")
                            Text(text = "Color: ${categoria.color}")
                            Spacer(modifier = Modifier.height(8.dp))

                            Row (horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        categoriaAEditar = categoria
                                        descripcionEditada = categoria.descripcion
                                    }
                                ) {
                                    Text("Editar")
                                }

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
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)

                                ) {
                                    Text("Eliminar")
                                }
                            }
                        }
                    }
                }
            }
            Button(
                onClick = {
                    navController.navigate("configuracion")
                }
            ) {
                Text("cofiguracion")
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