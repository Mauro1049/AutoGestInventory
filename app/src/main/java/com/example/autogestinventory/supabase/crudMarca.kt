package com.example.autogestinventory.supabase

import com.example.autogestinventory.Client.SupabaseClient.supabase
import com.example.autogestinventory.model.Marca
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put


class crudMarca {

    suspend fun insertarMarca(descripcion: String, idEmpresa: Int): Result<Unit> {
        return try {
            val params = buildJsonObject {
                put("_descripcion", descripcion)
                put("_idempresa", idEmpresa)
            }

            supabase.postgrest.rpc(
                "insertarmarca",
                params
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMarcasPorEmpresa(idEmpresa: Int): Result<List<Marca>>{
        return try {
            val marcas = supabase.from("marca")
                .select {
                    filter {
                        eq("id_empresa", idEmpresa)
                    }
                }
                .decodeList<Marca>()
            Result.success(marcas)
        } catch (e: Exception) {
            println("Error al obtener Marcas: ${e}")
            Result.failure(e)
        }
    }

    suspend fun elimarMarca(idMarca: Int): Result<Boolean>{
        return try {
            val response = supabase.from("marca")
                .delete{
                    filter {
                        eq("id", idMarca)
                    }
                }
            Result.success(true)
        } catch (e: Exception){
            println("Error al borrar marca")
            Result.failure(e)
        }
    }

    suspend fun editarMarca(id: Int, descripcion: String): Result<Unit>{
        return try {
            val params = buildJsonObject {
                put("_id", id)
                put("_descripcion", descripcion)
            }

            supabase.postgrest.rpc(
                "editarmarca",
                params
            )

            Result.success(Unit)
        }catch (e: Exception){
            Result.failure(e)
        }
    }

}