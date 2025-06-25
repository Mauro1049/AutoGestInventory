package com.example.autogestinventory.model

import kotlinx.serialization.Serializable
import java.sql.Date

@Serializable
data class Movimiento(
    val fecha: String,
    val tipo: String,
    val cantidad: Double,
    val id_producto: Int,
    val id_empresa: Int,
    val id_usuario: String,
    val estado: Int = 1,
    val detalle: String
)
