package com.example.autogestinventory.supabase

import com.example.autogestinventory.Client.SupabaseClient.supabase
import com.example.autogestinventory.model.Empresa
import com.example.autogestinventory.model.Modulos
import com.example.autogestinventory.model.UsuarioConModulos
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

class crudModulos {

    suspend fun obtenerTodosLosModulos(): List<Modulos> {
        val response = supabase.from("modulos").select().decodeList<Modulos>()
        return response
    }

    suspend fun obtenerModulosAsignados(idAuth: String): List<String> {
        return try {
            val response = supabase.from("usuarios")
                .select(Columns.raw("id, permisos(id_modulo, modulos(nombre))")) {
                    filter {
                        eq("idauth", idAuth)
                    }
                }
                .decodeSingle<UsuarioConModulos>()

            response.permisos.mapNotNull { it.modulos?.nombre }
        } catch (e: Exception) {
            println("Error al obtener módulos: ${e.message}")
            emptyList()
        }
    }
}