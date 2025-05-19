package com.example.autogestinventory.supabase

import com.example.autogestinventory.Client.SupabaseClient.supabase
import com.example.autogestinventory.model.Categoria
import com.example.autogestinventory.model.Marca
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put


class crudCategorias {

    suspend fun insertarCategoria(descripcion: String, idEmpresa: Int, color: String): Result<Unit> {
        return try {
            val params = buildJsonObject {
                put("_descripcion", descripcion)
                put("_idempresa", idEmpresa)
                put("_color", color)
            }

            supabase.postgrest.rpc(
                "insertarcategorias",
                params
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCategoriasPorEmpresa(idEmpresa: Int): Result<List<Categoria>>{
        return try {
            val categorias = supabase.from("categorias")
                .select {
                    filter {
                        eq("id_empresa", idEmpresa)
                    }
                }
                .decodeList<Categoria>()
            Result.success(categorias)
        } catch (e: Exception) {
            println("Error al obtener Categorias: ${e}")
            Result.failure(e)
        }
    }

    suspend fun elimarCategoria(idCategoria: Int): Result<Boolean>{
        return try {
            val response = supabase.from("categorias")
                .delete{
                    filter {
                        eq("id", idCategoria)
                    }
                }
            Result.success(true)
        } catch (e: Exception){
            println("Error al borrar categoria")
            Result.failure(e)
        }
    }

    suspend fun editarCategoria(id: Int, descripcion: String, color: String): Result<Unit>{
        return try {
            val params = buildJsonObject {
                put("_id", id)
                put("_descripcion", descripcion)
                put("_color", color)
            }

            supabase.postgrest.rpc(
                "editarcategoria",
                params
            )

            Result.success(Unit)
        }catch (e: Exception){
            Result.failure(e)
        }
    }

}