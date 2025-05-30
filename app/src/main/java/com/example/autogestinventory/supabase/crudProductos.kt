package com.example.autogestinventory.supabase

import com.example.autogestinventory.Client.SupabaseClient.supabase
import com.example.autogestinventory.model.Marca
import com.example.autogestinventory.model.Producto
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put


class crudProductos {

    suspend fun insertarProducto(producto: Producto): Result<Unit> {
        return try {
            val params = buildJsonObject {
                put("_descripcion", producto.descripcion)
                put("_idmarca", producto.id_marca)
                put("_stock", producto.stock)
                put("_stock_minimo", producto.stock_minimo)
                put("_codigobarras", producto.codigobarras)
                put("_codigointerno", producto.codigointerno)
                put("_precioventa", producto.precioventa)
                put("_preciocompra", producto.precioventa)
                put("_id_categoria", producto.id_categoria)
                put("_id_empresa", producto.id_empresa)
            }

            supabase.postgrest.rpc(
                "insertarproductos",
                params
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductosPorEmpresa(idEmpresa: Int): Result<List<Producto>>{
        return try {
            val productos = supabase.from("productos")
                .select {
                    filter {
                        eq("id_empresa", idEmpresa)
                    }
                }
                .decodeList<Producto>()
            Result.success(productos)
        } catch (e: Exception) {
            println("Error al obtener Productos: ${e}")
            Result.failure(e)
        }
    }

    suspend fun elimarProducto(idProducto: Int): Result<Boolean>{
        return try {
            val response = supabase.from("productos")
                .delete{
                    filter {
                        eq("id", idProducto)
                    }
                }
            Result.success(true)
        } catch (e: Exception){
            println("Error al borrar producto")
            Result.failure(e)
        }
    }

    suspend fun editarProducto(id: Int, descripcion: String): Result<Unit>{
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