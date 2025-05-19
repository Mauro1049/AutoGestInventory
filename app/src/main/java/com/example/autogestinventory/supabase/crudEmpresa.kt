package com.example.autogestinventory.supabase

import com.example.autogestinventory.Client.SupabaseClient.supabase
import com.example.autogestinventory.model.AsignarEmpresa
import com.example.autogestinventory.model.Empresa
import com.example.autogestinventory.model.Usuario
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.rpc

class crudEmpresa {

    suspend fun obtenerTodasLasEmpresas(): List<Empresa> {
        val response = supabase.from("empresa").select().decodeList<Empresa>()
        return response
    }

    suspend fun getEmpresaDelUsuarioActual(userIdAuth: String): Result<Empresa> {
        return try {
            val usuario = supabase.from("usuarios")
                .select {
                    filter {
                        eq("idauth", userIdAuth)
                    }
                }
                .decodeSingle<Usuario>()

            val asignacion = supabase.from("asignar_empresa")
                .select {
                    filter {
                        eq("id_usuario", usuario.id ?: return Result.failure(Exception("ID del usuario es nulo")))
                    }
                }
                .decodeSingle<AsignarEmpresa>()

            val empresa = supabase.from("empresa")
                .select {
                    filter {
                        eq("id", asignacion.id_empresa)
                    }
                }
                .decodeSingle<Empresa>()

            Result.success(empresa)

        } catch (e: Exception) {
            println("Error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun contarUsuariosPorEmpresa(idEmpresa: Int): Result<Int> {
        return try {
            val cantidadUsuarios = supabase.postgrest.rpc(
                function = "contarusuariosporempresas",
                parameters = mapOf("id_empresa_param" to idEmpresa)
            ).decodeAs<Int>()

            Result.success(cantidadUsuarios)
        } catch (e: Exception) {
            println("Error al contar usuarios desde getEmpresa: ${e.message}")
            Result.failure(e)
        }
    }

}