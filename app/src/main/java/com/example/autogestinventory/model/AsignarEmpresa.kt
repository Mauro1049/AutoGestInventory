package com.example.autogestinventory.model

import kotlinx.serialization.Serializable

@Serializable
data class AsignarEmpresa(
    val id: Int,
    val id_empresa: Int,
    val id_usuario: Int
)
