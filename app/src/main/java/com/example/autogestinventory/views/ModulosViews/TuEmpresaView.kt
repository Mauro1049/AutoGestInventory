package com.example.autogestinventory.views.ModulosViews

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.autogestinventory.AuthRepository.LoginUser
import com.example.autogestinventory.Client.SupabaseClient.supabase
import com.example.autogestinventory.components.InfoCard
import com.example.autogestinventory.model.Empresa
import com.example.autogestinventory.model.Usuario
import com.example.autogestinventory.supabase.crudEmpresa
import com.example.autogestinventory.supabase.crudUsuarios
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TuEmpresaView(navController: NavController){

    var empresa by remember { mutableStateOf<Empresa?>(null) }
    var usuario by remember { mutableStateOf<Usuario?>(null) }
    val userId = supabase.auth.currentUserOrNull()?.id
    var usuarios by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        userId?.let {
            crudEmpresa().getEmpresaDelUsuarioActual(it).onSuccess { empresaObtenida ->
                empresa = empresaObtenida

                empresaObtenida.id.let { idEmpresa ->
                    if (idEmpresa != null) {
                        crudEmpresa().contarUsuariosPorEmpresa(idEmpresa).onSuccess {  cantidad ->
                            usuarios = cantidad
                        }.onFailure {
                            println("Error al contar usuarios desde getEmpresa: ${it.message}")
                        }
                    }
                }
            }

            crudUsuarios().getUsuarioById(userId).onSuccess { usuarioObtenido ->
                usuario = usuarioObtenido
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bienvenido: ${usuario?.nombres ?: "Cargando..."}") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                empresa?.let {
                    Text(
                        text = it.nombre,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Panel de Control", style = MaterialTheme.typography.titleMedium, color = Color.Gray)

                Spacer(modifier = Modifier.height(24.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    empresa?.let {
                        InfoCard(label = "Monedas", value = it.simbolomoneda, icon = Icons.Filled.AccountCircle)
                        InfoCard(label = "Usuarios", value = usuarios.toString(), icon = Icons.Filled.AccountCircle)
                    }

                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
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
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Cerrar sesión", color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navController.navigate("configuracion") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Configuración", color = Color.White)
                }
            }
        }
    }
}