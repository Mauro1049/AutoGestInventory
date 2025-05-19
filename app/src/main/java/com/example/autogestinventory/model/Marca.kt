package com.example.autogestinventory.model

import kotlinx.serialization.Serializable

@Serializable
data class Marca(
    val id: Int,
    val descripcion: String,
    val id_empresa: Int
)
