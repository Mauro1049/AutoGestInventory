package com.example.autogestinventory.model

import kotlinx.serialization.Serializable

@Serializable
data class Producto(
    val id: Int,
    val descripcion: String,
    val id_marca: Int,
    val stock: Double,
    val stock_minimo: Double,
    val codigobarras: String?,
    val codigointerno: String?,
    val precioventa: Double,
    val preciocompra: Double,
    val id_categoria: Int,
    val id_empresa: Int
)
