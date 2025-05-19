package com.example.autogestinventory.AuthRepository

import com.example.autogestinventory.Client.SupabaseClient.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

class LoginUser {

    suspend fun loginUser(email: String, password: String): Result<String> {
        return try {
            val session = supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            Result.success("Inicio de sesión exitoso.")

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signOut(){
        try {
            supabase.auth.signOut()
        }catch (e : Exception){
            println(e)
        }
    }
}