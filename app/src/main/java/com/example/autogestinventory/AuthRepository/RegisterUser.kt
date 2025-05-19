package com.example.autogestinventory.AuthRepository

import com.example.autogestinventory.Client.SupabaseClient.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.datetime.Clock

class RegisterUser {
    suspend fun registerUser(
        email: String,
        password: String,
        nombres: String,
        nroDoc: String,
        telefono: String,
        direccion: String,
        tipodoc: String,
        tipouser: String
        ): Result<String> {
        return try {
            val user = supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }

            val userIdAuth = supabase.auth.currentUserOrNull()?.id

            val insertResult = supabase.from("usuarios").insert(
                listOf(
                    mapOf(
                        "nombres" to nombres,
                        "nro_doc" to nroDoc,
                        "telefono" to telefono,
                        "direccion" to direccion,
                        "fecharegistro" to Clock.System.now().toString(),
                        "estado" to "activo",
                        "tipouser" to tipouser,
                        "idauth" to userIdAuth,
                        "tipodoc" to tipodoc,
                        "correo" to email
                    )
                )
            )

            Result.success("Registro Exitoso")

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}