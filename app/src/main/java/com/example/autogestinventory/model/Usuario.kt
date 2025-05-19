package com.example.autogestinventory.model

import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val id: Int? = null,
    val nombres: String,
    val nro_doc: String,
    val telefono: String,
    val direccion: String,
    val fecharegistro: String? = null,
    val estado: String,
    val tipouser: String,
    val idauth: String? = null,
    val tipodoc: String,
    val correo: String
)
