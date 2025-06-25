package com.example.autogestinventory.model

import kotlinx.serialization.Serializable

@Serializable
data class KardexDetalle(
    val id: Int,
    val descripcion: String,
    val fecha: String,
    val cantidad: Float,
    val tipo: String,
    val nombres: String,
    val stock: Float,
    val estado: Int
)
