package com.example.autogestinventory.views.AuthViews

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.example.autogestinventory.Client.SupabaseClient.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionSource
import io.github.jan.supabase.auth.status.SessionStatus

@Composable
fun SplashView(navController: NavController){
    LaunchedEffect(Unit) {
        supabase.auth.sessionStatus.collect { status ->
            when (status) {
                is SessionStatus.Authenticated -> {
                    when (status.source) {
                        is SessionSource.SignIn -> {
                            println("Usuario ha iniciado sesión manualmente.")
                            navController.navigate("tuempresa") {
                                popUpTo("splash") { inclusive = true }
                            }
                        }
                        else -> {
                            println("Sesión autenticada desde otra fuente: ${status.source}")
                            navController.navigate("tuempresa") {
                                popUpTo("splash") { inclusive = true }
                            }
                        }
                    }
                }
                is SessionStatus.NotAuthenticated -> {
                    println("Usuario no autenticado.")
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
                SessionStatus.Initializing -> println("Inicializando sesión...")
                is SessionStatus.RefreshFailure -> println("Error al refrescar sesión: ${status.cause}")
                else -> {}
            }
        }
    }
}