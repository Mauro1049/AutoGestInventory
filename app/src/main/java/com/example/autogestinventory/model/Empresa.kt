package com.example.autogestinventory.model

import kotlinx.serialization.Serializable

@Serializable
data class Empresa(
    val id: Int? = null,
    val nombre: String,
    val simbolomoneda: String,
    val iduseradmin: Int
)
