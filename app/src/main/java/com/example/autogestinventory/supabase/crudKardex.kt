package com.example.autogestinventory.supabase

import com.example.autogestinventory.model.KardexDetalle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.autogestinventory.Client.SupabaseClient.supabase
import com.example.autogestinventory.model.Movimiento
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put


class crudKardex {
    suspend fun obtenerKardexPorEmpresa(idEmpresa: Int): Result<List<KardexDetalle>> {
        return withContext(Dispatchers.IO) {
            try {
                val params = buildJsonObject {
                    put("_id_empresa", idEmpresa)
                }

                val resultado = supabase.postgrest.rpc(
                    "mostrarkardexporempresa",
                    params
                ).decodeList<KardexDetalle>()

                Result.success(resultado)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun registrarMovimiento(
        fecha: String,
        tipo: String,
        cantidad: Int,
        idProducto: Int,
        idEmpresa: Int,
        idUsuario: String,
        detalle: String
    ): Result<Unit> {
        return try {
            val movimiento = Movimiento(
                fecha = fecha,
                tipo = tipo,
                cantidad = cantidad.toDouble(),
                id_producto = idProducto,
                id_empresa = idEmpresa,
                id_usuario = idUsuario,
                detalle = detalle
            )
            supabase.from("kardex").insert(movimiento)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }




}