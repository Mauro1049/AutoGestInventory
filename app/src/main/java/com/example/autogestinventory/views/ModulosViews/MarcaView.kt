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
import com.example.autogestinventory.model.Marca
import com.example.autogestinventory.supabase.crudEmpresa
import com.example.autogestinventory.supabase.crudMarca
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarcaView(navController: NavController){
    var marcas by remember { mutableStateOf<List<Marca>>(emptyList()) }
    val userId = supabase.auth.currentUserOrNull()?.id
    var idEmpresa by remember { mutableStateOf<Int?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var nuevaDescripcion by remember { mutableStateOf("") }

    var marcaAEditar by remember { mutableStateOf<Marca?>(null) }
    var descripcionEditada by remember { mutableStateOf("") }

    suspend fun cargarMarcas(idEmpresa: Int) {
        val resultado = crudMarca().getMarcasPorEmpresa(idEmpresa)
        if (resultado.isSuccess) {
            marcas = resultado.getOrDefault(emptyList())
        }
    }

    LaunchedEffect(Unit) {
        userId?.let { user ->
            val empresa = crudEmpresa().getEmpresaDelUsuarioActual(userId)
            empresa.onSuccess { empresa ->
                idEmpresa = empresa.id
                empresa.id?.let {
                    idEmpresa?.let { it1 ->
                        crudMarca().getMarcasPorEmpresa(it1).onSuccess { lista ->
                            marcas = lista
                        }
                    }
                }
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp).fillMaxSize()){
        Column {
            Text("Marca View")

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nuevaDescripcion,
                onValueChange = { nuevaDescripcion = it },
                label = { Text("Nueva marca") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        idEmpresa?.let { empresaId ->
                            val resultado = crudMarca().insertarMarca(nuevaDescripcion, empresaId)
                            if (resultado.isSuccess) {
                                nuevaDescripcion = ""
                                val recargar = crudMarca().getMarcasPorEmpresa(empresaId)
                                if (recargar.isSuccess) {
                                    marcas = recargar.getOrDefault(emptyList())
                                }
                            } else {
                                println("Error al insertar marca: ${resultado.exceptionOrNull()?.message}")
                            }
                        }
                    }
                }
            ) {
                Text("Insertar Marca")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Text("Lista de Marcas", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(marcas) { marca ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(text = "ID: ${marca.id}")
                            Text(text = "Descripción: ${marca.descripcion}")
                            Text(text = "Empresa: ${marca.id_empresa}")
                            Spacer(modifier = Modifier.height(8.dp))

                            Row (horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        marcaAEditar = marca
                                        descripcionEditada = marca.descripcion
                                    }
                                ) {
                                    Text("Editar")
                                }

                                Button(
                                    onClick = {
                                        marca.id.let { idMarca ->
                                            coroutineScope.launch {
                                                val result = crudMarca().elimarMarca(idMarca)
                                                if (result.isSuccess){
                                                    idEmpresa?.let { cargarMarcas(it) }
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
        marcaAEditar?.let {
            AlertDialog(
                onDismissRequest = {
                    marcaAEditar = null
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
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                marcaAEditar?.id.let {
                                    val result = it?.let { it1 -> crudMarca().editarMarca(it1, descripcionEditada) }
                                    idEmpresa.let {
                                        if (it != null) {
                                            cargarMarcas(it)
                                        }
                                    }
                                    marcaAEditar = null
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
                            marcaAEditar = null
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