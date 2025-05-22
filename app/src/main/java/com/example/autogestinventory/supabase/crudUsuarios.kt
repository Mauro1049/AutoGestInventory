package com.example.autogestinventory.supabase

import io.github.jan.supabase.postgrest.from
import com.example.autogestinventory.Client.SupabaseClient.supabase
import com.example.autogestinventory.model.Modulos
import com.example.autogestinventory.model.Usuario
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.datetime.Clock
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class crudUsuarios {

    suspend fun obtenerUsuarios(){

    }

    suspend fun obtenerPersonalTodos(idEmpresa: Int): List<Usuario> {
        return try {
            val response = supabase.postgrest
                .rpc("mostrarpersonal", buildJsonObject {
                    put("_id_empresa", idEmpresa)
                })
                .decodeList<Usuario>()
            response
        } catch (e: Exception) {
            println("Error al traer Personal ${e}")
            emptyList()
        }
    }

    suspend fun createUser(
        email: String,
        password: String,
        nombres: String,
        nroDoc: String,
        telefono: String,
        direccion: String,
        tipodoc: String,
        tipouser: String,
        idEmpresa: Int,
        modulosSeleccionados: List<Modulos>
    ): Result<String> {
        return try {
            val user = supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }

            val userIdAuth = supabase.auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("Error al obtener UID"))

            val insertUserResult = supabase.from("usuarios").insert(
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

            val userRecord = supabase.from("usuarios")
                .select {
                    filter {
                        eq("idauth", userIdAuth)
                    }
                }
                .decodeSingle<Usuario>()

            val idUsuario = userRecord.id

            if (idUsuario != null) {
                val asignacionResult = supabase.from("asignar_empresa").insert(
                    listOf(
                        mapOf(
                            "id_usuario" to idUsuario,
                            "id_empresa" to idEmpresa
                        )
                    )
                )
            } else {
                return Result.failure(Exception("No se pudo obtener el ID del usuario insertado"))
            }

            val asignacionModulos = modulosSeleccionados.map { modulos ->
                mapOf(
                    "id_usuario" to idUsuario,
                    "id_modulo" to modulos.id
                )
            }

            if (asignacionModulos.isNotEmpty()) {
                supabase.from("permisos").insert(asignacionModulos)
            }

            return Result.success("Usuario, empresa y módulos asignados correctamente.")

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUsuarioById(id: String): Result<Usuario> {
        val user = supabase.auth.currentUserOrNull()?.id
        return try {
            val response = supabase.from("usuarios")
                .select {
                    filter {
                        if (user != null) {
                            eq("idauth", id)
                        }
                    }
                }.decodeSingle<Usuario>()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun elimarUsuario(idUsuario: Int): Result<Boolean>{
        return try {
            val response = supabase.from("usuarios")
                .delete{
                    filter {
                        eq("id", idUsuario)
                    }
                }
            Result.success(true)
        } catch (e: Exception){
            println("Error al borrar usuario")
            Result.failure(e)
        }
    }
}