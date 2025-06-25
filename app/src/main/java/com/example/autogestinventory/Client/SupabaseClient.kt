package com.example.autogestinventory.Client

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {
    val supabase = createSupabaseClient(
        // URL del proyecto Supabase
        supabaseUrl = "https://xzivzhljheysbhdvjoug.supabase.co",
        // API key de acceso
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inh6aXZ6aGxqaGV5c2JoZHZqb3VnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDMxMTgwNjksImV4cCI6MjA1ODY5NDA2OX0.yyEhidma5eiZEz5qmGyHEgjg6ciAFMmS1540u54Sjoo"
    ){
        // Habilita acceso a base de datos
        install(Postgrest)
        // Habilita módulo de autenticación
        install(Auth)
    }
    // Acceso directo al módulo de autenticación
    val auth = supabase.auth
}